package com.disablerouting.sidemenu.adapter;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.disablerouting.R;
import com.disablerouting.sidemenu.model.SideMenuData;
import com.disablerouting.sidemenu.presenter.ISideMenuViewListener;

public class SideMenuAdapter extends ArrayAdapter<SideMenuData> {

    private ISideMenuViewListener mISideMenuViewListener;

    public SideMenuAdapter(@NonNull Context context, ISideMenuViewListener sideMenuFragment, @LayoutRes int resource) {
        super(context, resource);
        mISideMenuViewListener = sideMenuFragment;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        final SideMenuData model = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.side_menu_row_item_view, parent, false);
        }
        ImageView rowIcon = (ImageView) convertView.findViewById(R.id.rowIcon);
        TextView title = (TextView) convertView.findViewById(R.id.txv_title);
        TextView subTitle = (TextView) convertView.findViewById(R.id.txv_sub_title);
        rowIcon.setImageResource(model.getIconId());
        title.setText(model.getTitleID());
        subTitle.setVisibility(View.GONE);
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mISideMenuViewListener.onSideMenuItemClick(model.getTitleID());
            }
        });
        return convertView;
    }
}
