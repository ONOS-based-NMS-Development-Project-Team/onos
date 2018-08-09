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
package univ.bupt.soon.almpred;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.felix.scr.annotations.*;
import org.onlab.packet.ChassisId;
import org.onosproject.app.ApplicationService;
import org.onosproject.cfg.ComponentConfigService;
import org.onosproject.cluster.ClusterService;
import org.onosproject.core.ApplicationId;
import org.onosproject.core.CoreService;
import org.onosproject.mastership.MastershipAdminService;
import org.onosproject.mastership.MastershipService;
import org.onosproject.net.*;
import org.onosproject.net.device.*;
import org.onosproject.net.link.*;
import org.onosproject.net.provider.AbstractProvider;
import org.onosproject.net.provider.ProviderId;
import org.onosproject.net.topology.TopologyProviderRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Skeletal ONOS application component.
 */
@Component(immediate = true)
public class AlarmPrediction extends AbstractProvider implements DeviceProvider, LinkProvider {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected CoreService coreService;
    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected ComponentConfigService cfgService;

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

    private ApplicationId appId;
    private DeviceProviderService devProService;
    private LinkProviderService linkProService;

    public AlarmPrediction() {
        super(new ProviderId("test", "univ.bupt.soon.provider.alarm_pred"));
    }

    @Activate
    protected void activate() {

        devProService = deviceProviderRegistry.register(this);
        linkProService = linkProviderRegistry.register(this);
        appId = coreService.registerApplication("unive.bupt.soon.almpred");
        log.info("SOON - alarm prediction - Started");

        buildSimpleTopo(4);
    }

    @Deactivate
    protected void deactivate() {
        deviceProviderRegistry.unregister(this);
        linkProviderRegistry.unregister(this);
        log.info("SOON - alarm prediction - Stopped");
    }

    @Override
    public void triggerProbe(DeviceId deviceId) {
        log.info("triggerProbe");
    }

    @Override
    public void roleChanged(DeviceId deviceId, MastershipRole mastershipRole) {
        log.info("roleChanged");
//        devProService.receivedRoleReply(deviceId, mastershipRole, mastershipRole);
        mastershipAdminService.setRole(clusterService.getLocalNode().id(), deviceId, MastershipRole.MASTER);
    }

    @Override
    public boolean isReachable(DeviceId deviceId) {
        return true;
    }

    @Override
    public void changePortState(DeviceId deviceId, PortNumber portNumber, boolean b) {
        log.info("changePortState");
    }

    @Override
    public ProviderId id() {
        return super.id();
    }


    private void buildSimpleTopo(int nodeNum) {
        // 构建节点和端口。每个节点构建两个端口111和222
        for (int i=1; i<nodeNum+1; i++) {
            // 构建节点
            String id = "test:0000000000"+String.valueOf(i);
            log.info(id);
            DeviceId deviceId = DeviceId.deviceId(id);
            DeviceDescription desc = new DefaultDeviceDescription(deviceId.uri(),
                    Device.Type.SWITCH,
                    "bupt",
                    "1.0",
                    "2.0",
                    "9527",
                    new ChassisId(666));
            // 更新节点
            devProService.deviceConnected(deviceId, desc);
            // 构建端口
            List<PortDescription> ports = Lists.newArrayListWithCapacity(2);
            DefaultAnnotations annotations = DefaultAnnotations.builder()
                    .set("Purpose", "Testing")
                    .build();
            ports.add(new DefaultPortDescription(PortNumber.portNumber(111),
                    false, Port.Type.FIBER,0,
                    annotations));
            ports.add(new DefaultPortDescription(PortNumber.portNumber(222),
                    false, Port.Type.FIBER,0,
                    annotations));
            // 更新端口
            devProService.updatePorts(deviceId, ports);
            // 设置mastership
            mastershipAdminService.setRole(clusterService.getLocalNode().id(), deviceId, MastershipRole.MASTER);
        }
        // 增加一条链路
        ConnectPoint src = new ConnectPoint(DeviceId.deviceId("test:00000000001"), PortNumber.portNumber(111));
        ConnectPoint dst = new ConnectPoint(DeviceId.deviceId("test:00000000002"), PortNumber.portNumber(111));
        LinkDescription linkDescription = new DefaultLinkDescription(src, dst, Link.Type.DIRECT,true,
                DefaultAnnotations.builder().build());
        linkProService.linkDetected(linkDescription);
    }

}
