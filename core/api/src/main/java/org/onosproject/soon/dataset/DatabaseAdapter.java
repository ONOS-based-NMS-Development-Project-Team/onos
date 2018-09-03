package org.onosproject.soon.dataset;

import com.google.common.collect.Lists;
import org.onosproject.soon.dataset.original.Item;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Types;
import java.util.List;

public abstract class DatabaseAdapter {


    /**
     * 数据库连接
     * @return 是否连接成功
     */
    public abstract boolean connect();

    /**
     * 关闭数据库连接
     */
    public abstract void close();


    /**
     * 单表查询，不支持联合查询
     * @param items select之后的部分
     * @param constraint 查询的约束语句
     * @param tableName 表名
     * @param cls 返回Item的实现类名
     * @return 返回查询结果
     */
    public List<Item> queryData(String items, String constraint, String tableName, Class cls) {
        List<String> it = Lists.newArrayList();
        it.add(items);
        return queryData(it, constraint, tableName, cls);
    }

//    /**
//     * 给出最通用的查询语句的执行
//     * @param sql
//     * @return
//     */
//    public abstract  List<Item> queryData(String sql);

    /**
     * 单表查询
     * @param items select之后的部分
     * @param constraint 查询的约束语句，可能是where语句，order by语句，group by语句等等
     * @param tableName 要查询的表名
     * @param cls 返回Item的实现类名
     * @return 返回查询结果
     */
    public abstract List<Item> queryData(List<String> items, String constraint, String tableName, Class cls);



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
                                item[t] = Double.parseDouble(datas[t]);
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
