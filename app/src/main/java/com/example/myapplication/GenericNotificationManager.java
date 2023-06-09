package com.example.myapplication;


import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;

import android.app.PendingIntent;
import android.content.Context;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.widget.RemoteViews;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;



import java.util.Random;


public class GenericNotificationManager {
    public static NotificationManager notificationManager;


    private static final String MISCELLENEOUS_CHANNEL_ID ="video_master_miscelleneous";
    private static final String MISCELLENEOUS_CHANNEL_NAME ="Miscelleneous";
    private static final String MISCELLENEOUS_CHANNEL_DESCRIPTION ="Notification for Video Editor, Trim, Sticker, Bakground, Music";

    private static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(MISCELLENEOUS_CHANNEL_ID, MISCELLENEOUS_CHANNEL_NAME, importance);
            channel.setDescription(MISCELLENEOUS_CHANNEL_DESCRIPTION);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }


    public static void handleGenericNotification(Context context, NotificationModel notification){
//        Log.d("@message","notificaiton");

        createNotificationChannel(context);
        if(notification !=null) {
            PendingIntent pendingIntent = null;

                pendingIntent = getPendingIntentForModel(context, notification);
                if(pendingIntent != null)
                {
                    createNotification(context, notification, pendingIntent,  R.mipmap.ic_launcher);
                }

        }

    }

    private static Intent getIntentForHome(Context context,NotificationModel notification) {
        if (notification != null && context != null) {
            Intent notificationIntent = new Intent(context, MainActivity.class);
            notificationIntent.putExtra("long",notification.longitude);
            notificationIntent.putExtra("lat",notification.latitude);
            notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);

            return notificationIntent;
        }
        return null;
    }










    static PendingIntent getPendingIntentForModel(Context context, NotificationModel notification) {
        Intent intent = null;
            intent=  getIntentForHome(context, notification);


        int requestCode = new Random().nextInt(1000);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            return PendingIntent.getActivity(context, requestCode, intent,  PendingIntent.FLAG_IMMUTABLE|PendingIntent.FLAG_UPDATE_CURRENT );
        }else {
            return PendingIntent.getActivity(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }

    }


    private static void notifyNotification(int notificationId, NotificationCompat.Builder notificationBuilder) {
        try {
            if (notificationManager != null) {
                notificationManager.notify(notificationId, notificationBuilder.build());
            }
        } catch (Exception e) {

        }
    }

    protected static void createNotification(Context context, NotificationModel notification, PendingIntent pendingIntent, int appSmallIconId) {
        int notificationId = new Random().nextInt(60000);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        notificationManager = (android.app.NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        String title = "MockApplication";
        String body ="MockApplication";
        if(TextUtils.isEmpty(title) || TextUtils.isEmpty(body))
            return;
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context, MISCELLENEOUS_CHANNEL_ID)
                        //   .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),R.drawable.app_icon))
                        .setSmallIcon(appSmallIconId)
                        .setContentTitle(title)
                        .setContentText(body)
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent)
                        .setSound(defaultSoundUri);


      /*  Glide.with(context)
                .load(notification.getBig_image())
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        Bitmap bitmap = ((BitmapDrawable) resource).getBitmap();
                        notificationBuilder.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(bitmap));
                        notificationManager.notify(notificationId, notificationBuilder.build());
                        return false;
                    }
                }).submit();

        if (notification.getLarge_icon() != null) {
            Glide.with(context)
                    .load(notification.getLarge_icon())
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            Bitmap bitmap = ((BitmapDrawable) resource).getBitmap();
                            notificationBuilder.setLargeIcon(bitmap);
                            notificationManager.notify(notificationId, notificationBuilder.build());
                            return false;
                        }
                    }).submit();*/
//        }
        notificationManager.notify(notificationId, notificationBuilder.build());
    }




}

