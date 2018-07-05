package x.com.dialogdialog;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import x.com.dialogmobile.CheckUpdate.CheckDialogFragment;
import x.com.dialogmobile.CheckUpdate.NotificationHelper;

public class Main2Activity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new CheckDialogFragment(this,
                "版本更新内容",
                0,
                "http://imtt.dd.qq.com/16891/337A49BBE7A8A0B42E2312893903BBB3.apk?fsname=com.coolsnow.screenshot_5.6.0_56000.apk",
//                "http://imtt.dd.qq.com/16891/1F9DFAAC8C158F24D5A320A044AD352A.apk?fsname=com.qiyi.video_9.6.5_81100.apk",
                0,
                R.mipmap.icon_error,
                "下载通知标题",
                new CheckDialogFragment.OnCheckcallback() {
                    @Override
                    public void onCancel() {
                        Toast.makeText(Main2Activity.this, "345", Toast.LENGTH_SHORT).show();
                    }
                }).show(getSupportFragmentManager(), "checkup");

        //优先级default（3）
        new NotificationHelper(this, "默认通知")
                .setContent("测试1")//通知内容
                .setDefaults(Notification.DEFAULT_ALL)//设置提醒方式
                .setType(NotificationHelper.NOTIFICATION_TYPE_NORMAL)//通知类型
                .setSmallIcon(R.mipmap.ic_launcher)//
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.icon_error))
                .setContextIntent(new Intent(Intent.ACTION_SEND))//设置跳转
                .notifyShow();

        //优先级min（1）
        new NotificationHelper(this, "其他")
                .setContent("测试1")//通知内容
                .setDefaults(Notification.DEFAULT_ALL)//设置提醒方式
                .setType(NotificationHelper.NOTIFICATION_TYPE_OTHER)//通知类型
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.icon_error))
                .setContextIntent(new Intent(Intent.ACTION_SEND))
                .notifyShow();

        //优先级low（2）
        NotificationHelper notificationHelper = new NotificationHelper(this, "下载通知")
                .setContent("测试1")//通知内容
                .setDefaults(Notification.DEFAULT_ALL)//设置提醒方式
                .setType(NotificationHelper.NOTIFICATION_TYPE_DOWNLOAD)//通知类型
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.icon_error));
        notificationHelper.setProgress(50, new Intent(Intent.ACTION_SEND));
        notificationHelper.cancel();
        notificationHelper.notifyShow();

        NotificationHelper.cancelAll(this);

    }

}
