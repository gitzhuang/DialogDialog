package x.com.dialogdialog;

import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Toast;

import java.io.File;

import androidx.appcompat.app.AppCompatActivity;
import x.com.dialogmobile.CheckUpdate.CheckDialogFragment;
import x.com.dialogmobile.DownloadHelper;
import x.com.dialogmobile.NDialogBuilder;


public class CheckNotificationActivity extends AppCompatActivity {

    private DownloadHelper downloadHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //检查更新
//        new CheckDialogFragment(this,
//                "版本更新内容",
//                0,
////                "http://imtt.dd.qq.com/16891/50CC095EFBE6059601C6FB652547D737.apk?fsname=com.tencent.mm_6.6.7_1321.apk&csr=1bbd",
////                "http://imtt.dd.qq.com/16891/337A49BBE7A8A0B42E2312893903BBB3.apk?fsname=com.coolsnow.screenshot_5.6.0_56000.apk&csr=1bbd",
//                "http://imtt.dd.qq.com/16891/337A49BBE7A8A0B42E2312893903BBB3.apk?fsname=com.coolsnow.screenshot_5.6.0_56000.apk",
////                "http://imtt.dd.qq.com/16891/1F9DFAAC8C158F24D5A320A044AD352A.apk?fsname=com.qiyi.video_9.6.5_81100.apk",
//                0,
//                "下载失败提示信息",
//                new CheckDialogFragment.OnCheckcallback() {
//                    @Override
//                    public void onCancel() {
//                        Toast.makeText(CheckNotificationActivity.this, "345", Toast.LENGTH_SHORT).show();
//                    }
//                }).show(getSupportFragmentManager(), "checkup");


//        //通知使用
//        //优先级default（3）默认通知，不可折叠
//        new NotificationHelper(this, "默认通知")
//                .setContent("测试1")//通知内容
//                .setDefaults(Notification.DEFAULT_ALL)//设置提醒方式
//                .setType(NotificationHelper.NOTIFICATION_TYPE_NORMAL)//通知类型
//                .setSmallIcon(R.mipmap.ic_launcher)//
//                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.icon_error))
//                .setContextIntent(new Intent(Intent.ACTION_SEND))//设置跳转
//                .setNotificationId(123)//传入通知id，可以不传，id相同会覆盖
//                .setAutoCancel(true)//true点击自动删除，false滑动才能删除
//                .setOngoing(true)//正在进行的通知，禁止滑动删除
//                .notifyShow();
//
//        //优先级min（1）没有提醒，可以折叠
//        new NotificationHelper(this, "其他通知")
//                .setContent("测试1")//通知内容
//                .setDefaults(Notification.DEFAULT_ALL)//设置提醒方式
//                .setType(NotificationHelper.NOTIFICATION_TYPE_OTHER)//通知类型
//                .setSmallIcon(R.mipmap.ic_launcher)
//                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.icon_error))
//                .setContextIntent(new Intent(Intent.ACTION_SEND))
//                .notifyShow();
//
//        //优先级low（2）下载通知，带进度条
//        NotificationHelper notificationHelper = new NotificationHelper(this, "下载通知")
//                .setContent("测试1")//通知内容
//                .setDefaults(Notification.DEFAULT_ALL)//设置提醒方式
//                .setType(NotificationHelper.NOTIFICATION_TYPE_DOWNLOAD)//通知类型
//                .setSmallIcon(R.mipmap.ic_launcher)
//                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.icon_error))
//                .setNotificationId(123);
//
//        notificationHelper.setProgress(50, new Intent(Intent.ACTION_SEND));
//        notificationHelper.cancel();
//        notificationHelper.notifyShow();
//
//        //优先级heigh（4）应用内通知，弹出框
//        new NotificationHelper(this, "QQ")
//                .setContent("收到一条未读消息")//通知内容
//                .setType(NotificationHelper.NOTIFICATION_TYPE_DIALOG)//通知类型
//                .setNotificationId(111)
//                .notifyShow();
//
//        //取消当前通知
////        notificationHelper.cancel();
//        //取消全部通知
////        NotificationHelper.cancelAll(this);
//
//
//        //权限申请
//        PermissionHelper.getInstance().applyPermission(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                Manifest.permission.READ_EXTERNAL_STORAGE}, new PermissionHelper.RequestPermissionCallBack() {
//
//            @Override
//            public void requestPermissionSuccess() {
//                //申请成功
//            }
//
//            @Override
//            public void requestPermissionFail() {
//                //申请被拒绝
//            }
//        });

//        //下载工具
//        new DownloadHelper(this,
//                "http://imtt.dd.qq.com/16891/337A49BBE7A8A0B42E2312893903BBB3.apk?fsname=com.coolsnow.screenshot_5.6.0_56000.apk",
//                new DownloadHelper.DownloadCallBack() {
//                    @Override
//                    public void installCancel() {
//                        //取消安装
//                    }
//
//                    @Override
//                    public void downloadSuccess(File file) {
//                        //下载成功
//                    }
//
//                    @Override
//                    public void downloadFail() {
//                        //下载失败
//                    }
//                })
//                .setIsForce(false)//设置是否强制升级
//                .setNotificationShow(true, "下载通知标题", R.mipmap.ic_launcher)//配置通知参数
//                .setDialogShow(true, "取消安装", "立即安装")//配置弹框
//                .setSavePath(Environment.getExternalStorageDirectory() + "/" + "checkUp")//配置下载路径
//                .setCheckUp(true)//配置是否为版本升级，true则自动执行安装apk
//                .start();//开启下载

        new NDialogBuilder(this, 0, 1.0f)
                .setTouchOutSideCancelable(false)
                .setMessage("", NDialogBuilder.MSG_LAYOUT_LEFT)
                .setDialogAnimation(NDialogBuilder.DIALOG_ANIM_NORMAL)
                .setTitle("发现新版本")
                .setBtnClickListener(true, "取消", "确定", null)
                .create()
                .show();

    }

    public void download(View view){
        downloadHelper = new DownloadHelper(this,
                "http://imtt.dd.qq.com/16891/1F9DFAAC8C158F24D5A320A044AD352A.apk?fsname=com.qiyi.video_9.6.5_81100.apk",
                new DownloadHelper.DownloadCallBack() {
                    @Override
                    public void installCancel() {
                        //取消安装
                    }

                    @Override
                    public void downloadSuccess(File file) {
                        //下载成功
                        //downloadHelper.stop();
                    }

                    @Override
                    public void downloadFail() {
                        //下载失败
                    }
                })
                .setIsForce(false)//设置是否强制升级
                .setNotificationShow(true, "下载通知标题", R.mipmap.ic_launcher)//配置通知参数
                .setDialogShow(false, "取消安装", "立即安装")//配置弹框
                .setSavePath(Environment.getExternalStorageDirectory() + "/" + "checkUp")//配置下载路径
                .setCheckUp(false);
        downloadHelper//配置是否为版本升级，true则自动执行安装apk
                .start();//开启下载
    }

}
