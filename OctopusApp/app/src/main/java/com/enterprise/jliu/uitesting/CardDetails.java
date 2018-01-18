package com.enterprise.jliu.uitesting;

import android.app.ActionBar;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Layout;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;


public class CardDetails extends AppCompatActivity implements Listener {

    public static final String TAG = CardAction.class.getSimpleName();
    private topupFragment topUpActionFragment;
    private CardAction cCardActionFragment;

    private Boolean isDialogDisplayed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_details);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        LinearLayout transactionDetail = (LinearLayout) findViewById(R.id.transactionDetail);
        TextView currentAmount = (TextView) findViewById(R.id.currentAmount);
        Button topUpButton = (Button) findViewById(R.id.topupButton);
        toolbar.setNavigationIcon(R.drawable.back_button);

        /* Open up TopUp Fragment Dialog */
        topUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTopUpDialog();
            }
        });

        /* Trigger back button */
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
            }
        });

        /* Display all transaction based on ID */
        getTransaction(this, transactionDetail);

        /* Display current amount */
        getAmount(this, currentAmount);
    }


    /*
       Show topup dialog
     */
    public void showTopUpDialog(){
        String topupaction = TopUpAction.TAG;

        //topUpActionFragment = (TopUpAction) getFragmentManager().findFragmentByTag(TopUpAction.TAG);

        if (topUpActionFragment == null){
            topupFragment topupFrag = new topupFragment();
            FragmentManager fp = getSupportFragmentManager();
            topupFrag.show(fp, topupFragment.TAG);
        }
    }

    /*
        Get Octopus Current Amount
     */
    public static void getAmount(final Context context, TextView currentAmount)
    {
        try {
            RequestQueue queue = Volley.newRequestQueue(context);
            String url = "http://ergonomic.cricket/Octopus/getCurrAmount";
            final JSONObject jsonBody = new JSONObject();
            String value = OctopusIDStorage.GetAccount(context);
            jsonBody.put("OctopusID", value);
            final String requestBody = jsonBody.toString();

            // Request a string response from the provided URL.
            JsonObjectRequest jsObjRequest = new JsonObjectRequest
            (Request.Method.POST, url, jsonBody, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject jsonObject) {
                    Log.d(TAG, jsonObject.toString());
                    //Log.d(TAG, jsonObject.toString());
                    //Log.d(TAG, "Breaker");
                    try {
                        JSONArray transaction = (JSONArray) jsonObject.get("Current Amount");
                        String amount = Double.toString( (double) transaction.getJSONObject(0).get("currentAmount"));
                        currentAmount.setText(amount);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    //return Response.success(responseString.toString(), HttpHeaderParser.parseCacheHeaders(response));
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    // TODO Auto-generated method stub
                    Log.i("VOLLEY", error.toString());
                }
            });

            queue.add(jsObjRequest);
        }
        catch (JSONException e){
            e.printStackTrace();
        }
    }


    /*
        Get All Transaction based on ID
     */
    public static void getTransaction(final Context context, final LinearLayout transactionDetail)
    {
        try {
            // Instantiate the RequestQueue
            RequestQueue queue = Volley.newRequestQueue(context);
            String url = "http://ergonomic.cricket/Octopus/getTransaction";
            final JSONObject jsonBody = new JSONObject();
            String value = OctopusIDStorage.GetAccount(context);
            jsonBody.put("OctopusID", value);
            final String requestBody = jsonBody.toString();


            // Request a string response from the provided URL.
            JsonObjectRequest jsObjRequest = new JsonObjectRequest
            (Request.Method.POST, url, jsonBody, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject jsonObject) {
                    //Log.d(TAG, jsonObject.toString());
                    //Log.d(TAG, "Breaker");
                    try {
                        JSONArray transaction = (JSONArray) jsonObject.get("Transaction");

                        if (transaction.length() <= 0){
                            TextView message = new TextView(context);
                            message.setText("No Transaction");
                            transactionDetail.addView(message);
                        }
                        else {

                            LinearLayout horizontalLayoutTitle = new LinearLayout(context);
                            // Setting Title
                            TextView nameT = new TextView(context);
                            String nameTitle = "Place";
                            nameT.setText(nameTitle);
                            nameT.setTextColor(Color.WHITE);
                            nameT.setTypeface(null, Typeface.BOLD);
                            nameT.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1));


                            TextView amountT = new TextView(context);
                            String amountTitle = "Amount";
                            amountT.setText(amountTitle);
                            amountT.setTextColor(Color.WHITE);
                            amountT.setTypeface(null, Typeface.BOLD);
                            amountT.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1));

                            TextView createdOnT = new TextView(context);
                            String dateT = "Date";
                            createdOnT.setText(dateT);
                            createdOnT.setTextColor(Color.WHITE);
                            createdOnT.setTypeface(null, Typeface.BOLD);
                            createdOnT.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1));

                            horizontalLayoutTitle.addView(nameT);
                            horizontalLayoutTitle.addView(amountT);
                            horizontalLayoutTitle.addView(createdOnT);

                            horizontalLayoutTitle.setBackgroundColor(Color.BLACK);

                            transactionDetail.addView(horizontalLayoutTitle);

                            // iterating Transaction
                            for (int i = 0; i < transaction.length(); i++) {

                                LinearLayout horizontalLayout = new LinearLayout(context);


                                TextView name = new TextView(context);
                                String nameDet = (String) transaction.getJSONObject(i).get("name");
                                name.setText(nameDet);
                                name.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1));


                                TextView amount = new TextView(context);
                                String amountSpent;
                                // If amountSpent has nothing, we check if amountAdded has something
                                if (!transaction.getJSONObject(i).get("amountSpent").toString().equals("null")){
                                    amountSpent = "- " + Double.toString((double) transaction.getJSONObject(i).get("amountSpent"));
                                    amount.setTextColor(Color.RED);
                                }
                                else{
                                    amountSpent = "+ " + Double.toString((double) transaction.getJSONObject(i).get("amountAdded"));
                                    amount.setTextColor(Color.GREEN);
                                }

                                amount.setText(amountSpent);
                                amount.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1));

                                TextView createdOn = new TextView(context);
                                String date = (String) transaction.getJSONObject(i).get("createdOn");

                                // Split string so it only shows Date and Month
                                // Expected format: Month Dat (ie. Nov 26)
                                String[] tokenize = date.split(" ");
                                String finalDate = tokenize[2] + " " + tokenize[1];

                                createdOn.setText(finalDate);
                                createdOn.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1));

                                horizontalLayout.addView(name);
                                horizontalLayout.addView(amount);
                                horizontalLayout.addView(createdOn);

                                transactionDetail.addView(horizontalLayout);
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    //return Response.success(responseString.toString(), HttpHeaderParser.parseCacheHeaders(response));
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    // TODO Auto-generated method stub
                    TextView message = new TextView(context);
                    message.setText("This did not work");
                    transactionDetail.addView(message);
                    Log.i("VOLLEY", error.toString());
                }
            });

            queue.add(jsObjRequest);

        }
        catch (JSONException exception) {
            exception.printStackTrace();
            Log.e("Error", exception.toString());
        }

    }

    @Override
    public void onDialogDisplayed() {
        isDialogDisplayed = true;
    }

    @Override
    public void onDialogDismissed() {
        isDialogDisplayed = false;
    }
}
