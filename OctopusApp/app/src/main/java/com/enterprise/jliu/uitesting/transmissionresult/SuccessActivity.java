package com.enterprise.jliu.uitesting.transmissionresult;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;

import com.enterprise.jliu.uitesting.MainActivity;
import com.enterprise.jliu.uitesting.R;

public class SuccessActivity extends AppCompatActivity {

    public static String msgSetter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success);

        Button home = (Button) findViewById(R.id.buttonHome);
        TextView transactionMsg = (TextView) findViewById(R.id.transmission_result);
        if (msgSetter != null){
            transactionMsg.setText(msgSetter);
        }
        home.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(),MainActivity.class)));

    }

}
