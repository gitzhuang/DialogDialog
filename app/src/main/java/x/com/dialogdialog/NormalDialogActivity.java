package x.com.dialogdialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import x.com.dialogmobile.NDialogBuilder;

public class NormalDialogActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new NDialogBuilder(this, 0, 1.0f)
                .setTouchOutSideCancelable(false)
                .setMessage("我是鬼，我来抓你了！", NDialogBuilder.MSG_LAYOUT_CENTER)
                .setBtnClickListener(true, "好", "不好", new NDialogBuilder.onDialogbtnClickListener() {
                    @Override
                    public void onDialogbtnClick(Context context, Dialog dialog, int whichBtn) {
                        switch (whichBtn) {
                            case 1:
                                Toast.makeText(context, "点击了好", Toast.LENGTH_SHORT).show();
                                break;
                            case 2:
                                Toast.makeText(context, "点击了不好", Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }
                }).create()
                .show();
    }
}
