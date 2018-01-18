package com.enterprise.jliu.uitesting;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

public class CardAction_Layout extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_action);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        EditText et = (EditText) findViewById(R.id.octopusNumber) ;
        setSupportActionBar(toolbar);
    }

}
