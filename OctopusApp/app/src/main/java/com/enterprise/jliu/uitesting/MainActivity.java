package com.enterprise.jliu.uitesting;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements Listener{

    public static final String TAG = "MainActivity";

    private Button octopusCard;
    private FloatingActionButton addButton;
    private boolean isDialogDisplayed = false;

    private CardAction cCardActionFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        octopusCard = (Button) findViewById(R.id.octopusCard);
        addButton = (FloatingActionButton) findViewById(R.id.addCard);

        TextView octopusNumber = findViewById(R.id.mainOctoNum);
        String value = OctopusIDStorage.GetAccount(this);
        octopusNumber.setText(value);
        octopusNumber.addTextChangedListener(new AccountUpdater(this));

        addButton.setOnClickListener(view -> showActionMenu());

        octopusCard.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                showTransaction(v);
            }
        });
    }

    private class AccountUpdater implements TextWatcher {

        private Context c;
        private AccountUpdater(Context c){
            this.c = c;
        }

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
            OctopusIDStorage.SetOctopus(c, account);
        }
    }

    private void showActionMenu(){
        cCardActionFragment = (CardAction) getFragmentManager().findFragmentByTag(cCardActionFragment.TAG);

        if (cCardActionFragment == null ){
            cCardActionFragment = CardAction.newInstance();
        }
        cCardActionFragment.show(getFragmentManager(), CardAction.TAG);
    }

    @Override
    public void onDialogDisplayed() {
        isDialogDisplayed = true;
    }

    @Override
    public void onDialogDismissed() {
        isDialogDisplayed = false;
    }

    /* Called when user touches the octopus card */
    public void showTransaction(View view){
        View inflatedView = getLayoutInflater().inflate(R.layout.activity_card_details, null);
        Intent intent = new Intent(this , CardDetails.class);
        this.startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
