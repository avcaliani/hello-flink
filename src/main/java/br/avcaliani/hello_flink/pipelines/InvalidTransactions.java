package br.avcaliani.hello_flink.pipelines;

import br.avcaliani.hello_flink.cli.Args;
import br.avcaliani.hello_flink.helpers.MyDeserializer;
import br.avcaliani.hello_flink.models.RichTransaction;
import br.avcaliani.hello_flink.models.Transaction;
import br.avcaliani.hello_flink.models.User;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.connector.file.src.FileSource;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.core.fs.Path;
import org.apache.flink.formats.csv.CsvReaderFormat;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;


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
        transactions
                .map((MapFunction<Transaction, RichTransaction>) RichTransaction::new)
                .print();

        // TODO: Try to use connect, instead of join.
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
