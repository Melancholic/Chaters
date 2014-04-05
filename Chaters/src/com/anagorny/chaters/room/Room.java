package com.anagorny.chaters.room;

import com.anagorny.chaters.user.User;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: sosnov
 * Date: 04.01.14
 * Time: 19:07
 * To change this template use File | Settings | File Templates.
 */
public class Room {
    private long room_id;
    private String room_name;
    private String room_descript;
    private boolean room_open;
    private ArrayList<com.anagorny.chaters.user.User> users_list;

    public Room(String R_n, String R_d, boolean r_op) {
        this.room_id = this.hashCode();
        this.room_name = R_n;
        this.room_descript = R_d;
        this.room_open = r_op;
    }

    public long getRoom_id() {
        return room_id;
    }

    public String getRoom_name() {
        return room_name;
    }

    public String getRoom_descript() {
        return room_descript;
    }

    public boolean isRoom_open() {
        return room_open;
    }

    public ArrayList<User> getUsers_list() {
        return users_list;
    }
}

