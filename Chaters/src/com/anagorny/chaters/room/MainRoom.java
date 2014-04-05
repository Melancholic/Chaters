package com.anagorny.chaters.room;

/**
 * Created with IntelliJ IDEA.
 * User: sosnov
 * Date: 08.02.14
 * Time: 15:45
 * To change this template use File | Settings | File Templates.
 */
public class MainRoom extends Room {
    private long room_id;
    public static final long MAIN_ROOM_ID = -1;
    public static final String MAIN_ROOM_NAME = "main_room";

    public MainRoom() {
        super(MAIN_ROOM_NAME, MAIN_ROOM_NAME, true);
        this.room_id = MAIN_ROOM_ID;
    }

    public long getRoom_id() {
        return room_id;
    }
}
