package com.parsin.bletool.internal.wifi;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.parsin.bletool.R;
import com.parsin.bletool.Model.database.Event;
import com.parsin.bletool.Model.database.InternalDataBase;

import java.util.List;



public class WifiArrayAdapter extends ArrayAdapter<WifiObject> {

    private static final String TAG = WifiArrayAdapter.class.getSimpleName();
    private InternalDataBase db;

    // Constructor
    public WifiArrayAdapter(Context mContext, int layoutResourceId, List<WifiObject> objects) {
        super(mContext, layoutResourceId, objects);
        db = new InternalDataBase(mContext);
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final WifiObject wifiItem = getItem(position);

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.wifi_list_item, parent, false);
        }

        // Getting UI components
        final TextView wifiName = (TextView) convertView.findViewById(R.id.wifiName);
        TextView wifiGroup = (TextView) convertView.findViewById(R.id.fieldGrpName);
        TextView wifiUser = (TextView) convertView.findViewById(R.id.fieldUsrName);
        RelativeLayout relativeLayout = (RelativeLayout) convertView.findViewById(R.id.relativeLayout);

        final Event event = new Event();

        // Setting UI components
        wifiName.setText(wifiItem.wifiName);
        event.setWifiName(wifiItem.wifiName);

        wifiGroup.setText(wifiItem.grpName);
        event.setWifiGroup(wifiItem.grpName);

        wifiUser.setText(wifiItem.userName);
        event.setWifiUser(wifiItem.userName);

/*
        relativeLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                db.deleteRecord(event);
                remove(wifiItem);
                return false;
            }
        });
*/


        return convertView;
    }
}
