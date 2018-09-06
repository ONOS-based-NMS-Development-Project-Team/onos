import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketListener;
import org.eclipse.jetty.websocket.api.WriteCallback;

import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

class MyEchoSocket implements WebSocketListener {
    //维持session
    private static final Set< Session > sessions =
            Collections.synchronizedSet( new HashSet< Session >() );
    private Session session;

    //连接关闭，移除session列表
    public void onWebSocketClose(int statusCode, String reason) {
        System.out.println(reason);
        sessions.remove(this.session);
        this.session = null;
    }

    //建立连接后保存session
    public void onWebSocketConnect(Session session) {
        this.session = session;
        System.out.println(session.isOpen());
        sessions.add(session);
    }

    //错误处理
    public void onWebSocketError(Throwable cause) {
        System.out.println(cause.getMessage());

    }

    //接收字符串类型消息，并通知所有客户端
    public void onWebSocketBinary(byte[] payload, int offset, int len) {
        System.out.println("WebSocketBinary:"+new String(payload));
        for(final Session session : sessions){
            session.getRemote().sendBytes(ByteBuffer.wrap(payload), new WriteCallback() {
                //回调处理发送是否成功
                public void writeSuccess() {
                    System.out.println("success");
                }

                public void writeFailed(Throwable x) {
                    System.out.println("senderror:"+x.getMessage());
                }
            });
        }
    }

    //接收字符串类型消息，并转发给所有客户端
    public void onWebSocketText(String message) {
        System.out.println("lenght of text message: "+message.length());
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public MyEchoSocket(Session session) {
        this.session = session;
    }
}