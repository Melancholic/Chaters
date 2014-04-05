package com.anagorny.chaters.server;

import com.anagorny.chaters.room.MainRoom;
import com.anagorny.chaters.room.Room;
import com.anagorny.chaters.user.User;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: sosnov
 * Date: 04.01.14
 * Time: 18:10
 * To change this template use File | Settings | File Templates.
 */
public class Server {
    public static ArrayList<Long> UsersIDList;
    public static ArrayList<String> UsersNickList;
    public static Set<User> UsersList;
    public static HashMap<User, Socket> UsrSock;
    public static HashMap<Long, ArrayList<Long>> RoomUsrs;
    public static Set<Room> RoomsList;
    public static ArrayList<String> RoomsNameList;
    protected static ArrayList<ClientThread> ClientsSockets;
    private String date;

    public static void main(String[] args) {
        //список всех пользователей
        UsersList = new HashSet<>();
        UsersNickList = new ArrayList<>();
        UsrSock = new HashMap<>();
        RoomUsrs = new HashMap<>();
        UsersIDList = new ArrayList<>();
        RoomsNameList = new ArrayList<>();
        RoomsList = new HashSet<>();
        ClientsSockets = new ArrayList<>();
        try {
            createMainRoom();
            ServerSocket Socket = new ServerSocket(Config.PORT());
            System.err.println("Worked at " + Config.PORT() + " port ...");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        System.err.printf("\n\n\nnull%n");
                        System.err.println(getDate());
                        Calendar.getInstance();
                        System.err.println("Register user: ");
                        for (User i : UsersList) {
                            System.err.println("\t " + i.getUser_id() + " " + i.getUser_nick() + ":" + i.getPass());
                        }

                        System.err.println("On-line user: ");
                        for (User i : Server.GetOnLineUsers()) {
                            System.err.println("\t " + i.getUser_id() + " " + i.getUser_nick() + ":" + i.getPass());
                        }

                        System.err.println("Register room: ");
                        for (Room i : RoomsList) {
                            System.err.println("\t " + i.getRoom_id() + " " + i.getRoom_name());
                        }

                        System.err.println("In room: ");
                        for (Room i : RoomsList) {
                            System.err.println("\t " + i.getRoom_id() + " " + i.getRoom_name() + ":");
                            for (User j : getUserInTheRoom(i)) {
                                System.err.println("\t " + j.getUser_id() + " " + j.getUser_nick() + ":" + j.getPass());
                            }
                        }

                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                        }
                    }
                }
            }).start();
            while (true) {
                Socket client = null;
                do {
                    System.err.println("Waiting for connection ...");
                    client = Socket.accept();
                } while (client == null);
                ClientsSockets.add(new ClientThread(client));

            }
        } catch (SocketException se) {
            System.err.println("Socket Exception while created socket!");
            se.printStackTrace();
        } catch (IOException ioe) {
            System.err.println("IO Exception while created socket!");
            ioe.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public static ArrayList<User> GetOnLineUsers() {
        ArrayList<User> res = new ArrayList<>();
        for (User i : UsrSock.keySet()) {
            if (UsrSock.get(i) != null) {
                res.add(i);
            }
        }
        return res;
    }

    public static ArrayList<String> GetUsrsName(ArrayList<User> arr) {
        ArrayList<String> res = new ArrayList<>();
        for (User i : arr) {
            res.add(i.getUser_nick());
        }
        return res;
    }

    public static void createRoom(Room nr, long userid) {
        Server.RoomsList.add(nr);
        Server.RoomsNameList.add(nr.getRoom_name());
        ArrayList<Long> users = new ArrayList<Long>();
        users.add(userid);
        Server.RoomUsrs.put(nr.getRoom_id(), users);
    }

    public static void addUsrToRoom(long roomId, long userId) {
        //Server.RoomUsrs.get(roomId).add(userId);
        if (Server.RoomUsrs.get(roomId).indexOf(userId) == -1) {
            Server.RoomUsrs.get(roomId).add(userId);
        }
    }

    private static void createMainRoom() {
        MainRoom nr = new MainRoom();
        Server.RoomsList.add(nr);
        Server.RoomsNameList.add(nr.getRoom_name());
        ArrayList<Long> users = new ArrayList<Long>();
        Server.RoomUsrs.put(nr.getRoom_id(), users);
    }

    public static void createUser(User usr, Socket cliSock) {
        Server.UsersList.add(usr);//В общий список
        Server.UsrSock.put(usr, cliSock);//В список с сокетами(онлайн)
        Server.UsersNickList.add(usr.getUser_nick()); //Юзер-ник
        Server.UsersIDList.add(usr.getUser_id());   // Юзер-ID
        Server.RoomUsrs.get(MainRoom.MAIN_ROOM_ID).add(usr.getUser_id());
    }

    public static ArrayList<User> getUserInTheRoom(Room rm) {
        ArrayList<User> res = new ArrayList<>();
        for (long i : RoomUsrs.get(rm.getRoom_id())) {
            User usr = getUserFromId(i);
            if (usr != null) {
                res.add(usr);
            }
        }
        return res;
    }

    public static User getUserFromId(long id) {
        for (User i : UsersList) {
            if (i.getUser_id() == id) {
                return i;
            }
        }
        return null;
    }

    public static Room getRoomFromId(long id) {
        for (Room i : RoomsList) {
            if (i.getRoom_id() == id) {
                return i;
            }
        }
        return null;
    }

    public static Room getRoomFromName(String name) {
        for (Room i : RoomsList) {
            if (i.getRoom_name().equals(name)) {
                return i;
            }
        }
        return null;
    }

    static public String getDate() {
        return "[" + new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(new Date()) + "]: ";
    }
}
