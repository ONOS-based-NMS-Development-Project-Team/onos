package univ.bupt.soon.failure.classification;


import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.tuple.Pair;
import org.onosproject.soon.dataset.DatabaseAdapter;
import org.onosproject.soon.dataset.dataset.SegmentForDataset;
import org.onosproject.soon.dataset.original.FailureClassificationItem;
import org.onosproject.soon.dataset.original.Item;
import org.onosproject.soon.foreground.ForegroundCallback;
import org.onosproject.soon.foreground.MLAppType;
import org.onosproject.soon.foreground.ModelControlServiceAbstract;
import org.onosproject.soon.platform.MLPlatformService;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * 故障分类的case
 */
public class FailureClassification extends ModelControlServiceAbstract {
    ForegroundCallback foregroundCallback = null;

    public FailureClassification(MLAppType serviceName, String tableName, Class itemClass,
                                 Class platformCallbackClass, DatabaseAdapter database,
                                 MLPlatformService platformService) {
        super(serviceName, tableName, itemClass, platformCallbackClass, database, platformService);
    }

    @Override
    public Pair<Set<Integer>, Set<Integer>> transAvailableDataset(int modelId) {
        // 根据王菲的数据集,训练集和测试集是分开的.要分别做传输
        int websocketId = wsIds.get(modelId);
        int trainDatasetId = platformService.requestNewTrainDatasetId(websocketId);
        int testDatasetId = platformService.requestNewTestDatasetId(websocketId);
        Set<Integer> trids = Sets.newHashSet(trainDatasetId);
        Set<Integer> teids = Sets.newHashSet(testDatasetId);

        List<Item> trainData = database.queryData("*", " where train=true ", tableName, itemClass);
        trainIds.put(trainDatasetId, trainData.size());
        List<Item> testData = database.queryData("*", " where train=false ", tableName, itemClass);
        testIds.put(testDatasetId, testData.size());

        SegmentForDataset trainSeg = convertToSegmentForDataset(trainData, trainDatasetId, true);
        platformService.sendTrainData(websocketId, trainSeg);

        SegmentForDataset testSeg = convertToSegmentForDataset(testData, testDatasetId, false);
        platformService.sendTestData(websocketId, testSeg);
        return Pair.of(trids, teids);
    }

    /**
     * 从data中将input解析出来.input中包含六组节点数据
     * @param data
     * @return
     */
    @Override
    public List<List<Double>> parseInput(List<Item> data) {
        List<List<Double>> inputData = Lists.newArrayList();
        for (Item lpi : data) {
            FailureClassificationItem item = (FailureClassificationItem) lpi;
            List<Double> tmpIn = Lists.newArrayList();
            // 输入是长度为30, 注意顺序
            tmpIn.add(item.getLevel0());
            tmpIn.add(item.getName0());
            tmpIn.add(item.getNode0());
            tmpIn.add(item.getBoard0());
            tmpIn.add(item.getTime0());

            tmpIn.add(item.getLevel1());
            tmpIn.add(item.getName1());
            tmpIn.add(item.getNode1());
            tmpIn.add(item.getBoard1());
            tmpIn.add(item.getTime1());

            tmpIn.add(item.getLevel2());
            tmpIn.add(item.getName2());
            tmpIn.add(item.getNode2());
            tmpIn.add(item.getBoard2());
            tmpIn.add(item.getTime2());

            tmpIn.add(item.getLevel3());
            tmpIn.add(item.getName3());
            tmpIn.add(item.getNode3());
            tmpIn.add(item.getBoard3());
            tmpIn.add(item.getTime3());

            tmpIn.add(item.getLevel4());
            tmpIn.add(item.getName4());
            tmpIn.add(item.getNode4());
            tmpIn.add(item.getBoard4());
            tmpIn.add(item.getTime4());

            tmpIn.add(item.getLevel5());
            tmpIn.add(item.getName5());
            tmpIn.add(item.getNode5());
            tmpIn.add(item.getBoard5());
            tmpIn.add(item.getTime5());
            inputData.add(tmpIn);
        }
        return inputData;
    }



    /**
     * 将数据转换成SegmentForDataset类型的数据
     * @param data 要被转化的数据
     * @param datasetId 数据集id
     * @param isTrain 是否是训练集
     * @return 转化后的对象
     */
    private SegmentForDataset convertToSegmentForDataset(List<Item> data, int datasetId, boolean isTrain) {
        SegmentForDataset rtn = new SegmentForDataset();
        rtn.setDatasetId(datasetId);
        rtn.setTrainData(isTrain);
        rtn.setPartOfDataset(false);
        rtn.setIndex(0);
        List<List<Double>> inputData = parseInput(data);  // 解析数据
        List<List<Double>> outputData = Lists.newArrayList();
        for (Item lpi : data) {
            FailureClassificationItem item = (FailureClassificationItem) lpi;
            List<Double> tmpOut = Lists.newArrayList();
            // 输出长度为1
            tmpOut.add(item.getCls());
            outputData.add(tmpOut);
        }
        rtn.setInput(inputData);
        rtn.setOutput(outputData);
        return rtn;
    }


    /**
     * 要保证拿到的都是训练集的数据
     *
     */
    @Override
    public List<Item> updateData(int offset, int limit) {
        return  database.queryData("*", " offset "+offset+" limit "+limit+" where train=true",
                tableName, itemClass);
    }

    @Override
    public void setForegroundCallback(ForegroundCallback foregroundCallback) {
<<<<<<< Updated upstream

=======
        this.foregroundCallback = foregroundCallback;
>>>>>>> Stashed changes
    }
}
