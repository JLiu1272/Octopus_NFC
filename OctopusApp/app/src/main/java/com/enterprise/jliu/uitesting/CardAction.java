package com.enterprise.jliu.uitesting;

import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;

/**
 * Created by JenniferLiu on 3/12/2017.
 */

public class CardAction extends DialogFragment {

    public static final String TAG = CardAction.class.getSimpleName();

    public String userID;

    private Listener mListener;

    public static CardAction newInstance() {
        return new CardAction();
    }

    private EditText test;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_card_action,container,false);
        initViews(view);
        return view;
    }

    private void initViews(View view) {
        EditText octopusIDVal = (EditText) view.findViewById(R.id.octopusNumber);

        // Update Button
        Button updateButton = (Button) view.findViewById(R.id.update_octo);
        updateButton.setOnClickListener(v -> showMain(true));

        // Cancel Button
        Button cancelButton = (Button) view.findViewById(R.id.cancel_octo);
        cancelButton.setOnClickListener(v -> showMain(false));

        octopusIDVal.setText(OctopusIDStorage.GetAccount(getActivity()));
        octopusIDVal.addTextChangedListener(new AccountUpdater());
    }

    public void showMain(Boolean updating){
        View inflatedView = getActivity().getLayoutInflater().inflate(R.layout.activity_main, null);
        Intent intent = new Intent(getActivity().getApplicationContext() , MainActivity.class);

        // If user has clicked update, it will update the octopus value
        // Otherwise it won't
        if (updating) {
            userID = OctopusIDStorage.GetAccount(getActivity());
            TextView userid = (TextView) inflatedView.findViewById(R.id.mainOctoNum);
            userid.setText(userID);
        }

        getActivity().getApplicationContext().startActivity(intent);
    }

    private class AccountUpdater implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            // Not implemented.
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // Not implemented.
        }

        @Override
        public void afterTextChanged(Editable s) {
            String account = s.toString();
            OctopusIDStorage.SetOctopus(getActivity(), account);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mListener = (MainActivity)context;
        mListener.onDialogDisplayed();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener.onDialogDismissed();
    }

    /** Called when sample is created. Displays generic UI with welcome text. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


}
