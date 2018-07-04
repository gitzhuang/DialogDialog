package x.com.dialogmobile.CheckUpdate;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import x.com.dialogmobile.NDialogBuilder;

public class CheckDialogFragment extends DialogFragment {
    private final String[] mPermissionList = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
    };

    private final String[] mPermissionList_O = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.REQUEST_INSTALL_PACKAGES,
    };


    private AppCompatActivity activity;
    private int layoutStyle;
    private String downloadUrl;
    private int isforce;
    private String msg;
    private int notificationIconId;
    private String notificationTitle;
    private OnCheckcallback callback;
    private DownloadHelper downloadHelper;

    public interface OnCheckcallback{
        void onCancel();
    }

    public CheckDialogFragment(AppCompatActivity activity, String msg, int layoutStyle, String downloadUrl, int isforce, int notificationIconId, String notificationTitle, CheckDialogFragment.OnCheckcallback callback) {
        this.activity = activity;
        this.layoutStyle = layoutStyle;
        this.downloadUrl = downloadUrl;
        this.isforce = isforce;
        this.msg = msg;
        this.notificationIconId = notificationIconId;
        this.notificationTitle = notificationTitle;
        this.callback = callback;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Log.d("TAG", "onCreateDialog: " + this.toString());
        return new NDialogBuilder(getContext(), layoutStyle, 1.0f)
                .setTouchOutSideCancelable(false)
                .setMessage(msg, NDialogBuilder.MSG_LAYOUT_LEFT)
                .setDialogAnimation(NDialogBuilder.DIALOG_ANIM_NORMAL)
                .setTitle("发现新版本")
                .setBtnClickListener(true, isforce == 1 ? "退出程序" : "稍后更新" , "立即更新",
                        new NDialogBuilder.onDialogbtnClickListener() {
                            @Override
                            public void onDialogbtnClick(Context context, final Dialog dialog, int whichBtn) {
                                switch (whichBtn) {
                                    case 1://稍后、退出
                                        if (isforce == 1) {
                                            System.exit(0);
                                        } else {
                                            callback.onCancel();
                                        }
                                        break;
                                    case 2://立即更新
                                        PermissionHelper.getInstance().requestPermission(
                                                CheckDialogFragment.this,
                                                Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ? mPermissionList_O : mPermissionList,
                                                new PermissionHelper.RequestPermissionCallBack(){

                                                    @Override
                                                    public void requestPermissionAgainHint() {
                                                        Toast.makeText(activity, "此功能需要允许存储权限，才能正常使用。", Toast.LENGTH_LONG).show();
                                                    }

                                                    @Override
                                                    public void requestPermissionSuccess() {
                                                        downloadHelper = new DownloadHelper(
                                                                activity,
                                                                downloadUrl,
                                                                isforce,
                                                                notificationIconId,
                                                                notificationTitle,
                                                                new DownloadHelper.DownloadCallBack() {
                                                                    @Override
                                                                    public void downloadCancel() {
                                                                        //取消下载
                                                                        Toast.makeText(activity, "取消了", Toast.LENGTH_SHORT).show();
                                                                    }

                                                                    @Override
                                                                    public void installCancel() {
                                                                        //取消安装
                                                                        Toast.makeText(activity, "取消安装了", Toast.LENGTH_SHORT).show();
                                                                    }

                                                                    @Override
                                                                    public void downloadFail() {
                                                                        activity.runOnUiThread(new Runnable() {
                                                                            @Override
                                                                            public void run() {
                                                                                new NDialogBuilder(activity, 0, 1.0f)
                                                                                        .setTitle("下载失败")
                                                                                        .setTouchOutSideCancelable(false)
                                                                                        .setMessage("123", NDialogBuilder.MSG_LAYOUT_LEFT)
                                                                                        .setDialogAnimation(NDialogBuilder.DIALOG_ANIM_NORMAL)
                                                                                        .setBtnClickListener(true, "重新下载", new NDialogBuilder.onDialogbtnClickListener() {
                                                                                            @Override
                                                                                            public void onDialogbtnClick(Context context, Dialog dialog, int whichBtn) {
                                                                                                downloadHelper.startDownload();
                                                                                            }
                                                                                        })
                                                                                        .create().show();
                                                                            }
                                                                        });
                                                                    }
                                                                });
                                                    }

                                                    @Override
                                                    public void requestPermissionFail() {
                                                        Toast.makeText(activity, "权限申请失败", Toast.LENGTH_SHORT).show();
                                                    }
                                                });

                                        break;
                                }
                            }
                        })
                .create();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionHelper.getInstance().requestPermissionsResult(getContext(), requestCode, permissions, grantResults);
        dismiss();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        //super.onDismiss(dialog);
    }
}
