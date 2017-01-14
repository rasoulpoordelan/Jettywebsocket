/*
 * it' write by rasoulpoordelan@gmail.com
 * you can use it with name
 */
package yara.socket.socketserver;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import java.io.FileReader;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.websocket.server.WebSocketHandler;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

/**
 *
 * @author rasoul
 */
public class Main {

    public static void main(String[] args) throws Exception {
        Integer port = 8031;

        try {
            Gson gson = new Gson();
            JsonReader reader = new JsonReader(new FileReader("Config.Conf"));
            ConfigModel data = gson.fromJson(reader, ConfigModel.class);
            port = data.getPort();
        } catch (Exception e) {
            System.out.println("you dont have config file");
        }

        Server server = new Server(port);
        WebSocketHandler wsHandler = new WebSocketHandler() {
            @Override
            public void configure(WebSocketServletFactory factory) {
                factory.register(MyWebSocketHandler.class);
            }
        };

        System.out.println("************************************************");
        System.out.println("                WebSocket Server                ");
        System.out.println("                                                ");
        System.out.println("                 Listen : " + port + "                  ");
        System.out.println("************************************************");

        server.setHandler(wsHandler);
        server.start();
        server.join();

    }

}
