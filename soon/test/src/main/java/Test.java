import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.websocket.server.WebSocketHandler;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketListener;
import org.eclipse.jetty.websocket.api.WriteCallback;



public class Test {
    public static void main(String args[])
    {
        Server server = new Server(9999);
        WebSocketHandler  wsHandler = new WebSocketHandler(){
            @Override
            public void configure(WebSocketServletFactory factory)
            {
                //注册自定义事件监听器
                factory.register(MyEchoSocket.class);
            }
        };
        ContextHandler context = new ContextHandler();
        context.setContextPath("/");
        context.setHandler(wsHandler);

        server.setHandler(wsHandler);
        try
        {
            server.start();
            server.join();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


}




