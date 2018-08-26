package univ.bupt.soon.mlplatform;

import org.eclipse.jetty.websocket.WebSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;


/**
 * 实现websocket通信的状态机处理机制
 */
public class SoonWebsocket implements WebSocket.OnTextMessage, WebSocket.OnControl {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final int id;
    private Connection conn = null;

    public SoonWebsocket(int id) {
        this.id = id;
    }

    @Override
    public boolean onControl(byte b, byte[] bytes, int i, int i1) {
        return false;
    }

    @Override
    public void onMessage(String s) {
        log.info("received message from {} is {} ", id, s);
        try {
            conn.sendMessage("echo message: " + s);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onOpen(Connection connection) {
        conn = connection;
        log.info("new websocket connection {} opened!", id);
    }

    @Override
    public void onClose(int i, String s) {
        conn.close();
        log.info("websocket connection {} closed!", id);
    }
}
