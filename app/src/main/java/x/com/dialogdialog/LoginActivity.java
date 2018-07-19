package x.com.dialogdialog;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import x.com.dialogmobile.LoginDialog.LDialogBuilder;

public class LoginActivity extends AppCompatActivity {
    private LDialogBuilder a;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        a = new LDialogBuilder(this).inputphone().setonInputPhoneListener(new LDialogBuilder.onInputPhone() {
            @Override
            public void inputPhonenext(String phone, int type) {
                if (type == 0) {
                    a.dismissdialog1();
                    a.inputcode(phone).setonInputCodeListener(new LDialogBuilder.onInputCode() {
                        @Override
                        public void inputCodenext(String phone, String code) {
                            //校验验证码，接口调用
                        }
                    });
                }
            }
        });
//        a.dismissdialog2();
    }
}
