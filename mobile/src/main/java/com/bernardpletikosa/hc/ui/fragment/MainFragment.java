package com.bernardpletikosa.hc.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.bernardpletikosa.hc.R;
import com.bernardpletikosa.hc.storage.Storage;
import com.bernardpletikosa.hc.storage.Control;
import com.bernardpletikosa.hc.ui.MainActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.support.design.widget.Snackbar.LENGTH_SHORT;

public class MainFragment extends Fragment {

    @BindView(R.id.control_name) EditText mControlName;
    @BindView(R.id.control_on) EditText mControlOn;
    @BindView(R.id.control_off) EditText mControlOff;
    @BindView(R.id.control_add_container) View mAddContainer;
    @BindView(R.id.control_list) ListView mControlsList;

    @BindViews({R.id.control_name, R.id.control_on, R.id.control_off}) List<EditText> mControlWidgets;
    static final ButterKnife.Action<EditText> CLEAR = new ButterKnife.Action<EditText>() {
        @Override public void apply(@NonNull EditText view, int index) {
            view.setText("");
        }
    };

    private ControlsAdapter mControlsAdapter;
    private List<Control> mControls = new ArrayList<>();

    public MainFragment() {}

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saved) {
        final View view = inflater.inflate(R.layout.main_fragment, container, false);
        ButterKnife.bind(this, view);

        initList();
        initAction();

        return view;
    }

    @SuppressWarnings("unused") @OnClick(R.id.control_update)
    public void onUpdateClicked() {
        notifyActivity();
    }

    @SuppressWarnings("unused") @OnClick(R.id.control_add_title)
    public void onAddClicked() {
        mAddContainer.setVisibility(mAddContainer.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
    }

    @SuppressWarnings("unused") @OnClick(R.id.control_add_btn)
    public void onSaveClicked() {
        final String controlName = mControlName.getText().toString().trim();
        if (controlName.isEmpty()) {
            mControlName.setError(getResources().getString(R.string.fragment_main_provide_name));
            return;
        }

        final int controlOn = checkControl(mControlOn.getText().toString());
        if (controlOn <= 0) {
            mControlOn.setError(getResources().getString(R.string.fragment_main_provide_on_code));
            return;
        }

        final int controlOff = checkControl(mControlOff.getText().toString());
        if (controlOff <= 0) {
            mControlOff.setError(getResources().getString(R.string.fragment_main_provide_off_code));
            return;
        }

        Storage.instance(getContext()).saveControl(controlName, controlOn, controlOff);

        updateUi();
        notifyActivity();
    }

    private int checkControl(String control) {
        if (control.trim().isEmpty()) return -1;
        try {
            return Integer.parseInt(control);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private void initAction() {
        mControlOff.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId != EditorInfo.IME_ACTION_DONE) return false;
                onSaveClicked();
                return true;
            }
        });
    }

    private void initList() {
        mControls.clear();
        mControls.addAll(Storage.instance(getContext()).getAllControls());
        mControlsAdapter = new ControlsAdapter(getContext(), mControls, new ControlsAdapter.OnControlDelete() {
            @Override public void controlDeleted(String name) {
                Storage.instance(getContext()).deleteControl(name);
                updateUi();
            }
        });
        mControlsList.setAdapter(mControlsAdapter);
    }

    private void updateUi() {
        hideKeyboard(mControlName);

        mControls.clear();
        mControls.addAll(Storage.instance(getContext()).getAllControls());
        mControlsAdapter.notifyDataSetChanged();

        ButterKnife.apply(mControlWidgets, CLEAR);
    }

    private void notifyActivity() {
        if (getView() != null) Snackbar.make(getView(), R.string.fragment_main_update, LENGTH_SHORT).show();
        ((MainActivity) getActivity()).updateAndRestart();
    }

    private void hideKeyboard( View view) {
        if (view == null) return;
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}

