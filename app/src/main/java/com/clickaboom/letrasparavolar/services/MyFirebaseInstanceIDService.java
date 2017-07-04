package com.clickaboom.letrasparavolar.services;

import android.content.Context;
import android.provider.Settings;
import android.util.Log;

import com.android.volley.VolleyError;
import com.clickaboom.letrasparavolar.R;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.gson.Gson;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by karen on 22/05/17.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIIDService";

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    // [START refresh_token]
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        String deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        //sendRegistrationToServer(refreshedToken, deviceId, getApplicationContext());
    }
    // [END refresh_token]

    /**
     * Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    public static void sendRegistrationToServer(final String token, final String deviceId, final Context context) {
        // Get token
        /*final String deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        // Log and toast
        String msg = getString(R.string.msg_token_fmt, token);
        Log.d(TAG, msg);*/

        /*if (authToken != null) {
            if (authToken.getAccessToken() != null) {
                APIConnection apiConnection = APIConnection.getInstance();
                // Headers
                Map<String, String> headers = new HashMap<>();
                headers.put("Accept", "Accept");
                headers.put("Authorization", "Bearer " + authToken.getAccessToken());

                // Params
                Map<String, String> params = new HashMap<>();
                params.put("os", "android");
                params.put("token", token);
                params.put("device_id", deviceId);

                apiConnection.volleyPostRequest(context, context.getResources().getString(R.string.api_register_device), headers, params, new APICallback() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        Gson gson = new Gson();
                        ApiResponse res = gson.fromJson(response.toString(), ApiResponse.class);
                        Log.d(TAG, res.message);
                    }

                    @Override
                    public void onError(VolleyError error) {
                        //Log.d(TAG, error.getMessage());
                    }
                }, false);
            }
        }*/
    }
}