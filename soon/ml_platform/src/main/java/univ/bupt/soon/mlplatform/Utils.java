package univ.bupt.soon.mlplatform;

import org.eclipse.jetty.websocket.WebSocketClient;

import java.net.URI;

/**
 * 工具类的实现
 */
public class Utils {
    private static Utils ourInstance = new Utils();

    public static Utils getInstance() {
        return ourInstance;
    }

    private Utils() {
    }

    /**
     * Java端主动打开一个websocket连接。
     * @param address 连接对端地址
     * @return 如果连接建立成功，返回该连接的id;否则，返回-1。
     */
    public int addWebsocketConnection(URI address) {
        try {
            WebSocketClient client = new WebSocketClient();
            SoonWebSocketServlet.id++;
            SoonWebsocket websocket = new SoonWebsocket(SoonWebSocketServlet.id);
            client.open(address, websocket);
            SoonWebSocketServlet.socks.put(SoonWebSocketServlet.id, websocket);
        } catch (Exception e) {
            SoonWebSocketServlet.socks.remove(SoonWebSocketServlet.id);
            e.printStackTrace();
            return -1;
        }
        return SoonWebSocketServlet.id;
    }
}
