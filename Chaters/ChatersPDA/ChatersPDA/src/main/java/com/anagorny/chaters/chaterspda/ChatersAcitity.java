package com.anagorny.chaters.chaterspda;

import java.util.ArrayList;

/**
 * Created by sosnov on 15.02.14.
 */
public interface ChatersAcitity {
    public void setUnknowList(ArrayList<? extends Object> arr, int type);

    public void makeUserMsg(String text, int len);

    public static final int USRS_ONLINE = 0;
    public static final int ROOMS_ONLINE = 1;
    public static final int USRS_IN_THE_ROOM = 2;

    public String getTag();

}
