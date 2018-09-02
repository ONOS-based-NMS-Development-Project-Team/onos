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
package univ.bupt.soon.servadj;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import univ.bupt.soon.servadj.Service.ComputePath;
import univ.bupt.soon.servadj.Service.PoissionStream;
import univ.bupt.soon.servadj.Service.Service;
import univ.bupt.soon.servadj.Topology.SimpleGraph;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Skeletal ONOS application component.
 */

public class AppComponent {

    private final Logger log = LoggerFactory.getLogger(getClass());
    // 存储模型相关的内容。模型类型，模型id，模型详细配置
//    private final Map<MLAlgorithmType, Map<Integer, MLModelDetail>> models = Maps.newConcurrentMap();
//
//    private ApplicationId appId;
//
//    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
//    protected CoreService coreService;
//    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
//    protected MLAppRegistry mlAppRegistry;


    @Activate
    protected void activate()  {
//        appId = coreService.registerApplication("unive.bupt.soon.servconstruct");
//        mlAppRegistry.register(this, MLAppType.SERVICE_RECONSTRUCTION);
        log.info("SOON - service reconstruction - Started");
        SimpleGraph simpleGraph = new SimpleGraph();
        try {
            simpleGraph.parseJsonToGraph();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //业务发生 与 初步算路
        BlockingQueue<univ.bupt.soon.servadj.Service.Service> servicesToComputePath = new ArrayBlockingQueue<Service>(10);
        PoissionStream poissionStreamThread = new PoissionStream(servicesToComputePath);
        ComputePath computePathThread = new ComputePath(servicesToComputePath, simpleGraph.graph);

        poissionStreamThread.start();
        computePathThread.start();

    }

    @Deactivate
    protected void deactivate() {
//        mlAppRegistry.unregister(MLAppType.SERVICE_RECONSTRUCTION);

        log.info("SOON - service reconstruction - Stopped");
    }


}
