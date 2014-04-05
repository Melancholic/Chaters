package com.anagorny.chaters.chaterspda;

/**
 * Created by sosnov on 02.03.14.
 */
public class MsgObj {
    private String nick;
    private String text;

    public MsgObj() {
        super();
    }

    public MsgObj(String name, String txt) {
        super();
        this.nick = name;
        this.text = txt;
    }

    public String getNick() {
        return nick;
    }

    public String getText() {

        return text;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public void setText(String text) {
        this.text = text;
    }
}



