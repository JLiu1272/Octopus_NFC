package com.enterprise.jliu.uitesting;
/**
 * Created by JenniferLiu on 3/12/2017.
 */

import android.content.Context;
import android.content.Intent;
import android.nfc.cardemulation.HostApduService;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.enterprise.jliu.uitesting.common.logger.Log;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

// Constant Values
import com.enterprise.jliu.uitesting.constants.ConstantValues;
import com.enterprise.jliu.uitesting.constants.Station;
import com.enterprise.jliu.uitesting.exceptions.UnknownStation;

import com.enterprise.jliu.uitesting.transmissionresult.FailedActivity;
import com.enterprise.jliu.uitesting.transmissionresult.SuccessActivity;

/**
 * This is a sample APDU Service which demonstrates how to interface with the card emulation support
 * added in Android 4.4, KitKat.
 *
 * <p>This sample replies to any requests sent with the string "Hello World". In real-world
 * situations, you would need to modify this code to implement your desired communication
 * protocol.
 *
 * <p>This sample will be invoked for any terminals selecting AIDs of 0xF11111111, 0xF22222222, or
 * 0xF33333333. See src/main/res/xml/aid_list.xml for more details.
 *
 * <p class="note">Note: This is a low-level interface. Unlike the NdefMessage many developers
 * are familiar with for implementing Android Beam in apps, card emulation only provides a
 * byte-array based communication channel. It is left to developers to implement higher level
 * protocol support as needed.
 */
public class CardService extends HostApduService implements Listener{
    private static final String TAG = "CardService";
    // AID for our loyalty card service.
    private static final String SAMPLE_LOYALTY_CARD_AID = "F222222222";
    // ISO-DEP command HEADER for selecting an AID.
    // Format: [Class | Instruction | Parameter 1 | Parameter 2]
    private static final String SELECT_APDU_HEADER = "00A40400";
    // "OK" status word sent in response to SELECT AID command (0x9000)
    private static final byte[] SELECT_OK_SW = HexStringToByteArray("9000");
    // "UNKNOWN" status word sent in response to invalid APDU command (0x0000)
    private static final byte[] UNKNOWN_CMD_SW = HexStringToByteArray("0000");
    private static final byte[] SELECT_APDU = BuildSelectApdu(SAMPLE_LOYALTY_CARD_AID);


    private boolean isDialogDisplayed = false;

    /**
     * Called if the connection to the NFC card is lost, in order to let the application know the
     * cause for the disconnection (either a lost link, or another AID being selected by the
     * reader).
     *
     * @param reason Either DEACTIVATION_LINK_LOSS or DEACTIVATION_DESELECTED
     */
    @Override
    public void onDeactivated(int reason) { }

    @Override
    public void onDialogDisplayed() {

        isDialogDisplayed = true;
    }

    @Override
    public void onDialogDismissed() {

        isDialogDisplayed = false;
    }

    /**
     * This method will be called when a command APDU has been received from a remote device. A
     * response APDU can be provided directly by returning a byte-array in this method. In general
     * response APDUs must be sent as quickly as possible, given the fact that the user is likely
     * holding his device over an NFC reader when this method is called.
     *
     * <p class="note">If there are multiple services that have registered for the same AIDs in
     * their meta-data entry, you will only get called if the user has explicitly selected your
     * service, either as a default or just for the next tap.
     *
     * <p class="note">This method is running on the main thread of your application. If you
     * cannot return a response APDU immediately, return null and use the {@link
     * #sendResponseApdu(byte[])} method later.
     *
     * @param commandApdu The APDU that received from the remote device
     * @param extras A bundle containing extra data. May be null.
     * @return a byte-array containing the response APDU, or null if no response APDU can be sent
     * at this point.
     */
    // BEGIN_INCLUDE(processCommandApdu)
    @Override
    public byte[] processCommandApdu(byte[] commandApdu, Bundle extras) {
        Log.i(TAG, "Received APDU: " + ByteArrayToHexString(commandApdu));

        byte[] truncatedResponse = Arrays.copyOfRange(commandApdu,0, 10 );;
        // If the APDU matches the SELECT AID command for this service,
        // send the loyalty card account number, followed by a SELECT_OK status trailer (0x9000).
        /* For debugging */

        if (Arrays.equals(SELECT_APDU, truncatedResponse)) {
            sendAdpuMessage(commandApdu);
            return null;
        } else {
            return UNKNOWN_CMD_SW;
        }
    }
    // END_INCLUDE(processCommandApdu)


    public void sendAdpuMessage(byte[] commandApdu){

        byte[] truncatedResponse = Arrays.copyOfRange(commandApdu,0, 10 );
        String responseMsg = ByteArrayToHexString(Arrays.copyOfRange(commandApdu, 10, commandApdu.length));

        String octopus = OctopusIDStorage.GetAccount(getApplicationContext());
        byte[] accountBytes = octopus.getBytes();

        ConstantValues constantValues = new ConstantValues();

        try {
            Station station = constantValues.hexToStation(responseMsg);
            VolleyCallback callback = new VolleyCallback() {
                @Override
                public void onSuccess(String result) {
                    Intent intent;
                    if (result.equals("Octopus ID does not exist")) {
                        FailedActivity.msgSetter = "Octopus ID does not exist";
                        intent = new Intent(getApplicationContext(), FailedActivity.class);
                    } else if (result.equals("Not enough money left in the account")) {
                        FailedActivity.msgSetter = result;
                        intent = new Intent(getApplicationContext(), FailedActivity.class);
                    } else {
                        SuccessActivity.msgSetter = result;
                        intent = new Intent(getApplicationContext(), SuccessActivity.class);
                    }
                    getApplicationContext().startActivity(intent);

                    Log.i(TAG, "Sending account number: " + octopus);


                }
            };

            addTransaction(getApplicationContext(), station.getName(), station.getAmount(), callback);

            sendResponseApdu(ConcatArrays(accountBytes, SELECT_OK_SW));
        } catch (UnknownStation ex){
            ex.printStackTrace();
        }
    }


    /*
        Add a new transaction
     */
    public static void addTransaction(final Context context, String name, double amount, final VolleyCallback callback)
    {
        try {
            RequestQueue queue = Volley.newRequestQueue(context);
            String url = "http://ergonomic.cricket/Octopus/addTransaction";
            final JSONObject jsonBody = new JSONObject();
            String value = OctopusIDStorage.GetAccount(context);
            jsonBody.put("OctopusID", value);
            jsonBody.put("name", name);
            jsonBody.put("amountSpent", amount);
            final String requestBody = jsonBody.toString();

            // Request a string response from the provided URL.
            JsonObjectRequest jsObjRequest = new JsonObjectRequest
                    (Request.Method.POST, url, jsonBody, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject jsonObject) {

                            try {
                                callback.onSuccess(jsonObject.get("Add Result").toString());

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            android.util.Log.d(TAG, jsonObject.toString());
                            //Log.d(TAG, jsonObject.toString());
                            //return Response.success(responseString.toString(), HttpHeaderParser.parseCacheHeaders(response));
                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // TODO Auto-generated method stub
                            android.util.Log.i("VOLLEY", error.toString());
                        }
                    });

            queue.add(jsObjRequest);
        }
        catch (JSONException e){
            e.printStackTrace();
        }
    }


    /**
     * Build APDU for SELECT AID command. This command indicates which service a reader is
     * interested in communicating with. See ISO 7816-4.
     *
     * @param aid Application ID (AID) to select
     * @return APDU for SELECT AID command
     */
    public static byte[] BuildSelectApdu(String aid) {
        // Format: [CLASS | INSTRUCTION | PARAMETER 1 | PARAMETER 2 | LENGTH | DATA]
        Log.d(TAG, "Aid Length: " + aid.length());
        return HexStringToByteArray(SELECT_APDU_HEADER + String.format("%02X",
                aid.length() / 2) + aid);
    }

    /**
     * Utility method to convert a byte array to a hexadecimal string.
     *
     * @param bytes Bytes to convert
     * @return String, containing hexadecimal representation.
     */
    public static String ByteArrayToHexString(byte[] bytes) {
        final char[] hexArray = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
        char[] hexChars = new char[bytes.length * 2]; // Each byte has two hex characters (nibbles)
        int v;
        for (int j = 0; j < bytes.length; j++) {
            v = bytes[j] & 0xFF; // Cast bytes[j] to int, treating as unsigned value
            hexChars[j * 2] = hexArray[v >>> 4]; // Select hex character from upper nibble
            hexChars[j * 2 + 1] = hexArray[v & 0x0F]; // Select hex character from lower nibble
        }
        return new String(hexChars);
    }

    /**
     * Utility method to convert a hexadecimal string to a byte string.
     *
     * <p>Behavior with input strings containing non-hexadecimal characters is undefined.
     *
     * @param s String containing hexadecimal characters to convert
     * @return Byte array generated from input
     * @throws java.lang.IllegalArgumentException if input length is incorrect
     */
    public static byte[] HexStringToByteArray(String s) throws IllegalArgumentException {
        int len = s.length();
        if (len % 2 == 1) {
            throw new IllegalArgumentException("Hex string must have even number of characters");
        }
        byte[] data = new byte[len / 2]; // Allocate 1 byte per 2 hex characters
        for (int i = 0; i < len; i += 2) {
            // Convert each character into a integer (base-16), then bit-shift into place
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    /**
     * Utility method to concatenate two byte arrays.
     * @param first First array
     * @param rest Any remaining arrays
     * @return Concatenated copy of input arrays
     */
    public static byte[] ConcatArrays(byte[] first, byte[]... rest) {
        int totalLength = first.length;
        for (byte[] array : rest) {
            totalLength += array.length;
        }
        byte[] result = Arrays.copyOf(first, totalLength);
        int offset = first.length;
        for (byte[] array : rest) {
            System.arraycopy(array, 0, result, offset, array.length);
            offset += array.length;
        }
        return result;
    }
}
