package x.com.dialogdialog;

import android.app.Notification;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import x.com.dialogmobile.CheckUpdate.CheckDialogFragment;
import x.com.dialogmobile.NotificationHelper;


public class CheckNotificationActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new CheckDialogFragment(this,
                "版本更新内容",
                0,
//                "http://imtt.dd.qq.com/16891/337A49BBE7A8A0B42E2312893903BBB3.apk?fsname=com.coolsnow.screenshot_5.6.0_56000.apk",
                "http://imtt.dd.qq.com/16891/1F9DFAAC8C158F24D5A320A044AD352A.apk?fsname=com.qiyi.video_9.6.5_81100.apk",
                0,
                R.mipmap.icon_error,
                "下载通知标题",
                "下载失败提示信息",
                new CheckDialogFragment.OnCheckcallback() {
                    @Override
                    public void onCancel() {
                        Toast.makeText(CheckNotificationActivity.this, "345", Toast.LENGTH_SHORT).show();
                    }
                }).show(getSupportFragmentManager(), "checkup");

        //优先级default（3）默认通知，不可折叠
        new NotificationHelper(this, "默认通知")
                .setContent("测试1")//通知内容
                .setDefaults(Notification.DEFAULT_ALL)//设置提醒方式
                .setType(NotificationHelper.NOTIFICATION_TYPE_NORMAL)//通知类型
                .setSmallIcon(R.mipmap.ic_launcher)//
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.icon_error))
                .setContextIntent(new Intent(Intent.ACTION_SEND))//设置跳转
                .setNotificationId(123)//传入通知id，可以不传，id相同会覆盖
                .setAutoCancel(true)//true点击自动删除，false滑动才能删除
                .setOngoing(true)//正在进行的通知，禁止滑动删除
                .notifyShow();

        //优先级min（1）没有提醒，可以折叠
        new NotificationHelper(this, "其他通知")
                .setContent("测试1")//通知内容
                .setDefaults(Notification.DEFAULT_ALL)//设置提醒方式
                .setType(NotificationHelper.NOTIFICATION_TYPE_OTHER)//通知类型
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.icon_error))
                .setContextIntent(new Intent(Intent.ACTION_SEND))
                .notifyShow();

        //优先级low（2）下载通知，带进度条
        NotificationHelper notificationHelper = new NotificationHelper(this, "下载通知")
                .setContent("测试1")//通知内容
                .setDefaults(Notification.DEFAULT_ALL)//设置提醒方式
                .setType(NotificationHelper.NOTIFICATION_TYPE_DOWNLOAD)//通知类型
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.icon_error))
                .setNotificationId(123);

        notificationHelper.setProgress(50, new Intent(Intent.ACTION_SEND));
        notificationHelper.cancel();
        notificationHelper.notifyShow();

        //优先级heigh（4）应用内通知，弹出框
        new NotificationHelper(this, "QQ")
                .setContent("收到一条未读消息")//通知内容
                .setType(NotificationHelper.NOTIFICATION_TYPE_DIALOG)//通知类型
                .setNotificationId(111)
                .notifyShow();

        //取消当前通知
//        notificationHelper.cancel();
        //取消全部通知
//        NotificationHelper.cancelAll(this);

    }

}
