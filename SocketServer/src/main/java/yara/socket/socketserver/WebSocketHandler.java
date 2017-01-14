/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package yara.socket.socketserver;

/**
 *
 * @author rasoul
 */
import java.io.IOException;
import java.util.*;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import java.util.concurrent.ConcurrentHashMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@WebSocket
public class WebSocketHandler {

    private Session session;
    private String myToken;
    public static Map<String, Session> Clients = new ConcurrentHashMap<String, Session>();
    public static Map<String, HashSet<String>> Rooms = new ConcurrentHashMap<String, HashSet<String>>();
    public static Map<String, HashSet<String>> ClientRooms = new ConcurrentHashMap<String, HashSet<String>>();
    public static Session Server;
    Gson gson = new GsonBuilder().create();

    public static int Count = 0;

    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {
        try {
            System.out.println("------------Close--------- : " + this.myToken);
            if (Clients.containsKey(this.myToken)) {
                // remove connection
                // remove from room
                if (ClientRooms != null && ClientRooms.size() > 0) {

                    ClientRooms.get(this.myToken).forEach(room -> {
                        Rooms.get(room).remove(this.myToken);

                        if (Rooms.get(room).size() <= 0) {
                            Rooms.remove(room);
                        }

                    });

                    ClientRooms.remove(this.myToken);
                }
                WebSocketHandler.Clients.remove(this.myToken);
                System.out.println("=======delete");
            }

            System.out.println("Close: statusCode=" + statusCode + ", reason=" + reason);
        } catch (Exception e) {
            System.out.println("=======delete error " + e.getMessage());
        }
    }

    @OnWebSocketError
    public void onError(Throwable t) {
        System.out.println("Error: " + t.getMessage());
    }

    @OnWebSocketConnect
    public void onConnect(Session session) {
        System.out.println("Connect: " + session.getRemoteAddress().getAddress());
        try {

            WebSocketHandler.Count += 1;
            this.session = session;
            // MyWebSocketHandler.Clients.put(this.myUniqueId, this.session);
            //System.out.println(session);
            session.getRemote().sendString("hi im web socket server");

        } catch (IOException e) {
            // e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }

    @OnWebSocketMessage
    public void onMessage(String message) {

        System.out.println("obj: " + message);
        // ------------------------------------------

        EventRequestModel eventObj = gson.fromJson(message, EventRequestModel.class);

        System.out.println("Message: " + "event ==>>> " + eventObj.getEvent());
        System.out.println("Message: " + "payload ==>>> " + (gson.toJson(eventObj.getPayload())));

        switch (eventObj.getEvent().toLowerCase()) {
            case "p2p":
                MessageEventModel objMsgP2P = gson.fromJson(gson.toJson(eventObj.getPayload()), MessageEventModel.class);
                System.out.println("my message: " + objMsgP2P.getMsg().toString());
                try {
                    String response = gson.toJson(objMsgP2P.getMsg());

                    Session user = Clients.get(objMsgP2P.getUser());
                    // send to server
                    if (Server != null && Server.isOpen()) {
                        Server.getRemote().sendString(response);
                    }

                    if (user != null && user.isOpen()) {
                        user.getRemote().sendString(response);
                        this.session.getRemote().sendString(response);
                    } else {
                        // send error to sender
                        this.session.getRemote().sendString("{\"Error\":\"not_found\"}");
                    }

                    System.out.println(objMsgP2P.getUser());
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                    // e1.printStackTrace();
                }

                break;

            case "room":

                try {
                    RoomRequestModel objMsgRoom = gson.fromJson(gson.toJson(eventObj.getPayload()), RoomRequestModel.class);
                    String responseRoom = gson.toJson(objMsgRoom.getMsg());
                    Rooms.get(objMsgRoom.getRoom()).forEach(user -> {
                        try {
                            Session userSession = Clients.get(user);
                            if (userSession != null && userSession.isOpen()) {
                                userSession.getRemote().sendString(responseRoom);
                            }
                        } catch (Exception e) {
                            //e.printStackTrace();
                            System.out.println(e.getMessage());
                        }
                    });
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
                break;

            case "broadcast":
                try {

                    String responseBroad = gson.toJson(eventObj.getPayload());
                    Clients.values().stream().parallel().filter(Session::isOpen).forEach(user -> {
                        try {
                            user.getRemote().sendString(responseBroad);
                        } catch (IOException e) {
                            //e.printStackTrace();
                            System.out.println(e.getMessage());
                        }
                    });
                } catch (Exception e) {
                }

                break;

            case "register":
                // current user register in connect
                RegisterRequestModel objMsgReg = gson.fromJson(gson.toJson(eventObj.getPayload()), RegisterRequestModel.class);

                myToken = objMsgReg.getToken();
                WebSocketHandler.Clients.put(objMsgReg.getToken(), this.session);

                System.out.println("register user");
                break;

            case "registerserver":
                // current user register in connect
                Server = this.session;
                System.out.println("----------------Server Register-------------------");
                break;

            // ---------------JOIN IN ROOM-------------
            case "join":

                JoinRequestModel objMsgJoin = gson.fromJson(gson.toJson(eventObj.getPayload()), JoinRequestModel.class);
                // User add to Room
                HashSet<String> room = Rooms.get(objMsgJoin.getRoom());
                if (room != null) {
                    room.add(objMsgJoin.getToken());
                } else {
                    HashSet<String> firstUser = new HashSet<String>();
                    firstUser.add(objMsgJoin.getToken());
                    Rooms.put(objMsgJoin.getRoom(), firstUser);
                }
                // ---------user add to clientUser for Find
                HashSet<String> croom = ClientRooms.get(objMsgJoin.getToken());
                if (croom != null) {
                    croom.add(objMsgJoin.getRoom());
                } else {
                    HashSet<String> firstRoom = new HashSet<String>();
                    firstRoom.add(objMsgJoin.getRoom());
                    ClientRooms.put(objMsgJoin.getToken(), firstRoom);
                }

                // pring room client
                System.out.println("=========Room users============");
                Rooms.get(objMsgJoin.getRoom()).forEach(System.out::println);
                System.out.println("++++++++++users Room+++++++++++++++");
                ClientRooms.get(objMsgJoin.getToken()).forEach(System.out::println);

                System.out.println("---------------join------------------");
                break;

            case "ping":
                try {
                    this.session.getRemote().sendString("hi");
                    System.out.println("ping");
                } catch (IOException e1) {
                }
                break;

            default:
                System.out.println("default last");
                break;
        }

    }

}
