/*
 * it' write by rasoulpoordelan@gmail.com
 * you can use it with name
 */
package yara.socket.socketserver;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.websocket.server.WebSocketHandler;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

/**
 *
 * @author rasoul
 */
public class Main {

    public static void main(String[] args) throws Exception {
        Integer port = 8032;
        Server server = new Server(8032);
        WebSocketHandler wsHandler = new WebSocketHandler() {
            @Override
            public void configure(WebSocketServletFactory factory) {
                factory.register(WebSocketHandler.class);
            }
        };
        
        System.out.println("************************************************");
        System.out.println("                WebSocket Server                ");
        System.out.println("                                                ");
        System.out.println("                 Listen : "+port+"                  ");
        System.out.println("************************************************");
        
        server.setHandler(wsHandler);
        server.start();
        server.join();

    
    }

}
