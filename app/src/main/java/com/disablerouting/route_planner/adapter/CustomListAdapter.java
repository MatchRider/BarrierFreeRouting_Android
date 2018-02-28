package com.disablerouting.route_planner.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.disablerouting.R;
import com.disablerouting.geo_coding.model.Features;

import java.util.List;

public class CustomListAdapter extends ArrayAdapter {

    private List<Features> dataList;
    private Context mContext;
    private int itemLayout;

    public CustomListAdapter(Context context, int resource, List<Features> storeDataLst) {
        super(context, resource, storeDataLst);
        dataList = storeDataLst;
        mContext = context;
        itemLayout = resource;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public String getItem(int position) {
        Log.d("CustomListAdapter", dataList.get(position).getProperties().toString());
        return dataList.get(position).getProperties().toString();
    }

    @NonNull
    @Override
    public View getView(int position, View view, @NonNull ViewGroup parent) {

        if (view == null) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(itemLayout, parent, false);
        }
        TextView strName = (TextView) view.findViewById(R.id.txt_header);
        strName.setText(dataList.get(position).getProperties().getName());

        TextView strDes = (TextView) view.findViewById(R.id.txt_desc);
        strDes.setText(getItem(position));


        return view;
    }

} 