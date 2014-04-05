package com.anagorny.chaters.user;

import com.anagorny.chaters.server.ClientThread;

/**
 * Created with IntelliJ IDEA.
 * User: sosnov
 * Date: 04.01.14
 * Time: 19:12
 * To change this template use File | Settings | File Templates.
 */
public class User {
    private long user_id;
    private String user_name;
    private String user_surname;
    private String user_nick;
    private String user_descript;
    private String pass;
    private ClientThread sock;

    public User(String us_nick, String us_pass, ClientThread ct) {
        this.user_id = this.hashCode();
        this.user_nick = us_nick;
        this.pass = us_pass;
        this.sock = ct;
    }

    public long getUser_id() {
        return user_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public String getUser_surname() {
        return user_surname;
    }

    public String getUser_nick() {
        return user_nick;
    }

    public String getUser_descript() {
        return user_descript;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public void setUser_surname(String user_surname) {
        this.user_surname = user_surname;
    }

    public void setUser_nick(String user_nick) {
        this.user_nick = user_nick;
    }

    public void setUser_descript(String user_descript) {
        this.user_descript = user_descript;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public ClientThread getSock() {
        return sock;
    }
}
