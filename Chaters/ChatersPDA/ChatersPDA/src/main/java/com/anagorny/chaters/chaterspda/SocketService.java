package com.anagorny.chaters.chaterspda;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class SocketService extends Service {
    private String server;
    private String port;
    public static final String PORT = "port";
    public static final String SERVER = "server";
    private static final String tag = "SocetService_logs:";
    public static final String TYPE = "type";
    public static final String TEXT = "text";
    public static final int RECIEVE_MSG = 1;
    public final SocketService instance = this;
    private Handler h;
    private ConnectedThread ConThread;
    Socket s;
    LocalBinder binder = new LocalBinder();


    final IBinder myBinder = new LocalBinder();

    class LocalBinder extends Binder {
        public SocketService getService() {
            return SocketService.this;
        }
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return myBinder;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        s = new Socket();
        h = new Handler() {
            public void handleMessage(android.os.Message msg) {
                switch (msg.what) {
                    case RECIEVE_MSG: {
                        Intent intent = new Intent(MainActivity.BROADCAST_ACTION);
                        intent.putExtra(TYPE, RECIEVE_MSG);
                        intent.putExtra(TEXT, (String) msg.obj);
                        sendBroadcast(intent);
                    }
                }
            }
        };
    }

    public void IsBoundable() {
        Toast.makeText(this, "I bind like butter", Toast.LENGTH_SHORT).show();
    }

 /*   public void onStartCommand(Intent intent, int startId) {
        Toast.makeText(this, "Service created ...", Toast.LENGTH_LONG).show();
        port = intent.getStringExtra(PORT);
        server = intent.getStringExtra(SERVER);
        Runnable connect = new ConnectedThread();
        new Thread(connect).start();
    }*/

    public int onConnect(String p, String adr) {
        Toast.makeText(this, "Connection created ...", Toast.LENGTH_SHORT).show();
        port = p;
        server = adr;
        //Runnable connect = new ConnectedThread();
        ConThread = new ConnectedThread();
        new Thread(ConThread).start();
        final long timeout = (long) Math.pow(10, 9) * 5;
        long start = System.nanoTime();
        while (ConThread.getCliSocket() == null) {
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
        if (ConThread.getCliSocket() == null) {
            return 0;
        } else {
            return 1;
        }
    }

    public void WriteMsg(String msg) {
        //   Toast.makeText(this, "Write msg ...", Toast.LENGTH_LONG).show();
        ConThread.write(msg);
    }

    private class ConnectedThread implements Runnable {
        private Socket cliSocket;
        private DataInputStream InStream;
        private DataOutputStream OutStream;

        public void run() {
            try {
                cliSocket = new Socket(server, Integer.decode(port));
                this.InStream = new DataInputStream(cliSocket.getInputStream());
                this.OutStream = new DataOutputStream(cliSocket.getOutputStream());

                while (true) {

                    String msg = this.InStream.readUTF();
                    if (msg != null) {
                        Log.d(SocketService.tag, "Получено msg:\n" + msg);
                        h.obtainMessage(RECIEVE_MSG, msg).sendToTarget();
                    }

                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        /* Call this from the main activity to send data to the remote device */
        public void write(String message) {
            Log.d(tag, "...Данные для отправки: " + message + "...");
            try {
                this.OutStream.writeUTF(message);
            } catch (IOException e) {
                Log.d(tag, "...Ошибка отправки данных: " + e.getMessage() + "...");
            }
        }

        /* Call this from the main activity to shutdown the connection */
        public void cancel() {
            try {
                cliSocket.close();
            } catch (IOException e) {
            }
        }

        public Socket getCliSocket() {
            return cliSocket;
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            s.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        s = null;
    }
}