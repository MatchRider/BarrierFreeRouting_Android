package com.disablerouting.directions;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.disablerouting.R;
import com.disablerouting.route_planner.model.Steps;

import java.util.ArrayList;
import java.util.List;

public class DirectionAdapter extends RecyclerView.Adapter<DirectionAdapter.StepViewHolder> {

    private Context mContext;
    private List<Steps> mStepsList= new ArrayList<>();
    private OnInstructionsClickListener mOnInstructionsClickListener;

    public DirectionAdapter(Context context, List<Steps> stepsList, OnInstructionsClickListener onInstructionsClickListener) {
        mContext = context;
        mStepsList = stepsList;
        mOnInstructionsClickListener = onInstructionsClickListener;
    }


    @Override
    public StepViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.diretion_item_view, parent, false);

        return new StepViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(StepViewHolder holder, int position) {
        final Steps steps = mStepsList.get(position);
        if(steps!=null) {
            if (steps.getInstructions() != null && !steps.getInstructions().isEmpty()) {
                holder.textViewDirection.setText(steps.getInstructions());
            } else {
                holder.textViewDirection.setText("");
            }
            if (steps.getType() != -1) {

                if(steps.getType()==0){
                    holder.imageViewDirection.setImageDrawable(getImage(mContext, "ic_left"));
                }
                if(steps.getType()==1){
                    holder.imageViewDirection.setImageDrawable(getImage(mContext, "ic_right"));

                }
                if(steps.getType()==2){
                    holder.imageViewDirection.setImageDrawable(getImage(mContext, "ic_left"));
                }
                if(steps.getType()==4){
                    holder.imageViewDirection.setImageDrawable(getImage(mContext, "ic_slight_left"));
                }
                if(steps.getType()==5){
                    holder.imageViewDirection.setImageDrawable(getImage(mContext, "ic_slight_right"));
                }
                if(steps.getType()==6){
                    holder.imageViewDirection.setImageDrawable(getImage(mContext, "ic_up"));
                }
                if(steps.getType()==7){
                    holder.imageViewDirection.setImageDrawable(getImage(mContext, "ic_round"));
                }
                if(steps.getType()==10){
                    holder.imageViewDirection.setImageDrawable(getImage(mContext, "ic_slight_right"));
                }

                //   viewHolder.imageViewDirection.setDrawable(steps.getInstructions());
            } else {
                  holder.imageViewDirection.setImageDrawable(
                          mContext.getResources().getDrawable(R.drawable.ic_menu_compass));
            }
        }

        holder.textViewDirection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnInstructionsClickListener.onInstructionClick(steps);
            }
        });
    }


    @Override
    public int getItemCount() {
        return mStepsList.size();
    }

    public static class StepViewHolder extends RecyclerView.ViewHolder {

        ImageView imageViewDirection;
        TextView textViewDirection;

        private StepViewHolder(View view) {
            super(view);
            imageViewDirection = (ImageView)view.findViewById(R.id.img_direction);
            textViewDirection = (TextView) view.findViewById(R.id.txv_direction);

        }

    }

    public static Drawable getImage(Context context, String name) {
        return context.getResources().getDrawable(context.getResources().getIdentifier(name, "drawable", context.getPackageName()));
    }
    public void  getResourceType(){
        for (int j = 1; j < 6; j++) {
            Drawable drawable = mContext.getResources().getDrawable(mContext.getResources()
                    .getIdentifier("d002_p00"+j, "drawable", mContext.getPackageName()));
        }
    }
    public interface OnInstructionsClickListener{
        void onInstructionClick(Steps steps);
    }
}
