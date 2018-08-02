package x.com.dialogmobile.ProgressDialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.CountDownTimer;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import x.com.dialogmobile.R;

/**
 * 加载进度框，10秒后弹出出错提示
 */
public class PDialog1Builder {
    private Dialog dialog;
    private Context context;
    private TextView dialogMsg;
    private long mtime = 10000;
    private onProgressFinishListener finishListener;
    // 弹出dialog时候是否要显示阴影
    private static boolean dimEnable = true;
    private static CountDownTimer a;
    /**
     * 对话框透明比例
     */
    public static final float ALPHAFACTOR = 1.0f;
    /**
     * 对话框宽度所占屏幕宽度的比例
     */
    public static final float WIDTHFACTOR = 0.75f;

    /**
     * 构造器
     *
     * @param context          上下文
     * @param layoutStyle      对话框布局样式
     * @param widthcoefficient 对话框宽度时占屏幕宽度的比重（0-1）
     */
    public PDialog1Builder(Context context, int layoutStyle, float widthcoefficient) {
        this(context, layoutStyle, false, widthcoefficient, dimEnable);
    }

    /**
     * 构造器
     *
     * @param context
     * @param layoutStyle      布局样式
     * @param isSystemAlert    是否是系统弹框（service等地方用到系统级别不依赖activity）
     * @param widthcoefficient 对话框宽度所占屏幕宽度的比重（0-1）
     */
    private PDialog1Builder(Context context, int layoutStyle, boolean isSystemAlert, float widthcoefficient, boolean dimEnable) {
        Dialog dialog;
        if (dimEnable) {
            dialog = new Dialog(context, R.style.Dialog);
        } else {
            dialog = new Dialog(context, R.style.DialogDim);
        }
        // 设置对话框风格
        if (layoutStyle == 0) {
            layoutStyle = R.layout.pdialog_layout;
        }
        dialog.setContentView(layoutStyle);
        Window window = dialog.getWindow();
        // 是否系统级弹框
        if (isSystemAlert) {
            window.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        }
        // 获取屏幕宽度
        DisplayMetrics metrics = new DisplayMetrics();
        window.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int screenwidth = metrics.widthPixels;
        int width = 0;
        if (widthcoefficient > 0) {
            width = (int) (screenwidth * widthcoefficient);
        } else {
            width = (int) (screenwidth * WIDTHFACTOR);
        }
        // 设置对话框宽度
        window.getAttributes().width = width;
        window.setGravity(Gravity.CENTER);
        this.dialog = dialog;
        this.context = context;
    }


    //属性设置
    public PDialog1Builder setTouchOutSideCancelable(boolean touchOutSideCancel) {
        this.dialog.setCanceledOnTouchOutside(touchOutSideCancel);
        this.dialog.setCancelable(touchOutSideCancel);
        return this;
    }

    /**
     * 设置对话框的消息内容
     *
     * @param message 消息内容
     * @return this
     */
    public PDialog1Builder setMessage(String message) {
        dialogMsg = dialog.findViewById(R.id.pdialog_message);
        if (dialogMsg != null) {
            if (message != null) {
                dialogMsg.setText(message);
                dialogMsg.setVisibility(View.VISIBLE);
            } else {
                dialogMsg.setVisibility(View.GONE);
            }
        }
        return this;
    }

    public PDialog1Builder settime(long mtime) {
        this.mtime = mtime;
        return this;
    }

    public interface onProgressFinishListener {
        void onProgressFinish();
    }

    public PDialog1Builder setonInputCodeListener(final onProgressFinishListener finishListener) {
        this.finishListener = finishListener;
        return this;
    }

    public Dialog create() {
        if (context instanceof Activity) {
            dialog.setOwnerActivity((Activity) context);
        }
        a = new CountDownTimer(mtime, 1000) {
            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                //显示错误对话框
                dialog.dismiss();
                finishListener.onProgressFinish();

            }
        }.start();
        return dialog;
    }

    public static void stop() {
        if (a != null) {
            a.cancel();
        }
    }
}
