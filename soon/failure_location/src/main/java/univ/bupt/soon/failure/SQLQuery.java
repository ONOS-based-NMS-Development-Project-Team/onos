package univ.bupt.soon.failure;

import com.google.common.collect.Lists;
import org.onosproject.soon.dataset.original.AlarmPredictionItem;
import org.onosproject.soon.dataset.original.FailureClassificationItem;
import org.onosproject.soon.dataset.original.Item;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.List;
import org.postgresql.Driver;

/**
 * 从数据库中进行相关数据查询的类
 */
public class SQLQuery {

    private final Logger log = LoggerFactory.getLogger(getClass());

    public static SQLQuery instance = new SQLQuery();
    private Connection conn = null;
    private Statement stmt = null;
    private final String JDBC_DRIVER = "org.postgresql.Driver";
    private final String DB_URL = "jdbc:postgresql://10.108.69.165:5432/ecoc2018";
    private final String USER = "postgres";
    private final String PASS = "bupt";

    private SQLQuery(){}

    /**
     * 连接数据库
     * @return 是否连接成功
     */
    boolean connect() {
        Driver driver = new Driver();
        try {
            if (conn == null || conn.isClosed()) {
                // 如果连接已经关闭，则重启连接
                Class.forName(JDBC_DRIVER);
                conn = DriverManager.getConnection(DB_URL, USER, PASS);
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

    /**
     * 单表查询，不支持联合查询
     * @param items select之后的部分
     * @param constraint 查询的约束语句
     * @return 返回查询结果
     */
    public List<Item> queryData(String items, String constraint) {
        List<String> it = Lists.newArrayList();
        it.add(items);
        return queryData(it, constraint);
    }


    /**
     * 单表查询，不支持联合查询
     * @param items select之后的部分
     * @param constraint 查询的约束语句，即在where之后的部分
     * @return 返回查询结果
     */
    public List<Item> queryData(List<String> items, String constraint) {
        StringBuilder builder = new StringBuilder("SELECT ");
        for (String item : items) {
            builder.append(item).append(',');
        }
        builder.deleteCharAt(builder.length()-1);
        builder.append(" FROM failure_class ").append(constraint);
        String query = builder.toString();
        log.info(query);
        // 开始查询ResultSet
        try {
            ResultSet rs = stmt.executeQuery(query);
            return parseResultSet(rs, FailureClassificationItem.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 关闭数据库连接
     */
    void close() {
        try {
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    /**
     *
     * @param rs
     * @param cls
     * @return
     */
    public List<Item> parseResultSet(ResultSet rs, Class cls) {
        List<Item> rtn = Lists.newArrayList();
        List<String> valNames = extractValueInOrder(rs, cls);
        try {
            while (rs.next()) {
                Object obj = cls.getDeclaredConstructor().newInstance();
                for (int i=1; i<=valNames.size(); i++) {
                    Field field = cls.getDeclaredField(valNames.get(i-1));
                    boolean flag = field.isAccessible();
                    // 临时改变可访问状态
                    field.setAccessible(true);
                    // 判断类型是否为list
                    int tp = rs.getMetaData().getColumnType(i);
                    switch (tp) {
                        case Types.ARRAY:
                            // 如果是数组
                            StringBuilder builder = new StringBuilder(rs.getString(i));
                            // 删除头尾的大括号 { }
                            builder.deleteCharAt(0);
                            builder.deleteCharAt(builder.length()-1);
                            String[] datas = builder.toString().split(",");
                            double[] item = new double[datas.length];
                            for (int t=0; t<item.length; t++) {
                                item[t] = Double.parseDouble(datas[i]);
                            }
                            field.set(obj, item);
                            break;
                        case Types.BIT:
                            // bit类型为boolean
                            field.set(obj, rs.getBoolean(i));
                            break;
                        case Types.TIMESTAMP:
                            // 如果是timestamp类型
                            field.set(obj, rs.getDate(i));
                            break;
                        case Types.TIMESTAMP_WITH_TIMEZONE:
                            field.set(obj, rs.getDate(i));
                            // 如果是timestamp with time zone类型
                            break;
                        case Types.VARCHAR:
                            // 如果是varchar
                            field.set(obj, rs.getString(i));
                            break;
                        case Types.REAL:
                            // 如果是real
                            field.set(obj, rs.getFloat(i));
                            break;
                        case Types.BOOLEAN:
                            // 如果是bool
                            field.set(obj, rs.getBoolean(i));
                            break;
                        case Types.DOUBLE:
                            // 如果是double
                            field.set(obj, rs.getDouble(i));
                            break;
                        case Types.INTEGER:
                            // 如果是integer
                            field.set(obj, rs.getInt(i));
                            break;
                        case Types.SMALLINT:
                            // 如果是smallint
                            field.set(obj, rs.getInt(i));
                            break;
                        default:
                            throw new RuntimeException("Can't parse type from ResultSet");
                    }
                    // 恢复可访问状态
                    field.setAccessible(flag);
                }
                rtn.add((Item) obj);
            }
            return rtn;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 提取rs结果中，每一条数据的值的排列顺序
     * @param rs
     * @param cls
     * @return
     */
    private List<String> extractValueInOrder(ResultSet rs, Class cls) {
        try {
            // 获取得到的rs里面，每一列的列名的排列顺序
            ResultSetMetaData rsmd = rs.getMetaData();
            int num = rsmd.getColumnCount();
            List<String> colNames = Lists.newArrayListWithCapacity(num);
            Field[] fields = cls.getDeclaredFields();
            String name = fields[0].getName();
            for (int i=1; i<=num; i++) {
                colNames.add(rsmd.getColumnName(i));
                cls.getDeclaredField(rsmd.getColumnName(i));
            }
            return colNames;
        } catch(NoSuchFieldException nsfe) {
            // 说明存在没有的变量名
            nsfe.printStackTrace();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

}
