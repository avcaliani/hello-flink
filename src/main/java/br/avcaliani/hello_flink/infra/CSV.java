package br.avcaliani.hello_flink.infra;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.connector.file.src.FileSource;
import org.apache.flink.core.fs.Path;
import org.apache.flink.formats.csv.CsvReaderFormat;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.dataformat.csv.CsvMapper;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class CSV extends Infra {

    public CSV(StreamExecutionEnvironment env) {
        super(env, "csv");
    }

    /**
     * Create a Data Stream based on CSV Source.
     *
     * @param path  CSV File path.
     * @param clazz POJO Class Kafka Message (also the 'T').
     * @return Users Data Stream.
     */
    public <T> DataStream<T> read(String path, Class<T> clazz) {

        CsvReaderFormat<T> csvFormat = CsvReaderFormat.forSchema(
                CsvMapper::new,
                csvMapper ->
                        csvMapper.schemaFor(clazz)
                                .withoutQuoteChar()
                                .withColumnSeparator(','),
                TypeInformation.of(clazz)
        );

        return env.fromSource(
                FileSource.forRecordStreamFormat(csvFormat, new Path(path)).build(),
                WatermarkStrategy.noWatermarks(),
                sourceName(fileName(path))
        );
    }

    private String fileName(String path) {
        return path.replaceAll("/$", "")
                .replaceAll(".*/", "");
    }
}
