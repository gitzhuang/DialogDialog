package x.com.dialogmobile

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast

/**
 * Created by gps-xing on 2016/11/1.
 */

class NewToast(context: Context) : Toast(context) {
    companion object {
        fun makeText(context: Context, resId: Int, text: CharSequence, duration: Int): Toast {
            val result = Toast(context)

            //获取LayoutInflater对象
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            //由layout文件创建一个View对象
            val layout = inflater.inflate(R.layout.toast_message, null)

            //实例化ImageView和TextView对象
            val imageView = layout.findViewById<View>(R.id.dialog_title) as ImageView
            val textView = layout.findViewById<View>(R.id.toast_message) as TextView

            imageView.setImageResource(resId)
            textView.text = text

            result.view = layout
            result.setGravity(Gravity.CENTER_VERTICAL, 0, 0)
            result.duration = duration

            return result
        }
    }
}
