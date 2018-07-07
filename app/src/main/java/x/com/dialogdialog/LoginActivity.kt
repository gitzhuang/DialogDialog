package x.com.dialogdialog

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import x.com.dialogmobile.LoginDialog.LDialogBuilder

/**
 * 手机号+验证码登录弹窗
 */
class LoginActivity : AppCompatActivity() {
    var a: LDialogBuilder? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        a = LDialogBuilder(this).inputphone()
                .setonInputPhoneListener { phone, i ->
                    //获取验证码，接口调用
                    if (i == 0) {
                        a!!.dismissdialog1()
                        a!!.inputcode(phone).setonInputCodeListener { phone, code ->
                            //校验验证码，接口调用
                        }
                    }
                }

//        a!!.dismissdialog2()


    }
}
