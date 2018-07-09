package x.com.dialogmobile;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by gps-xing on 2016/11/1.
 */

public class NewToast extends Toast {
    public NewToast(Context context) {
        super(context);
    }
    public static Toast makeText(Context context, int resId, CharSequence text, int duration) {
        Toast result = new Toast(context);

        //获取LayoutInflater对象
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //由layout文件创建一个View对象
        View layout = inflater.inflate(R.layout.toast_message, null);

        //实例化ImageView和TextView对象
        ImageView imageView = (ImageView) layout.findViewById(R.id.dialog_title);
        TextView textView = (TextView) layout.findViewById(R.id.toast_message);

        imageView.setImageResource(resId);
        textView.setText(text);

        result.setView(layout);
        result.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        result.setDuration(duration);

        return result;
    }
}
