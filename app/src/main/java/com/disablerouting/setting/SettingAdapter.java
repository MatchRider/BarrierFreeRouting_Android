package com.disablerouting.setting;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.support.annotation.NonNull;
import android.support.v4.widget.CompoundButtonCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.disablerouting.R;
import com.disablerouting.common.AppConstant;
import com.disablerouting.curd_operations.model.Attributes;
import com.disablerouting.setting.model.SettingModel;
import com.disablerouting.utils.Utility;

import java.util.HashMap;
import java.util.List;

public class SettingAdapter extends RecyclerView.Adapter<SettingAdapter.ViewHolderSetting> {

    private Context mContext;
    private List<SettingModel> mStringArrayList;
    private SettingAdapterListener mOnClickListener;
    @SuppressLint("UseSparseArrays")
    private HashMap<Integer, Attributes> mSelectionMap = new HashMap<>();
    private boolean mIsValidChoosed;
    private boolean mIsFromOSM;
    private boolean mIsValidScreen=false;


    SettingAdapter(Context context, List<SettingModel> stringArrayList, SettingAdapterListener settingAdapterListener,
                   boolean isFromOSM, boolean isValidScreen) {
        mContext = context;
        mStringArrayList = stringArrayList;
        mOnClickListener = settingAdapterListener;
        mIsFromOSM = isFromOSM;
        mIsValidScreen= isValidScreen;

    }


    @NonNull
    @Override
    public ViewHolderSetting onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.setting_view, parent, false);
        return new ViewHolderSetting(itemView);
    }

    @SuppressLint("ResourceType")
    @Override
    public void onBindViewHolder(@NonNull ViewHolderSetting holder, @SuppressLint("RecyclerView") final int position) {
        String data = mStringArrayList.get(position).getKeyString();
        if (mIsFromOSM) {
            holder.mLinearLayoutVerify.setVisibility(View.GONE);
        } else {
            holder.mLinearLayoutVerify.setVisibility(View.VISIBLE);

        }
        if (data != null) {
            holder.mTextViewTitle.setText(data);
        }
        final int checkPos = mStringArrayList.get(position).getKeyPosition();

        holder.mImageViewEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnClickListener.OnIconEditViewOnClick(view, position);
            }
        });
        holder.mCheckBoxVerify.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                mOnClickListener.OnIconCheckBoxOnClick(compoundButton, checkPos, b, getSelectionMap().get(checkPos));

            }
        });
        String subTitle = "";
        boolean isValid;

        if (mSelectionMap.containsKey(mStringArrayList.get(position).getKeyPosition())) {
            if (mSelectionMap.get(checkPos) != null && mSelectionMap.get(checkPos).getKey().equalsIgnoreCase(AppConstant.KEY_WIDTH)) {
                if (mSelectionMap.get(checkPos).getValue() != null) {
                    if (mSelectionMap.get(checkPos).getValue().contains(".")
                            && Utility.isParsableAsDouble(mSelectionMap.get(checkPos).getValue())) {
                        subTitle = Utility.trimTWoDecimalPlaces(Double.parseDouble(mSelectionMap.get(checkPos).getValue()));
                    } else {
                        subTitle = mSelectionMap.get(checkPos).getValue();
                    }
                }
            }
            else {
                if (mSelectionMap.get(checkPos) != null && mSelectionMap.get(checkPos).getValue() != null && !mSelectionMap.get(checkPos).getValue().isEmpty()) {
                    if (mSelectionMap.get(checkPos).getKey().equalsIgnoreCase(AppConstant.KEY_SURFACE)) {
                        if (mSelectionMap.get(checkPos).getValue().equalsIgnoreCase("asphalt")) {
                            subTitle = mContext.getString(R.string.asphalt);
                        }
                        else if (mSelectionMap.get(checkPos).getValue().equalsIgnoreCase("concrete")) {
                            subTitle = mContext.getString(R.string.concrete);
                        }
                        else if (mSelectionMap.get(checkPos).getValue().equalsIgnoreCase("paving_stones")) {
                            subTitle = mContext.getString(R.string.paving_stones);
                        }
                        else if (mSelectionMap.get(checkPos).getValue().equalsIgnoreCase("cobblestone")) {
                            subTitle = mContext.getString(R.string.cobblestone);
                        }
                       else if (mSelectionMap.get(checkPos).getValue().equalsIgnoreCase("compacted")) {
                            subTitle = mContext.getString(R.string.compacted);
                        }
                    } else if (mSelectionMap.get(checkPos).getKey().equalsIgnoreCase(AppConstant.KEY_KERB_HEIGHT)) {
                        if (mSelectionMap.get(checkPos).getValue().equalsIgnoreCase(mContext.getString(R.string.kerb_zero))) {
                            subTitle = mContext.getString(R.string.zero_curb);
                        }
                        else if (mSelectionMap.get(checkPos).getValue().equalsIgnoreCase(mContext.getString(R.string.value_kerb_three_validation))) {
                            subTitle = mContext.getString(R.string.value_kerb_three_validation);
                        }
                        else if (mSelectionMap.get(checkPos).getValue().equalsIgnoreCase(mContext.getString(R.string.value_kerb_six_validation))) {
                            subTitle = mContext.getString(R.string.value_kerb_six_validation);
                        }
                        else if (mSelectionMap.get(checkPos).getValue().equalsIgnoreCase(mContext.getString(R.string.value_kerb_any_validation))) {
                            subTitle = mContext.getString(R.string.value_kerb_any_validation);
                        }
                        else if (mSelectionMap.get(checkPos).getValue().contains(".") && Utility.isParsableAsDouble(mSelectionMap.get(checkPos).getValue())) {
                            subTitle = Utility.trimTWoDecimalPlaces(Double.parseDouble(mSelectionMap.get(checkPos).getValue()));
                        }

                    } else {
                        subTitle = mSelectionMap.get(checkPos).getValue();

                    }
                }
            }

            isValid = mSelectionMap.get(checkPos).isValid();
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
                    if(mIsValidScreen){
                        holder.mCheckBoxVerify.setChecked(false);
                        holder.mCheckBoxVerify.setClickable(false);
                        holder.mCheckBoxVerify.setText(mContext.getResources().getString(R.string.not_verify));
                        holder.mCheckBoxVerify.setTextColor(mContext.getResources().getColor(R.color.colorBlack));
                        holder.mImageViewEdit.setVisibility(View.GONE);
                        holder.mImageViewEdit.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_edit_black));
                        holder.mImageViewEdit.setClickable(false);

                    }else {
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
        LinearLayout mLinearLayoutVerify;

        ViewHolderSetting(View itemView) {
            super(itemView);
            mTextViewTitle = (TextView) itemView.findViewById(R.id.txv_list_header);
            mTextViewSubTitle = (TextView) itemView.findViewById(R.id.txv_list_sub_title);
            mImageViewEdit = (ImageView) itemView.findViewById(R.id.img_edit);
            mCheckBoxVerify = (CheckBox) itemView.findViewById(R.id.chk_verify);
            mLinearLayoutVerify = (LinearLayout) itemView.findViewById(R.id.ll_verify);
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
