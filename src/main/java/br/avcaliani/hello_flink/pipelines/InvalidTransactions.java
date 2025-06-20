package br.avcaliani.hello_flink.pipelines;

import br.avcaliani.hello_flink.cli.Args;
import br.avcaliani.hello_flink.helpers.MyDeserializer;
import br.avcaliani.hello_flink.models.RichTransaction;
import br.avcaliani.hello_flink.models.Transaction;
import br.avcaliani.hello_flink.models.User;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.state.BroadcastState;
import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.api.common.state.ReadOnlyBroadcastState;
import org.apache.flink.connector.file.src.FileSource;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.core.fs.Path;
import org.apache.flink.formats.csv.CsvReaderFormat;
import org.apache.flink.streaming.api.datastream.BroadcastStream;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import org.apache.flink.streaming.api.functions.co.BroadcastProcessFunction;
import org.apache.flink.util.Collector;


/**
 * This Pipeline reads the data from a Kafka stream, enrich the data and output to another topic.
 * </br>
 * Here's the guide I used to create this 👉
 * <a href="https://nightlies.apache.org/flink/flink-docs-master/docs/connectors/datastream/kafka/">
 * Doc: Apache Flink + Kafka Connector
 * </a>
 */
public class InvalidTransactions extends Pipeline {

    private static final String KAFKA_TOPIC = "DONU_TRANSACTIONS_V1";
    private static final String KAFKA_GROUP_ID = "hello-flink--invalid-txn-pipeline";

    @Override
    public Pipeline run(Args args) throws Exception {

        var env = args.getEnv();
        var users = readUsers(env, args.getBucket() + "/raw/users/");
        var transactions = readTransactions(env, args.getKafkaBrokers());

        joinData(transactions, users)
                .print();

        env.execute("hello-flink--invalid-transactions");
        return this;
    }

    // TODO: Refine this code, not sure if it works!
    // TODO: Explain why I'm using connect and not a window join.
    private DataStream<RichTransaction> joinData(DataStream<Transaction> transactions, DataStream<User> users) {
        MapStateDescriptor<String, User> userStateDescriptor =
                new MapStateDescriptor<>("userBroadcastState", String.class, User.class);

        BroadcastStream<User> broadcastUserStream = users.broadcast(userStateDescriptor);
        return transactions
                .connect(broadcastUserStream)
                .process(new BroadcastProcessFunction<Transaction, User, RichTransaction>() {

                    @Override
                    public void processBroadcastElement(User user, Context ctx, Collector<RichTransaction> out) throws Exception {
                        BroadcastState<String, User> userState = ctx.getBroadcastState(userStateDescriptor);
                        userState.put(user.getId(), user);
                    }

                    @Override
                    public void processElement(
                            Transaction txn,
                            ReadOnlyContext ctx,
                            Collector<RichTransaction> out
                    ) throws Exception {

                        ReadOnlyBroadcastState<String, User> userState = ctx.getBroadcastState(userStateDescriptor);
                        User sender = userState.get(txn.getFrom());
                        User receiver = userState.get(txn.getTo());

                        if (sender == null || receiver == null) {
                            log.warn("Missing user(s) for transaction: {}", txn);
                            return;
                        }

                        var richTxn = new RichTransaction(txn);
                        richTxn.setFrom(sender);
                        richTxn.setTo(receiver);
                        out.collect(richTxn);
                    }

                });
    }

    /**
     * Read transactions data from a Kafka topic.
     *
     * @param env     Flink Stream Exec. Env.
     * @param brokers List of Kafka Brokers.
     * @return Transactions Data Stream.
     */
    private DataStream<Transaction> readTransactions(StreamExecutionEnvironment env, String brokers) {

        KafkaSource<Transaction> source = KafkaSource.<Transaction>builder()
                .setBootstrapServers(brokers)
                .setTopics(KAFKA_TOPIC)
                .setGroupId(KAFKA_GROUP_ID)
                .setStartingOffsets(OffsetsInitializer.earliest())
                .setValueOnlyDeserializer(new MyDeserializer<>(Transaction.class))
                .build();

        return env.fromSource(source, WatermarkStrategy.noWatermarks(), "kafka-source--donu-txn-v1");
    }

    /**
     * Read users data from a CSV file.
     *
     * @param env  Flink Stream Exec. Env.
     * @param path CSV File path.
     * @return Users Data Stream.
     */
    private DataStream<User> readUsers(StreamExecutionEnvironment env, String path) {

        var fileSource = FileSource
                .forRecordStreamFormat(CsvReaderFormat.forPojo(User.class), new Path(path))
                .build();

        return env.fromSource(
                fileSource,
                WatermarkStrategy.noWatermarks(),
                "csv-source--users"
        );
    }

}
