package com.anagorny.chaters.server;

import com.anagorny.chaters.messege.Messege;
import com.anagorny.chaters.room.MainRoom;
import com.anagorny.chaters.room.Room;
import com.anagorny.chaters.user.User;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Created with IntelliJ IDEA.
 * User: sosnov
 * Date: 04.01.14
 * Time: 19:54
 * To change this template use File | Settings | File Templates.
 */
public class ClientThread extends Thread {
    private Socket cliSock;
    private User usr;
    private DataInputStream In;
    private DataOutputStream Out;

    public ClientThread(Socket client) {
        this.cliSock = client;
        this.start();

    }

    public void run() {
        try {
            this.Out = new DataOutputStream(this.cliSock.getOutputStream());
            this.In = new DataInputStream(this.cliSock.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        System.err.println("Client has connected from " + cliSock.getInetAddress().getAddress());
        while (true) {
            try {
                String Msg = this.In.readUTF();
                JSONParser prsr = new JSONParser();
                JSONObject jsonObj = (JSONObject) prsr.parse(Msg);
                int operation = ((Long) jsonObj.get(Messege.TYPE)).intValue();
                switch (operation) {
                    case Messege.MSG.TYPE_ID: {
                        System.err.println("Recieved msg");
                        SendToRoom(jsonObj);
                    }
                    ;
                    break;
                    case Messege.AUTH_MSG.TYPE_ID: {
                        authProcess(jsonObj);
                    }
                    ;
                    break;
                    case Messege.REG_MSG.TYPE_ID: {
                        regProcess(jsonObj);
                    }
                    ;
                    break;
                    case Messege.CRT_ROOM.TYPE_ID: {
                        regRoomProcess(jsonObj);
                    }
                    ;
                    break;
                    case Messege.USERS_IN_THE_ROOM_REQUEST.TYPE_ID: {
                        String RoomName = (String) jsonObj.get(Messege.ROOM_NAME);
                        JSONObject msg = new JSONObject();
                        msg.put(Messege.TYPE, Messege.USERS_IN_THE_ROOM.TYPE_ID);
                        JSONArray ar = new JSONArray();
                        ar.addAll(Server.GetUsrsName(Server.getUserInTheRoom(Server.getRoomFromName(RoomName))));
                        msg.put(Messege.ARRAY, ar);
                        sendToThisUser(msg);
                    }
                    ;
                    break;
                    case Messege.CONNECTED_TO_ROOM.TYPE_ID: {
                        String RoomName = (String) jsonObj.get(Messege.ROOM_NAME);
                        Server.addUsrToRoom(Server.getRoomFromName(RoomName).getRoom_id(), usr.getUser_id());
                        JSONObject msg = new JSONObject();
                        msg.put(Messege.TYPE, Messege.HAS_CONNECTED_TO_ROOM.TYPE_ID);
                        msg.put(Messege.ROOM_ID, Server.getRoomFromName(RoomName).getRoom_id());
                        msg.put(Messege.USR_NICK, this.usr.getUser_nick());
                        sendToThisUser(msg);
                        msg = new JSONObject();
                        msg.put(Messege.TYPE, Messege.SRV_MSG.TYPE_ID);
                        Server.addUsrToRoom(Server.getRoomFromName(RoomName).getRoom_id(), usr.getUser_id());
                        msg.put(Messege.SRV_MSG.TEXT, Server.getDate() + "Client " + usr.getUser_name() + " has be connected to room.");
                        msg.put(Messege.ROOM_ID, Server.getRoomFromName(RoomName).getRoom_id());
                        System.err.println(Server.getDate() + "Client " + usr.getUser_name() + " has be connected to room.");
                        SendToRoom(msg);
                    }
                    ;
                    break;
                }

            } catch (java.io.EOFException eofe) {
                System.err.println("Clients Socket " + cliSock.getInetAddress().getAddress().toString() + " has disconected!");
                if (usr != null) {
                    Server.UsrSock.put(usr, null);
                    Server.ClientsSockets.remove(this);
                    try {
                        this.In.close();
                        this.Out.close();
                        cliSock.close();
                        return;
                    } catch (IOException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.


            } catch (IOException ioe) {
                System.err.println("Error in Clients Socket");
                ioe.printStackTrace();

                return;

            }

        }
    }

    private void regRoomProcess(JSONObject msg) {
        //To change body of created methods use File | Settings | File Templates.
        for (Room i : Server.RoomsList) {
            if (i.getRoom_name().equals((String) msg.get(Messege.ROOM_NAME))) {
                System.err.println("Неверное имя комнаты!");
                JSONObject m = new JSONObject();
                m.put(Messege.TYPE, Messege.ERR_MSG.TYPE_ID);
                m.put(Messege.ERR_ID, Messege.ERR_MSG.UNCORRECT_ROOM_NAME);
                m.put(Messege.ERR_TEXT, "Комната с таким именем уже существует!");
                try {
                    this.Out.writeUTF(m.toJSONString());

                } catch (IOException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
        }

        Room nr = new Room((String) msg.get(Messege.ROOM_NAME), (String) msg.get(Messege.ROOM_DESCRIPT), (boolean) msg.get(Messege.ROOM_IS_OPENED));
        Server.createRoom(nr, usr.getUser_id());

        sendRoomList();
    }

    private void mesProcess(JSONObject msg) {
        for (long i : Server.RoomUsrs.get((Long) (msg.get(Messege.ROOM_ID)))) {
            try {
                this.Out.writeUTF(msg.toJSONString());
                this.Out.flush();
                //out.close();
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
    }

    private void regProcess(JSONObject msg) {
        for (User i : Server.UsersList) {
            if ((msg.get(Messege.USR_NICK)).equals(i.getUser_nick())) {
                System.err.println("ERR in regProcess: Данное имя пользователя уже используется");
                JSONObject m = new JSONObject();
                m.put(Messege.TYPE, Messege.ERR_MSG.TYPE_ID);
                m.put(Messege.ERR_ID, Messege.ERR_MSG.UNCORRECT_USER);
                m.put(Messege.ERR_TEXT, "Данное имя пользователя уже используется!");
                try {
                    this.Out.writeUTF(m.toJSONString());
                    // out.close();
                } catch (IOException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
                return;
            }
        }
        User new_usr = new User((String) msg.get(Messege.USR_NICK), (String) msg.get(Messege.USR_PASS), this);
        new_usr.setUser_name((String) msg.get(Messege.USR_NAME));
        new_usr.setUser_surname((String) msg.get(Messege.USR_SURNAME));
        new_usr.setUser_descript((String) msg.get(Messege.USR_DESCRIPT));
        Server.createUser(new_usr, cliSock);
        this.usr = new_usr;
        sendRoomList();
        sendUserList();
    }

    private void authProcess(JSONObject msg) {
        if (Server.UsersList.size() == 0) {
            JSONObject m = new JSONObject();
            m.put(Messege.TYPE, Messege.ERR_MSG.TYPE_ID);
            m.put(Messege.ERR_ID, Messege.ERR_MSG.UNCORRECT_USER);
            m.put(Messege.ERR_TEXT, "Неверное имя пользователя!");
            try {
                this.Out.writeUTF(m.toJSONString());
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
        for (User i : Server.UsersList) {
            if (i.getUser_nick().equals((String) msg.get(Messege.USR_NICK))) {
                if (i.getPass().equals((String) msg.get(Messege.USR_PASS))) {
                    this.usr = i;
                    Server.UsrSock.put(i, cliSock);
                    sendRoomList();
                    sendUserList();
                    break;
                } else {
                    JSONObject m = new JSONObject();
                    m.put(Messege.TYPE, Messege.ERR_MSG.TYPE_ID);
                    m.put(Messege.ERR_ID, Messege.ERR_MSG.UNCORRECT_PASS);
                    m.put(Messege.ERR_TEXT, "Неверный пароль!");
                    try {
                        this.Out.writeUTF(m.toJSONString());
                        // AppendingObjectOutputStream out = new AppendingObjectOutputStream(cliSock.getOutputStream());
                        //out.writeObject(m);
                        // out.flush();
                        // out.close();
                    } catch (IOException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }
                    return;
                }
            } else {
                JSONObject m = new JSONObject();
                m.put(Messege.TYPE, Messege.ERR_MSG.TYPE_ID);
                m.put(Messege.ERR_ID, Messege.ERR_MSG.UNCORRECT_USER);
                m.put(Messege.ERR_TEXT, "Неверное имя пользователя!");
                try {
                    this.Out.writeUTF(m.toJSONString());
                } catch (IOException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
                return;
            }


        }
    }

    private void sendRoomList() {
        JSONObject m = new JSONObject();
        m.put(Messege.TYPE, Messege.ROOM_ONLINE.TYPE_ID);
        JSONArray arr = new JSONArray();
        arr.addAll(Server.RoomsNameList);
        arr.remove(MainRoom.MAIN_ROOM_NAME);
        m.put(Messege.ARRAY, arr);
        //this.Out.writeUTF(m.toJSONString());
        SendToAll(m);
    }

    private void sendUserList() {
        JSONObject m = new JSONObject();
        m.put(Messege.TYPE, Messege.USRS_ONLINE.TYPE_ID);
        JSONArray arr = new JSONArray();
        arr.addAll(Server.GetUsrsName(Server.GetOnLineUsers()));
        m.put(Messege.ARRAY, arr);
        // this.Out.writeUTF(m.toJSONString());
        SendToAll(m);
    }

    private void sendToThisUser(JSONObject m) {
        try {
            this.Out.writeUTF(m.toJSONString());
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public void SendToRoom(JSONObject m) {
        long r_id = ((Long) m.get(Messege.ROOM_ID)).longValue();
        if (Server.RoomUsrs.get(r_id) != null) {
            for (Long i : Server.RoomUsrs.get(r_id)) {
                try {
                    Server.getUserFromId(i).getSock().Out.writeUTF(m.toJSONString());
                } catch (IOException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
        }
    }

    public void SendToAll(JSONObject m) {
        for (ClientThread i : Server.ClientsSockets) {
            try {
                i.Out.writeUTF(m.toJSONString());
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
    }

    public Socket getCliSock() {
        return cliSock;
    }

    public User getUsr() {
        return usr;
    }

    public DataInputStream getIn() {
        return In;
    }

    public DataOutputStream getOut() {
        return Out;
    }
}

