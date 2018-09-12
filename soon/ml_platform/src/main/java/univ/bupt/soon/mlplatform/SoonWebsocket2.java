package univ.bupt.soon.mlplatform;

import org.eclipse.jetty.websocket.WebSocket;
import org.eclipse.jetty.websocket.WebSocketClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;


/**
 * /result 接受Python发来的结果文件
 */
public class SoonWebsocket2 implements WebSocket.OnTextMessage, WebSocket.OnControl {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final int id;
    private Connection conn = null;

    public SoonWebsocket2(int id) {
        this.id = id;
    }

    @Override
    public boolean onControl(byte b, byte[] bytes, int i, int i1) {
        return false;
    }

    @Override
    public void onMessage(String s) {
//        log.info("received message from {} is {} ", id, s);
        String uri = "ws://localhost:5001/result";
        URI revResultUri = URI.create(uri);

        log.info("Received  message from python " );

        String filepath = "/home/ecoc/resultFromPython.txt";
        WebSocketClient client = null;
        try {
            client = new WebSocketClient();
            client.open(revResultUri, new SoonWebsocket(2));
        } catch (Exception e) {
            e.printStackTrace();
        }


        try
        {

            // 创建文件对象
            File fileText = new File(filepath);
            // 向文件写入对象写入信息
            FileWriter fileWriter = new FileWriter(fileText);

            // 写文件
            fileWriter.write(s);
            // 关闭
            fileWriter.close();
        }
        catch (IOException e)
        {
            //
            e.printStackTrace();
        }

    }

    @Override
    public void onOpen(Connection connection) {
        conn = connection;
        try {
            conn.sendMessage("this is from java");
        } catch (IOException e) {
            e.printStackTrace();
        }
        log.info("new websocket connection {} opened!", id);
    }

    @Override
    public void onClose(int i, String s) {
        conn.close();
        log.info("websocket connection {} closed!", id);
    }
}
