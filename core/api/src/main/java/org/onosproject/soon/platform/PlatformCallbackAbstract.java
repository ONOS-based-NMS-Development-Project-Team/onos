package org.onosproject.soon.platform;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.onosproject.soon.MonitorData;
import org.onosproject.soon.dataset.original.Item;
import org.onosproject.soon.foreground.ForegroundCallback;

import java.net.URI;
import java.util.List;
import java.util.Map;

/**
 *
 */
public abstract class PlatformCallbackAbstract implements PlatformCallback {

    protected int modelId = -1;
    protected ForegroundCallback fcb = null;

    protected Map<Integer, List<List<Double>>> inputs = Maps.newHashMap();


    @Override
    public void setModelId(int modelId) {
        this.modelId = modelId;
    }

    @Override
    public void setForegroundCallback(ForegroundCallback fcb) {
        this.fcb = fcb;
    }

    @Override
    public Map<Integer, List<List<Double>>> getInputs() {
        return inputs;
    }

    @Override
    public void trainDatasetTransEnd(int msgId, int trainDatasetId) {
        // 训练集传输结束
        fcb.trainDatasetTransEnd(msgId, trainDatasetId);
    }

    @Override
    public void testDatasetTransEnd(int msgId, int testDatasetId) {
        // 测试集传输结束
        fcb.testDatasetTransEnd(msgId, testDatasetId);
    }

    @Override
    public void trainingEnd(int msgId) {
        // 训练结束
        fcb.trainEnd(msgId);
    }

    @Override
    public void ResultUrl(int msgId, URI uri) {
        // 获取URI的结果
        fcb.ResultUrl(msgId, uri);
    }

    @Override
    public void intermediateResult(int msgId, MonitorData monitorData) {
        // 中间训练结果的提示
        fcb.intermediateResult(msgId, monitorData);
    }

    @Override
    public void applyResult(int msgId, List<List<Double>> list) {
        // 模型应用的结果
        List<String> result = Lists.newArrayList();
        for (List<Double> i : list) {
            result.add(i.toString());
        }
        List<List<Double>> tmp = inputs.get(msgId);
        List<Item> data = parse(tmp);
        fcb.appliedModelResult(msgId, data, result);
    }


    @Override
    public abstract void evalResult(int msgId, int testDatasetId, List<List<Double>> results);


    @Override
    public abstract void configException(int msgId, String description);

    @Override
    public abstract void runningException(int msgId, String description);

    /**
     * 将input解析成List<Item>对象.inp中未必会包含label相关的数据
     * @param inp
     * @return
     */
    protected abstract List<Item> parse(List<List<Double>> inp);
}