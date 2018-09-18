package univ.bupt.soon.servadj.predservice;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.tuple.Pair;
import org.onosproject.soon.MonitorData;
import org.onosproject.soon.Statistics;
import org.onosproject.soon.dataset.DatabaseAdapter;
import org.onosproject.soon.dataset.dataset.SegmentForDataset;
import org.onosproject.soon.dataset.original.Item;
import org.onosproject.soon.dataset.original.servadj.EdgePredictionItem;
import org.onosproject.soon.foreground.ForegroundCallback;
import org.onosproject.soon.foreground.MLAppType;
import org.onosproject.soon.foreground.ModelControlService;
import org.onosproject.soon.foreground.ModelControlServiceAbstract;
import org.onosproject.soon.mlmodel.MLAlgorithmConfig;
import org.onosproject.soon.mlmodel.MLAlgorithmType;
import org.onosproject.soon.platform.MLPlatformService;
import org.onosproject.soon.platform.PlatformCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import univ.bupt.soon.servadj.ServiceAdjustComponent;

import java.io.FileInputStream;
import java.net.URI;
import java.util.*;

/**
 * 链路预测模型
 */
public class LinkPredictionImpl extends ModelControlServiceAbstract {

    ForegroundCallback foregroundCallback = null;

    public LinkPredictionImpl(MLAppType serviceName, String tableName, Class itemClass, Class platformCallbackClass,
                              DatabaseAdapter database, MLPlatformService platformService) {
        super(serviceName, tableName, itemClass, platformCallbackClass, database, platformService);
    }

    /**
     * 为模型modelId传输所有可用的训练集和测试集，并且返回
     * @param modelId 模型id
     * @return Pair.Left表示训练集id的集合，Pair.Right表示测试集id的集合
     */
    @Override
    public Pair<Set<Integer>, Set<Integer>> transAvailableDataset(int modelId) {
        // TODO 关于训练集的设置太简单，后期一定要抽象出来
        // 目前来说，只有一个训练集，训练集就是测试集
        int websocketId = wsIds.get(modelId);
        int trainDatasetId = platformService.requestNewTrainDatasetId(websocketId);
        int testDatasetId = platformService.requestNewTestDatasetId(websocketId);
        Set<Integer> trids = Sets.newHashSet(trainDatasetId);
        Set<Integer> teids = Sets.newHashSet(testDatasetId);
        // 查询训练集
        List<Item> trainData = database.queryData("*", "", tableName, itemClass);
        int size = trainData.size();
        trainIds.put(trainDatasetId, size);
        testIds.put(testDatasetId, size);

        SegmentForDataset segmentForDataset = convertToSegmentForDataset(trainData, trainDatasetId, true);
        //Todo 转化数据
        List<List<Double>> labDataInp = segmentForDataset.getInput();
        List<List<Double>> labDataOutp = segmentForDataset.getInput();
        List<List<Double>> originDataInp = displayOriginData(labDataInp);
        List<List<Double>> originDataOup = displayOriginData(labDataOutp);
        SegmentForDataset sfd = new SegmentForDataset();
        sfd.setInput(originDataInp);
        sfd.setInput(originDataOup);
        foregroundCallback.originData(sfd);
        platformService.sendTrainData(websocketId, segmentForDataset);

        segmentForDataset.setTrainData(false);
        segmentForDataset.setDatasetId(testDatasetId);
        platformService.sendTestData(websocketId, segmentForDataset);
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
            EdgePredictionItem item = (EdgePredictionItem) i;
            List<Double> tmp = Lists.newArrayList();
            double[] edge_id = item.getEdge_id();
            for (double j : edge_id) {
                tmp.add(j);
            }
            tmp.add(item.getTimepoint());
            double[] in = item.getTwo_hours_before();
            for (double j : in) {
                tmp.add(j);
            }
            rtn.add(tmp);
        }
        return rtn;
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
        List<List<Double>> inputData = Lists.newArrayList();
        List<List<Double>> outputData = Lists.newArrayList();
        for (Item lpi : data) {
            EdgePredictionItem item = (EdgePredictionItem) lpi;
            List<Double> tmpIn = Lists.newArrayList();
            List<Double> tmpOut = Lists.newArrayList();
            // 增加edge_id
            for (double i : item.getEdge_id()) {
                tmpIn.add(i);
            }
            // 增加时间
            tmpIn.add(item.getTimepoint());
            // 增加过去两个小时
            for (double i : item.getTwo_hours_before()) {
                tmpIn.add(i);
            }
            // 增加模型输出
            for (double i : item.getOne_hour_after()) {
                tmpOut.add(i);
            }
            inputData.add(tmpIn);
            outputData.add(tmpOut);
        }
        rtn.setInput(inputData);
        rtn.setOutput(outputData);
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
