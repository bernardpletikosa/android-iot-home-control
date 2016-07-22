package com.bernardpletikosa.hc.ui.widget;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.bernardpletikosa.hc.R;
import com.bernardpletikosa.hc.handler.HomeApiTask;
import com.bernardpletikosa.hc.storage.Storage;
import com.bernardpletikosa.hc.storage.Control;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WidgetAdapter extends ArrayAdapter<Control> {

    public WidgetAdapter(Context context) {
        super(context, R.layout.widget_item, Storage.instance(context).getAllControls());
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            convertView = View.inflate(getContext(), R.layout.widget_item, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }

        holder.name.setText(getItem(position).getName());
        holder.on.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                new HomeApiTask(getContext()).execute(getItem(position).getOn());
            }
        });
        holder.off.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                new HomeApiTask(getContext()).execute(getItem(position).getOff());
            }
        });

        return convertView;
    }

    static class ViewHolder {

        @BindView(R.id.widget_item_name) TextView name;
        @BindView(R.id.widget_item_on) TextView on;
        @BindView(R.id.widget_item_off) TextView off;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
