package univ.bupt.soon.mlshow.original;

import org.apache.commons.lang3.tuple.Pair;
import org.onosproject.soon.Statistics;
import org.onosproject.soon.dataset.original.Item;
import org.onosproject.soon.foreground.ForegroundCallback;
import org.onosproject.soon.foreground.MLAppType;
import org.onosproject.soon.foreground.ModelControlService;
import org.onosproject.soon.mlmodel.MLAlgorithmConfig;
import org.onosproject.soon.mlmodel.MLAlgorithmType;
import org.postgresql.Driver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * 从电网采集到的数据的相关查询
 */
public class OriginalDataAccess implements ModelControlService {

    private final Logger log = LoggerFactory.getLogger(getClass());
    // 数据库查询类
    private OriginalDatabaseAdapter query = new OriginalDatabaseAdapter();
    // 应用类型
    private final MLAppType type;
    // 数据库中的表名
    private final String tableName;
    // 表中数据对应的类
    private final Class itemClass;

    public OriginalDataAccess(String tableName, MLAppType type, Class itemClass) {
        this.tableName = tableName;
        this.type = type;
        this.itemClass = itemClass;
        if (query.connect()) {
            log.info("create OriginalDataAccess instance successfully");
        } else {
            log.error("fail to create OriginalDataAccess instance");
        }
    }

    @Override
    public Pair<Integer, Integer> addNewModel(MLAlgorithmType mlAlgorithmType, MLAlgorithmConfig mlAlgorithmConfig, ForegroundCallback foregroundCallback) {
        throw new RuntimeException("Shouldn't invoke this function at OriginalDataAccess class!!!");
    }

    @Override
    public Pair<Set<Integer>, Set<Integer>> transAvailableDataset(int i) {
        throw new RuntimeException("Shouldn't invoke this function at OriginalDataAccess class!!!");
    }

    @Override
    public int setDataset(int i, int i1) {
        throw new RuntimeException("Shouldn't invoke this function at OriginalDataAccess class!!!");
    }

    @Override
    public Pair<Boolean, Integer> startTraining(int i) {
        throw new RuntimeException("Shouldn't invoke this function at OriginalDataAccess class!!!");
    }

    @Override
    public Pair<Boolean, Integer> stopTraining(int i) {
        throw new RuntimeException("Shouldn't invoke this function at OriginalDataAccess class!!!");
    }

    @Override
    public Pair<Boolean, Integer> applyModel(int i, int i1) {
        throw new RuntimeException("Shouldn't invoke this function at OriginalDataAccess class!!!");
    }

    @Override
    public Pair<Boolean, Integer> deleteModel(int i) {
        throw new RuntimeException("Shouldn't invoke this function at OriginalDataAccess class!!!");
    }

    @Override
    public Pair<Boolean, Integer> getModelEvaluation(int i, int i1) {
        throw new RuntimeException("Shouldn't invoke this function at OriginalDataAccess class!!!");
    }

    @Override
    public MLAppType getServiceName() {
        return type;
    }

    @Override
    public MLAlgorithmConfig getModelConfig(int i) {
        throw new RuntimeException("Shouldn't invoke this function at OriginalDataAccess class!!!");
    }

    @Override
    public Statistics getAppStatics() {
        throw new RuntimeException("Shouldn't invoke this function at OriginalDataAccess class!!!");
    }

    @Override
    public Pair<Boolean, Integer> queryURL(int i) {
        throw new RuntimeException("Shouldn't invoke this function at OriginalDataAccess class!!!");
    }

    @Override
    public List<Item> updateData(Date begin, Date end, String node, String board, String port, int itemNum) {
        // 查询数据
        // TODO 暂时不支持该查询方式
        return null;
    }

    @Override
    public List<Item> updateData(int offset, int limit) {
        // 查询数据
        return query.queryData("*", " offset "+offset+" limit "+limit, tableName, itemClass);
    }
}