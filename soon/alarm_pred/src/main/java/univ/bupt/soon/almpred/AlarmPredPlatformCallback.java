package univ.bupt.soon.almpred;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.onosproject.soon.dataset.original.AlarmPredictionItem;
import org.onosproject.soon.dataset.original.Item;
import org.onosproject.soon.foreground.ForegroundCallback;
import org.onosproject.soon.platform.PlatformCallbackAbstract;

import java.util.List;
import java.util.Map;

public class AlarmPredPlatformCallback extends PlatformCallbackAbstract {

    @Override
    public void evalResult(int msgId, int testDatasetId, List<List<Double>> results) {
        // 评估模型在测试集上的应用结果
        fcb.modelEvaluation(msgId,"Not implemented");
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
    /**
     * 将inp解析成List<Item>对象
     * @param inp
     * @return
     */
    @Override
    protected List<Item> parse(List<List<Double>> inp) {
        List<Item> rtn = Lists.newArrayList();
        for (List<Double> list : inp){
            AlarmPredictionItem item = new AlarmPredictionItem();
            double[] input = new double[36];
            double[] isAlarmhappen = new double[1];
            for (int i=0;i<35;i++) {
                input[i] = list.get(i);
            }
            if (list.size() == 37){     // 如果包含label数据
                isAlarmhappen[0] = list.get(36);
                if (isAlarmhappen[0] == 1.0){
                    item.setAlarm_happen(true);
                }else {
                    item.setAlarm_happen(false);
                }
            }
            item.setInput(input);
            rtn.add(item);
        }


        return rtn;
    }
}
