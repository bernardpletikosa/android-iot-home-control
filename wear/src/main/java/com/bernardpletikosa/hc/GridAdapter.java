package com.bernardpletikosa.hc;

import android.content.Context;
import android.support.wearable.view.GridPagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bernardpletikosa.hc.storage.Control;

import java.util.List;

public class GridAdapter extends GridPagerAdapter {

    private Context mContext;
    private List<Control> mControls;

    public GridAdapter(Context context, List<Control> controls) {
        mContext = context;
        this.mControls = controls;
    }

    @Override
    public int getRowCount() {
        return Math.max(1, mControls.size() / 3);
    }

    @Override
    public int getColumnCount(int i) {
        return 3;
    }

    @Override
    public int getCurrentColumnForRow(int row, int currentColumn) {
        return currentColumn;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int row, int col) {
        final int position = getPosition(row, col);
        if (mControls.size() <= position) return new TextView(mContext);

        final View view = LayoutInflater.from(mContext).inflate(R.layout.control_item, container, false);

        final TextView title = (TextView) view.findViewById(R.id.title);
        title.setText(mControls.get(position).name);

        final View btnOn = view.findViewById(R.id.button_on);
        btnOn.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                ((MainActivity) mContext).sendMessage(mControls.get(position).on);
            }
        });

        final View btnOff = view.findViewById(R.id.button_off);
        btnOff.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                ((MainActivity) mContext).sendMessage(mControls.get(position).off);
            }
        });

        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup viewGroup, int i, int i2, Object o) {
        viewGroup.removeView((View) o);
    }

    @Override
    public boolean isViewFromObject(View view, Object o) {
        return view.equals(o);
    }

    // Trick to use single dimensional array as multidimensional array.
    private int getPosition(int row, int col) {
        return row + col + (row * 2);
    }
}