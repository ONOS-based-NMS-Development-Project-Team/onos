package univ.bupt.soon.almpred;


import org.apache.commons.lang3.tuple.Pair;
import org.apache.felix.scr.annotations.*;
import org.onosproject.core.ApplicationId;
import org.onosproject.core.CoreService;
import org.onosproject.soon.MLAppRegistry;
import org.onosproject.soon.ModelControlService;
import org.onosproject.soon.Statistics;
import org.onosproject.soon.mlmodel.MLModelConfig;
import org.onosproject.soon.mlmodel.MLModelType;
import org.postgresql.Driver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Component(immediate = true)
public class AlarmPredComponent implements ModelControlService {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final String JDBC_DRIVER = "org.postgresql.Driver";
    private final String DB_URL = "jdbc:postgresql://10.108.69.165:5432/state_grid_sdh";
    private final String USER = "postgres";
    private final String PASS = "bupt";
    private final String serviceName = "alarm_prediction";

    private Connection conn = null;
    private Statement stmt = null;
    private ApplicationId appId;

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected CoreService coreService;
    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected MLAppRegistry mlAppRegistry;
    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected MLPlatformService service;

    @Activate
    protected void activate() {

        appId = coreService.registerApplication("unive.bupt.soon.almpred");
        mlAppRegistry.register(this, serviceName);
        Driver driver = new Driver();
        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            stmt = conn.createStatement();
            String sql = "SELECT * FROM his_alarms LIMIT 5";
            ResultSet rs = stmt.executeQuery(sql);
            while(rs.next()){
                // 通过字段检索
                String level = rs.getString("level");
                String name = rs.getString("name");
                Timestamp happen_time = rs.getTimestamp("happen_time");
                // 输出数据
                log.info(level);
                log.info(name);
                log.info(happen_time.toString());
            }
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            log.info("SOON - alarm prediction - Started");
        }
    }

    @Deactivate
    protected void deactivate() {
        mlAppRegistry.unregister(serviceName);
        try {
            stmt.close();
            conn.close();
            log.info("SOON - alarm prediction - Stopped");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int addNewModel(MLModelType mlModelType, int i, MLModelConfig mlModelConfig) {
        return 0;
    }

    @Override
    public boolean startTraining(MLModelType mlModelType, int i, int i1, List<Integer> list) {
        return false;
    }

    @Override
    public boolean stopTraining(MLModelType mlModelType, int i, int i1) {
        return false;
    }

    @Override
    public List<String> getAppliedResult(MLModelType mlModelType, int i, int i1, int i2) {
        return null;
    }

    @Override
    public List<Double> getModelEvaluation(MLModelType mlModelType, int i, int i1, int i2, List<Integer> list) {
        return null;
    }

    @Override
    public Pair<Class, List<String>> updateData(Date date, Date date1, String s, String s1, String s2, int i) {
        return null;
    }

    @Override
    public MLModelConfig getModelConfig(MLModelType mlModelType, int i, Map<String, String> map) {
        return null;
    }

    @Override
    public Statistics getAppStatics() {
        return null;
    }
}
