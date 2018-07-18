package x.com.dialogmobile.CheckUpdate;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.Toast;

import java.io.File;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import x.com.dialogmobile.DownloadHelper;
import x.com.dialogmobile.NDialogBuilder;
import x.com.dialogmobile.PermissionHelper;
import x.com.dialogmobile.R;

public class CheckDialogFragment extends DialogFragment {
    private Activity activity;
    private int layoutStyle;
    private String downloadUrl;
    private int isforce;
    private String msg;
    private OnCheckcallback callback;
    private DownloadHelper downloadHelper;
    private String downloadFailMessgae;
    private boolean isCheckUp = true;
    private Dialog dialog;

    private final String[] mPermissionList = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
    };

    private final String[] mPermissionList_O = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
    };


    public interface OnCheckcallback {
        void onCancel();

        void onInstallCancel();

        void onDownloadFinish(File apkFile);
    }

    public CheckDialogFragment(Activity activity, String msg, int layoutStyle, String downloadUrl,
                               int isforce, String downloadFailMessgae,
                               CheckDialogFragment.OnCheckcallback callback) {
        this.activity = activity;
        this.layoutStyle = layoutStyle;
        this.downloadUrl = downloadUrl;
        this.isforce = isforce;
        this.msg = msg;
        this.downloadFailMessgae = downloadFailMessgae;
        this.callback = callback;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        dialog = new NDialogBuilder(getContext(), layoutStyle, 1.0f)
            .setTouchOutSideCancelable(false)
            .setMessage(msg, NDialogBuilder.MSG_LAYOUT_LEFT)
            .setDialogAnimation(NDialogBuilder.DIALOG_ANIM_NORMAL)
            .setTitle("发现新版本")
            .setBtnClickListener(true, isforce == 1 ? "退出程序" : "稍后更新", "立即更新",
                    new NDialogBuilder.onDialogbtnClickListener() {
                        @Override
                        public void onDialogbtnClick(Context context, final Dialog dialog, int whichBtn) {
                            switch (whichBtn) {
                                case 1://稍后、退出
                                    if (isforce == 1) {
                                        System.exit(0);
                                    } else {
                                        callback.onCancel();
                                        dismiss();
                                    }
                                    break;
                                case 2://立即更新
                                    PermissionHelper.getInstance().applyPermission(
                                            CheckDialogFragment.this,
                                            Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ? mPermissionList_O : mPermissionList,
                                            new PermissionHelper.RequestPermissionCallBack() {
                                                @Override
                                                public void requestPermissionSuccess() {
                                                    dismiss();
                                                    downloadHelper = new DownloadHelper(activity, downloadUrl,
                                                            new DownloadHelper.DownloadCallBack() {

                                                                @Override
                                                                public void installCancel() {
                                                                    //取消安装
                                                                    callback.onInstallCancel();
                                                                }

                                                                @Override
                                                                public void downloadSuccess(File file) {
                                                                    //下载完成，setCheckUp(true)时自动执行安装
                                                                    callback.onDownloadFinish(file);
                                                                }


                                                                @Override
                                                                public void downloadFail() {
                                                                    activity.runOnUiThread(new Runnable() {
                                                                        @Override
                                                                        public void run() {
                                                                            new NDialogBuilder(activity, 0, 1.0f)
                                                                                    .setTitle("下载失败")
                                                                                    .setTouchOutSideCancelable(false)
                                                                                    .setMessage(downloadFailMessgae, NDialogBuilder.MSG_LAYOUT_LEFT)
                                                                                    .setDialogAnimation(NDialogBuilder.DIALOG_ANIM_NORMAL)
                                                                                    .setBtnClickListener(true, "重新下载", new NDialogBuilder.onDialogbtnClickListener() {
                                                                                        @Override
                                                                                        public void onDialogbtnClick(Context context, Dialog dialog, int whichBtn) {
                                                                                            downloadHelper.start();
                                                                                        }
                                                                                    })
                                                                                    .create().show();
                                                                        }
                                                                    });
                                                                }
                                                            })
                                                    .setCheckUp(isCheckUp)
                                                    .setIsForce(isforce == 1)
                                                    .setNotificationShow(true, "正在下载...", R.mipmap.ic_launcher)
                                                    .setDialogShow(true, "下次再说", "立即安装");
                                                    downloadHelper.start();
                                                }

                                                @Override
                                                public void requestPermissionFail() {
                                                    dismiss();
                                                    Toast.makeText(activity, "权限申请失败", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                    break;
                            }
                        }
                    })
            .create();
        //屏蔽返回键
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                return keyEvent.getKeyCode() == KeyEvent.KEYCODE_BACK;
            }
        });
        return dialog;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionHelper.getInstance().requestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        //super.onDismiss(dialog);
    }

    public CheckDialogFragment setAutoInstall(boolean isAutoInstall){
        this.isCheckUp = isAutoInstall;
        return this;
    }
}
