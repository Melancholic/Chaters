package com.anagorny.chaters.chaterspda;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.view.ViewPager;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.anagorny.chaters.messege.Messege;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity implements View.OnClickListener, ChatersAcitity {
    public static final String HANDLER_KEY = "handler";
    public static final String CONNECT_THRREAD_KEY = "connect_thread";
    private static Intent ChatActiv;
    private Button conBut;
    private EditText serv;
    private EditText port;
    private Socket client;
    private CheckBox reg;
    private LinearLayout RegArea;
    private Button Auth;
    private final int AuthID = 1000;
    private Button Reg;
    private final int RegID = 1001;
    private EditText nick;
    private EditText pass;
    private EditText uName;
    private EditText uSurName;
    private EditText uDescript;
    private ListView lRooms;
    private ListView lUsrInRooms;
    private ListView lAllUsrs;
    public static final String tag = "MainActivity_logs:";
    public static final String BROADCAST_ACTION = "send_messege";
    BroadcastReceiver Reciever;
    public final static int RECIEVE_MESSAGE = 1;
    private Button registerRoom;
    private SocketService service;
    private ServiceConnection sConn;
    private Intent intent;
    boolean bound = false;
    private ArrayList<Integer> RoomsIDList;
    private ArrayList<String> RoomsNameList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // h = new ParseInputMsg(this);
        RoomsIDList = new ArrayList<Integer>();
        RoomsNameList = new ArrayList<String>();
        List<View> pages = new ArrayList<View>();
        LayoutInflater inflater = LayoutInflater.from(this);/*(LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);*/
        View page = inflater.inflate(R.layout.one, null);
        nick = new EditText(this);
        nick.setHint("Nick");
        nick.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
        pass = new EditText(this);
        pass.setHint("Password");
        pass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        uName = new EditText(this);
        uName.setHint("User Name");
        uName.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
        uSurName = new EditText(this);
        uSurName.setHint("User SurName");
        uSurName.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
        uDescript = new EditText(this);
        uDescript.setHint("User's Descript");
        serv = (EditText) page.findViewById(R.id.serv);
        port = (EditText) page.findViewById(R.id.port);
        conBut = (Button) page.findViewById(R.id.conBut2);
        Log.d(tag, (conBut.getText().toString()));
        conBut.setOnClickListener(this);
        reg = (CheckBox) page.findViewById(R.id.regRad);
        reg.setOnClickListener(this);
        RegArea = (LinearLayout) page.findViewById(R.id.regArea);
        ConPageToStart();
        pages.add(page);

        page = inflater.inflate(R.layout.two, null);
        lRooms = (ListView) page.findViewById(R.id.Rooms);
        lRooms.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           int position, long id) {
                MainActivity.ChatActiv = new Intent(MainActivity.this, ChatActivity.class);
                //ChatActiv.putExtra(Messege.ROOM_ID,MainActivity.this.getRoomsIDList().get(position));
                ChatActiv.putExtra(Messege.ROOM_NAME, MainActivity.this.getRoomsNameList().get(position));
                startActivity(ChatActiv);

                return false;
            }
        });
        lRooms.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                JSONObject msg = new JSONObject();
                try {
                    msg.put(Messege.TYPE, Messege.USERS_IN_THE_ROOM_REQUEST.TYPE_ID);
                    msg.put(Messege.ROOM_NAME, RoomsNameList.get(i));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                service.WriteMsg(msg.toString());

            }
        });
        lUsrInRooms = (ListView) page.findViewById(R.id.UsrInRoom);
        lAllUsrs = (ListView) page.findViewById(R.id.AllUsrs);
        registerRoom = (Button) page.findViewById(R.id.crtRmsBut);
        registerRoom.setOnClickListener(this);
        pages.add(page);

        page = inflater.inflate(R.layout.three, null);
        pages.add(page);


        SamplePagerAdapter pagerAdapter = new SamplePagerAdapter(pages);
        ViewPager viewPager = new ViewPager(this);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(0);

        setContentView(viewPager);

        Reciever = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int status = intent.getIntExtra(SocketService.TYPE, 0);
                Log.d(tag, "Получено msg - id: " + status);
                switch (status) {
                    case SocketService.RECIEVE_MSG: {
                        try {
                            JSONObject msg = new JSONObject(intent.getStringExtra(SocketService.TEXT));
                            new ParseInputMsg(MainActivity.this, msg);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        };
        IntentFilter intFilt = new IntentFilter(BROADCAST_ACTION);
        registerReceiver(Reciever, intFilt);
        intent = new Intent(getApplicationContext(), SocketService.class);
        sConn = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder binder) {
                Log.d(tag, "MainActivity onServiceConnected");
                service = ((SocketService.LocalBinder) binder).getService();
                bound = true;
            }

            public void onServiceDisconnected(ComponentName name) {
                Log.d(tag, "MainActivity onServiceDisconnected");
                bound = false;
            }
        };


    }

    public ArrayList<Integer> getRoomsIDList() {
        return RoomsIDList;
    }

    public ArrayList<String> getRoomsNameList() {
        return RoomsNameList;
    }

    public void setRoomsIDList(ArrayList<Integer> roomsIDList) {
        RoomsIDList = roomsIDList;
    }

    public void setRoomsNameList(ArrayList<String> roomsNameList) {
        RoomsNameList = roomsNameList;
    }


    @Override
    protected void onResume() {
        super.onResume();
        getApplicationContext().bindService(intent, sConn, Context.BIND_AUTO_CREATE);
        getApplicationContext().startService(intent);
    }

    @Override
    protected void onPause() {
        super.onStop();
        if (!bound) return;
        Log.d(tag, "MainActivity Paused");
        getApplicationContext().unbindService(sConn);
        bound = false;
    }


    private void ConPageToStart() {
        conBut.setEnabled(true);
        port.setEnabled(true);
        serv.setEnabled(true);
        reg.setEnabled(false);
    }

    private void AuthPageToStart() {
        conBut.setEnabled(true);
        port.setEnabled(true);
        serv.setEnabled(true);
        reg.setEnabled(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void RegOrAuthViewes(boolean status) {
        if (!status) {
            RegArea.removeAllViews();
            RegArea.addView(nick);
            RegArea.addView(pass);
            Auth = new Button(this);
            Auth.setText("Authorization");
            Auth.setOnClickListener(this);
            Auth.setId(AuthID);
            RegArea.addView(Auth);

        } else {
            RegArea.removeAllViews();
            RegArea.addView(nick);
            RegArea.addView(pass);
            RegArea.addView(uName);
            RegArea.addView(uSurName);
            RegArea.addView(uDescript);
            Reg = new Button(this);
            Reg.setText("Registration");
            Reg.setOnClickListener(this);
            Reg.setId(RegID);
            RegArea.addView(Reg);

        }
    }

    @Override
    public void onClick(View view) {
        Log.d(tag, "Я вызван!!");
        switch (view.getId()) {
            case R.id.conBut2: {
                if (port.getText().toString().equals("") || serv.getText().toString().equals("")) {
                    makeUserMsg("Заполните все поля!", Toast.LENGTH_LONG);
                    return;
                } else {
                 /*   ConThread = new ConnectedThread();
                    Thread thread = new Thread(ConThread);
                    thread.start();*/
                  /*  service = new Intent(this, SocketService.class);
                    service.putExtra(SERVER, serv.getText().toString());
                    service.putExtra(PORT, port.getText().toString());*/
                    conBut.setEnabled(false);
                    port.setEnabled(false);
                    serv.setEnabled(false);
                    int res = service.onConnect(port.getText().toString(), serv.getText().toString());
                    if (res == 0) {
                        makeUserMsg("Соединение не установлено!", Toast.LENGTH_SHORT);
                        ConPageToStart();
                        return;
                    } else {
                        // final long timeout = (long) Math.pow(10, 9) * 5;
                        // long start = System.nanoTime();
                    /*while (client == null) {
                        try {
                            Thread.sleep(10);

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        long stop = System.nanoTime();
                        if (Math.abs(start - stop) > timeout) {
                            break;
                        }
                    }
                    if (client == null) {
                        makeUserMsg("Соединение не установлено!", Toast.LENGTH_SHORT);
                        ConPageToStart();
                        return;
                    }*/
                        makeUserMsg("Соединение установлено!", Toast.LENGTH_SHORT);
                        reg.setEnabled(true);
                        RegOrAuthViewes(reg.isSelected());
                    }
                }
            }
            break;
            case R.id.regRad: {
                RegOrAuthViewes(reg.isChecked());
            }
            ;
            break;

            case AuthID: {
                if (nick.getText().toString().equals("") || pass.getText().toString().equals("")) {
                    makeUserMsg("Заполните все поля!", Toast.LENGTH_LONG);
                    return;
                } else {
                    Log.d(tag, "NicK: " + nick.getText() + " Pass: " + pass.getText());
                    JSONObject auth = new JSONObject();
                    try {
                        auth.put(Messege.TYPE, Messege.AUTH_MSG.TYPE_ID);
                        auth.put(Messege.USR_NICK, nick.getText().toString());
                        auth.put(Messege.USR_PASS, pass.getText().toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    service.WriteMsg(auth.toString());
                }
            }
            ;
            break;
            case RegID: {
                if (nick.getText().toString().equals("") || pass.getText().toString().equals("")) {
                    makeUserMsg("\"Nick\" и \"Password\"\n обязательны для заполнения!", Toast.LENGTH_LONG);
                    return;
                } else {
                    Log.d(tag, "NicK: " + nick.getText() + " Pass: " + pass.getText());
                    JSONObject reg = new JSONObject();
                    try {
                        reg.put(Messege.TYPE, Messege.REG_MSG.TYPE_ID);
                        reg.put(Messege.USR_NICK, nick.getText().toString());
                        reg.put(Messege.USR_PASS, pass.getText().toString());
                        reg.put(Messege.USR_NAME, uName.getText().toString());
                        reg.put(Messege.USR_SURNAME, uSurName.getText().toString());
                        reg.put(Messege.USR_DESCRIPT, uDescript.getText().toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    service.WriteMsg(reg.toString());

                }
            }
            ;
            break;
            case R.id.crtRmsBut: {
                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setTitle("Создание новой комнаты");
                alert.setMessage("Введите имя комнаты:");
                final EditText input = new EditText(this);
                alert.setView(input);
                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String value = input.getText().toString();
                        JSONObject nm = new JSONObject();
                        try {
                            nm.put(Messege.TYPE, Messege.CRT_ROOM.TYPE_ID);
                            nm.put(Messege.ROOM_NAME, value);
                            nm.put(Messege.ROOM_DESCRIPT, "test");
                            nm.put(Messege.ROOM_IS_OPENED, true);
                            service.WriteMsg(nm.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Canceled.
                    }
                });

                alert.show();
            }
            break;
         /*   case R.id.Rooms: {
                ChatActiv=new Intent(this,ChatActivity.class);


                startActivity(ChatActiv);
            }*/


        }
    }


    @Override
    public void setUnknowList(ArrayList<? extends Object> ar, int type) {
        switch (type) {
            case ChatersAcitity.USRS_ONLINE: {
                ArrayList<String> arr = new ArrayList<String>();
                for (Object i : ar) {
                    arr.add(String.valueOf(i));
                }
                makeAllUsrsList(arr);
            }
            ;
            break;
            case ChatersAcitity.ROOMS_ONLINE: {
                ArrayList<String> arr = new ArrayList<String>();
                for (Object i : ar) {
                    arr.add(String.valueOf(i));
                }
                setRoomsNameList(arr);
                makeRoomsList(arr);
            }
            ;
            break;
            case ChatersAcitity.USRS_IN_THE_ROOM: {
                ArrayList<String> arr = new ArrayList<String>();
                for (Object i : ar) {
                    arr.add(String.valueOf(i));
                }
                makeUsrsinRoomList(arr);
            }
            ;
            break;

        }

    }

    @Override
    public void makeUserMsg(String txt, int dur) {
        Toast.makeText(this, txt, dur).show();
    }

    @Override
    public String getTag() {
        return tag;
    }

    protected void makeRoomsList(ArrayList<String> arr) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arr);
        lRooms.setAdapter(adapter);
    }


    protected void makeAllUsrsList(ArrayList<String> arr) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arr);
        lAllUsrs.setAdapter(adapter);
    }

    protected void makeUsrsinRoomList(ArrayList<String> arr) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arr);
        lUsrInRooms.setAdapter(adapter);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        // дерегистрируем (выключаем) BroadcastReceiver
        unregisterReceiver(Reciever);
    }

}



