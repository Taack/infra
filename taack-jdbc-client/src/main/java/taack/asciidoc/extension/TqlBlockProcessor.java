package taack.asciidoc.extension;

import org.asciidoctor.ast.*;
import org.asciidoctor.extension.BlockProcessor;
import org.asciidoctor.extension.Contexts;
import org.asciidoctor.extension.Name;
import org.asciidoctor.extension.Reader;
import taack.jdbc.client.TaackConnection;
import taack.jdbc.client.TaackDriver;
import taack.jdbc.client.TaackResultSetJdbc;
import taack.jdbc.common.TaackResultSetOuterClass;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.Properties;

@Name("tql")
@Contexts({Contexts.LISTING})
@ContentModel(ContentModel.RAW)
public class TqlBlockProcessor extends BlockProcessor {


    final String serverUrl;
    final Properties infos;

    public TqlBlockProcessor() {
        URL r = Thread.currentThread().getContextClassLoader().getResource("");
        String rootPath;
        if (r == null) rootPath = ""; else rootPath = r.getPath();
        System.out.println("Getting app.properties from " + rootPath);
        String appConfigPath = rootPath + "app.properties";

        Properties appProps = new Properties();
        try {
            appProps.load(new FileInputStream(appConfigPath));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        this.serverUrl = appProps.getProperty("serverUrl");
        this.infos = appProps;
    }

    @Override
    public Object process(StructuralNode parent, Reader reader, Map<String, Object> attributes) {
        String content = reader.read();
        System.out.println("TQL " + content);
        TaackResultSetJdbc rs;
        try {
            TaackDriver driver = new TaackDriver();
            TaackConnection connection = (TaackConnection) driver.connect(serverUrl, infos);
            Statement statement = connection.createStatement();
            statement.setMaxRows(20);
            rs = (TaackResultSetJdbc) statement.executeQuery(content);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        TaackResultSetOuterClass.TaackResultSet res = rs.taackResultSetProto;
        int lineCount = res.getCellsCount() / res.getColumnsCount();

        Table table = createTable(parent);

        Column[] columns = new Column[res.getColumnsCount()];
        Row rh = createTableRow(table);
        for (int i = 0; i < res.getColumnsCount(); i++) {
            columns[i] = createTableColumn(table, i);
            rh.getCells().add(createTableCell(columns[i], res.getColumns(i).getName()));
        }
        table.getHeader().add(rh);

        for (int i = 0; i < lineCount; i++) {
            Row r = createTableRow(table);
            for (int j = 0; j < res.getColumnsCount(); j++) {
                int index = i * res.getColumnsCount() + j;
                Cell c = null;
                switch (res.getColumns(j).getJavaType()) {
                    case DATE -> c = createTableCell(columns[j], res.getCells(index).getDateValue() + "");
                    case LONG -> c = createTableCell(columns[j], res.getCells(index).getLongValue() + "");
                    case BIG_DECIMAL -> c = createTableCell(columns[j], res.getCells(index).getBigDecimal() + "");
                    case STRING -> {
                        String cc = res.getCells(index).getStringValue();
                        if (cc.startsWith("<")) {
                            cc = "\n+++\n" + cc + "\n+++\n";
                            c = createTableCell(columns[j], cc);
                        } else c = createTableCell(columns[j], cc + "");
                    }
                    case BOOL -> c = createTableCell(columns[j], res.getCells(index).getBoolValue() + "");
                    case BYTE -> c = createTableCell(columns[j], res.getCells(index).getByteValue() + "");
                    case SHORT -> c = createTableCell(columns[j], res.getCells(index).getShortValue() + "");
                    case INT -> c = createTableCell(columns[j], res.getCells(index).getIntValue() + "");
                    case BYTES -> c = createTableCell(columns[j], res.getCells(index).getBytesValue() + "");
                    case UNRECOGNIZED -> System.out.println("|UNRECOGNIZED " + columns[j]);
                }
                r.getCells().add(c);
            }
            table.getBody().add(r);
        }
        return table;
    }
}
