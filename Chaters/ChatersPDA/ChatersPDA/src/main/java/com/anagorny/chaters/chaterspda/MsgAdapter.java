package com.anagorny.chaters.chaterspda;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by sosnov on 02.03.14.
 */
public class MsgAdapter extends ArrayAdapter<MsgObj> {

    Context context;
    int layoutResourceId;
    ArrayList<MsgObj> data = null;

    public MsgAdapter(Context context, int layoutResourceId, ArrayList<MsgObj> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        MsgHolder holder = null;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new MsgHolder();
            holder.UserNick = (TextView) row.findViewById(R.id.UsrNick);
            holder.MsgText = (TextView) row.findViewById(R.id.TextMsg);

            row.setTag(holder);
        } else {
            holder = (MsgHolder) row.getTag();
        }

        MsgObj msgs = data.get(position);
        holder.UserNick.setText(msgs.getNick());
        holder.MsgText.setText(msgs.getText());

        return row;
    }

    static class MsgHolder {
        TextView UserNick;
        TextView MsgText;
    }
}