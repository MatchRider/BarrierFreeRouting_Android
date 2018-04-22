package com.disablerouting.setting.setting_detail;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.disablerouting.R;

import java.util.List;

public class SettingDetailAdapter extends RecyclerView.Adapter<SettingDetailAdapter.ViewHolderSettingDetail>{

    private List<String> mStringArrayList;
    private SettingDetailAdapterListener mOnClickListener;


    public SettingDetailAdapter(List<String> stringArrayList, SettingDetailAdapterListener settingAdapterListener) {
        mStringArrayList = stringArrayList;
        mOnClickListener= settingAdapterListener;

    }


    @Override
    public ViewHolderSettingDetail onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.setting_detail_view, parent, false);
        return new ViewHolderSettingDetail(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolderSettingDetail holder, final int position) {
        String data = mStringArrayList.get(position);
        if(data!=null){
            holder.mTextViewTitle.setText(data);
        }
        holder.mLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnClickListener.onDetailItemClick(view,position);
            }
        });


    }

    @Override
    public int getItemCount() {
        return mStringArrayList.size();
    }

    public class ViewHolderSettingDetail extends RecyclerView.ViewHolder{

        TextView mTextViewTitle;
        ImageView mImageView;
        LinearLayout mLinearLayout;

        public ViewHolderSettingDetail(View itemView) {
            super(itemView);
            mTextViewTitle = (TextView)itemView.findViewById(R.id.txv_list_item_name);
            mImageView = (ImageView) itemView.findViewById(R.id.img_item);
            mLinearLayout = (LinearLayout) itemView.findViewById(R.id.ll_setting_detail_item);
        }
    }

}
