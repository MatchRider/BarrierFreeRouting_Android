package com.disablerouting.setting;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.support.v4.widget.CompoundButtonCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.disablerouting.R;
import com.disablerouting.curd_operations.model.Attributes;
import com.disablerouting.utils.Utility;

import java.util.HashMap;
import java.util.List;

public class SettingAdapter extends RecyclerView.Adapter<SettingAdapter.ViewHolderSetting> {

    private Context mContext;
    private List<String> mStringArrayList;
    private SettingAdapterListener mOnClickListener;
    @SuppressLint("UseSparseArrays")
    private HashMap<Integer, Attributes> mSelectionMap = new HashMap<>();
    private boolean mIsValidChoosed;


    public SettingAdapter(Context context, List<String> stringArrayList, SettingAdapterListener settingAdapterListener) {
        mContext = context;
        mStringArrayList = stringArrayList;
        mOnClickListener = settingAdapterListener;

    }


    @Override
    public ViewHolderSetting onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.setting_view, parent, false);
        return new ViewHolderSetting(itemView);
    }

    @SuppressLint("ResourceType")
    @Override
    public void onBindViewHolder(ViewHolderSetting holder, final int position) {
        String data = mStringArrayList.get(position);
        if (data != null) {
            holder.mTextViewTitle.setText(data);
        }
        holder.mImageViewEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnClickListener.OnIconEditViewOnClick(view, position);
            }
        });
        holder.mCheckBoxVerify.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                mOnClickListener.OnIconCheckBoxOnClick(compoundButton, position, b, getSelectionMap().get(position));

            }
        });
        String subTitle = "";
        boolean isValid = false;
        if (mSelectionMap.containsKey(position)) {
            if (position == 3) {
                if(Utility.isParsableAsDouble(mSelectionMap.get(position).getValue())) {
                    subTitle = Utility.trimTWoDecimalPlaces(Double.parseDouble(mSelectionMap.get(position).getValue()));
                }else {
                    subTitle = mSelectionMap.get(position).getValue();
                }
            } else {
                if(mSelectionMap.get(position)!=null && mSelectionMap.get(position).getValue()!=null && !mSelectionMap.get(position).getValue().isEmpty()){
                    subTitle = mSelectionMap.get(position).getValue();
                }
            }
            isValid = mSelectionMap.get(position).isValid();
            setCheckBoxColor(holder.mCheckBoxVerify, mContext.getResources().getColor(R.color.colorAccent),
                    mContext.getResources().getColor(R.color.colorBlack));
            if (!mIsValidChoosed) {
                holder.mTextViewSubTitle.setText(subTitle);
                holder.mTextViewSubTitle.setVisibility(View.VISIBLE);
                if (isValid) {
                    holder.mCheckBoxVerify.setChecked(true);
                    holder.mCheckBoxVerify.setClickable(false);
                    holder.mCheckBoxVerify.setText(mContext.getResources().getString(R.string.verified));
                    holder.mCheckBoxVerify.setTextColor(mContext.getResources().getColor(R.color.colorPrimary));
                    holder.mImageViewEdit.setVisibility(View.GONE);
                } else {
                    holder.mCheckBoxVerify.setChecked(false);
                    holder.mCheckBoxVerify.setClickable(true);
                    holder.mCheckBoxVerify.setText(mContext.getResources().getString(R.string.not_verify));
                    holder.mCheckBoxVerify.setTextColor(mContext.getResources().getColor(R.color.colorBlack));
                    holder.mImageViewEdit.setVisibility(View.VISIBLE);
                    holder.mImageViewEdit.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_edit_black));
                    holder.mImageViewEdit.setClickable(true);

                }
            } else {
                if (isValid) {
                    holder.mTextViewSubTitle.setText(subTitle);
                    holder.mTextViewSubTitle.setVisibility(View.VISIBLE);
                    holder.mCheckBoxVerify.setChecked(true);
                    holder.mCheckBoxVerify.setClickable(true);
                    holder.mCheckBoxVerify.setText(mContext.getResources().getString(R.string.verified));
                    holder.mCheckBoxVerify.setTextColor(mContext.getResources().getColor(R.color.colorPrimary));
                    holder.mImageViewEdit.setVisibility(View.VISIBLE);
                    holder.mImageViewEdit.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_edit_black));
                    holder.mImageViewEdit.setClickable(true);

                } else {
                    holder.mTextViewSubTitle.setText(subTitle);
                    holder.mTextViewSubTitle.setVisibility(View.VISIBLE);
                    holder.mCheckBoxVerify.setChecked(false);
                    holder.mCheckBoxVerify.setClickable(true);
                    holder.mCheckBoxVerify.setText(mContext.getResources().getString(R.string.not_verify));
                    holder.mCheckBoxVerify.setTextColor(mContext.getResources().getColor(R.color.colorBlack));
                    holder.mImageViewEdit.setVisibility(View.VISIBLE);
                    holder.mImageViewEdit.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_edit_black));
                    holder.mImageViewEdit.setClickable(true);
                }

            }

        } else {
            holder.mTextViewTitle.setTextColor(mContext.getResources().getColor(R.color.colorTextGray));
            holder.mTextViewSubTitle.setText(subTitle);
            holder.mTextViewSubTitle.setVisibility(View.VISIBLE);
            holder.mCheckBoxVerify.setChecked(false);
            holder.mCheckBoxVerify.setClickable(false);
            holder.mCheckBoxVerify.setText(mContext.getResources().getString(R.string.not_verify));
            holder.mCheckBoxVerify.setTextColor(mContext.getResources().getColor(R.color.colorTextGray));
            holder.mImageViewEdit.setVisibility(View.VISIBLE);
            holder.mImageViewEdit.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_edit));
            holder.mImageViewEdit.setClickable(false);
        }

    }

    @Override
    public int getItemCount() {
        return mStringArrayList.size();
    }

    class ViewHolderSetting extends RecyclerView.ViewHolder {

        TextView mTextViewTitle;
        TextView mTextViewSubTitle;
        ImageView mImageViewEdit;
        CheckBox mCheckBoxVerify;

        ViewHolderSetting(View itemView) {
            super(itemView);
            mTextViewTitle = (TextView) itemView.findViewById(R.id.txv_list_header);
            mTextViewSubTitle = (TextView) itemView.findViewById(R.id.txv_list_sub_title);
            mImageViewEdit = (ImageView) itemView.findViewById(R.id.img_edit);
            mCheckBoxVerify = (CheckBox) itemView.findViewById(R.id.chk_verify);
        }

    }

    public void setSelectionMap(HashMap<Integer, Attributes> selectionMap, boolean isValidChoosed) {
        mSelectionMap = selectionMap;
        mIsValidChoosed = isValidChoosed;
    }

    private HashMap<Integer, Attributes> getSelectionMap() {
        return mSelectionMap;
    }

    private void setCheckBoxColor(CheckBox checkBox, int checkedColor, int uncheckedColor) {
        int states[][] = {{android.R.attr.state_checked}, {}};
        int colors[] = {checkedColor, uncheckedColor};
        CompoundButtonCompat.setButtonTintList(checkBox, new
                ColorStateList(states, colors));
    }


}
