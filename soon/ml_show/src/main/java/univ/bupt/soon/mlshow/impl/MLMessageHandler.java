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
package univ.bupt.soon.mlshow.impl;

import com.google.common.collect.ImmutableSet;

import com.google.common.collect.Maps;
import org.onosproject.soon.foreground.MLAppType;
import org.onosproject.soon.foreground.ModelControlService;
import org.onosproject.ui.RequestHandler;
import org.onosproject.ui.UiMessageHandler;
import univ.bupt.soon.mlshow.impl.handler.AlarmHistoricalDataRequestHandler;
import univ.bupt.soon.mlshow.impl.handler.AlarmHistoricalDetailRequestHandler;
import univ.bupt.soon.mlshow.impl.handler.AlarmPredDataRequestHandler;

import java.util.*;

/**
 * Skeletal ONOS UI Table-View message handler.
 */
public class MLMessageHandler extends UiMessageHandler {

    // 存储所有的应用类型与模型控制接口
    public static Map<MLAppType, ModelControlService> modelServices = Maps.newConcurrentMap();

    @Override
    protected Collection<RequestHandler> createRequestHandlers() {
        return ImmutableSet.of(
                new AlarmHistoricalDetailRequestHandler(),
                new AlarmHistoricalDataRequestHandler(),
                new AlarmPredDataRequestHandler()
        );
    }

}
