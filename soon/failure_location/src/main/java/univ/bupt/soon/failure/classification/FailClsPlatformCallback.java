package univ.bupt.soon.failure.classification;

import com.google.common.collect.Lists;
import org.onosproject.soon.dataset.original.FailureClassificationItem;
import org.onosproject.soon.dataset.original.Item;
import org.onosproject.soon.platform.PlatformCallbackAbstract;

import java.util.List;


/**
 *
 */
public class FailClsPlatformCallback extends PlatformCallbackAbstract {


    /**
     * 将inp解析成List<Item>对象,其实是FailureClassificationItem对象.该对象解析中,只能解析出数据集的输入,不一定能解析出数据集的输出.
     * 数据解析的顺序一定要和数据编码的顺序保持一致
     * @param inp
     * @return
     */
    @Override
    protected List<Item> parse(List<List<Double>> inp) {
        List<Item> rtn = Lists.newArrayList();
        for (List<Double> list : inp) {
            FailureClassificationItem item = new FailureClassificationItem();
            item.setId(-1);
            item.setTrain(false);

            item.setLevel0(list.get(0));
            item.setName0(list.get(1));
            item.setNode0(list.get(2));
            item.setBoard0(list.get(3));
            item.setTime0(list.get(4));

            item.setLevel1(list.get(5));
            item.setName1(list.get(6));
            item.setNode1(list.get(7));
            item.setBoard1(list.get(8));
            item.setTime1(list.get(9));

            item.setLevel2(list.get(10));
            item.setName2(list.get(11));
            item.setNode2(list.get(12));
            item.setBoard2(list.get(13));
            item.setTime2(list.get(14));

            item.setLevel3(list.get(15));
            item.setName3(list.get(16));
            item.setNode3(list.get(17));
            item.setBoard3(list.get(18));
            item.setTime3(list.get(19));

            item.setLevel4(list.get(20));
            item.setName4(list.get(21));
            item.setNode4(list.get(22));
            item.setBoard4(list.get(23));
            item.setTime4(list.get(24));

            item.setLevel5(list.get(25));
            item.setName5(list.get(26));
            item.setNode5(list.get(27));
            item.setBoard5(list.get(28));
            item.setTime5(list.get(29));

            if (list.size() == 31) {
                item.setCls(list.get(30));
            }
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
