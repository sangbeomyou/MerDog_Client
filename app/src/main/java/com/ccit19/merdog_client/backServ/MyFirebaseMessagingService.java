package com.ccit19.merdog_client.backServ;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.ccit19.merdog_client.ChatActivity;
import com.ccit19.merdog_client.MainActivity;
import com.ccit19.merdog_client.R;
import com.ccit19.merdog_client.SplashActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * NOTE: There can only be one service in each app that receives FCM messages. If multiple
 * are declared in the Manifest then the first one will be chosen.
 *
 * In order to make this Java sample functional, you must remove the following from the Kotlin messaging
 * service in the AndroidManifest.xml:
 *
 * <intent-filter>
 *   <action android:name="com.google.firebase.MESSAGING_EVENT" />
 * </intent-filter>
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    String url_ = urlset.getInstance().getData();
    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages. Data messages
        // are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data
        // messages are the type
        // traditionally used with GCM. Notification messages are only received here in
        // onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated
        // notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages
        // containing both notification
        // and data payloads are treated as notification messages. The Firebase console always
        // sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
                /*Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                intent.putExtra("chat_room",remoteMessage.getData().get("chat_room"));
                intent.putExtra("chat_request_id",remoteMessage.getData().get("chat_request_id"));
                intent.putExtra("pet_name",remoteMessage.getData().get("pet_name"));
                PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);
                try
                {
                    pendingIntent.send();
                }
                catch(PendingIntent.CanceledException e)
                {
                    e.printStackTrace();
                }*/

                //sendNotification(remoteMessage.getData());

            //


            //sendNotification(remoteMessage.getData());
            if (/* Check if data needs to be processed by long running job */ true) {
                // For long-running tasks (10 seconds or more) use WorkManager.
                scheduleJob();
            } else {
                // Handle message within 10 seconds
                handleNow();
            }

        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            if (!remoteMessage.getNotification().getChannelId().equals("3")){
                sendNotification(remoteMessage);
            }else {

            }
            //sendNotification(remoteMessage.getNotification().getBody());
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.

    }
    // [END receive_message]


    // [START on_new_token]

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(token);
    }
    // [END on_new_token]

    /**
     * Schedule async work using WorkManager.
     */
    private void scheduleJob() {
        // [START dispatch_job]
        OneTimeWorkRequest work = new OneTimeWorkRequest.Builder(MyWorker.class)
                .build();
        WorkManager.getInstance().beginWith(work).enqueue();
        // [END dispatch_job]
    }

    /**
     * Handle time allotted to BroadcastReceivers.
     */
    private void handleNow() {
        Log.d(TAG, "Short lived task is done.");
    }

    /**
     * Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        // TODO: Implement this method to send token to your app server.
        if (!SaveSharedPreference.getUserID(getApplicationContext()).isEmpty()){
            String url = url_ + "/userapp/fcm_token";
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            /* Create request */
            StringRequest updateTokenForm = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            boolean success = false;
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                success = jsonObject.getBoolean("result");
                                if (success) {

                                } else {

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                        Toast.makeText(getApplicationContext(), "서버로부터 응답이 없습니다.", Toast.LENGTH_LONG).show();
                    } else if (error instanceof AuthFailureError) {
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                    } else if (error instanceof ServerError) {
                        Toast.makeText(getApplicationContext(), "서버오류입니다.\n잠시후에 다시 시도해주세요.", Toast.LENGTH_LONG).show();
                    } else if (error instanceof NetworkError) {
                        Toast.makeText(getApplicationContext(), "인터넷 연결을 확인해주세요.", Toast.LENGTH_LONG).show();
                    } else if (error instanceof ParseError) {
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            })
            {
                @Override
                public  Map<String, String> getParams() throws AuthFailureError
                {
                    Map<String, String> params = new HashMap<String, String>();

                    params.put("user_id",SaveSharedPreference.getUserID(getApplicationContext()));
                    params.put("fcm_token", token);
                    return params;
                }
            };
            updateTokenForm.setRetryPolicy(new DefaultRetryPolicy(
                    0,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            queue.add(updateTokenForm);
        }

    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param remoteMessage FCM message body received.
     */
    private void sendNotification(RemoteMessage remoteMessage) {
        Map<String, String> messageData = remoteMessage.getData();
        /*Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("chat_room",messageData.get("chat_room"));
        intent.putExtra("chat_request_id",messageData.get("chat_request_id"));
        intent.putExtra("pet_name",messageData.get("pet_name"));
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);*/

        //String channelId = getString(R.string.default_notification_channel_id);
        String channelId = remoteMessage.getNotification().getChannelId();
        Intent intent;
        PendingIntent pendingIntent;
        Uri defaultSoundUri;
        NotificationCompat.Builder notificationBuilder;
        NotificationManager notificationManager;
        switch (channelId) {
            case "0":
//                intent = new Intent(this, MainActivity.class);
//                pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
//                        PendingIntent.FLAG_ONE_SHOT);
                defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                notificationBuilder =
                        new NotificationCompat.Builder(this, channelId)
                                .setSmallIcon(R.drawable.logo)
                                .setContentTitle(remoteMessage.getNotification().getTitle())
                                .setContentText(remoteMessage.getNotification().getBody())
                                .setAutoCancel(true)
                                .setSound(defaultSoundUri);
                                //.setContentIntent(pendingIntent);
                notificationManager =
                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                notificationManager.notify(Integer.parseInt(channelId) /* ID of notification */, notificationBuilder.build());
                break;
            case "3":
                break;
            default:
                intent = new Intent(this, ChatActivity.class);
                intent.putExtra("chat_room", messageData.get("chat_room"));
                intent.putExtra("chat_request_id", messageData.get("chat_request_id"));
                intent.putExtra("pet_name", messageData.get("pet_name"));
                pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                        PendingIntent.FLAG_ONE_SHOT);
                defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                notificationBuilder =
                        new NotificationCompat.Builder(this, channelId)
                                .setSmallIcon(R.drawable.logo)
                                .setContentTitle(remoteMessage.getNotification().getTitle())
                                .setContentText(remoteMessage.getNotification().getBody())
                                .setAutoCancel(true)
                                .setSound(defaultSoundUri)
                                .setContentIntent(pendingIntent);
                notificationManager =
                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                notificationManager.notify(Integer.parseInt(channelId) /* ID of notification */, notificationBuilder.build());
                break;
        }


    }
}