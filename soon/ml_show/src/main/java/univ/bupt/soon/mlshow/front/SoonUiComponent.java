package univ.bupt.soon.mlshow.front;

import com.google.common.collect.ImmutableList;
import org.apache.felix.scr.annotations.*;
import org.onosproject.cluster.ClusterService;
import org.onosproject.mastership.MastershipAdminService;
import org.onosproject.net.device.DeviceProviderRegistry;
import org.onosproject.net.device.DeviceService;
import org.onosproject.net.link.LinkProviderRegistry;
import org.onosproject.net.topology.TopologyProviderRegistry;
import org.onosproject.soon.dataset.original.sdhnet.CurrentAlarmItem;
import org.onosproject.soon.dataset.original.sdhnet.HistoryAlarmItem;
import org.onosproject.soon.dataset.original.sdhnet.PerformanceItem;
import org.onosproject.soon.foreground.MLAppRegistry;
import org.onosproject.soon.foreground.MLAppType;
import org.onosproject.soon.foreground.ModelControlService;
import org.onosproject.ui.UiExtension;
import org.onosproject.ui.UiExtensionService;
import org.onosproject.ui.UiMessageHandlerFactory;
import org.onosproject.ui.UiView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import univ.bupt.soon.mlshow.front.handler.model.*;
import univ.bupt.soon.mlshow.original.OriginalDataAccess;

import java.util.List;

/**
 * SOON的UI展示。
 */
@Component(immediate = true)
@Service
public class SoonUiComponent implements MLAppRegistry {

    private static final String VIEW_ID = "soon";
    private static final String VIEW_TEXT = "Self Optimizing Optical Network";

    private final Logger log = LoggerFactory.getLogger(getClass());

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

//    private DeviceProviderService devProService;
//    private LinkProviderService linkProService;
//    private TopoReport topoReport;


    // List of application views
    private final List<UiView> uiViews = ImmutableList.of(
            new UiView(UiView.Category.OTHER, VIEW_ID, VIEW_TEXT)
    );

    // Factory for UI message handlers
    private final UiMessageHandlerFactory messageHandlerFactory =
            () -> ImmutableList.of(
                    new MLMessageHandler(),
                    new ModelLibraryMessageHandler(),
                    new FaultClassificationMessageRequestHandler(),
                    new AlarmPredictionMessageRequestHandler(),
                    new EdgePredictionMessageRequestHandler(),
                    new AreaPredictionMessageRequestHandler()
            );

    // Application UI extension
    private UiExtension extension =
            new UiExtension.Builder(getClass().getClassLoader(), uiViews)
                    .resourcePath(VIEW_ID)
                    .messageHandlerFactory(messageHandlerFactory)
                    .build();

    @Activate
    protected void activate() {
        uiExtensionService.register(extension);
        // 注册历史告警的服务
        OriginalDataAccess hisAlarmService = new OriginalDataAccess("his_alarms", MLAppType.ORIGINAL_HISTORY_ALARM_DATA, HistoryAlarmItem.class);
        register(hisAlarmService, hisAlarmService.getServiceName());
        // 注册当前告警的服务
        OriginalDataAccess curAlarmService = new OriginalDataAccess("cur_alarms", MLAppType.ORIGINAL_CURRENT_ALARM_DATA, CurrentAlarmItem.class);
        register(curAlarmService, curAlarmService.getServiceName());
        // 注册性能记录的服务
        OriginalDataAccess performanceService = new OriginalDataAccess("performance", MLAppType.ORIGINAL_PERFORMANCE_DATA, PerformanceItem.class);
        register(performanceService, performanceService.getServiceName());

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
        // 注销所有ModelControlService服务
        for (MLAppType type : MLMessageHandler.modelServices.keySet()) {
            unregister(type);
        }
        log.info("Stopped");
    }

    @Override
    public boolean register(ModelControlService modelControlService, MLAppType s) {
        if (MLMessageHandler.modelServices.containsKey(s)) {
            return false;
        } else {
            MLMessageHandler.modelServices.put(s, modelControlService);
            return true;
        }
    }

    @Override
    public boolean unregister(MLAppType s) {
        MLMessageHandler.modelServices.remove(s);
        return true;
    }

}
