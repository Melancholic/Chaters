package com.anagorny.chaters.chaterspda;

import android.util.Log;
import android.widget.Toast;

import com.anagorny.chaters.messege.Messege;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by sosnov on 04.02.14.
 */
public class ParseInputMsg {
    private ChatersAcitity main;
    private JSONObject msg;

    public ParseInputMsg(ChatersAcitity obj, JSONObject m) {
        this.main = obj;
        this.msg = m;
        parse();
    }

    public void parse() {
        if (this.msg != null) {
            int operation = 0;
            try {
                operation = (Integer) this.msg.get(Messege.TYPE);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            switch (operation) {
                case Messege.ROOM_ONLINE.TYPE_ID: {
                    try {
                        ArrayList<String> ar = JSONArrayTOArrayList(this.msg.getJSONArray(Messege.ARRAY));
                        main.setUnknowList(ar, ChatersAcitity.ROOMS_ONLINE);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.d(main.getTag(), "msg - получение списка комнат");
                }
                ;
                break;
                case Messege.USRS_ONLINE.TYPE_ID: {
                /*    try {
                        main.makeAllUsrsList(JSONArrayTOArrayList(this.msg.getJSONArray(Messege.ARRAY)));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }*/
                    try {
                        main.setUnknowList(JSONArrayTOArrayList(this.msg.getJSONArray(Messege.ARRAY)), ChatersAcitity.USRS_ONLINE);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.d(main.getTag(), "msg - получение пользователей онлайн");
                }
                ;
                break;
                case Messege.ERR_MSG.TYPE_ID: {
                    try {
                        main.makeUserMsg((String) this.msg.get(Messege.ERR_TEXT), Toast.LENGTH_LONG);
                        Log.d(main.getTag(), "msg - сообщение об ошибке:\n" + (String) this.msg.get(Messege.ERR_TEXT));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                ;
                break;
                case Messege.USERS_IN_THE_ROOM.TYPE_ID: {

                    try {
                        main.setUnknowList(JSONArrayTOArrayList(this.msg.getJSONArray(Messege.ARRAY)), ChatersAcitity.USRS_IN_THE_ROOM);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.d(main.getTag(), "msg - получение списка полбзователей в комнате");
                }
                ;
                break;
            }
        }

    }

    public static ArrayList<String> JSONArrayTOArrayList(JSONArray arr) {
        ArrayList<String> res = new ArrayList<String>();
        if (arr != null) {
            for (int i = 0; i < arr.length(); i++) {
                try {
                    res.add(arr.get(i).toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return res;
    }
}
