package x.com.dialogdialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import x.com.dialogmobile.NDialogBuilder

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        NDialogBuilder(this, 0, 1.0f)
                .setTouchOutSideCancelable(false)
                .setMessage("我是鬼，我来抓你了！", NDialogBuilder.MSG_LAYOUT_CENTER)
                .setBtnClickListener(true, "好", "不好") { context: Context, dialog: Dialog, i: Int ->
                    when (i) {
                        1 -> Toast.makeText(context, "点击了好", Toast.LENGTH_SHORT).show()
                        2 -> Toast.makeText(context, "点击了不好", Toast.LENGTH_SHORT).show()
                    }
                }
                .create()
                .show()
    }
}

