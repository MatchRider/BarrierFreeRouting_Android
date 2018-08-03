package com.disablerouting.setting;


import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.disablerouting.R;

import java.util.HashMap;
import java.util.List;

public class SettingAdapter extends RecyclerView.Adapter<SettingAdapter.ViewHolderSetting>{

    private List<String> mStringArrayList;
    private SettingAdapterListener mOnClickListener;
    private HashMap<Integer, String> mSelectionMap = new HashMap<>();


    public SettingAdapter(List<String> stringArrayList, SettingAdapterListener settingAdapterListener) {
        mStringArrayList = stringArrayList;
        mOnClickListener= settingAdapterListener;

    }


    @Override
    public ViewHolderSetting onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.setting_view, parent, false);
        return new ViewHolderSetting(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolderSetting holder, final int position) {
        String data = mStringArrayList.get(position);
        if(data!=null){
            holder.mTextViewTitle.setText(data);
        }
        holder.mImageViewEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnClickListener.OnIconEditViewOnClick(view,position);
            }
        });
        holder.mCheckBoxVerify.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    mOnClickListener.OnIconCheckBoxOnClick(compoundButton,position,b);

            }
        });
        String subTitle = null;
        if (mSelectionMap.containsKey(position)) {
            subTitle = mSelectionMap.get(position);
        }
        if (!TextUtils.isEmpty(subTitle)) {
            holder.mTextViewSubTitle.setText(subTitle);
            holder.mTextViewSubTitle.setVisibility(View.VISIBLE);
            holder.mCheckBoxVerify.setChecked(true);
            holder.mCheckBoxVerify.setText("Verified");
        }

    }

    @Override
    public int getItemCount() {
        return mStringArrayList.size();
    }

    public class ViewHolderSetting extends RecyclerView.ViewHolder{

        TextView mTextViewTitle;
        TextView mTextViewSubTitle;
        ImageView mImageViewEdit;
        CheckBox mCheckBoxVerify;

        public ViewHolderSetting(View itemView) {
            super(itemView);
            mTextViewTitle = (TextView)itemView.findViewById(R.id.txv_list_header);
            mTextViewSubTitle = (TextView)itemView.findViewById(R.id.txv_list_sub_title);
            mImageViewEdit = (ImageView) itemView.findViewById(R.id.img_edit);
            mCheckBoxVerify = (CheckBox) itemView.findViewById(R.id.chk_verify);
        }
    }

    public void setSelectionMap(HashMap<Integer, String> selectionMap) {
        mSelectionMap = selectionMap;
    }

}
