package com.disablerouting.instructions;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.disablerouting.R;
import com.disablerouting.route_planner.model.Steps;
import com.disablerouting.utils.Utility;

import java.util.ArrayList;
import java.util.List;

public class InstructionsAdapter extends RecyclerView.Adapter<InstructionsAdapter.StepViewHolder> {

    private Context mContext;
    private List<Steps> mStepsList= new ArrayList<>();
    private OnInstructionsClickListener mOnInstructionsClickListener;

    InstructionsAdapter(Context context, List<Steps> stepsList, OnInstructionsClickListener onInstructionsClickListener) {
        mContext = context;
        mStepsList = stepsList;
        mOnInstructionsClickListener = onInstructionsClickListener;
    }


    @NonNull
    @Override
    public StepViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.diretion_item_view, parent, false);

        return new StepViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull StepViewHolder holder, int position) {
        final Steps steps = mStepsList.get(position);
        if(steps!=null) {
            if (steps.getInstructions() != null && !steps.getInstructions().isEmpty()) {
                holder.textViewDirection.setText(steps.getInstructions());
            } else {
                holder.textViewDirection.setText("");
            }
            if (steps.getDistance() != 0) {
                String value=Utility.trimTWoDecimalPlaces(steps.getDistance());
                holder.textViewDistance.setText(String.format("%s%s", value, mContext.getResources().getString(R.string.meter)));
            } else {
                holder.textViewDistance.setText("");
            }
            if (steps.getType() != -1) {
                holder.imageViewDirection.setImageDrawable(DirectionInstruction.getEnumDrawable(mContext,steps.getType()));

            } else {
                  holder.imageViewDirection.setImageDrawable(
                          mContext.getResources().getDrawable(R.drawable.ic_menu_compass));
            }
            if (steps.getName() != null && !steps.getName().isEmpty()) {
                holder.textViewStreet.setVisibility(View.VISIBLE);
                holder.textViewStreet.setText(steps.getName());

            } else {
                holder.textViewStreet.setVisibility(View.GONE);
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

    static class StepViewHolder extends RecyclerView.ViewHolder {

        ImageView imageViewDirection;
        TextView textViewDirection;
        TextView textViewDistance;
        TextView textViewStreet;


        private StepViewHolder(View view) {
            super(view);
            imageViewDirection = (ImageView)view.findViewById(R.id.img_direction);
            textViewDirection = (TextView) view.findViewById(R.id.txv_direction);
            textViewDistance = (TextView) view.findViewById(R.id.txv_km);
            textViewStreet = (TextView) view.findViewById(R.id.txv_street);

        }

    }

    private static Drawable getImage(Context context, String name) {
        return context.getResources().getDrawable(context.getResources().getIdentifier(name, "drawable", context.getPackageName()));
    }

    public interface OnInstructionsClickListener{
        void onInstructionClick(Steps steps);
    }
}
