package univ.bupt.soon.mlshow.original;

import org.onosproject.soon.dataset.DatabaseAdapter;
import org.onosproject.soon.dataset.original.Item;
import org.postgresql.Driver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.List;
import java.util.Properties;


/**
 * state_grid_sdh数据库的相关查询
 */
public class OriginalDatabaseAdapter extends DatabaseAdapter {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private Connection conn = null;
    private Statement stmt = null;

    private final String JDBC_DRIVER;
    private final String DB_URL;
    private final String USER;
    private final String PASS;
    private final String DB_NAME = "state_grid_sdh";

    OriginalDatabaseAdapter() {
        String path = System.getenv("ONOS_ROOT");
        String file = path + "/soon/resources/database.properties";
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(file));
        } catch (IOException e) {
            e.printStackTrace();

        }
        JDBC_DRIVER = properties.getProperty("JDBC_DRIVER");
        DB_URL = properties.getProperty("DB_URL");
        USER = properties.getProperty("USER");
        PASS = properties.getProperty("PASS");
    }

    @Override
    public boolean connect() {
        Driver driver = new Driver();
        try {
            if (conn == null || conn.isClosed()) {
                // 如果连接已经关闭，则重启连接
                Class.forName(JDBC_DRIVER);
                conn = DriverManager.getConnection(DB_URL+DB_NAME, USER, PASS);
                stmt = conn.createStatement();
                return true;
            } else {
                // 如果连接仍然开启，则直接返回
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void close() {
        try {
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Item> queryData(List<String> items, String constraint, String tableName, Class cls) {
        StringBuilder builder = new StringBuilder("SELECT ");
        for (String item : items) {
            builder.append(item).append(',');
        }
        builder.deleteCharAt(builder.length()-1);
        builder.append(" FROM ").append(tableName).append(" ").append(constraint);
        String query = builder.toString();
        log.info(query);
        // 开始查询ResultSet
        try {
            ResultSet rs = stmt.executeQuery(query);
            return parseResultSet(rs, cls);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Item> queryData(String s, Class cls) {
        try {
            ResultSet rs = stmt.executeQuery(s);
            return parseResultSet(rs, cls);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
