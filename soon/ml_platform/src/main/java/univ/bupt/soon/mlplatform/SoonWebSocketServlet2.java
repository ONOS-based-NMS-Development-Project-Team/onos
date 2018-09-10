package univ.bupt.soon.mlplatform;

import com.google.common.collect.Maps;
import org.eclipse.jetty.websocket.WebSocket;
import org.eclipse.jetty.websocket.WebSocketServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public class SoonWebSocketServlet2 extends WebSocketServlet {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private int id=0;
    static Map<Integer, SoonWebsocket2> socks = Maps.newHashMap();  // 存储已经存在的socket连接

    @Override
    public void init() throws ServletException {
        super.init();
    }

    @Override
    public WebSocket doWebSocketConnect(HttpServletRequest httpServletRequest, String s) {
        id++;
        if (!socks.containsKey(id)) {
            SoonWebsocket2 swsock2 = new SoonWebsocket2(id);
            socks.put(id, swsock2);
            return swsock2;
        }
        return null;
    }
}
