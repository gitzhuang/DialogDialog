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

import java.util.List;

import androidx.core.app.NotificationCompat;
import x.com.dialogmobile.BuildConfig;
import x.com.dialogmobile.R;

import static android.content.Context.NOTIFICATION_SERVICE;

public class NotificationHelper {
    private static final int NOTIFICATION_REQUEST_CODE = 0x001;
    public static final int NOTIFICATION_TYPE_NORMAL = 0x0011;
    public static final int NOTIFICATION_TYPE_DOWNLOAD = 0x012;
    public static final int NOTIFICATION_TYPE_OTHER = 0x013;
    private static final String NOTIFICATION_CHANNEL_ID_OTHER = "other";
    private static final String NOTIFICATION_CHANNEL_ID_NORMAL = "normal";
    private static final String NOTIFICATION_CHANNEL_ID_DOWNLOAD = "download";
    private static final String NOTIFICATION_CHANNEL_NAME_OTHER = "其他";//优先级1 min
    private static final String NOTIFICATION_CHANNEL_NAME_NORMAL = "应用通知";//优先级3 default
    private static final String NOTIFICATION_CHANNEL_NAME_DOWNLOAD = "下载通知";//优先级2 low

    private static int NOTIFICATION_ID_DOWNLOAD = 2002;//推送id，相同会覆盖
    private Context mContext;
    private NotificationManager mNotifyManager;
    private NotificationCompat.Builder mBuilder;
    private NotificationChannel mNotificationChannel;
    private int mNotificationId;
    private String mPushChannelName;
    private String mPushChannelId;

    /**
     *
     * @param context 上下文
     * @param contentTitle 推送标题
     */
    public NotificationHelper(Context context, String contentTitle) {
        mContext = context;
        initNotification(contentTitle);
    }

    /**
     * 设置内容
     * @param contentText 内容
     */
    public NotificationHelper setContent(String contentText){
        mBuilder.setContentText(contentText);
        mBuilder.setTicker(contentText);
        return this;
    }

    /**
     * 设置标题
     * @param title 标题
     */
    public NotificationHelper setTitle(String title){
        mBuilder.setContentTitle(title);
        return this;
    }

    /**
     * 设置大图标
     * @param smallIconId 大图标
     */
    public NotificationHelper setSmallIcon(int smallIconId){
        mBuilder.setSmallIcon(smallIconId);
        return this;
    }

    /**
     * 设置大图标
     * @param largeIcon 大图标
     */
    public NotificationHelper setLargeIcon(Bitmap largeIcon){
        mBuilder.setLargeIcon(largeIcon);
        return this;
    }

    /**
     * 设置内容跳转Intent
     * @param intent 跳转
     */
    public NotificationHelper setContextIntent(Intent intent){
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, NOTIFICATION_REQUEST_CODE,
                Intent.createChooser(intent, ""), PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pendingIntent);
        return this;
    }

    public NotificationHelper setType(int notificationType){
        if(notificationType == NOTIFICATION_TYPE_DOWNLOAD){
            //类型为下载
            downloadSetting();
        }else if(notificationType == NOTIFICATION_TYPE_OTHER){
            otherSetting();
        }else {
            //默认设置
        }
        return this;
    }

    /**
     * Notification.PRIORITY_DEFAULT(优先级为0)
     * Notification.PRIORITY_HIGH
     * Notification.PRIORITY_LOW
     * Notification.PRIORITY_MAX(优先级为2)
     * Notification.PRIORITY_MIN(优先级为-2)
     *
     * Oreo不用Priority了，用importance
     * IMPORTANCE_NONE 关闭通知
     * IMPORTANCE_MIN 开启通知，不会弹出，但没有提示音，状态栏中无显示
     * IMPORTANCE_LOW 开启通知，不会弹出，不发出提示音，状态栏中显示
     * IMPORTANCE_DEFAULT 开启通知，不会弹出，发出提示音，状态栏中显示
     * IMPORTANCE_HIGH 开启通知，会弹出，发出提示音，状态栏中显示
     */

    /**
     *
     * @param defaults 默认提醒
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
        if(mNotifyManager == null){
            mNotifyManager = (NotificationManager) mContext.getSystemService(NOTIFICATION_SERVICE);
        }
        //区分开下载通知，根据当前时间可以直接show出多个应用通知
        if(mNotificationId != NOTIFICATION_ID_DOWNLOAD){
            mNotificationId = (int) System.currentTimeMillis();
        }
        mNotifyManager.notify(mNotificationId, mBuilder.build());
    }

    /**
     * 初始化通知
     *
     */
    private void initNotification(String contentTitle) {
        //默认设置
        mPushChannelId = NOTIFICATION_CHANNEL_ID_NORMAL;
        mPushChannelName = NOTIFICATION_CHANNEL_NAME_NORMAL;
        if(mNotifyManager == null){
            mNotifyManager = (NotificationManager) mContext.getSystemService(NOTIFICATION_SERVICE);
        }
        mBuilder = new NotificationCompat.Builder(mContext, mPushChannelId);
        mBuilder.setContentTitle(contentTitle)//设置通知栏标题
                .setSmallIcon(R.mipmap.ic_launcher);//设置通知小ICON// ;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //清除掉上一次推送建立的消息通道，否则新通道设置无效
            mNotificationChannel = new NotificationChannel(mPushChannelId, mPushChannelName, NotificationManager.IMPORTANCE_DEFAULT);
            mNotifyManager.createNotificationChannel(mNotificationChannel);
            mBuilder.setGroup(NOTIFICATION_CHANNEL_ID_NORMAL);
        }else {
            mBuilder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
            mBuilder.setDefaults(NotificationCompat.DEFAULT_ALL);
        }
    }

    /**
     * 下载通知 配置
     */
    private void downloadSetting(){
        mPushChannelName = NOTIFICATION_CHANNEL_NAME_DOWNLOAD;
        mPushChannelId = NOTIFICATION_CHANNEL_ID_DOWNLOAD;
        mNotificationId = NOTIFICATION_ID_DOWNLOAD;//控制更新在同一条推送上
        //设置本次推送走那个通道
        mBuilder.setChannelId(mPushChannelId);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mNotificationChannel = new NotificationChannel(mPushChannelId, mPushChannelName, NotificationManager.IMPORTANCE_LOW);
            mNotificationChannel.enableVibration(false);//取消震动
            mNotificationChannel.setSound(null, null);
            mNotifyManager.createNotificationChannel(mNotificationChannel);
        }else {
            mBuilder.setPriority(NotificationCompat.PRIORITY_LOW);
            mBuilder.setDefaults(NotificationCompat.FLAG_ONGOING_EVENT);
        }
    }

    /**
     * 其他通知 配置
     */
    private void otherSetting(){
        mPushChannelName = NOTIFICATION_CHANNEL_NAME_OTHER;
        mPushChannelId = NOTIFICATION_CHANNEL_ID_OTHER;
        //设置本次推送走那个通道
        mBuilder.setChannelId(mPushChannelId);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mNotificationChannel = new NotificationChannel(mPushChannelId, mPushChannelName, NotificationManager.IMPORTANCE_MIN);
            mNotificationChannel.enableVibration(false);//取消震动
            mNotificationChannel.setSound(null, null);
            mNotifyManager.createNotificationChannel(mNotificationChannel);
        }else {
            mBuilder.setPriority(NotificationCompat.PRIORITY_MIN);
        }
    }

    /**
     * 更新通知栏的进度(下载中)
     *
     * @param progress 进度
     */
    public void setProgress(int progress, Intent intent) {
        if(NOTIFICATION_CHANNEL_ID_DOWNLOAD.equals(mPushChannelId)){
            Log.d("TAG", "setProgress: " + progress);
            if(progress == 100){
                mBuilder.setContentText("下载完成").setProgress(100, progress, false);
                //设置点击启动安装
                if(intent != null){
                    PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, Intent.createChooser(intent, "标题"),
                            PendingIntent.FLAG_UPDATE_CURRENT);
                    mBuilder.setContentIntent(pendingIntent);
                }
            }else {
                mBuilder.setContentText(String.format("正在下载:%1$d%%" , progress)).setProgress(100, progress, false);
            }
            mNotifyManager.notify(mNotificationId, mBuilder.build());
        }
    }

}
