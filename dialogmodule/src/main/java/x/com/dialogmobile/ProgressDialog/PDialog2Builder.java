package x.com.dialogmobile.ProgressDialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import x.com.dialogmobile.R;

public class PDialog2Builder {
    private Dialog dialog;
    private Dialog errordialog;
    private Context context;
    private TextView dialogTitle, dialogMsg, tvCancle, tvSure, tvProgress;
    private ProgressBar progressBar;
    // 弹出dialog时候是否要显示阴影
    private static boolean dimEnable = true;
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
    public PDialog2Builder(Context context, int layoutStyle, float widthcoefficient) {
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
    private PDialog2Builder(Context context, int layoutStyle, boolean isSystemAlert, float widthcoefficient, boolean dimEnable) {
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
        this.dialog = dialog;
        this.context = context;
    }


    //属性设置
    public PDialog2Builder setTouchOutSideCancelable(boolean touchOutSideCancel) {
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
    public PDialog2Builder setMessage(String message) {
        dialogMsg = dialog.findViewById(R.id.mdialog_message);
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

    /**
     * 取消按钮
     * @param cancleTitle
     * @param listener
     * @return
     */
    public PDialog2Builder setBtnCancle(String cancleTitle, View.OnClickListener listener) {
        tvCancle = dialog.findViewById(R.id.tv_cancle);
        if (dialog != null) {
            if (cancleTitle != null) {
                tvCancle.setText(cancleTitle);
                tvCancle.setVisibility(View.VISIBLE);
            } else {
                tvCancle.setVisibility(View.GONE);
            }
        }
        tvCancle.setOnClickListener(listener);
        return this;
    }

    /**
     * 确定按钮
     * @param cancleTitle
     * @param listener
     * @return
     */
    public PDialog2Builder setBtnSure(String cancleTitle, View.OnClickListener listener) {
        tvSure = dialog.findViewById(R.id.tv_sure);
        if (dialog != null) {
            if (cancleTitle != null) {
                tvSure.setText(cancleTitle);
                tvSure.setVisibility(View.VISIBLE);
            } else {
                tvSure.setVisibility(View.GONE);
            }
        }
        tvSure.setOnClickListener(listener);
        return this;
    }

    /**
     * 设置取消、确定按钮是否显示
     * @param btnCancle
     * @param btnSure
     * @return
     */
    public PDialog2Builder setBtnVisity(boolean btnCancle, boolean btnSure){

        if(tvCancle != null){
            tvCancle.setVisibility(btnCancle ? View.VISIBLE :View.GONE);
        }
        if(tvSure != null){
            tvSure.setVisibility(btnSure ? View.VISIBLE :View.GONE);
        }

        return this;
    }

    /**
     * 设置取消、确定按钮是否显示
     * @param progress
     * @return
     */
    public PDialog2Builder setProgress(int progress){
        if(progressBar == null){
            progressBar = dialog.findViewById(R.id.progress);
        }
        if (tvProgress == null) {
            tvProgress = dialog.findViewById(R.id.tv_progress);
        }
        progressBar.setProgress(progress);
        tvProgress.setText(String.valueOf(progress));
        return this;
    }

    /**
     * 进度框超时监听
     *
     * @author zhl
     */
    public interface onProgressOutTimeListener {
        public void onProgressOutTime(Dialog dialog, TextView dialogMsgTextView);
    }

    /**
     * 创建对话框
     *
     * @return
     */

    public Dialog create() {
        if (context instanceof Activity) {
            dialog.setOwnerActivity((Activity) context);
        }
//        new CountDownTimer(10000, 1000) {
//            public void onTick(long millisUntilFinished) {
//            }
//
//            public void onFinish() {
//                //显示错误对话框
//                dialog.dismiss();
//                errordialog = new NDialogBuilder(context, 0, 1.0f)
//                        .setTouchOutSideCancelable(false)
//                        .setMessage("345tret", NDialogBuilder.MSG_LAYOUT_LEFT)
//                        .setDialogAnimation(NDialogBuilder.DIALOG_ANIM_NORMAL)
//                        .setBtnClickListener(true, "hao", new NDialogBuilder.onDialogbtnClickListener() {
//                            @Override
//                            public void onDialogbtnClick(Context context, Dialog dialog, int whichBtn) {
//                                errordialog.dismiss();
//                            }
//                        })
//                        .create();
//                errordialog.show();
//            }
//        }.start();
        return dialog;
    }
}
