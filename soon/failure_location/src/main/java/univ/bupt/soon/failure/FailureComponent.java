package univ.bupt.soon.failure;


import org.apache.felix.scr.annotations.*;
import org.onosproject.core.ApplicationId;
import org.onosproject.core.CoreService;
import org.onosproject.soon.MLAppRegistry;
import org.onosproject.soon.dataset.original.Item;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import univ.bupt.soon.failure.classification.FailureClassification;

import java.util.List;

/**
 * 故障处理相关的app。目前看来包括故障预测和故障分类
 */
@Component
public class FailureComponent {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected CoreService coreService;
//    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
//    protected MLAppRegistry mlAppRegistry;
    //    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
//    protected MLPlatformService platformService;

    private ApplicationId appId;
    private SQLQuery query = SQLQuery.instance;
    private FailureClassification failureClassification = new FailureClassification();

    @Activate
    protected void activate() {

        appId = coreService.registerApplication("unive.bupt.soon.almpred");
//        mlAppRegistry.register(failureClassification, FailureClassification.serviceName);

        query.connect();

        List<Item> items = failureClassification.updateData(2, 10);
        log.info(items.toString());
    }

    @Deactivate
    protected void deactivate() {
//        mlAppRegistry.unregister(FailureClassification.serviceName);
        query.close();
        log.info("SOON - failure - Stopped");
    }
}
