package x.com.dialogmobile.CheckUpdate;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;

import x.com.dialogmobile.NDialogBuilder;

public class CheckDialog {
    private String btn1text;

    public CheckDialog(Context context, int layoutStyle, int isforce, String msg, OnCheckcallback callback) {
        if (isforce == 1) {
            btn1text = "退出程序";
        } else {
            btn1text = "稍后更新";
        }
        check(context, layoutStyle, msg, isforce, callback);
    }

    public interface OnCheckcallback {
        void onSuccess();
    }

    private void check(Context mContext, int layoutStyle, String msg, final int isforce, final OnCheckcallback callback) {
        new NDialogBuilder(mContext, layoutStyle, 1.0f)
                .setTouchOutSideCancelable(false)
                .setMessage(msg, NDialogBuilder.MSG_LAYOUT_LEFT)
                .setDialogAnimation(NDialogBuilder.DIALOG_ANIM_NORMAL)
                .setTitle("发现新版本")
                .setBtnClickListener(true, btn1text, "立即更新",
                        new NDialogBuilder.onDialogbtnClickListener() {
                            @Override
                            public void onDialogbtnClick(Context context, Dialog dialog, int whichBtn) {
                                switch (whichBtn) {
                                    case 1://稍后、退出
                                        if (isforce == 1) {
                                            System.exit(0);
                                        } else {
                                            callback.onSuccess();
                                        }
                                        break;
                                    case 2://立即更新
                                        Log.d("gengxin", "gengxin");
                                        break;
                                }
                            }
                        })
                .create()
                .show();
    }
}