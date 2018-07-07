package x.com.dialogmobile;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 单横线输入框
 */
public class EDialogBuilder {
    /**
     * 对话框处于屏幕顶部位置
     */
    public static final int DIALOG_LOCATION_TOP = 12;
    /**
     * 对话框处于屏幕中间位置
     */
    public static final int DIALOG_LOCATION_CENTER = 10;
    /**
     * 对话框处于屏幕底部位置
     */
    public static final int DIALOG_LOCATION_BOTTOM = 11;
    /**
     * 消息位于对话框的位置 居左
     */
    public static final int MSG_LAYOUT_LEFT = 1;
    /**
     * 消息位于对话框的位置 居中
     */
    public static final int MSG_LAYOUT_CENTER = 0;
    /**
     * 缩放动画
     */
    public static final int DIALOG_ANIM_NORMAL = R.style.DialogAnimation;
    /**
     * 从下往上滑动动画
     */
    public static final int DIALOG_ANIM_SLID_BOTTOM = R.style.DialogAnimationSlidBottom;
    /**
     * 从上往下滑动动画
     */
    public static final int DIALOG_ANIM_SLID_TOP = R.style.DialogAnimationSlidTop;
    /**
     * 从右往左滑动动画
     */
    public static final int DIALOG_ANIM_SLID_RIGHT = R.style.DialogAnimationSlidRight;
    /**
     * 对话框透明比例
     */
    public static final float ALPHAFACTOR = 1.0f;
    // 弹出dialog时候是否要显示阴影
    private static boolean dimEnable = true;
    /**
     * 对话框宽度所占屏幕宽度的比例
     */
    public static final float WIDTHFACTOR = 0.75f;
    /**
     * Dialog对象
     */
    private Dialog dialog;
    private Context context;
    private TextView dialogTitle, dialogMsg;
    private EditText et_input;


    /**
     * 构造器
     *
     * @param context          上下文
     * @param layoutStyle      对话框布局样式
     * @param widthcoefficient 对话框宽度时占屏幕宽度的比重（0-1）
     */
    public EDialogBuilder(Context context, int layoutStyle, float widthcoefficient) {
        this(context, layoutStyle, false, widthcoefficient, ALPHAFACTOR, dimEnable);
    }

    /**
     * 构造器
     *
     * @param context
     * @param layoutStyle      布局样式
     * @param isSystemAlert    是否是系统弹框（service等地方用到系统级别不依赖activity）
     * @param widthcoefficient 对话框宽度所占屏幕宽度的比重（0-1）
     * @param alpha            对话框透明度
     */
    private EDialogBuilder(Context context, int layoutStyle, boolean isSystemAlert, float widthcoefficient, float alpha, boolean dimEnable) {
        Dialog dialog;
        if (dimEnable) {
            dialog = new Dialog(context, R.style.Dialog);
        } else {
            dialog = new Dialog(context, R.style.DialogDim);
        }
        // 设置对话框风格
        if (layoutStyle == 0) {
            layoutStyle = R.layout.edialog_layout;
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

        // 设置透明
        WindowManager.LayoutParams lp = window.getAttributes();
        if (alpha > 0) {
            lp.alpha = 1.0f;
        } else {
            lp.alpha = ALPHAFACTOR;
        }
        window.setAttributes(lp);
        ImageView imgv_clear = dialog.findViewById(R.id.imgv_clear);
        imgv_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (et_input != null) {
                    et_input.setText("");
                }
            }
        });
        this.dialog = dialog;
        this.context = context;
    }


    //属性设置
    public EDialogBuilder setTouchOutSideCancelable(boolean touchOutSideCancel) {
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
    public EDialogBuilder setMessage(String message, int layout) {
        dialogMsg = dialog.findViewById(R.id.mdialog_message);
        if (dialogMsg != null) {
            if (message != null) {
                dialogMsg.setText(message);
                dialogMsg.setVisibility(View.VISIBLE);
                if (layout == MSG_LAYOUT_LEFT) {
                    dialogMsg.setGravity(Gravity.START);
                } else if (layout == MSG_LAYOUT_CENTER) {
                    dialogMsg.setGravity(Gravity.CENTER);
                }
            } else {
                dialogMsg.setVisibility(View.GONE);
            }
        }
        return this;
    }

    /**
     * 设置对话框标题
     *
     * @param title 标题
     * @return this
     */
    public EDialogBuilder setTitle(String title) {
        dialogTitle = dialog.findViewById(R.id.mdialog_title);
        if (title != null) {
            dialogTitle.setText(title);
            dialogTitle.setVisibility(View.VISIBLE);
        } else {
            dialogTitle.setVisibility(View.GONE);
        }
        return this;
    }

    /**
     * 设置输入类型
     */
    public EDialogBuilder setInputtype(int type) {
        et_input = dialog.findViewById(R.id.et_input);
        if (type != -1) {
            et_input.setInputType(type);
        } else {
            et_input.setInputType(InputType.TYPE_CLASS_TEXT);
        }
        return this;
    }

    /**
     * 给对话框设置动画
     *
     * @param resId
     */
    public EDialogBuilder setDialogAnimation(int resId) {
        this.dialog.getWindow().setWindowAnimations(resId);
        return this;
    }

    /**
     * 设置对话框的位置
     *
     * @param location
     * @return
     */
    public EDialogBuilder setDialoglocation(int location) {
        Window window = this.dialog.getWindow();
        switch (location) {
            case DIALOG_LOCATION_CENTER:
                window.setGravity(Gravity.CENTER);
                break;
            case DIALOG_LOCATION_BOTTOM:
                window.setGravity(Gravity.BOTTOM);
                break;
            case DIALOG_LOCATION_TOP:
                window.setGravity(Gravity.TOP);
                break;
        }
        return this;
    }

    /**
     * 监听器监听对话框按钮点击
     *
     * @author zhl
     */
    public interface onDialogbtnClickListener {
        /**
         * （区分点击的事左边按钮还是右边按钮）--确认
         */
        int BUTTON_CONFIRM = 1;
        /**
         * （区分点击的事左边按钮还是右边按钮）--取消
         */
        int BUTTON_CANCEL = 2;

        /**
         * @param context  上下文
         * @param dialog   点击的哪个对话框
         * @param whichBtn 点击的哪个按钮
         */
        void onDialogbtnClick(Context context, Dialog dialog, int whichBtn, String inputtext);

    }

    //默认一个按钮
    public EDialogBuilder setBtnClickListener(final boolean isDissmiss, String btn1text, final onDialogbtnClickListener btnClickListener) {
        return this.setClickListener(isDissmiss, R.id.mdialog_btn1, btn1text, 0, "", btnClickListener);
    }

    //自定义一个按钮
    public EDialogBuilder setBtnClickListener(final boolean isDissmiss, int btn1, String btn1text, final onDialogbtnClickListener btnClickListener) {
        return this.setClickListener(isDissmiss, btn1, btn1text, 0, "", btnClickListener);
    }

    //默认两个个按钮
    public EDialogBuilder setBtnClickListener(final boolean isDissmiss, String btn1text, String btn2text, final onDialogbtnClickListener btnClickListener) {
        return this.setClickListener(isDissmiss, R.id.mdialog_btn1, btn1text, R.id.mdialog_btn2, btn2text, btnClickListener);
    }

    //自定义两个个按钮
    public EDialogBuilder setBtnClickListener(final boolean isDissmiss, int btn1, String btn1text, int btn2, String btn2text, final onDialogbtnClickListener btnClickListener) {
        return this.setClickListener(isDissmiss, btn1, btn1text, btn2, btn2text, btnClickListener);
    }

    /**
     * 给按钮设置回调监听
     *
     * @param btnClickListener 按钮的回调监听
     * @param isDissmiss       点击按钮后是否取消对话框
     * @return
     */
    private EDialogBuilder setClickListener(final boolean isDissmiss, int btn1, String btn1text, int btn2, String btn2text, final onDialogbtnClickListener btnClickListener) {
        if (btn1 != 0) {
            // 设置确认按钮
            final Button btnConfirm = dialog.findViewById(btn1);
            btnConfirm.setText(btn1text);
            btnConfirm.setVisibility(View.VISIBLE);
            // 给按钮绑定监听器
            btnConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isDissmiss) {
                        dialog.dismiss();
                    }
                    if (btnClickListener != null) {
                        btnClickListener.onDialogbtnClick(context, dialog,
                                onDialogbtnClickListener.BUTTON_CONFIRM, et_input.getText().toString());
                    }
                }
            });
            if (btn2 == 0) {
                btnConfirm.setBackgroundResource(R.drawable.button_onclick);
            }
        }
        if (btn2 != 0) {
            // 设置取消按钮
            final Button btnCancel = dialog.findViewById(btn2);
            btnCancel.setVisibility(View.VISIBLE);
            btnCancel.setText(btn2text);
            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isDissmiss) {
                        dialog.dismiss();
                    }
                    if (btnClickListener != null) {
                        btnClickListener.onDialogbtnClick(context, dialog,
                                onDialogbtnClickListener.BUTTON_CANCEL, "");
                    }
                }
            });
        }
        return this;
    }

    /**
     * 创建对话框
     *
     * @return dialog
     */
    public Dialog create() {
        if (context instanceof Activity) {
            dialog.setOwnerActivity((Activity) context);
        }
        return dialog;
    }
}
