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
package univ.bupt.soon.mlshow.front;

import com.google.common.collect.ImmutableSet;

import com.google.common.collect.Maps;
import org.onosproject.soon.foreground.MLAppType;
import org.onosproject.soon.foreground.ModelControlService;
import org.onosproject.ui.RequestHandler;
import org.onosproject.ui.UiMessageHandler;
import univ.bupt.soon.mlshow.front.handler.dataset.ModelLibraryDataRequestHandler;
import univ.bupt.soon.mlshow.front.handler.dataset.app.AreaPredDataSetDataRequestHandler;
import univ.bupt.soon.mlshow.front.handler.dataset.app.EdgePredDataSetDataRequestHandler;
import univ.bupt.soon.mlshow.front.handler.dataset.app.FailClassDataSetDataRequestHandler;
import univ.bupt.soon.mlshow.front.handler.dataset.original.CurrentAlarmDataRequestHandler;
import univ.bupt.soon.mlshow.front.handler.dataset.original.HistoricalAlarmDataRequestHandler;
import univ.bupt.soon.mlshow.front.handler.dataset.app.AlarmPredDataSetDataRequestHandler;
import univ.bupt.soon.mlshow.front.handler.dataset.original.PerformanceDataRequestHandler;
import univ.bupt.soon.mlshow.front.handler.model.AlarmPredDataRequestHandler;
import univ.bupt.soon.mlshow.front.handler.model.ModelLibraryMessageHandler;

import java.util.*;

/**
 * Skeletal ONOS UI Table-View message handler.
 */
public class MLMessageHandler extends UiMessageHandler {

    // 存储所有的应用类型与模型控制接口
    public static Map<MLAppType, ModelControlService> modelServices = Maps.newConcurrentMap();

    @Override
    protected Collection<RequestHandler> createRequestHandlers() {
        /* 电网采集到的相关数据的展示 */
        // 历史告警
        HistoricalAlarmDataRequestHandler ahdrh = new HistoricalAlarmDataRequestHandler();
        ahdrh.setService(modelServices.get(MLAppType.ORIGINAL_HISTORY_ALARM_DATA));
        // 当前告警
        CurrentAlarmDataRequestHandler cadrh = new CurrentAlarmDataRequestHandler();
        cadrh.setService(modelServices.get(MLAppType.ORIGINAL_CURRENT_ALARM_DATA));
        // 性能信息
        PerformanceDataRequestHandler pdrh = new PerformanceDataRequestHandler();
        pdrh.setService(modelServices.get(MLAppType.ORIGINAL_PERFORMANCE_DATA));

        /* 告警预测数据的展示 */
        // 告警预测数据集
//        AlarmPredDataSetDataRequestHandler apdsdrh = new AlarmPredDataSetDataRequestHandler();
//        apdsdrh.setService(modelServices.get(MLAppType.ALARM_PREDICTION));
        // 故障定位数据集
        FailClassDataSetDataRequestHandler fcdsdrh = new FailClassDataSetDataRequestHandler();
        fcdsdrh.setService(modelServices.get(MLAppType.FAILURE_CLASSIFICATION));
        // 链路预测数据集
        EdgePredDataSetDataRequestHandler epdsdrh = new EdgePredDataSetDataRequestHandler();
        epdsdrh.setService(modelServices.get(MLAppType.LINK_PREDICTION));
        // 区域预测数据集
        AreaPredDataSetDataRequestHandler areaPdsdrh = new AreaPredDataSetDataRequestHandler();
        areaPdsdrh.setService(modelServices.get(MLAppType.BUSINESS_AREA_PREDICTION));  // TODO 此处有冲突

        return ImmutableSet.of(
                ahdrh,
                cadrh,
                pdrh,
//                apdsdrh,
                fcdsdrh,
                epdsdrh,
                areaPdsdrh
        );
    }

}
