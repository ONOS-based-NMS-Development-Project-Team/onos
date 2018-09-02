package univ.bupt.soon.mlshow.front.handler;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.onosproject.soon.dataset.original.Item;
import org.onosproject.soon.dataset.original.sdhnet.HistoryAlarmItem;
import org.onosproject.soon.foreground.MLAppType;
import org.onosproject.ui.RequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import univ.bupt.soon.mlshow.front.MLMessageHandler;
import univ.bupt.soon.mlshow.front.SoonUiComponent;

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

    }
}
