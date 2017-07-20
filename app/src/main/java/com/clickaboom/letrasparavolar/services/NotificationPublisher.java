package com.clickaboom.letrasparavolar.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import com.clickaboom.letrasparavolar.R;
import com.clickaboom.letrasparavolar.activities.MainActivity;
import com.google.firebase.messaging.RemoteMessage;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by moyhdez on 29/12/16.
 */

public class NotificationPublisher {

    public static final String EXTRA_CHAT_NOTIFICATION = "com.tejuino.twinchat.chat_notification";
    private static NotificationPublisher shared;


    public static NotificationPublisher getInstance() {
        if (shared == null) {
            shared = new NotificationPublisher();
        }
        return shared;
    }

    private NotificationPublisher() {

    }

    public void showNotification(Context context, RemoteMessage remoteMessage) {
        Intent intent = new Intent();

        if (remoteMessage.getData().get("notification_type") != null) {
            Intent i = new Intent("broadCastName");
            i.putExtra("type", remoteMessage.getData().get("notification_type"));
            i.putExtra("trip_id", remoteMessage.getData().get("trip_id"));
            i.putExtra("status", remoteMessage.getData().get("status"));
            context.sendBroadcast(i);
            return;
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent = new Intent(context, MainActivity.class);
        HashMap<String, String> dataHash = new HashMap<>(remoteMessage.getData());
        intent.putExtra(EXTRA_CHAT_NOTIFICATION, dataHash);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Map<String, String> data = remoteMessage.getData();
        //Bitmap bitmap = getBitmapFromURL(remoteMessage.getData().get("image"));
        Bitmap bitmap = getBitmapFromURL("http://www.startupremarkable.com/wp-content/uploads/2015/02/a-book-a-week-image.jpg");
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.icon_noti)
                .setLargeIcon(bitmap)
                .setColor(ContextCompat.getColor(context, R.color.colorAccent))
                //.setContentTitle("Letras para volar")
                //.setContentText("¡Visualiza el nuevo contenido!")
                .setContentTitle(remoteMessage.getData().get("title"))
                .setContentText(remoteMessage.getData().get("message"))
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());
    }

    public Bitmap getBitmapFromURL(String strURL) {
        try {
            URL url = new URL(strURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            Bitmap output;

            if (myBitmap.getWidth() > myBitmap.getHeight()) {
                output = Bitmap.createBitmap(myBitmap.getHeight(), myBitmap.getHeight(), Bitmap.Config.ARGB_8888);
            } else {
                output = Bitmap.createBitmap(myBitmap.getWidth(), myBitmap.getWidth(), Bitmap.Config.ARGB_8888);
            }

            Canvas canvas = new Canvas(output);

            final int color = 0xff424242;
            final Paint paint = new Paint();
            final Rect rect = new Rect(0, 0, myBitmap.getWidth(), myBitmap.getHeight());

            float r = 0;

            if (myBitmap.getWidth() > myBitmap.getHeight()) {
                r = myBitmap.getHeight() / 2;
            } else {
                r = myBitmap.getWidth() / 2;
            }

            paint.setAntiAlias(true);
            canvas.drawARGB(0, 0, 0, 0);
            paint.setColor(color);
            canvas.drawCircle(r, r, r, paint);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            canvas.drawBitmap(myBitmap, rect, rect, paint);
            return output;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
