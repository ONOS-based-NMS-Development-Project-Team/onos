package univ.bupt.soon.servadj.predservice;

import com.google.common.collect.Lists;
import org.onosproject.soon.dataset.original.Item;
import org.onosproject.soon.dataset.original.servadj.AreaPredictionItem;
import org.onosproject.soon.platform.PlatformCallbackAbstract;

import java.util.List;

public class AreaPredPlatformCallback  extends PlatformCallbackAbstract {

    /**
     * 将inp解析成List<Item>对象
     * @param inp
     * @return
     */
    @Override
    protected List<Item> parse(List<List<Double>> inp) {
        List<Item> rtn = Lists.newArrayList();
        for (List<Double> list : inp) {
            AreaPredictionItem item = new AreaPredictionItem();
            double timepoint = list.get(0);
            double[] two_hours_before = new double[30];
            double tide = -1;
            double[] one_hour_after = new double[15];
            for (int i=1; i<31; i++) {
                two_hours_before[i-1] = list.get(i);
            }
            if (list.size() == 47) {
                // 如果包含label的数据
                tide = list.get(31);
                for (int i=32; i<47; i++) {
                    one_hour_after[i-32] = list.get(i);
                }
            }

            item.setArea_id(-1); // 无法解析area
            item.setTimepoint(timepoint);
            item.setTwo_hours_before(two_hours_before);
            item.setTide(tide);
            item.setOne_hour_after(one_hour_after);
            rtn.add(item);
        }
        return rtn;
    }


    @Override
    public void evalResult(int msgId, int testDatasetId, List<List<Double>> results) {
        // 评估模型在测试集上的应用结果
        // TODO 这个还不知道该怎么评估,直接先给1.0
        fcb.modelEvaluation(msgId, "Not implemented yet, Hahahahahaha");
    }

    @Override
    public void configException(int msgId, String description) {
        // 解析配置异常
        // TODO 不知道该怎么做
        fcb.operationFailure(modelId,msgId, "Can't handle exception: "+description);
    }

    @Override
    public void runningException(int msgId, String description) {
        // 解析运行异常
        // TODO 不知道该怎么做
        fcb.operationFailure(modelId,msgId, "Can't handle exception: "+description);
    }
}
