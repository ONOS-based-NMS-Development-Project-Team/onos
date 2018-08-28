package univ.bupt.soon.mlshow.demonet;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.onlab.packet.ChassisId;
import org.onosproject.cluster.ClusterService;
import org.onosproject.mastership.MastershipAdminService;
import org.onosproject.net.*;
import org.onosproject.net.device.*;
import org.onosproject.net.link.DefaultLinkDescription;
import org.onosproject.net.link.LinkDescription;
import org.onosproject.net.link.LinkProvider;
import org.onosproject.net.link.LinkProviderService;
import org.onosproject.net.provider.AbstractProvider;
import org.onosproject.net.provider.ProviderId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * 上报拓扑的主类
 */
public class TopoReport extends AbstractProvider implements DeviceProvider, LinkProvider {


    private static final String linkFile = "connections.csv";
    private static final String nodeFile = "nodes.csv";
    private DeviceProviderService devProService;
    private LinkProviderService linkProService;
    private DeviceService deviceService;
    private MastershipAdminService mastershipAdminService;
    private ClusterService clusterService;
    // 妈卖批我要疯了！！！scheme里面包含的所有大写都会转化成小写。如果包含大写字母，会直接造成provider和device的不匹配。。。
    private static final String scheme = "sgcc";
    private final Logger log = LoggerFactory.getLogger(getClass());

    public TopoReport() {
        super(new ProviderId(scheme, "univ.bupt.soon.mlshow.demonet"));
    }

    /**
     * 上报电网全国骨干拓扑的节点
     */
    public void reportNodes() {
        try {
            String path = System.getenv("ONOS_ROOT");
            String file = path + "/soon/resources/" + nodeFile;
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            reader.readLine(); // 去掉表头
            String line;
            while ((line = reader.readLine()) != null) {
                String[] values = line.split(","); //name	type	id	fiber_num	subnet	gateway
                String id = scheme + ":" + values[0].replace(' ', '_');
                DeviceId devId = DeviceId.deviceId(id);
                DeviceDescription devDesc = new DefaultDeviceDescription(devId.uri(),
                        Device.Type.OTN,
                        "Huawei",
                        "1.0",
                        "2.0",
                        "9527",
                        new ChassisId(666),
                        DefaultAnnotations.builder().set("type", values[1])
                                .set("id", values[2])
                                .set("fiber_num", values[3])
                                .set("subnet", values[4])
                                .set("gateway", values[5])
                                .build());
                // 更新节点
                devProService.deviceConnected(devId, devDesc);
                // 设置mastership
                mastershipAdminService.setRole(clusterService.getLocalNode().id(), devId, MastershipRole.MASTER);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 上报电网全国骨干拓扑的链路
     */
    public void reportLinks() {
        Map<DeviceId, List<PortDescription>> portsMap = Maps.newHashMap();
        List<LinkDescription> conns = Lists.newArrayList();
        try {
            String path = System.getenv("ONOS_ROOT");
            String file = path + "/soon/resources/" + linkFile;
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            int portNum = 1;
            reader.readLine(); // 去掉表头
            String line;
            while ((line = reader.readLine()) != null) {
                String[] values = line.split(","); //name	volume	src_node	src_port	dst_node	dst_port
                Device srcNode = deviceService.getDevice(
                        DeviceId.deviceId(scheme + ":" + values[2].replace(' ', '_')));
                Device dstNode = deviceService.getDevice(
                        DeviceId.deviceId(scheme + ":" + values[4].replace(' ', '_')));
                if (srcNode == null || dstNode == null) {
                    throw new TimeoutException("cannot get the src or dst node");
                }
                // 存储src的port
                PortDescription port = DefaultPortDescription.builder()
                        .withPortNumber(PortNumber.portNumber(portNum))
                        .isEnabled(false)
                        .type(Port.Type.FIBER)
                        .portSpeed(8000000)
                        .annotations(DefaultAnnotations.builder().set("detail", values[3]).build())
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
                        .annotations(DefaultAnnotations.builder().set("detail", values[5]).build())
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
            }
            // 增加端口
            for (Map.Entry<DeviceId, List<PortDescription>> entry : portsMap.entrySet()) {
                devProService.updatePorts(entry.getKey(), entry.getValue());
            }
            // 增加链路
            for (LinkDescription link : conns) {
                linkProService.linkDetected(link);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void triggerProbe(DeviceId deviceId) {
        // do nothing
    }

    @Override
    public void roleChanged(DeviceId deviceId, MastershipRole mastershipRole) {
        mastershipAdminService.setRole(clusterService.getLocalNode().id(), deviceId, MastershipRole.MASTER);
    }

    @Override
    public boolean isReachable(DeviceId deviceId) {
        return true;
    }

    @Override
    public void changePortState(DeviceId deviceId, PortNumber portNumber, boolean b) {

    }

    public void setDevProService(DeviceProviderService devProService) {
        this.devProService = devProService;
    }

    public void setLinkProService(LinkProviderService linkProService) {
        this.linkProService = linkProService;
    }

    public void setMastershipAdminService(MastershipAdminService mastershipAdminService) {
        this.mastershipAdminService = mastershipAdminService;
    }

    public void setClusterService(ClusterService clusterService) {
        this.clusterService = clusterService;
    }

    public DeviceService getDeviceService() {
        return deviceService;
    }

    public void setDeviceService(DeviceService deviceService) {
        this.deviceService = deviceService;
    }
}
