package x.com.dialogmobile.LoginDialog;

import android.app.Dialog;
import android.content.Context;
import android.text.InputType;
import android.widget.Toast;

import x.com.dialogmobile.EDialogBuilder;
import x.com.dialogmobile.NDialogBuilder;

public class LDialogBuilder {
    private Context context;
    private Dialog edialog1, edialog2;
    private String phone;
    private onInputPhone inputPhone;
    private onInputCode inputCode;

    public interface onInputPhone {
        void inputPhonenext(String phone, int type);
    }

    public interface onInputCode {
        void inputCodenext(String phone, String code);
    }

    public LDialogBuilder(Context context) {
        this.context = context;
    }

    /**
     * 设置时间到了监听
     */
    public LDialogBuilder setonInputPhoneListener(final onInputPhone inputPhone) {
        this.inputPhone = inputPhone;
        return this;
    }

    /**
     * 设置时间到了监听
     */
    public LDialogBuilder setonInputCodeListener(final onInputCode inputCode) {
        this.inputCode = inputCode;
        return this;
    }

    public LDialogBuilder inputphone() {
        edialog1 = new EDialogBuilder(context, 0, 1.0f).setTouchOutSideCancelable(false)
                .setMessage("您的手机号码", NDialogBuilder.MSG_LAYOUT_LEFT)
                .setInputtype(InputType.TYPE_CLASS_NUMBER)
                .setDialogAnimation(EDialogBuilder.DIALOG_ANIM_SLID_RIGHT)
                .setBtnClickListener(false, "下一步", new EDialogBuilder.onDialogbtnClickListener() {
                    @Override
                    public void onDialogbtnClick(Context context, Dialog dialog, int whichBtn, String inputtext) {
                        if (whichBtn == 1) {
                            phone = inputtext;
                            if (phone.isEmpty() || phone.length() != 11) {
                                Toast.makeText(context, "手机号不符合要求", Toast.LENGTH_SHORT).show();
                            } else {
                                inputPhone.inputPhonenext(phone, 0);
                            }
                        }
                    }
                })
                .create();
        edialog1.show();
        return this;
    }


    public LDialogBuilder inputcode(String phonenum) {
        edialog2 = new EDialog2Builder(context, 0, 1.0f)
                .setTouchOutSideCancelable(false)
                .setTitle("输入验证码")
                .setMessage("验证码已发送至" + phonenum, NDialogBuilder.MSG_LAYOUT_CENTER)
                .setDialogAnimation(EDialogBuilder.DIALOG_ANIM_SLID_RIGHT)
                .setBtnClickListener(false, "点击重新获取", new EDialog2Builder.onDialogbtnClickListener() {
                    @Override
                    public void onDialogbtnClick(Context context, Dialog dialog, int whichBtn) {
                        if (whichBtn == 1) {
                            inputPhone.inputPhonenext(phone, 1);
                        }
                    }
                })
                .setonInputFinishListener(new EDialog2Builder.onInputFinishListener() {
                    @Override
                    public void onInputFinish(String vcode) {
                        inputCode.inputCodenext(phone, vcode);
                    }
                })
                .setOnTimeoutListener(new EDialog2Builder.onOutTimeListener() {
                    @Override
                    public void onOutTime() {
                    }
                })
                .create();
        edialog2.show();
        return this;
    }

    public void dismissdialog1() {
        if (edialog1 != null && edialog1.isShowing()) {
            edialog1.dismiss();
        }
    }

    public void dismissdialog2() {
        if (edialog2 != null && edialog2.isShowing()) {
            edialog2.dismiss();
        }
    }
}
