package univ.bupt.soon.servadj.predservice;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.tuple.Pair;
import org.onosproject.soon.dataset.DatabaseAdapter;
import org.onosproject.soon.dataset.dataset.SegmentForDataset;
import org.onosproject.soon.dataset.original.Item;
import org.onosproject.soon.dataset.original.servadj.AreaPredictionItem;
import org.onosproject.soon.foreground.ForegroundCallback;
import org.onosproject.soon.foreground.MLAppType;
import org.onosproject.soon.foreground.ModelControlServiceAbstract;
import org.onosproject.soon.platform.MLPlatformService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 区域预测模型. 不同区域有自己的预测模型应用
 */
public class AreaPredictionImpl extends ModelControlServiceAbstract {

    private final int areaId;  // 区域id
    ForegroundCallback foregroundCallback = null;

    public AreaPredictionImpl(MLAppType serviceName, String tableName, Class itemClass, Class platformCallbackClass,
                              DatabaseAdapter database, MLPlatformService platformService, int areaId) {
        super(serviceName, tableName, itemClass, platformCallbackClass, database, platformService);
        this.areaId = areaId;
    }

    /**
     * 为模型modelId传输所有可用的训练集和测试集，并且返回
     * @param modelId 模型id
     * @return Pair.Left表示训练集id的集合，Pair.Right表示测试集id的集合
     */
    @Override
    public Pair<Set<Integer>, Set<Integer>> transAvailableDataset(int modelId) {
        // 要分别对区域1和区域3进行区域流量预测.因此需要传输两个训练集
        // 区域1和区域3的训练集和测试集一样,因此需要传输两个训练集
        // 我也很无奈啊...
        int websocketId = wsIds.get(modelId);
        int trainDatasetId = platformService.requestNewTrainDatasetId(websocketId);
        int testDatasetId = platformService.requestNewTestDatasetId(websocketId);
        Set<Integer> trids = Sets.newHashSet(trainDatasetId);
        Set<Integer> teids = Sets.newHashSet(testDatasetId);
        List<Item> trainData = database.queryData("*", " where area_id="+areaId, tableName, itemClass);
        int size = trainData.size();
        trainIds.put(trainDatasetId, size);
        testIds.put(testDatasetId, size);

        // 构建可供传输的数据集
        SegmentForDataset segment = new SegmentForDataset();
        segment.setPartOfDataset(false);
        segment.setIndex(0);
        List<List<Double>> inp = Lists.newArrayList();
        List<List<Double>> out = Lists.newArrayList();
        for (Item i : trainData) {
            AreaPredictionItem item = (AreaPredictionItem) i;
            List<Double> itemIn = Lists.newArrayList();
            List<Double> itemOut = Lists.newArrayList();
            itemIn.add(item.getTimepoint());  // 时间点
            for (double d : item.getTwo_hours_before()) {
                itemIn.add(d);
            }
            itemOut.add((double)item.getTide());
            for (double d : item.getOne_hour_after()) {
                itemOut.add(d);
            }
            inp.add(itemIn);
            out.add(itemOut);
        }
        segment.setInput(inp);
        segment.setOutput(out);

        List<List<Double>> originDataInp = displayOriginData(inp);
        List<List<Double>> originDataOup = displayOriginData(out);
        SegmentForDataset sfd = new SegmentForDataset();
        sfd.setInput(originDataInp);
        sfd.setInput(originDataOup);
        foregroundCallback.originData(sfd);

        // 两次传输,一次作为训练集,一次作为测试集
        segment.setDatasetId(trainDatasetId);
        segment.setTrainData(true);
        platformService.sendTrainData(websocketId, segment);
        segment.setDatasetId(testDatasetId);
        segment.setTrainData(false);
        platformService.sendTestData(websocketId, segment);

        return Pair.of(trids, teids);
    }

    @Override
    public void setForegroundCallback(ForegroundCallback foregroundCallback) {
        this.foregroundCallback = foregroundCallback;
    }


    /**
     * 从data中将input解析出来
     * @param data
     * @return
     */
    @Override
    public List<List<Double>> parseInput(List<Item> data) {
        List<List<Double>> rtn = Lists.newArrayList();
        for (Item i : data) {
            AreaPredictionItem item = (AreaPredictionItem) i;
            List<Double> ds = Lists.newArrayList();
            ds.add(item.getTimepoint());
            for (double d : item.getTwo_hours_before()) {
                ds.add(d);
            }
            rtn.add(ds);
        }
        return rtn;
    }

    private List<List<Double>> displayOriginData(List<List<Double>> segmentForDataset){
        List<List<Double>> tmpIn = Lists.newArrayList();
        for (List<Double> list : segmentForDataset) {
            List<Double> tmp = Lists.newArrayList();
            for (Double dou:list) {
                Double origin = dou * 60;
                tmp.add(origin);
            }
            tmpIn.add(tmp);
        }
        return tmpIn;
    }

}
