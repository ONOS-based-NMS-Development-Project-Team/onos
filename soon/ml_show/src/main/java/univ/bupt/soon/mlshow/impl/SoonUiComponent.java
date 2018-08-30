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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import org.apache.felix.scr.annotations.*;
import org.onosproject.cluster.ClusterService;
import org.onosproject.mastership.MastershipAdminService;
import org.onosproject.net.device.DeviceProviderRegistry;
import org.onosproject.net.device.DeviceProviderService;
import org.onosproject.net.device.DeviceService;
import org.onosproject.net.link.LinkProviderRegistry;
import org.onosproject.net.link.LinkProviderService;
import org.onosproject.net.topology.TopologyProviderRegistry;
import org.onosproject.soon.MLAppRegistry;
import org.onosproject.soon.MLAppType;
import org.onosproject.soon.ModelControlService;
import org.onosproject.ui.UiExtension;
import org.onosproject.ui.UiExtensionService;
import org.onosproject.ui.UiMessageHandlerFactory;
import org.onosproject.ui.UiView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import univ.bupt.soon.mlshow.demonet.TopoReport;

import java.util.List;
import java.util.Map;

/**
 * SOON的UI展示。
 */
@Component(immediate = true)
@Service
public class SoonUiComponent implements MLAppRegistry {

    private static final String VIEW_ID = "soon";
    private static final String VIEW_TEXT = "Self Oprimizing Optical Network";

    private final Logger log = LoggerFactory.getLogger(getClass());

    public static Map<MLAppType, ModelControlService> modelServices = Maps.newConcurrentMap();

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected UiExtensionService uiExtensionService;
    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected DeviceProviderRegistry deviceProviderRegistry;
    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected LinkProviderRegistry linkProviderRegistry;
    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected TopologyProviderRegistry topologyProviderRegistry;
    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected MastershipAdminService mastershipAdminService;
    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected ClusterService clusterService;
    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected DeviceService deviceService;

    private DeviceProviderService devProService;
    private LinkProviderService linkProService;
    private TopoReport topoReport;

    // 构建不同应用的handler
    private AlarmPredMessageHandler alarmPredMessageHandler = new AlarmPredMessageHandler();


    // List of application views
    private final List<UiView> uiViews = ImmutableList.of(
            new UiView(UiView.Category.OTHER, VIEW_ID, VIEW_TEXT)
    );

    // Factory for UI message handlers
    private final UiMessageHandlerFactory messageHandlerFactory =
            () -> ImmutableList.of(
                    // 添加handler
                    alarmPredMessageHandler
            );

    // Application UI extension
    protected UiExtension extension =
            new UiExtension.Builder(getClass().getClassLoader(), uiViews)
                    .resourcePath(VIEW_ID)
                    .messageHandlerFactory(messageHandlerFactory)
                    .build();

    @Activate
    protected void activate() {
        uiExtensionService.register(extension);
        // 拓扑注入服务
//        topoReport = new TopoReport();
//        devProService = deviceProviderRegistry.register(topoReport);
//        linkProService = linkProviderRegistry.register(topoReport);
//        topoReport.setClusterService(clusterService);
//        topoReport.setMastershipAdminService(mastershipAdminService);
//        topoReport.setDevProService(devProService);
//        topoReport.setLinkProService(linkProService);
//        topoReport.setDeviceService(deviceService);
//        topoReport.reportNodes();
//        topoReport.reportLinks();

        log.info("Started");
    }

    @Deactivate
    protected void deactivate() {
        uiExtensionService.unregister(extension);
//        deviceProviderRegistry.unregister(topoReport);
//        linkProviderRegistry.unregister(topoReport);
        log.info("Stopped");
    }

    @Override
    public boolean register(ModelControlService modelControlService, MLAppType s) {
        if (modelServices.containsKey(s)) {
            return false;
        } else {
            modelServices.put(s, modelControlService);
            return true;
        }
    }

    @Override
    public boolean unregister(MLAppType s) {
        modelServices.remove(s);
        return true;
    }

}
