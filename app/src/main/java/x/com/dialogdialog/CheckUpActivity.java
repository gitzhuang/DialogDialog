package x.com.dialogdialog;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Toast;

import java.io.File;

import androidx.appcompat.app.AppCompatActivity;
import x.com.dialogmobile.CheckUpdate.CheckDialogFragment;
import x.com.dialogmobile.DownloadHelper;
import x.com.dialogmobile.NDialogBuilder;
import x.com.dialogmobile.PermissionHelper;

import static x.com.dialogmobile.NDialogBuilder.MSG_LAYOUT_LEFT;


public class CheckUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_up);
        //检查更新
        new CheckDialogFragment(this,
                "版本更新内容",
                0,
//                "http://imtt.dd.qq.com/16891/50CC095EFBE6059601C6FB652547D737.apk?fsname=com.tencent.mm_6.6.7_1321.apk&csr=1bbd",
//                "http://imtt.dd.qq.com/16891/337A49BBE7A8A0B42E2312893903BBB3.apk?fsname=com.coolsnow.screenshot_5.6.0_56000.apk&csr=1bbd",
                "http://imtt.dd.qq.com/16891/337A49BBE7A8A0B42E2312893903BBB3.apk?fsname=com.coolsnow.screenshot_5.6.0_56000.apk",
//                "http://imtt.dd.qq.com/16891/1F9DFAAC8C158F24D5A320A044AD352A.apk?fsname=com.qiyi.video_9.6.5_81100.apk",
                0,
                "下载失败提示信息",
                new CheckDialogFragment.OnCheckcallback() {
                    @Override
                    public void onCancel() {
                        Toast.makeText(CheckUpActivity.this, "345", Toast.LENGTH_SHORT).show();
                    }
                }).show(getSupportFragmentManager(), "checkup");


        //下载工具
        new DownloadHelper(this,
                "http://imtt.dd.qq.com/16891/337A49BBE7A8A0B42E2312893903BBB3.apk?fsname=com.coolsnow.screenshot_5.6.0_56000.apk",
                new DownloadHelper.DownloadCallBack() {
                    @Override
                    public void installCancel() {
                        //取消安装
                    }

                    @Override
                    public void downloadSuccess(File file) {
                        //下载成功
                    }

                    @Override
                    public void downloadFail() {
                        //下载失败
                    }
                })
                //设置是否强制升级
                .setIsForce(false)
                //配置通知参数
                .setNotificationShow(true, "下载通知标题", R.mipmap.ic_launcher)
                //配置弹框
                .setDialogShow(true, "取消安装", "立即安装")
                //配置下载路径
                .setSavePath(Environment.getExternalStorageDirectory() + "/" + "checkUp")
                //配置是否为版本升级，true则自动执行安装apk
                .setCheckUp(true)
                //配置存储地址 默认Environment.getExternalStorageDirectory() + "/" + "deanDownload";
                .setSavePath(Environment.getExternalStorageDirectory() + "/" + "deanDownload");
                //开启下载
                //.start();

    }

    public void download(View view) {
        new DownloadHelper(this,
                "http://imtt.dd.qq.com/16891/337A49BBE7A8A0B42E2312893903BBB3.apk?fsname=com.coolsnow.screenshot_5.6.0_56000.apk",
//                "http://imtt.dd.qq.com/16891/1F9DFAAC8C158F24D5A320A044AD352A.apk?fsname=com.qiyi.video_9.6.5_81100.apk",
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
                .setCheckUp(false)//配置是否为版本升级，true则自动执行安装apk
                .start();//开启下载

    }

    public void stop(View view) {
        DownloadHelper.cancelAll();
    }

    public void requesPermission(View view) {
        //权限申请
        PermissionHelper.getInstance().applyPermission(this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                }, new PermissionHelper.RequestPermissionCallBack() {

                    @Override
                    public void requestPermissionSuccess() {
                        //申请成功
                        Toast.makeText(CheckUpActivity.this, "申请成功", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void requestPermissionFail() {
                        //申请被拒绝
                        Toast.makeText(CheckUpActivity.this, "申请失败", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void showDialog(View view) {
        new NDialogBuilder(this, 0, 1.0f)
                .setTouchOutSideCancelable(false)
                .setMessage("", MSG_LAYOUT_LEFT)
                .setDialogAnimation(NDialogBuilder.DIALOG_ANIM_NORMAL)
                .setTitle("发现新版本")
                .setMessage("内容", MSG_LAYOUT_LEFT)
                .setBtnClickListener(true, "取消", "确定", null)
                .create()
                .show();
    }

    public void intent(View view) {
        startActivity(new Intent(this, NotificationActivity.class));
    }

}
