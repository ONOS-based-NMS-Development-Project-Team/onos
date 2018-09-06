package univ.bupt.soon.servadj;


import com.google.common.collect.Lists;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.felix.scr.annotations.*;
import org.onosproject.core.ApplicationId;
import org.onosproject.core.CoreService;
import org.onosproject.soon.MonitorData;
import org.onosproject.soon.dataset.DatabaseAdapter;
import org.onosproject.soon.dataset.original.Item;
import org.onosproject.soon.dataset.original.servadj.AreaPredictionItem;
import org.onosproject.soon.dataset.original.servadj.EdgePredictionItem;
import org.onosproject.soon.foreground.ForegroundCallback;
import org.onosproject.soon.foreground.MLAppRegistry;
import org.onosproject.soon.foreground.MLAppType;
import org.onosproject.soon.foreground.ModelControlService;
import org.onosproject.soon.mlmodel.MLAlgorithmConfig;
import org.onosproject.soon.mlmodel.MLAlgorithmType;
import org.onosproject.soon.mlmodel.config.nn.*;
import org.onosproject.soon.platform.MLPlatformService;
import org.postgresql.Driver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import univ.bupt.soon.servadj.predservice.AreaPredPlatformCallback;
import univ.bupt.soon.servadj.predservice.AreaPredictionImpl;
import univ.bupt.soon.servadj.predservice.LinkPredPlatformCallback;
import univ.bupt.soon.servadj.predservice.LinkPredictionImpl;

import java.net.URI;
import java.sql.*;
import java.util.List;
import java.util.Set;

/**
 * 业务调整的内容，包括潮汐峰谷区域流量预测，链路预测，以及基于预测的重构算法
 */
@Component(immediate = true)
public class ServiceAdjustComponent {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private ApplicationId appId;
    private InternalDatabaseAdapter databese = new InternalDatabaseAdapter();

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected CoreService coreService;
    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected MLPlatformService platformService;
    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected MLAppRegistry mlAppRegistry;


    @Activate
    protected void activate()  {
        appId = coreService.registerApplication("unive.bupt.soon.servadj");

        databese.connect();
        LinkPredictionImpl lpi = new LinkPredictionImpl(MLAppType.LINK_PREDICTION, "edge_load",
                EdgePredictionItem.class, LinkPredPlatformCallback.class, databese, platformService);
        AreaPredictionImpl area1 = new AreaPredictionImpl(MLAppType.BUSINESS_AREA_PREDICTION, "area_load",
                AreaPredictionItem.class, AreaPredPlatformCallback.class, databese, platformService, 1);
        AreaPredictionImpl area3 = new AreaPredictionImpl(MLAppType.RESIDENTIAL_AREA_PREDICTION, "area_load",
                AreaPredictionItem.class, AreaPredPlatformCallback.class, databese, platformService, 2);
        mlAppRegistry.register(lpi, lpi.getServiceName());
        mlAppRegistry.register(area1, area1.getServiceName());
        mlAppRegistry.register(area3, area3.getServiceName());

//        test(lpi);
//        test(area1);
//        test(area3);
//        if (databese.connect()) {
            // 如果数据库连接成功，向前台注册应用
//            LinkPredictionImpl lpi = new LinkPredictionImpl(databese, platformService);
//            AreaPredictionImpl businessApi = new AreaPredictionImpl(true, databese, platformService);
//            AreaPredictionImpl residentialApi = new AreaPredictionImpl(false, databese, platformService);
//            mlAppRegistry.register(lpi, MLAppType.LINK_PREDICTION);
//            mlAppRegistry.register(businessApi, MLAppType.BUSINESS_AREA_PREDICTION);
//            mlAppRegistry.register(residentialApi, MLAppType.RESIDENTIAL_AREA_PREDICTION);

//        } else {
//             如果数据库连接失败
//            throw new RuntimeException("database connection fails.");
//        }
    }


    /**
     * 测试从app到平台再到底层的接口
     */
    private void test(ModelControlService service) {
        databese.connect();
        // 注册前台回调接口
        class TestForegroundCallback implements ForegroundCallback {

            int modelId;

            @Override
            public void operationFailure(int i, String s) {
                log.info("received message {} : {}", i, s);
            }

            @Override
            public void appliedModelResult(int i, List<Item> list, List<String> list1) {
                log.info("received message {} : ", i);
                log.info("model input is : {}", list);
                log.info("model output is : {}", list1);
            }

            @Override
            public void modelEvaluation(int i, String s) {
                log.info("received message {} : {}", i, s);
            }

            @Override
            public void trainDatasetTransEnd(int i, int i1) {
                log.info("received message {} : dataset {} trans end!", i, i1);
            }

            @Override
            public void testDatasetTransEnd(int i, int i1) {
                log.info("received message {} : dataset {} trans end!", i, i1);
                // 因为只有一个训练集和测试集,而且先传输训练集,再传输测试集.因此接到这个消息意味着所有数据集传输结束
                // 开始训练
                service.startTraining(modelId);
            }

            @Override
            public void ResultUrl(int i, URI uri) {
                log.info("received message {} : {}", i, uri);
            }

            @Override
            public void trainEnd(int i) {
                log.info("received message {} : training end", i);
                // 训练结束后开始应用
                service.applyModel(modelId, 5);
                // 并且请求URL
                service.queryURL(modelId);
            }

            @Override
            public void intermediateResult(int i, MonitorData monitorData) {
                log.info("received message {} : {}", i, monitorData.toString());
            }
        }
        TestForegroundCallback foregroundCallback = new TestForegroundCallback();

        // 构建神经网络配置
        MLAlgorithmConfig config = new NNAlgorithmConfig(MLAlgorithmType.FCNNModel, 31, 16,
                Lists.newArrayList(40),
                ActivationFunction.RELU,
                ParamInit.DEFAULT,
                ParamInit.CONSTANT0,
                LossFunction.MSELOSS,
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




    @Deactivate
    protected void deactivate() {
        // 注销服务
        mlAppRegistry.unregister(MLAppType.LINK_PREDICTION);
        mlAppRegistry.unregister(MLAppType.BUSINESS_AREA_PREDICTION);
        mlAppRegistry.unregister(MLAppType.RESIDENTIAL_AREA_PREDICTION);

        log.info("SOON - service reconstruction - Stopped");
    }



}


/**
 * 数据库操作的相关内部类
 */
class InternalDatabaseAdapter extends DatabaseAdapter {
    private final Logger log = LoggerFactory.getLogger(getClass());

    private Connection conn = null;
    private Statement stmt = null;
    private final String JDBC_DRIVER = "org.postgresql.Driver";
    private final String DB_URL = "jdbc:postgresql://10.108.69.165:5432/ecoc2018";
    private final String USER = "postgres";
    private final String PASS = "bupt";

    /**
     * 连接数据库
     * @return 是否连接成功
     */
    @Override
    public boolean connect() {
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
     * @param constraint 查询的约束语句，即在where之后的部分
     * @param tableName 表名
     * @param cls 返回Item的实现类名
     * @return 返回查询结果
     */
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
