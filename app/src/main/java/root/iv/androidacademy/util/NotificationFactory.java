package root.iv.androidacademy.util;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import root.iv.androidacademy.R;

public class NotificationFactory {
    private static final String CHANEL_ID = "chanel-1";
    private static final String CHANEL_NAME = "chanel-name";

    public static Notification loading(Context context) {
        Notification.Builder builder = new Notification.Builder(context)
                .setSmallIcon(R.drawable.ic_new_york_times)
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText(context.getString(R.string.loading))
                .setColor(context.getResources().getColor(R.color.colorPrimaryLight));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(CHANEL_ID);
        }

        return builder.build();
    }

    public static Notification error(Context context) {
        Notification.Builder builder = new Notification.Builder(context)
                .setSmallIcon(R.drawable.ic_new_york_times)
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText(context.getString(R.string.errorLoading))
                .setColor(context.getResources().getColor(R.color.colorPrimaryLight));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(CHANEL_ID);
        }

        return builder.build();
    }

    public static Notification complete(Context context) {
        Notification.Builder builder = new Notification.Builder(context)
                .setSmallIcon(R.drawable.ic_new_york_times)
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText(context.getString(R.string.successfulLoading))
                .setColor(context.getResources().getColor(R.color.colorPrimaryLight));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(CHANEL_ID);
        }

        return builder.build();
    }

    public static void show(Context context, Notification notification) {
        NotificationManager manager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manager.createNotificationChannel(new NotificationChannel(CHANEL_ID, CHANEL_NAME, NotificationManager.IMPORTANCE_HIGH));
        }

        manager.notify(0, notification);
    }
}
