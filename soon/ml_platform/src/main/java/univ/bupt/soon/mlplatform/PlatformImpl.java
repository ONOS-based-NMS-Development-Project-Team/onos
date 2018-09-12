/*
 * Copyright 2018-present Open Networking Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package univ.bupt.soon.mlplatform;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Service;
import org.eclipse.jetty.websocket.WebSocket;
import org.eclipse.jetty.websocket.WebSocketClient;
import org.onosproject.soon.ClassMapping;
import org.onosproject.soon.dataset.dataset.SegmentForDataset;
import org.onosproject.soon.mlmodel.MLAlgorithmConfig;
import org.onosproject.soon.mlmodel.MLAlgorithmType;
import org.onosproject.soon.mlmodel.config.nn.*;
import org.onosproject.soon.platform.MLPlatformService;
import org.onosproject.soon.platform.PlatformCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Skeletal ONOS application component.
 */
@Component(immediate = true)
@Service
public class PlatformImpl implements MLPlatformService {

    private final Logger log = LoggerFactory.getLogger(getClass());

    /**** 存储 ****/
    // 存储已经存在的socket连接。key表示websocketId
    static Map<Integer, SoonWebsocket> socks = Maps.newConcurrentMap();
    // 存储已经注册的回调类。key表示websocketId
    static Map<Integer, PlatformCallback> callbacks = Maps.newConcurrentMap();
    // 训练集id集合
    static Set<Integer> trainDatasetIdSet = Sets.newHashSet();
    // 测试集id集合
    static Set<Integer> testDatasetIdSet = Sets.newHashSet();
    // 模型id集合
    static Set<Integer> modelIdSet = Sets.newHashSet();

    /**** 编号 *****/
    private int trainId = 0;
    private int testId = 0;
    private int modelId = 0;
    static int sockId = 0;


//    /**
//     * 做简单的最小集测试
//     */
//    private void test() {
//        try {
//            // 新建连接
//            URI uri = URI.create("ws://10.117.63.234");
//            int wsId = addWebsocketConnection(uri);
//
//            // 请求训练集id、测试集id、模型id
//            int trainId = requestNewTrainDatasetId(wsId);
//            int modelId = requestNewModelId(wsId, MLAlgorithmType.FCNNModel);
//            int testId  = requestNewTestDatasetId(wsId);
//
//            // 发送数据集
//            SegmentForDataset sfd = new SegmentForDataset();
//            double i = 1.1;
//            List<List<Double>> datas = Lists.newArrayList();
//            for (int j = 0; j < 3; j++) {
//                List<Double> doubles = Lists.newArrayList(i, i + 1.1, i + 2.2);
//                i += 10;
//                datas.add(doubles);
//            }
//            sfd.setDatas(datas);
//            sfd.setTrainData(true);
//            int trainMsgId = sendTrainData(wsId, sfd);
//            sfd.setTrainData(false);
//            int testMsgId = sendTestData(wsId, sfd);
//
//            // 发送模型参数
//            NNAlgorithmConfig nnconfig = new NNAlgorithmConfig(MLAlgorithmType.FCNNModel, 30, 7,
//                    Lists.newArrayList(1, 2, 3), ActivationFunction.RELU, ParamInit.DEFAULT, ParamInit.CONSTANT0,
//                    LossFunction.MSELOSS, 64, 100, Optimizer.NESTROV,
//                    1e-4, LRAdjust.LINEAR, 0.0);
//            int configMsgId = sendMLConfig(wsId, nnconfig);
//
//            // 开始训练
//            int startMsgId = startTrain(wsId);
//
//            log.info("训练集id：{}\n 测试集id：{}\n 模型id：{}\n 训练集消息id：{}\n 测试集消息id：{}\n 配置消息id：{}\n 开始训练id：{}\n ",
//                    trainId, testId, modelId, trainMsgId, testMsgId, configMsgId, startMsgId);
//
//
//        }catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    @Activate
    protected void activate() {
        log.info("Started");
    }

    @Deactivate
    protected void deactivate() {
        log.info("Stopped");
    }


    @Override
    public int addWebsocketConnection(URI uri) {
        try {
            sockId++;
            String absPath = uri.toString()+"/"+sockId;
            WebSocketClient client = new WebSocketClient();
            client.setMaxTextMessageSize(100 * 1024 * 1024);

            SoonWebsocket websocket = new SoonWebsocket(sockId);
            Future<WebSocket.Connection> future = client.open(URI.create(absPath), websocket);
            WebSocket.Connection conn = future.get(5, TimeUnit.SECONDS);  // 最多等五秒获取结果
            log.info("connected to {} with id {}", absPath, sockId);
            socks.put(sockId, websocket);
        } catch (Exception e) {
            socks.remove(sockId);
            e.printStackTrace();
            return -1;
        }
        return sockId;
    }

    @Override
    public boolean registerCallback(int websocketId, PlatformCallback platformCallback) {
        if (socks.containsKey(websocketId) && !callbacks.containsKey(websocketId)) {
            callbacks.put(websocketId, platformCallback);
            socks.get(websocketId).pcb = platformCallback;  // 注入回调对象
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean unregisterCallback(int websocketId) {
        callbacks.remove(websocketId);
        return true;
    }

    @Override
    public int requestNewTrainDatasetId(int websocketId) {
        if (socks.containsKey(websocketId) && socks.get(websocketId).conn.isOpen()) {
            // 如果该websocket存在，并且处于open状态
            trainId++;
            trainDatasetIdSet.add(trainId);
            return trainId;
        } else {
            return -1;
        }
    }

    @Override
    public int requestNewTestDatasetId(int websocketId) {
        if (socks.containsKey(websocketId) && socks.get(websocketId).conn.isOpen()) {
            // 如果该websocket存在，并且处于open状态
            testId++;
            testDatasetIdSet.add(testId);
            return testId;
        } else {
            return -1;
        }
    }

    @Override
    public int requestNewModelId(int websocketId, MLAlgorithmType type) {
        if (socks.containsKey(websocketId) && socks.get(websocketId).conn.isOpen()) {
            SoonWebsocket ws = socks.get(websocketId);
            // 确定模型使用的算法类型
            ws.modelConfigCls = ClassMapping.mappings.get(type).getLeft();
            ws.monitorDataCls = ClassMapping.mappings.get(type).getRight();
            // 发送模型算法类型选择
            if (ws.state.sendMLType(ws, ws.conn, type)) {
                // 如果能发送
                modelId++;
                modelIdSet.add(modelId);
                ws.modelId = modelId;
                return modelId;
            } else {
                return -1;
            }
        } else {
            return -1;
        }
    }

    @Override
    public int sendTrainData(int websocketId, SegmentForDataset segmentForDataset) {
        if (socks.containsKey(websocketId) && socks.get(websocketId).conn.isOpen() && segmentForDataset.isTrainData()) {
            SoonWebsocket ws = socks.get(websocketId);
            if (ws.state.sendTrainData(ws, ws.conn, segmentForDataset)) {
                return ws.msgId;
            }
        }
        return -1;
    }

    @Override
    public int sendTestData(int websocketId, SegmentForDataset segmentForDataset) {
        if (socks.containsKey(websocketId) && socks.get(websocketId).conn.isOpen() && !segmentForDataset.isTrainData()) {
            SoonWebsocket ws = socks.get(websocketId);
            if (ws.state.sendTestData(ws, ws.conn, segmentForDataset)) {
                return ws.msgId;
            }
        }
        return -1;
    }

    @Override
    public int sendMLConfig(int websocketId, MLAlgorithmConfig mlAlgorithmConfig) {
        SoonWebsocket ws = socks.get(websocketId);
        if (ws!=null && ws.conn.isOpen()) {
            if (ws.state.sendMLConfig(ws, ws.conn, mlAlgorithmConfig)) {
                return ws.msgId;
            }
        }
        return -1;
    }

    @Override
    public int sendTrainDatasetId(int websocketId, int trainDataId) {
        SoonWebsocket ws = socks.get(websocketId);
        if (ws!=null && ws.conn.isOpen()) {
            if (ws.state.sendTrainDatasetId(ws, ws.conn, trainDataId)) {
                return ws.msgId;
            }
        }
        return -1;
    }

    @Override
    public int startTrain(int websocketId) {
        SoonWebsocket ws = socks.get(websocketId);
        if (ws!=null && ws.conn.isOpen()) {
            if (ws.state.startTrain(ws, ws.conn)) {
                return ws.msgId;
            }
        }
        return -1;
    }

    @Override
    public int stopTrain(int websocketId) {
        SoonWebsocket ws = socks.get(websocketId);
        if (ws!=null && ws.conn.isOpen()) {
            if (ws.state.stopTrain(ws, ws.conn)) {
                return ws.msgId;
            }
        }
        return -1;
    }

    @Override
    public int queryURL(int websocketId) {
        SoonWebsocket ws = socks.get(websocketId);
        if (ws!=null && ws.conn.isOpen()) {
            if (ws.state.queryURL(ws, ws.conn)) {
                return ws.msgId;
            }
        }
        return -1;
    }

    @Override
    public int applyModel(int websocketId, List<List<Double>> list) {
        SoonWebsocket ws = socks.get(websocketId);
        if (ws!=null && ws.conn.isOpen()) {
            if (ws.state.applyModel(ws, ws.conn, list)) {
                return ws.msgId;
            }
        }
        return -1;
    }

    @Override
    public int evalModel(int websocketId, int testDatasetId) {
        SoonWebsocket ws = socks.get(websocketId);
        if (ws!=null && ws.conn.isOpen()) {
            if ( ws.state.evalModel(ws, ws.conn, testDatasetId)) {
                return ws.msgId;
            }
        }
        return -1;

    }

    @Override
    public int deleteModel(int websocketId) {
        SoonWebsocket ws = socks.get(websocketId);
        if (ws!=null && ws.conn.isOpen()) {
            if (ws.state.deleteModel(ws, ws.conn)) {
                modelIdSet.remove(ws.modelId);
                return ws.msgId;
            }
        }
        return -1;
    }

    @Override
    public int deleteTrainDataset(int websocketId, int trainDatasetId) {
        SoonWebsocket ws = socks.get(websocketId);
        if (ws!=null && ws.conn.isOpen()) {
            if (ws.state.deleteTrainDataset(ws, ws.conn, trainDatasetId)) {
                trainDatasetIdSet.remove(trainDatasetId);
                return ws.msgId;
            }
        }
        return -1;
    }

    @Override
    public int deleteTestDataset(int websocketId, int testDatasetId) {
        SoonWebsocket ws = socks.get(websocketId);
        if (ws!=null && ws.conn.isOpen()) {
            if (ws.state.deleteTestDataset(ws, ws.conn, testDatasetId)) {
                testDatasetIdSet.remove(testDatasetId);
                return ws.msgId;
            }
        }
        return -1;
    }

    @Override
    public boolean containTrainId(int trainDatasetId) {
        if (trainDatasetIdSet.contains(trainDatasetId)) {
            return true;
        }
        return false;
    }

    @Override
    public boolean containTestId(int testDatasetId) {
        if (testDatasetIdSet.contains(testDatasetId)) {
            return true;
        }
        return false;
    }

    @Override
    public boolean containModelId(int modelId) {
        if (modelIdSet.contains(modelId)) {
            return true;
        }
        return false;
    }
}
