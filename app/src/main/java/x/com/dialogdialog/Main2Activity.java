package x.com.dialogdialog;

import android.app.Notification;
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

        new NotificationHelper(this,  R.mipmap.icon_error, "测试", false)
                .setContent("测试内容")
                .setDefaults(NotificationCompat.DEFAULT_SOUND)
                .notifyShow();

    }

}
