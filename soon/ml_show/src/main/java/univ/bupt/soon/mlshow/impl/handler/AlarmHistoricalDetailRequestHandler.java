package univ.bupt.soon.mlshow.impl.handler;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.onosproject.soon.dataset.original.Item;
import org.onosproject.soon.dataset.original.sdhnet.HistoryAlarmItem;
import org.onosproject.soon.foreground.MLAppType;
import org.onosproject.ui.RequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import univ.bupt.soon.mlshow.impl.SoonUiComponent;

import java.text.SimpleDateFormat;


/**
 * handler for AlarmHistorical details requests
  */
public class AlarmHistoricalDetailRequestHandler extends RequestHandler {

    private final Logger log = LoggerFactory.getLogger(getClass());


    private static final String ALARM_HIST_DETAILS_REQ = "alarmHistoricalDetailsRequest";
    private static final String ALARM_HIST_DETAILS_RESP = "alarmHistoricalDetailsResponse";
    private static final String ALARM_HIST_DETAILS_TABLES = "alarmHistoricalDetails";

    //    private static final String LEVEL = "level";
//    private static final String NAME = "name";
    private static final String ALARM_SRC = "alarm_src";
    private static final String TP = "tp";
    private static final String LOCATION = "location";
    private static final String HAPPEN_TIME = "happen_time";
    private static final String CLEAN_TIME = "clean_time";
    private static final String CONFIRM_TIME = "confirm_time";
    private static final String PATH_LEVEL = "path_level";

    public AlarmHistoricalDetailRequestHandler() {
        super(ALARM_HIST_DETAILS_REQ);
    }

    @Override
    public void process(ObjectNode payload) {
        int id = Integer.valueOf(string(payload, ID)).intValue();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        //当offset==limit时，取一条数据，所以只得到一条数据
        Item item = SoonUiComponent.modelServices.get(MLAppType.ORIGINAL_DATA).updateData(id,id).get(0);
        HistoryAlarmItem it = (HistoryAlarmItem) item;

        ObjectNode rootNode = objectNode();
        ObjectNode data = objectNode();
        rootNode.set(ALARM_HIST_DETAILS_TABLES, data);

        if (item == null) {
            rootNode.put(RESULT, "Item with id '" + id + "' not found");
            log.warn("attempted to get item detail for id '{}'", id);

        } else {
            rootNode.put(RESULT, "Found item with id '" + id + "'");

            data.put(LEVEL, it.getLevel());
            data.put(ALARM_SRC, it.getAlarm_src());
            data.put(NAME, it.getName());

            data.put(TP, it.getTp());
            data.put(LOCATION, it.getLocation());
            data.put(HAPPEN_TIME, formatter.format(it.getHappen_time()));
            data.put(CLEAN_TIME, formatter.format(it.getClean_time()));
            data.put(CONFIRM_TIME, formatter.format(it.getConfirm_time()));
            data.put(PATH_LEVEL, it.getPath_level());
        }

        sendMessage(ALARM_HIST_DETAILS_RESP, rootNode);
    }
}
