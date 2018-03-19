package com.disablerouting.capture_option;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;
import com.disablerouting.R;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private Context mContext;
    private List<String> mListDataHeader;
    private LinkedHashMap<String, List<String>> mListDataChild;
    private HashMap<Integer, Integer> mSelectionMap = new HashMap<>();

    public ExpandableListAdapter(Context context, List<String> listDataHeader,
                                 LinkedHashMap<String, List<String>> listChildData) {
        this.mContext = context;
        this.mListDataHeader = listDataHeader;
        this.mListDataChild = listChildData;
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this.mListDataChild.get(this.mListDataHeader.get(groupPosition))
                .get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }


    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        final String childText = (String) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert layoutInflater != null;
            convertView = layoutInflater.inflate(R.layout.expandable_list_item, null);
        }

        TextView txtListChild = (TextView) convertView.findViewById(R.id.txv_list_item);
        txtListChild.setText(childText);

        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this.mListDataChild.get(this.mListDataHeader.get(groupPosition))
                .size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.mListDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this.mListDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert layoutInflater != null;
            convertView = layoutInflater.inflate(R.layout.expandable_list_header, null);
        }

        TextView lblListHeader = (TextView) convertView.findViewById(R.id.txv_list_header);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);
        TextView txtSubTitle = (TextView) convertView.findViewById(R.id.txv_list_sub_title);
        txtSubTitle.setVisibility(View.GONE);
        txtSubTitle.setTypeface(null, Typeface.ITALIC);

        if (mSelectionMap.containsKey(groupPosition) && mSelectionMap.get(groupPosition) != -1) {
            String subTitle = (String) getChild(groupPosition, mSelectionMap.get(groupPosition));
            txtSubTitle.setText(subTitle);
            txtSubTitle.setVisibility(View.VISIBLE);
        }
        return convertView;
    }


    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    /**
     * Add sub title
     * @param groupPos header pos
     * @param childPos child pos
     * @param view View of header
     */
    public void addSubTitleWhenChildClicked(int groupPos, int childPos, View view) {
        mSelectionMap.put(groupPos, childPos);
        notifyDataSetChanged();
    }

    /**
     * For getting items;
     *
     * @return hash map of items;
     */
    public HashMap<Integer, Integer> mSelectedItems() {
        return mSelectionMap;
    }
}