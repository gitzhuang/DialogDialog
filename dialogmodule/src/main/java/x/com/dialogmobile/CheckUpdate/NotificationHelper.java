package x.com.dialogmobile.CheckUpdate;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import x.com.dialogmobile.BuildConfig;

import static android.content.Context.NOTIFICATION_SERVICE;

public class NotificationHelper {
    public static final int NOTIFICATION_REQUEST_CODE = 0x0010;
    private static final String PUSH_CHANNEL_NAME = "通知栏消息";//渠道名称
    private String  PUSH_CHANNEL_ID;//渠道id,唯一
    private int mNotificationPushId = 100;
    private Context mContext;
    private NotificationManager mNotifyManager;
    private NotificationCompat.Builder mBuilder;
    private String mContentTitle;
    private int mSmallIconId;


    /**
     *
     * @param context
     * @param smallIconId
     * @param contentTitle
     * @param isDownload 是否下载，需要控制声音、震动
     */
    public NotificationHelper(Context context, int smallIconId, String contentTitle, boolean isDownload) {
        mContext = context;
        mContentTitle = contentTitle;
        mSmallIconId = smallIconId;
        mNotificationPushId = this.hashCode();
        initNotification(isDownload);
    }

    /**
     * 设置内容
     * @param contentText
     */
    public NotificationHelper setContent(String contentText){
        mBuilder.setContentText(contentText);
        mBuilder.setTicker(contentText);
        return this;
    }

    /**
     * 设置标题
     * @param title
     */
    public NotificationHelper setTitle(String title){
        mBuilder.setContentTitle(title);
        return this;
    }

    /**
     * 设置大图标
     * @param largeIcon
     */
    public NotificationHelper setTitle(Bitmap largeIcon){
        mBuilder.setLargeIcon(largeIcon);
        return this;
    }

    /**
     * 设置内容跳转Intent
     * @param intent
     */
    public NotificationHelper setContextIntent(Intent intent){
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, NOTIFICATION_REQUEST_CODE,
                Intent.createChooser(intent, ""), PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pendingIntent);
        return this;
    }

    /**
     *
     * @param pri
     * @return
     *
     * Notification.PRIORITY_DEFAULT(优先级为0)

     * Notification.PRIORITY_HIGH

     * Notification.PRIORITY_LOW

     * Notification.PRIORITY_MAX(优先级为2)

     * Notification.PRIORITY_MIN(优先级为-2)

     */
    public NotificationHelper setPriority(int pri){
        mBuilder.setPriority(pri);
        return this;
    }

    /**
     *
     * @param defaults
     * @return
     *
     * Notification.DEFAULT_VIBRATE //添加默认震动提醒 需要 VIBRATE permission

     * Notification.DEFAULT_SOUND // 添加默认声音提醒

     * Notification.DEFAULT_LIGHTS// 添加默认三色灯提醒

     * Notification.DEFAULT_ALL// 添加默认以上3种全部提醒

     */
    public NotificationHelper setDefaults(int defaults){
        mBuilder.setDefaults(defaults);
        return this;
    }

    /**
     * 刷新显示通知
     */
    public void notifyShow(){
        mNotifyManager.notify(mNotificationPushId, mBuilder.build());
    }

    /**
     * 初始化通知
     * Oreo不用Priority了，用importance
     * IMPORTANCE_NONE 关闭通知
     * IMPORTANCE_MIN 开启通知，不会弹出，但没有提示音，状态栏中无显示
     * IMPORTANCE_LOW 开启通知，不会弹出，不发出提示音，状态栏中显示
     * IMPORTANCE_DEFAULT 开启通知，不会弹出，发出提示音，状态栏中显示
     * IMPORTANCE_HIGH 开启通知，会弹出，发出提示音，状态栏中显示
     */
    private void initNotification(boolean isDownload) {
        //下载消息需要另开渠道，与正常推送分开
        PUSH_CHANNEL_ID = BuildConfig.APPLICATION_ID + (isDownload ? ".download" : ".normal");

        mNotifyManager = (NotificationManager) mContext.getSystemService(NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(mContext,PUSH_CHANNEL_ID);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = null;
            if(isDownload){
                channel = new NotificationChannel(PUSH_CHANNEL_ID, PUSH_CHANNEL_NAME, NotificationManager.IMPORTANCE_MIN);
                channel.enableVibration(false);//取消震动
                channel.setSound(null, null);
            }else {
                channel = new NotificationChannel(PUSH_CHANNEL_ID, PUSH_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);

            }
            mNotifyManager.createNotificationChannel(channel);
        }

        mBuilder.setContentTitle(mContentTitle)//设置通知栏标题
//                .setContentText("")
//                .setTicker("") //通知首次出现在通知栏，带上升动画效果的
//                .setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示，一般是系统获取到的时间
                .setSmallIcon(mSmallIconId)//设置通知小ICON
                .setChannelId(PUSH_CHANNEL_ID);

        if(isDownload){
            mBuilder.setDefaults(NotificationCompat.FLAG_ONGOING_EVENT);
        }else {
            mBuilder.setDefaults(NotificationCompat.DEFAULT_ALL);
        }
    }

    /**
     * 更新通知栏的进度(下载中)
     *
     * @param progress
     */
    public void setProgress(int progress, Intent intent) {
        Log.d("TAG", "setProgress: " + progress);
        if(progress == 100){
            mBuilder.setContentText("下载完成").setProgress(100, progress, false);

            //设置点击启动安装
            if(intent != null){
                PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, Intent.createChooser(intent, "标题"), PendingIntent.FLAG_UPDATE_CURRENT);
                mBuilder.setContentIntent(pendingIntent);
            }

        }else {
            mBuilder.setContentText(String.format("正在下载:%1$d%%" , progress)).setProgress(100, progress, false);
        }

        mNotifyManager.notify(mNotificationPushId, mBuilder.build());
    }

}
