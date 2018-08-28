package univ.bupt.soon.failure.location;

import org.apache.commons.lang.NotImplementedException;
import org.onosproject.soon.ModelControlService;
import org.onosproject.soon.Statistics;
import org.onosproject.soon.dataset.original.Item;
import org.onosproject.soon.mlmodel.MLAlgorithmType;
import org.onosproject.soon.mlmodel.MLModelConfig;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 故障定位的功能实现。因为王菲还没做，留待下一级补充实现。
 */
public class FailureLocation implements ModelControlService {

    public static final String serviceName = "failure_prediction";

    @Override
    public int addNewModel(MLAlgorithmType mlAlgorithmType, int i, MLModelConfig mlModelConfig) {
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

    @Override
    public List<Item> updateData(Date date, Date date1, String s, String s1, String s2, int i) {
        throw new NotImplementedException("Not Implemented Yet!!!");
    }

    @Override
    public List<Item> updateData(int i, int i1) {
        throw new NotImplementedException("Not Implemented Yet!!!");
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
