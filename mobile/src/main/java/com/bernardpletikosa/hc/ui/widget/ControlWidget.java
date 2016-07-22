package com.bernardpletikosa.hc.ui.widget;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;

import com.bernardpletikosa.hc.R;

import static android.graphics.PixelFormat.TRANSLUCENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static android.view.WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
import static android.view.WindowManager.LayoutParams.TYPE_PHONE;

public class ControlWidget extends View {

    private Context mContext;
    private FrameLayout mContainer;
    private WindowManager mWindowManager;

    public ControlWidget(Context context) {
        super(context);

        mContext = context;
        mContainer = new FrameLayout(mContext);

        addToWindowManager();
    }

    private void addToWindowManager() {
        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WRAP_CONTENT, WRAP_CONTENT, TYPE_PHONE, FLAG_NOT_FOCUSABLE, TRANSLUCENT);
        params.gravity = Gravity.START;
        params.width = getResources().getDimensionPixelSize(R.dimen.widget_width);

        mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        mWindowManager.addView(mContainer, params);

        LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layoutInflater.inflate(R.layout.widget_control, mContainer);

        final ListView widgetList = (ListView) mContainer.findViewById(R.id.widget_list);
        widgetList.setAdapter(new WidgetAdapter(getContext()));

        final ImageView imageView = (ImageView) mContainer.findViewById(R.id.on_screen_btn);
        imageView.setOnTouchListener(new OnTouchListener() {
            private int initX, initY;
            private int initTouchX, initTouchY;
            private boolean moving = false;

            @Override public boolean onTouch(View v, MotionEvent event) {
                int x = (int) event.getRawX();
                int y = (int) event.getRawY();

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        moving = false;
                        initX = params.x;
                        initY = params.y;
                        initTouchX = x;
                        initTouchY = y;
                        return true;

                    case MotionEvent.ACTION_UP:
                        if (!moving)
                            widgetList.setVisibility(widgetList.getVisibility() == GONE ? VISIBLE : GONE);
                        moving = false;
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        if (Math.abs(initX - params.x) > 10) moving = true;

                        params.x = initX + (x - initTouchX);
                        params.y = initY + (y - initTouchY);

                        mWindowManager.updateViewLayout(mContainer, params);
                        return true;
                }
                return false;
            }
        });
    }

    public void destroy() {
        if (mWindowManager != null) mWindowManager.removeView(mContainer);
    }
}
