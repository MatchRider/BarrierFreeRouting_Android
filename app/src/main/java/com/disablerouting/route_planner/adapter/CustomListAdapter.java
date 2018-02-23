package com.disablerouting.route_planner.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;
import com.disablerouting.R;
import com.disablerouting.geo_coding.model.Features;

import java.util.ArrayList;
import java.util.List;

public class CustomListAdapter extends ArrayAdapter {

    private List<Features> dataList;
    private Context mContext;
    private int itemLayout;

    private ListFilter listFilter = new ListFilter();
    private List<Features> dataListAllItems;



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

    @NonNull
    @Override
    public Filter getFilter() {
        return listFilter;
    }

    public class ListFilter extends Filter {
        private Object lock = new Object();

        @Override
        protected FilterResults performFiltering(CharSequence prefix) {
            FilterResults results = new FilterResults();
            if (dataListAllItems == null) {
                synchronized (lock) {
                    dataListAllItems = new ArrayList<Features>(dataList);
                }
            }

            if (prefix == null || prefix.length() == 0) {
                synchronized (lock) {
                    results.values = dataListAllItems;
                    results.count = dataListAllItems.size();
                }
            } else {
                final String searchStrLowerCase = prefix.toString().toLowerCase();
                ArrayList<String> matchValues = new ArrayList<String>();

                for (Features dataItem : dataListAllItems) {
                    if (dataItem.getProperties().toString().startsWith(searchStrLowerCase)) {
                        matchValues.add(dataItem.getProperties().toString());
                    }
                }

                results.values = matchValues;
                results.count = matchValues.size();
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if (results.values != null) {
                dataList = (ArrayList<Features>)results.values;
            } else {
                dataList = null;
            }
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }

    }
} 