package com.bernardpletikosa.hc.ui.fragment;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bernardpletikosa.hc.R;
import com.bernardpletikosa.hc.storage.Control;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ControlsAdapter extends ArrayAdapter<Control> {

    interface OnControlDelete {
        void controlDeleted(String name);
    }

    private final OnControlDelete mDeleteListener;

    public ControlsAdapter(Context context, List<Control> controls, OnControlDelete listener) {
        super(context, R.layout.control_item_info, controls);
        this.mDeleteListener = listener;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        ViewHolder holder;
        if (view != null) {
            holder = (ViewHolder) view.getTag();
        } else {
            view = View.inflate(getContext(), R.layout.control_item_info, null);
            holder = new ViewHolder(view);
            view.setTag(holder);
        }

        final Control item = getItem(position);

        holder.info.setText(getContext().getResources().getString(R.string.control_info,
                item.getName(), item.getOn(), item.getOff()));

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                Snackbar bar = Snackbar.make(view, getContext().getString(R.string.control_delete_prompt, item.getName()), Snackbar.LENGTH_LONG)
                        .setAction(R.string.control_delete_prompt_yes, new View.OnClickListener() {
                            @Override public void onClick(View v) {
                                mDeleteListener.controlDeleted(item.getName());
                            }
                        });
                bar.show();
            }
        });

        return view;
    }

    static class ViewHolder {

        @BindView(R.id.control_info) TextView info;
        @BindView(R.id.control_info_btn) ImageView delete;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
