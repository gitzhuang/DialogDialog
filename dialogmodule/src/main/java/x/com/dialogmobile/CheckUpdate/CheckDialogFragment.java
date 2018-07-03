package x.com.dialogmobile.CheckUpdate;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;
import x.com.dialogmobile.NDialogBuilder;

public class CheckDialogFragment extends DialogFragment {
    private Activity activity;
    private int layoutStyle;
    private String downloadUrl;
    private int isforce;
    private String msg;
    private OnCheckcallback callback;

    public interface OnCheckcallback{
        void onCancel();
    }

    public CheckDialogFragment(Activity activity, int layoutStyle, String downloadUrl, int isforce, String msg, CheckDialogFragment.OnCheckcallback callback) {
        this.activity = activity;
        this.layoutStyle = layoutStyle;
        this.downloadUrl = downloadUrl;
        this.isforce = isforce;
        this.msg = msg;
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
                            public void onDialogbtnClick(Context context, Dialog dialog, int whichBtn) {
                                switch (whichBtn) {
                                    case 1://稍后、退出
                                        if (isforce == 1) {
                                            System.exit(0);
                                        } else {
                                            callback.onCancel();
                                        }
                                        break;
                                    case 2://立即更新
                                        PermissionHelper.getInstance().requestSavePermission(
                                                CheckDialogFragment.this,
                                                new PermissionHelper.RequestPermissionCallBack() {
                                            @Override
                                            public void requestPermissionSuccess() {
                                                new DownloadHelper(activity, downloadUrl, isforce, new DownloadHelper.DownloadCallBack() {
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
                                                });
                                            }

                                            @Override
                                            public void requestPermissionFail() {
                                                //申请权限被拒绝
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
