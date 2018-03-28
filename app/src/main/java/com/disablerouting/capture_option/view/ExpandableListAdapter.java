package com.disablerouting.capture_option.view;

import android.content.Context;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.disablerouting.R;
import com.disablerouting.capture_option.model.DataModelExpandableList;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private Context mContext;
    private List<String> mListDataHeader;
    private LinkedHashMap<String, List<DataModelExpandableList>> mListDataChild;
    private HashMap<Integer, Integer> mSelectionMap = new HashMap<>();

    public ExpandableListAdapter(Context context, List<String> listDataHeader,
                                 LinkedHashMap<String, List<DataModelExpandableList>> listChildData) {
        this.mContext = context;
        this.mListDataHeader = listDataHeader;
        this.mListDataChild = listChildData;
    }

    @Override
    public DataModelExpandableList getChild(int groupPosition, int childPosititon) {
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

        final DataModelExpandableList childText = getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert layoutInflater != null;
            convertView = layoutInflater.inflate(R.layout.expandable_list_item, null);
        }

        TextView txtListChild = (TextView) convertView.findViewById(R.id.txv_list_item);
        txtListChild.setText(childText.getValue());

        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this.mListDataChild.get(this.mListDataHeader.get(groupPosition))
                .size();
    }

    @Override
    public String getGroup(int groupPosition) {
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
        String headerTitle = getGroup(groupPosition);

        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert layoutInflater != null;
            convertView = layoutInflater.inflate(R.layout.expandable_list_header, null);
        }
        ImageView imageViewArrow = (ImageView) convertView.findViewById(R.id.img_arrow);
        if (isExpanded) {
            imageViewArrow.setImageResource(R.drawable.ic_arrow_up);
        } else {
            imageViewArrow.setImageResource(R.drawable.ic_arrow_down);
        }
        TextView lblListHeader = (TextView) convertView.findViewById(R.id.txv_list_header);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);

        TextView txtSubTitle = (TextView) convertView.findViewById(R.id.txv_list_sub_title);
        txtSubTitle.setVisibility(View.GONE);
        txtSubTitle.setTypeface(null, Typeface.ITALIC);

        String subTitle = null;
        if (mSelectionMap.containsKey(groupPosition)) {
            subTitle = getChild(groupPosition, mSelectionMap.get(groupPosition)).getValue();
        }
        if (!TextUtils.isEmpty(subTitle)) {
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
     *
     * @param groupPos header pos
     * @param childPos child pos
     * @param view     View of header
     */
    public void addSubTitleWhenChildClicked(int groupPos, int childPos, View view) {
        mSelectionMap.put(groupPos, childPos);
        notifyDataSetChanged();
    }

    /**
     * Remove subtitles when clear button clicked
     */
    public void removeSubTitlesWhenClearClicked() {
        mSelectionMap.clear();
        notifyDataSetChanged();
    }

    public HashMap<Integer, Integer> getSelectionMap() {
        return mSelectionMap;
    }

    public void setSelectionMap(HashMap<Integer, Integer> selectionMap) {
        mSelectionMap = selectionMap;
    }
}