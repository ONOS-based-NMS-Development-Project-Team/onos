package univ.bupt.soon.mlplatform;

import com.google.common.collect.Lists;
import org.eclipse.jetty.websocket.WebSocket;
import org.eclipse.jetty.websocket.WebSocketServlet;
import org.onosproject.soon.dataset.dataset.SegmentForDataset;
import org.onosproject.soon.mlmodel.MLAlgorithmType;
import org.onosproject.soon.mlmodel.config.nn.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.List;

public class SoonWebSocketServlet extends WebSocketServlet {

    private final Logger log = LoggerFactory.getLogger(getClass());


    @Override
    public void init() throws ServletException {
        super.init();
    }

    @Override
    public WebSocket doWebSocketConnect(HttpServletRequest httpServletRequest, String s) {
        PlatformImpl.sockId++;
        if (!PlatformImpl.socks.containsKey(PlatformImpl.sockId)) {
            SoonWebsocket swsock = new SoonWebsocket(PlatformImpl.sockId);
            PlatformImpl.socks.put(PlatformImpl.sockId, swsock);
            return swsock;
        }
        return null;
    }

}
