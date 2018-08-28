package univ.bupt.soon.almpred;


import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.felix.scr.annotations.*;
import org.onosproject.core.ApplicationId;
import org.onosproject.core.CoreService;
import org.onosproject.soon.*;
import org.onosproject.soon.dataset.original.AlarmPredictionItem;
import org.onosproject.soon.dataset.original.Item;
import org.onosproject.soon.mlmodel.MLAlgorithmType;
import org.onosproject.soon.mlmodel.MLModelConfig;
import org.onosproject.soon.mlmodel.MLModelDetail;
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
    // 存储模型相关的内容。模型类型，模型id，模型详细配置
    private final Map<MLAlgorithmType, Map<Integer, MLModelDetail>> models = Maps.newConcurrentMap();

    SQLQuery query = SQLQuery.instance;
    private ApplicationId appId;

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected CoreService coreService;
    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected MLAppRegistry mlAppRegistry;
//    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
//    protected MLPlatformService platformService;

    @Activate
    protected void activate() {

        appId = coreService.registerApplication("unive.bupt.soon.almpred");
        mlAppRegistry.register(this, MLAppType.ALARM_PREDICTION);
        query.connect();
    }

    @Deactivate
    protected void deactivate() {
        mlAppRegistry.unregister(MLAppType.ALARM_PREDICTION);
        query.close();
        log.info("SOON - alarm prediction - Stopped");
    }


    @Override
    public MLAppType getServiceName() {
        return MLAppType.ALARM_PREDICTION;
    }

    @Override
    public int addNewModel(MLAlgorithmType mlAlgorithmType, int trainDatasetId, MLModelConfig mlModelConfig) {
        throw new NotImplementedException("Not Implemented Yet!!!");
    }

    @Override
    public boolean startTraining(MLAlgorithmType mlAlgorithmType, int i, int i1, List<Integer> list) {
        throw new NotImplementedException("Not Implemented Yet!!!");
    }

    @Override
    public boolean stopTraining(MLAlgorithmType mlAlgorithmType, int i, int i1) {
        throw new NotImplementedException("Not Implemented Yet!!!");
    }

    @Override
    public List<String> getAppliedResult(MLAlgorithmType mlAlgorithmType, int i, int i1, int i2) {
        throw new NotImplementedException("Not Implemented Yet!!!");
    }

    @Override
    public List<Double> getModelEvaluation(MLAlgorithmType mlAlgorithmType, int i, int i1, int i2, List<Integer> list) {
        throw new NotImplementedException("Not Implemented Yet!!!");
    }

    /**
     *
     * 由于在目前得到的alarm_prediction的数据库表中，是已经处
     */
    @Override
    public List<Item> updateData(Date begin, Date end, String node, String board, String port, int itemNum) {
        throw new NotImplementedException("Not Implemented Yet!!!");
    }

    /**
     * 分页更新数据
     */
    @Override
    public List<Item> updateData(int offset, int limit) {
        List<Item> rtn = SQLQuery.instance.queryData("*", " limit "+limit+" offset "+offset+";");
        return rtn;
    }

    @Override
    public MLModelConfig getModelConfig(MLAlgorithmType mlAlgorithmType, int i, Map<String, String> map) {
        throw new NotImplementedException("Not Implemented Yet!!!");
    }

    @Override
    public Statistics getAppStatics() {
        throw new NotImplementedException("Not Implemented Yet!!!");
    }
}
