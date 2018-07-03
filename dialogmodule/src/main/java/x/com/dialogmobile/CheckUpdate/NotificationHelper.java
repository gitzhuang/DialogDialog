package x.com.dialogmobile.CheckUpdate;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;

import androidx.core.app.NotificationCompat;
import x.com.dialogmobile.R;

import static android.content.Context.NOTIFICATION_SERVICE;

public class NotificationHelper {
    private static final int PUSH_NOTIFICATION_ID = 0x0011;
    private static final String PUSH_CHANNEL_ID = "push_download";//渠道id
    private static final String PUSH_CHANNEL_NAME = "下载进度";//渠道名称
    private Context mContext;
    private NotificationManager mNotifyManager;
    private NotificationCompat.Builder mBuilder;
    private String mContentTitle;
    private String mFilePath;
    private String mFileName;


    public NotificationHelper(Context context, String contentTitle) {
        mContext = context;
        mContentTitle = contentTitle;
    }

    public NotificationHelper(Context context, String contentTitle, String filePath, String fileName) {
        mContext = context;
        mContentTitle = contentTitle;
        mFileName = fileName;
        mFilePath = filePath;

        initNotification();
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
    private void initNotification() {

       mNotifyManager = (NotificationManager) mContext.getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(PUSH_CHANNEL_ID, PUSH_CHANNEL_NAME, NotificationManager.IMPORTANCE_LOW);
            channel.enableVibration(false);//取消震动
            channel.setSound(null, null);

            if (mNotifyManager != null) {
                mNotifyManager.createNotificationChannel(channel);
            }
        }
        mBuilder = new NotificationCompat.Builder(mContext,PUSH_CHANNEL_ID);

        mBuilder.setContentTitle("通知标题")//设置通知栏标题
                .setContentText("通知内容")
                .setTicker("通知内容") //通知首次出现在通知栏，带上升动画效果的
                .setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示，一般是系统获取到的时间
                .setSmallIcon(R.mipmap.icon_error)//设置通知小ICON
                .setChannelId(PUSH_CHANNEL_ID)
                .setDefaults(Notification.FLAG_ONGOING_EVENT);

        mNotifyManager.notify(PUSH_NOTIFICATION_ID, mBuilder.build());
    }

    /**
     * 更新通知栏的进度(下载中)
     *
     * @param progress
     */
    public void setProgress(int progress, Intent intent) {

        if(progress == 100){
            mBuilder.setContentText("下载完成").setProgress(100, progress, false);

            //设置点击启动安装
            if(!TextUtils.isEmpty(mFilePath) && !TextUtils.isEmpty(mFileName) ){
                if(intent != null){
                    PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, Intent.createChooser(intent, "标题"), PendingIntent.FLAG_UPDATE_CURRENT);
                    mBuilder.setContentIntent(pendingIntent);
                }
            }
        }else {
            mBuilder.setContentText(String.format("正在下载:%1$d%%" , progress)).setProgress(100, progress, false);
        }

        mNotifyManager.notify(PUSH_NOTIFICATION_ID, mBuilder.build());
    }

}
