package br.avcaliani.hello_flink.pipelines;

import br.avcaliani.hello_flink.cli.Args;
import br.avcaliani.hello_flink.helpers.MyDeserializer;
import br.avcaliani.hello_flink.models.in.Transaction;
import br.avcaliani.hello_flink.models.in.User;
import br.avcaliani.hello_flink.models.out.DTOTransaction;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.connector.file.src.FileSource;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.core.fs.Path;
import org.apache.flink.formats.csv.CsvReaderFormat;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.dataformat.csv.CsvMapper;
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

        var richTxn = joinData(transactions, users)
                .map(isTxnValid());

        richTxn.filter(DTOTransaction::isValid).print();
        richTxn.filter(DTOTransaction::isInvalid).print();

        env.execute("hello-flink--invalid-transactions");
        return this;
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
                .setDeserializer(new MyDeserializer<>(Transaction.class))
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

        CsvReaderFormat<User> csvFormat = CsvReaderFormat.forSchema(
                CsvMapper::new,
                csvMapper ->
                        csvMapper.schemaFor(User.class)
                                .withoutQuoteChar()
                                .withColumnSeparator(','),
                TypeInformation.of(User.class)
        );

        return env.fromSource(
                FileSource.forRecordStreamFormat(csvFormat, new Path(path)).build(),
                WatermarkStrategy.noWatermarks(),
                "csv-source--users"
        );
    }


    /**
     * It enriches the transaction data adding the user information for receiver and sender.
     *
     * @param transactions Transactions Stream.
     * @param users        Users Stream.
     * @return Enriched Transaction Data.
     */
    private DataStream<DTOTransaction> joinData(DataStream<Transaction> transactions, DataStream<User> users) {
        var userStateDescriptor = new MapStateDescriptor<>("userBroadcastState", String.class, User.class);
        BroadcastStream<User> broadcastUserStream = users.broadcast(userStateDescriptor);
        // TODO: Explain why I'm using connect and not a window join.
        return transactions
                .connect(broadcastUserStream)
                .process(new EnrichTxnFunction(userStateDescriptor));
    }

    /**
     * Check and update the traction "isValid" field.
     *
     * @return Updated Transaction.
     */
    private MapFunction<DTOTransaction, DTOTransaction> isTxnValid() {
        return txn -> {

            var isMissingUser = txn.getSender() == null || txn.getReceiver() == null;
            var isSameUser = txn.getSender() == txn.getReceiver();
            var isValid = !isMissingUser && !isSameUser;

            txn.setIsValid(isValid);
            if (isValid)
                return txn;

            var errors = txn.getErrors();
            if (isMissingUser)
                errors.add("missing user(s) for transaction.");

            if (isSameUser)
                errors.add("transaction have the same user as sender and receiver.");

            return txn;
        };
    }

    /**
     * This class is the broadcast process function.
     * <p>
     * It will enrich the transaction entity with user entity data.
     * <p>
     * Here's the guide I used to create this 👉
     * <a href="https://nightlies.apache.org/flink/flink-docs-master/docs/dev/datastream/fault-tolerance/broadcast_state/">
     * Doc: Apache Flink - The Broadcast State Pattern
     * </a>
     */
    private static class EnrichTxnFunction extends BroadcastProcessFunction<Transaction, User, DTOTransaction> {

        private final MapStateDescriptor<String, User> descriptor;

        EnrichTxnFunction(MapStateDescriptor<String, User> descriptor) {
            this.descriptor = descriptor;
        }

        @Override
        public void processBroadcastElement(User user, Context ctx, Collector<DTOTransaction> out) throws Exception {
            var userState = ctx.getBroadcastState(this.descriptor);
            userState.put(user.getId(), user);
        }

        @Override
        public void processElement(Transaction txn, ReadOnlyContext ctx, Collector<DTOTransaction> out) throws Exception {
            var userState = ctx.getBroadcastState(this.descriptor);
            User sender = userState.get(txn.getFrom());
            User receiver = userState.get(txn.getTo());
            out.collect(new DTOTransaction(txn, sender, receiver));
        }

    }
}
