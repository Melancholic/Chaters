package com.anagorny.chaters.chaterspda;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.anagorny.chaters.messege.Messege;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ChatActivity extends Activity implements ChatersAcitity, View.OnClickListener {
    BroadcastReceiver Reciever;
    private String tag = "ChatActive: ";
    private SocketService service;
    private ServiceConnection sConn;
    public static final String BROADCAST_ACTION = "send_messege";
    boolean bound = false;
    private String RoomName;
    private long RoomID;
    private Intent intent;
    private Button sendBut;
    private TextView text;
    private String myName;
    private ArrayList<MsgObj> Messeges;
    private ListView ListMesseges;
    private MsgAdapter MsgAdptr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        //LayoutInflater inflater = LayoutInflater.from(this);
        //View page = inflater.inflate(R.layout.activity_chat, null);
        Intent intnt = getIntent();
        sendBut = (Button) findViewById(R.id.SendBut);
        sendBut.setOnClickListener(this);
        text = (TextView) findViewById(R.id.userOutText);
        RoomName = intnt.getStringExtra(Messege.ROOM_NAME);
        ListMesseges = (ListView) findViewById(R.id.listMsg);
        //  RoomID  = Long.decode(intent.getStringExtra(Messege.ROOM_ID));
        Messeges = new ArrayList<MsgObj>();
        MsgAdptr = new MsgAdapter(this, R.layout.msg, Messeges);
        ListMesseges.setAdapter(MsgAdptr);

        LayoutInflater inflater = LayoutInflater.from(this);
        View page = inflater.inflate(R.layout.activity_chat, null);
        this.ListMesseges = (ListView) page.findViewById(R.id.listMsg);
        Log.d(tag, "Value: " + (ListMesseges == null) + "   Adptr:   " + MsgAdptr);
        this.ListMesseges.setAdapter(MsgAdptr);

        Reciever = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int status = intent.getIntExtra(SocketService.TYPE, 0);
                switch (status) {
                    case SocketService.RECIEVE_MSG: {
                        try {
                            JSONObject msg = new JSONObject(intent.getStringExtra(SocketService.TEXT));
                            switch (((Integer) (msg.get(Messege.TYPE))).intValue()) {
                                case Messege.HAS_CONNECTED_TO_ROOM.TYPE_ID: {
                                    Log.d(tag, "HAS_CONNECTED_TO_ROOM");
                                    ChatActivity.this.RoomID = msg.getLong(Messege.ROOM_ID);
                                    myName = msg.getString(Messege.USR_NICK);
                                }
                                ;
                                break;
                                case Messege.MSG.TYPE_ID: {
                                    Log.d(tag, "MSG");
                                    Messeges.add(new MsgObj("by " + msg.getString(Messege.USR_NICK) + "  " + getDate(), msg.getString(Messege.MSG.TEXT)));
                                    MsgAdptr.notifyDataSetChanged();
                                    ListMesseges.setSelection(MsgAdptr.getCount() - 1);

                                }
                                ;
                                break;
                                case Messege.SRV_MSG.TYPE_ID: {
                                    Log.d(tag, "SRV_MSG");
                                    Messeges.add(new MsgObj("by Server " + getDate(), msg.getString(Messege.MSG.TEXT)));
                                    MsgAdptr.notifyDataSetChanged();
                                    ListMesseges.setSelection(MsgAdptr.getCount() - 1);

                                }
                                ;
                                break;

                                default:
                                    new ParseInputMsg(ChatActivity.this, msg);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

        ;
        IntentFilter intFilt = new IntentFilter(BROADCAST_ACTION);

        registerReceiver(Reciever, intFilt);


        this.intent = new

                Intent(getApplicationContext(), SocketService

                .class);
        this.sConn = new

                ServiceConnection() {
                    @Override
                    public void onServiceConnected(ComponentName name, IBinder binder) {
                        Log.d(tag, "MainActivity onServiceConnected");
                        Log.d(tag, "RERE  " + ((SocketService.LocalBinder) binder).getService());
                        ChatActivity.this.service = ((SocketService.LocalBinder) binder).getService();
                        Log.d(tag, "NUUUULLLLL " + (((SocketService.LocalBinder) binder).getService()) + "  " + binder + "  " + ChatActivity.this.intent);
                        bound = true;
                    }

                    public void onServiceDisconnected(ComponentName name) {
                        Log.d(tag, "MainActivity onServiceDisconnected");
                        bound = false;
                    }
                };

    }

    @Override
    protected void onResume() {
        super.onResume();
        getApplicationContext().bindService(this.intent, this.sConn, Context.BIND_AUTO_CREATE);
        startService(this.intent);

        new Thread(new Runnable() {
            @Override
            public void run() {
                sendBut.setEnabled(false);
                while (ChatActivity.this.service == null) {
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                sendBut.setEnabled(true);
                JSONObject msg = new JSONObject();
                try {
                    msg.put(Messege.TYPE, Messege.CONNECTED_TO_ROOM.TYPE_ID);
                    msg.put(Messege.ROOM_NAME, RoomName);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ChatActivity.this.service.WriteMsg(msg.toString());
            }
        }).start();


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.chat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void setUnknowList(ArrayList<? extends Object> arr, int type) {

    }

    @Override
    public void makeUserMsg(String text, int len) {
        Toast.makeText(this, text, len).show();
    }

    @Override
    public String getTag() {
        return tag;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (!bound) return;
        getApplicationContext().unbindService(sConn);
        bound = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //выключаем BroadcastReceiver
        unregisterReceiver(Reciever);
    }

    @Override
    public void onClick(View view) {
        Log.d(tag, "PK!!!");
        switch (view.getId()) {
            case R.id.SendBut: {
                JSONObject msg = new JSONObject();
                try {
                    msg.put(Messege.ROOM_ID, this.RoomID);
                    msg.put(Messege.TYPE, Messege.MSG.TYPE_ID);
                    msg.put(Messege.MSG.TEXT, this.text.getText().toString());
                    msg.put(Messege.USR_NICK, myName);
                    this.text.setText("");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                service.WriteMsg(msg.toString());
            }
            ;
            break;
        }

    }

    public long getRoomID() {
        return RoomID;
    }

    public void setRoomID(long roomID) {
        RoomID = roomID;
    }

    static public String getDate() {
        return "[" + new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(new Date()) + "]: ";
    }
}
