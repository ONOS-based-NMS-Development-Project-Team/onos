package univ.bupt.soon.almpred;


import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.felix.scr.annotations.*;
import org.onosproject.core.ApplicationId;
import org.onosproject.core.CoreService;
import org.onosproject.soon.MonitorData;
import org.onosproject.soon.dataset.DatabaseAdapter;
import org.onosproject.soon.dataset.original.AlarmPredictionItem;
import org.onosproject.soon.dataset.original.Item;
import org.onosproject.soon.foreground.ForegroundCallback;
import org.onosproject.soon.foreground.MLAppRegistry;
import org.onosproject.soon.foreground.MLAppType;
import org.onosproject.soon.foreground.ModelControlService;
import org.onosproject.soon.mlmodel.MLAlgorithmConfig;
import org.onosproject.soon.mlmodel.MLAlgorithmType;
import org.onosproject.soon.mlmodel.MLModelDetail;
import org.onosproject.soon.mlmodel.config.nn.*;
import org.onosproject.soon.platform.MLPlatformService;
import org.postgresql.Driver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.sql.*;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

@Component(immediate = true)
public class AlarmPredComponent {

    private final Logger log = LoggerFactory.getLogger(getClass());
    // 存储模型相关的内容。模型类型，模型id，模型详细配置
    private final Map<MLAlgorithmType, Map<Integer, MLModelDetail>> models = Maps.newConcurrentMap();

    DatabaseAdapter database = new InternalDatabaseAdapter();
    private ApplicationId appId;

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected CoreService coreService;
    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected MLAppRegistry mlAppRegistry;
    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected MLPlatformService platformService;

    @Activate
    protected void activate() {
        appId = coreService.registerApplication("unive.bupt.soon.almpred");
        database.connect();
        // 构造对象
        AlarmPredService aps = new AlarmPredService(MLAppType.ALARM_PREDICTION, "alarm_prediction",
                                AlarmPredictionItem.class, AlarmPredPlatformCallback.class, database, platformService);

        // 注册
        mlAppRegistry.register(aps, aps.getServiceName());
        log.info("hh");
//        test(aps);
        log.info("gg");

    }

    @Deactivate
    protected void deactivate() {
//        mlAppRegistry.unregister(MLAppType.ALARM_PREDICTION);
        database.close();
        log.info("SOON - alarm prediction - Stopped");
    }

    /**
     * 测试从app到平台再到底层的接口
     */
    private void test(ModelControlService service){
        database.connect();
        // 注册前台回调接口
        class TestForegroundCallback implements ForegroundCallback {

            int modelId;

            @Override
            public void operationFailure(int msgId, String description) {
                log.info("received message {} : {}", msgId, description);
            }

            @Override
            public void appliedModelResult(int msgId, List<Item> input, List<String> output) {
                log.info("received message {} : ", msgId);
                log.info("model input is : {}", input);
                log.info("model output is : {}", output);
            }

            @Override
            public void modelEvaluation(int msgId, String result) {
                log.info("received message {} : {}", msgId, result);
            }

            @Override
            public void trainDatasetTransEnd(int msgId, int trainDatasetId) {
                log.info("received message {} : dataset {} trans end!", msgId, trainDatasetId);
            }

            @Override
            public void testDatasetTransEnd(int msgId, int testDatasetId) {
                log.info("received message {} : dataset {} trans end!", msgId, testDatasetId);
                // 因为只有一个训练集和测试集,而且先传输训练集,再传输测试集.因此接到这个消息意味着所有数据集传输结束
                // 开始训练
                service.startTraining(modelId);
            }

            @Override
            public void ResultUrl(int msgId, URI uri) {
                log.info("received message {} : {}", msgId, uri);
            }

            @Override
            public void trainEnd(int msgId) {
                log.info("received message {} : training end", msgId);
                // 训练结束后开始应用
                service.applyModel(modelId, 5);
                // 并且请求URL
                service.queryURL(modelId);
            }

            @Override
            public void intermediateResult(int msgId, MonitorData monitorData) {
                log.info("received message {} : {}", msgId, monitorData.toString());
            }
        }
            TestForegroundCallback foregroundCallback = new TestForegroundCallback();
            // 构建神经网络配置
            MLAlgorithmConfig config = new NNAlgorithmConfig(MLAlgorithmType.FCNNModel, 36, 1,
                    Lists.newArrayList(36),
                    ActivationFunction.RELU,
                    ParamInit.DEFAULT,
                    ParamInit.CONSTANT0,
                    LossFunction.CROSSENTROPY,
                    1,
                    100,
                    Optimizer.SGD,
                    0.005,
                    LRAdjust.CONSTANT,
                    0);
            Pair<Integer, Integer> mm = service.addNewModel(MLAlgorithmType.FCNNModel, config, foregroundCallback);
            if (mm.getLeft() != -1) {
                    int modelId = mm.getLeft();
                    foregroundCallback.modelId = modelId;
                    Pair<Set<Integer>, Set<Integer>> tt = service.transAvailableDataset(modelId);
                    service.setDataset(modelId, tt.getLeft().iterator().next());
                    service.startTraining(modelId);
                }

    }



    /**
     * 数据库操作的相关内部类
     */
    class InternalDatabaseAdapter extends DatabaseAdapter {
        private final Logger log = LoggerFactory.getLogger(getClass());

        private Connection conn = null;
        private Statement stmt = null;
        private final String JDBC_DRIVER;
        private final String DB_URL;
        private final String USER;
        private final String PASS;
        private final String DB_NAME = "ecoc2018";

        public InternalDatabaseAdapter() {
            String path = System.getenv("ONOS_ROOT");
            String file = path + "/soon/resources/database.properties";
            java.util.Properties properties = new Properties();
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

        /**
         * 连接数据库
         *
         * @return 是否连接成功
         */
        @Override
        public boolean connect() {
            Driver driver = new Driver();
            try {
                if (conn == null || conn.isClosed()) {
                    // 如果连接已经关闭，则重启连接
                    Class.forName(JDBC_DRIVER);
                    conn = DriverManager.getConnection(DB_URL + DB_NAME, USER, PASS);
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
         *
         * @param items      select之后的部分
         * @param constraint 查询的约束语句，即在where之后的部分
         * @param tableName  表名
         * @param cls        返回Item的实现类名
         * @return 返回查询结果
         */
        @Override
        public List<Item> queryData(List<String> items, String constraint, String tableName, Class cls) {
            StringBuilder builder = new StringBuilder("SELECT ");
            for (String item : items) {
                builder.append(item).append(',');
            }
            builder.deleteCharAt(builder.length() - 1);
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

        /**
         * 关闭数据库连接
         */
        @Override
        public void close() {
            try {
                stmt.close();
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
