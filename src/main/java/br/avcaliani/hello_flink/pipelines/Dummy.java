package br.avcaliani.hello_flink.pipelines;

import br.avcaliani.hello_flink.cli.Args;
import br.avcaliani.hello_flink.models.User;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.connector.file.src.FileSource;
import org.apache.flink.core.fs.Path;
import org.apache.flink.formats.csv.CsvReaderFormat;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.dataformat.csv.CsvSchema;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.dataformat.csv.CsvSchema.Column;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.dataformat.csv.CsvSchema.ColumnType;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/** This Pipeline reads a CSV file, manipulated the data and print the result.
 * </br>
 * Here's the guide I used to create this 👉
 * <a href="https://nightlies.apache.org/flink/flink-docs-master/docs/connectors/datastream/formats/csv/">
 *     Doc: Apache Flink + CSV
 * </a>
 */
public class Dummy extends Pipeline {

    @Override
    public Pipeline run(Args args) throws Exception {
        var env = args.getEnv();
        var filePath = args.getBucket() + "/raw/users/";
        var users = this.readUsers(env, filePath);
        users.map((MapFunction<User, User>) user -> {
                    user.setEmail(buildEmail(user.getName()));
                    return user;
                })
                .print();
        env.execute("hello-flink--dummy");
        return this;
    }

    /**
     * Read the users CSVs. </br>
     * Instead of declaring the columns manually in the schema, you can use a POJO instead.</br>
     * Example in the <code>InvalidTransactions</code> pipeline.
     *
     * @return CSV Schema.
     */
    private DataStream<User> readUsers(StreamExecutionEnvironment env, String path) {

        var schema = CsvSchema.builder()
                .addColumn(new Column(0, "id", ColumnType.STRING))
                .addColumn(new Column(1, "name", ColumnType.STRING))
                .setColumnSeparator(',')
                .setUseHeader(true)
                .build();

        var format = CsvReaderFormat.forSchema(schema, TypeInformation.of(User.class));

        var fileSource = FileSource
                .forRecordStreamFormat(format, new Path(path))
                .build();

        /* What is a watermark?
         * https://www.decodable.co/blog/understanding-apache-flink-event-time-and-watermarks
         */
        return env.fromSource(
                fileSource,
                WatermarkStrategy.noWatermarks(),
                "csv-source--users"
        );
    }

    /**
     * Create a fake email based on the name.
     *
     * @param name User name.
     * @return User email.
     */
    private String buildEmail(String name) {
        return name.toLowerCase()
                .replaceAll("\\.", "")
                .replaceAll(" ", ".")
                + "@github.com";
    }
}
