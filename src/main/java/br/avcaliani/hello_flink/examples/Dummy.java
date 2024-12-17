package br.avcaliani.hello_flink.examples;

import br.avcaliani.hello_flink.Pipeline;
import br.avcaliani.hello_flink.models.Args;
import br.avcaliani.hello_flink.models.Transaction;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.connector.file.src.FileSource;
import org.apache.flink.core.fs.Path;
import org.apache.flink.formats.csv.CsvReaderFormat;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.dataformat.csv.CsvSchema;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.dataformat.csv.CsvSchema.Column;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.dataformat.csv.CsvSchema.ColumnType;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class Dummy extends Pipeline {

    @Override
    public Pipeline run(Args args) throws Exception {
        var env = args.getEnv();
        var filePath = args.getBucket() + "/input/transactions/";
        var transactions = readTransactions(env, filePath);
        transactions.print();
        env.execute("flink-csv-reader--transactions");
        return this;
    }

    /**
     * Read the Transactions CSV.
     * Instead of declaring the columns manually in the schema, you can use a POJO instead.
     *
     * @return CSV Schema.
     */
    private DataStream<Transaction> readTransactions(StreamExecutionEnvironment env, String path) {

        var schema = CsvSchema.builder()
                .addColumn(new Column(0, "user_id", ColumnType.STRING))
                .addColumn(new Column(1, "tid", ColumnType.STRING))
                .addColumn(new Column(2, "amount", ColumnType.STRING))
                .addColumn(new Column(3, "created_at", ColumnType.STRING))
                .addColumn(new Column(3, "tags", ColumnType.ARRAY))
                .setArrayElementSeparator("#")
                .setColumnSeparator(',')
                .setUseHeader(true)
                .build();

        var format = CsvReaderFormat.forSchema(schema, TypeInformation.of(Transaction.class));

        var fileSource = FileSource
                .forRecordStreamFormat(format, new Path(path))
                .build();

        return env.fromSource(
                fileSource,
                WatermarkStrategy.noWatermarks(),
                "csv-source-transactions"
        );
    }

}
