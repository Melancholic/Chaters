package com.anagorny.chaters.messege;

/**
 * Created with IntelliJ IDEA.
 * User: sosnov
 * Date: 04.01.14
 * Time: 19:00
 * To change this template use File | Settings | File Templates.
 */

public class Messege {
    public static final String USR_NICK = "user_nick";
    public static final String USR_PASS = "user_password";
    public static final String USR_NAME = "user_name";
    public static final String USR_SURNAME = "user_surname";
    public static final String USR_DESCRIPT = "user_descript";
    public static final String USR_ID = "user_id";
    public static final String ERR_ID = "error_id";
    public static final String ERR_TEXT = "error_text";
    public static final String TYPE = "type";
    public static final String ROOM_ID = "room_id";
    public static final String ROOM_NAME = "room_name";
    public static final String ROOM_DESCRIPT = "room_descript";
    public static final String ROOM_IS_OPENED = "room_is_opened";
    public static final String ARRAY = "array";

    public static class AUTH_MSG {
        public static final int TYPE_ID = 0;
    }

    public static class REG_MSG {
        public static final int TYPE_ID = 1;
    }

    public static class MSG {
        public static final int TYPE_ID = 2;
        public static final String TEXT = "text";
    }

    public static class ERR_MSG {
        public static final int TYPE_ID = 3;
        public static final int UNCORRECT_USER = 0;
        public static final int UNCORRECT_PASS = 1;
        public static final int UNCORRECT_ROOM_NAME = 2;
    }

    public static class PING_MSG {
        public static final int TYPE_ID = 4;
    }

    public static class ROOM_ONLINE {
        public static final int TYPE_ID = 5;
    }

    public static class USRS_ONLINE {
        public static final int TYPE_ID = 6;
    }

    public static class CRT_ROOM {
        public static final int TYPE_ID = 7;
    }

    public static class USERS_IN_THE_ROOM_REQUEST {
        public static final int TYPE_ID = 8;
    }

    public static class USERS_IN_THE_ROOM {
        public static final int TYPE_ID = 9;
    }

    public static class CONNECTED_TO_ROOM {
        public static final int TYPE_ID = 10;
    }
    public static class SRV_MSG extends  MSG{
        public static final int TYPE_ID = 11;
    }

    public static class HAS_CONNECTED_TO_ROOM{
        public static final int TYPE_ID = 12;
    }
}