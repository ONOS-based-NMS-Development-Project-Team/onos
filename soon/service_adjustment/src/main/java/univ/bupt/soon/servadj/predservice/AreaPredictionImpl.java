package univ.bupt.soon.servadj.predservice;



import org.apache.commons.lang3.tuple.Pair;
import org.onosproject.soon.MonitorData;
import org.onosproject.soon.Statistics;
import org.onosproject.soon.dataset.DatabaseAdapter;
import org.onosproject.soon.dataset.original.Item;
import org.onosproject.soon.foreground.ForegroundCallback;
import org.onosproject.soon.foreground.MLAppType;
import org.onosproject.soon.foreground.ModelControlService;
import org.onosproject.soon.mlmodel.MLAlgorithmConfig;
import org.onosproject.soon.mlmodel.MLAlgorithmType;
import org.onosproject.soon.platform.MLPlatformService;
import org.onosproject.soon.platform.PlatformCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 区域预测模型.尚未实现
 */
public class AreaPredictionImpl implements ModelControlService {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final boolean isBusiness;  // 是商业区还是住宅区

    private DatabaseAdapter database;

    private MLPlatformService platformService;

    public MLPlatformService getPlatformService() {
        return platformService;
    }

    public void setPlatformService(MLPlatformService platformService) {
        this.platformService = platformService;
    }

    public AreaPredictionImpl(boolean isBusiness, DatabaseAdapter database, MLPlatformService platformService) {
        this.isBusiness = isBusiness;
        this.database = database;
        this.platformService = platformService;
    }


    @Override
    public Pair<Integer, Integer> addNewModel(MLAlgorithmType mlAlgorithmType, MLAlgorithmConfig mlAlgorithmConfig, ForegroundCallback foregroundCallback) {
        return null;
    }

    @Override
    public Pair<Set<Integer>, Set<Integer>> transAvailableDataset(int i) {
        return null;
    }

    @Override
    public int setDataset(int i, int i1) {
        return 0;
    }

    @Override
    public Pair<Boolean, Integer> startTraining(int i) {
        return null;
    }

    @Override
    public Pair<Boolean, Integer> stopTraining(int i) {
        return null;
    }

    @Override
    public Pair<Boolean, Integer> applyModel(int i, int i1) {
        return null;
    }

    @Override
    public Pair<Boolean, Integer> deleteModel(int i) {
        return null;
    }

    @Override
    public Pair<Boolean, Integer> getModelEvaluation(int i, int i1) {
        return null;
    }

    @Override
    public MLAppType getServiceName() {
        return null;
    }

    @Override
    public MLAlgorithmConfig getModelConfig(int i) {
        return null;
    }

    @Override
    public Statistics getAppStatics() {
        return null;
    }

    @Override
    public Pair<Boolean, Integer> queryURL(int i) {
        return null;
    }

    @Override
    public List<Item> updateData(Date date, Date date1, String s, String s1, String s2, int i) {
        return null;
    }

    @Override
    public List<Item> updateData(int i, int i1) {
        return null;
    }
}
