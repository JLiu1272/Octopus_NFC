package com.enterprise.jliu.uitesting;

import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by JenniferLiu on 6/12/2017.
 */

public class TopUpAction extends DialogFragment{

    public static final String TAG = TopUpAction.class.getSimpleName();

    private Listener mListener;

    public static TopUpAction newInstance() {
        return new TopUpAction();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.content_topup, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View view) {
        //EditText topupAmount = (EditText) view.findViewById(R.id.topup_amount);

        //TextView topupResult = (TextView) view.findViewById(R.id.topup_result);

        //Button updateButton = (Button) view.findViewById(R.id.topup_update);
        /*updateButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                // Check if amount exist and not a negative number
                double amount = Double.parseDouble(topupAmount.getText().toString());
                if (!topupAmount.getText().toString().equals("") || amount < 0){
                    topupResult.setText("Invalid amount to add");
                }
                else{
                    getAmount(v.getContext(), amount, topupResult);
                }
            }
        });

        // Cancel Button
        Button cancelButton = (Button) view.findViewById(R.id.cancel_topup);
        cancelButton.setOnClickListener(v -> showTransactionDone());*/
    }


    /* Calling API to add amount to card */
    public static void getAmount(final Context context, double amount, TextView topupResult)
    {
        try {
            RequestQueue queue = Volley.newRequestQueue(context);
            String url = "http://ergonomic.cricket/Octopus/topUp";
            final JSONObject jsonBody = new JSONObject();
            String value = OctopusIDStorage.GetAccount(context);
            jsonBody.put("OctopusID", value);
            jsonBody.put("amount", Double.toString(amount));
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
                                String result = jsonObject.get("TopUp Result").toString();
                                /*
                                  If transaction was successful display card detail again
                                 */
                                if (result.equals("Amount successfully added"))
                                {
                                    TopUpAction topupaction = new TopUpAction();
                                    topupaction.showTransactionDone();
                                }
                                else
                                {
                                    // Show Result message indicating transaction has failed
                                    topupResult.setText(result);
                                    topupResult.setTextColor(Color.RED);
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
                            Log.i("VOLLEY", error.toString());
                        }
                    });

            queue.add(jsObjRequest);
        }
        catch (JSONException e){
            e.printStackTrace();
        }
    }

    /* Show canceled TopUp transaction */
    public void showTransactionDone(){
        //View inflatedView = getActivity().getLayoutInflater().inflate(R.layout.activity_card_details, null);
        Intent intent = new Intent(getActivity().getApplicationContext() , CardDetails.class);
        getActivity().getApplicationContext().startActivity(intent);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mListener = (CardDetails)context;
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
