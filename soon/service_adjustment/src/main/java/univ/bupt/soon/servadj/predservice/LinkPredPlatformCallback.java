package univ.bupt.soon.servadj.predservice;

import com.google.common.collect.Lists;
import org.onosproject.soon.dataset.original.Item;
import org.onosproject.soon.dataset.original.servadj.EdgePredictionItem;
import org.onosproject.soon.platform.PlatformCallbackAbstract;

import java.util.List;

/**
 * Link Prediction ML-Platform-Callback interface
 */
public class LinkPredPlatformCallback extends PlatformCallbackAbstract {

    /**
     * 将inp解析成List<Item>对象
     * @param inp
     * @return
     */
    @Override
    protected List<Item> parse(List<List<Double>> inp) {
        List<Item> rtn = Lists.newArrayList();
        for (List<Double> list : inp) {
            EdgePredictionItem item = new EdgePredictionItem();
            double[] edge_id = new double[15];
            double[] two_hours_before = new double[30];
            double[] one_hour_after = new double[15];
            for (int i=0; i<14; i++) {
                edge_id[i] = list.get(i);
            }
            for (int i=0; i<30; i++) {
                two_hours_before[i] = list.get(i+15);
            }
            if (list.size() == 60) {  // 如果包含label数据
                for (int i = 0; i < 15; i++) {
                    one_hour_after[i] = list.get(i + 45);
                }
            } else {
                one_hour_after = null;
            }
            item.setEdge_id(edge_id);
            item.setTimepoint(list.get(14));
            item.setTwo_hours_before(two_hours_before);
            item.setOne_hour_after(one_hour_after);
            rtn.add(item);
        }
        return rtn;
    }


    @Override
    public void evalResult(int msgId, int testDatasetId, List<List<Double>> results) {
        // 评估模型在测试集上的应用结果
        // TODO 这个还不知道该怎么评估,直接先给1.0
        String s  = "";
        for (List<Double> list : results) {
            for (Double rea:list) {
                String a = String.valueOf(rea);
                s = s + a + ",";
            }
        }
        s.substring(0,s.length()-1);
        fcb.modelEvaluation(msgId,s);
    }

    @Override
    public void configException(int msgId, String description) {
        // 解析配置异常
        // TODO 不知道该怎么做
        fcb.operationFailure(msgId, "Can't handle exception: "+description);
    }

    @Override
    public void runningException(int msgId, String description) {
        // 解析运行异常
        // TODO 不知道该怎么做
        fcb.operationFailure(msgId, "Can't handle exception: "+description);
    }
}
