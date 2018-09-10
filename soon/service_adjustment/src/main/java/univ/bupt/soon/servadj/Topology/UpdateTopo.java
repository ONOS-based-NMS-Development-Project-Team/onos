package univ.bupt.soon.servadj.Topology;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.onlab.packet.ChassisId;
import org.onosproject.net.*;
import org.onosproject.net.device.*;
import org.onosproject.net.link.DefaultLinkDescription;
import org.onosproject.net.link.LinkDescription;
import org.onosproject.net.link.LinkProvider;
import org.onosproject.net.link.LinkProviderService;
import org.onosproject.net.provider.AbstractProvider;
import org.onosproject.net.provider.ProviderId;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class UpdateTopo extends AbstractProvider implements DeviceProvider, LinkProvider {

    private DeviceProviderService devProService;
    private LinkProviderService linkProService;
    private DeviceService deviceService;
    private static final String scheme = "servadj";
    public UpdateTopo(ProviderId id) {
        super(id);
    }


    public void reportNodes() {
//        "vertex" :[1, 2, 3, 4, 5, 6, 7]

        List<String> IDs=new ArrayList<>();
        for(int i = 0; i < 8; i++){
            IDs.add("i");

        DeviceId devId = DeviceId.deviceId(IDs.get(i));
        DeviceDescription devDesc = new DefaultDeviceDescription(devId.uri(),
                Device.Type.OTN,
                "Huawei",
                "1.0",
                "2.0",
                "9527",
                new ChassisId(666),
                DefaultAnnotations.builder().set("type","test")
                        .set("id", "test")
                        .set("fiber_num", "test")
                        .set("subnet", "test")
                        .set("gateway","test")
                        .build());
        // 更新节点
        devProService.deviceConnected(devId,devDesc);}

    }


//        "edge": [
//    {"srcId": 1, "desId": 2, "metric": 3},
//    {"srcId": 2, "desId": 3, "metric": 4},
//    {"srcId": 1, "desId": 3, "metric": 4},
//    {"srcId": 3, "desId": 4, "metric": 15},
//    {"srcId": 4, "desId": 5, "metric": 3},
//    {"srcId": 5, "desId": 6, "metric": 10},
//    {"srcId": 2, "desId": 6, "metric": 12},
//    {"srcId": 6, "desId": 7, "metric": 2}

    public void reportLinks(){
        List<LinkDescription> conns = Lists.newArrayList();
        Map<DeviceId, List<PortDescription>> portsMap = Maps.newHashMap();
        int portNum = 1;

        Device srcNode = deviceService.getDevice(
                DeviceId.deviceId(scheme + ":" ));
        Device dstNode = deviceService.getDevice(
                DeviceId.deviceId(scheme + ":" ));

        // 存储src的port
        PortDescription port = DefaultPortDescription.builder()
                .withPortNumber(PortNumber.portNumber(portNum))
                .isEnabled(false)
                .type(Port.Type.FIBER)
                .portSpeed(8000000)
                .annotations(DefaultAnnotations.builder().set("detail", "aa").build())
                .build();
        if (!portsMap.containsKey(srcNode.id())) {
            portsMap.put(srcNode.id(), Lists.newArrayList());
        }
        portsMap.get(srcNode.id()).add(port);
        // 存储dst的port
        port = DefaultPortDescription.builder()
                .withPortNumber(PortNumber.portNumber(portNum))
                .isEnabled(false)
                .type(Port.Type.FIBER)
                .portSpeed(8000000)
                .annotations(DefaultAnnotations.builder().set("detail", "aa").build())
                .build();
        if (!portsMap.containsKey(dstNode.id())) {
            portsMap.put(dstNode.id(), Lists.newArrayList());
        }
        portsMap.get(dstNode.id()).add(port);


        // 存储连接
        ConnectPoint srcCp = new ConnectPoint(srcNode.id(), PortNumber.portNumber(portNum));
        ConnectPoint dstCp = new ConnectPoint(dstNode.id(), PortNumber.portNumber(portNum));
        conns.add(new DefaultLinkDescription(srcCp, dstCp, Link.Type.OPTICAL,
                true, DefaultAnnotations.builder().build()));
        conns.add(new DefaultLinkDescription(dstCp, srcCp, Link.Type.OPTICAL,
                true, DefaultAnnotations.builder().build()));

        // 递增portNum
        portNum++;

    // 增加端口
            for (Map.Entry<DeviceId, List<PortDescription>> entry : portsMap.entrySet()) {
        devProService.updatePorts(entry.getKey(), entry.getValue());
    }
    // 增加链路
            for (LinkDescription link : conns) {
        linkProService.linkDetected(link);
    }



    }

    @Override
    public void triggerProbe(DeviceId deviceId) {

    }

    @Override
    public void roleChanged(DeviceId deviceId, MastershipRole mastershipRole) {

    }

    @Override
    public boolean isReachable(DeviceId deviceId) {
        return false;
    }

    @Override
    public void changePortState(DeviceId deviceId, PortNumber portNumber, boolean b) {

    }
}
