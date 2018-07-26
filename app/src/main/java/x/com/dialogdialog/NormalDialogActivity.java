package x.com.dialogdialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import x.com.dialogmobile.NDialogBuilder;
import x.com.dialogmobile.ProgressDialog.PDialog1Builder;

public class NormalDialogActivity extends AppCompatActivity {
    private Dialog a;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button ewfe = findViewById(R.id.ewfe);
        ewfe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

        a = new NDialogBuilder(this, 0, 1.0f)
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
                }).create();
//        a.show();


        new PDialog1Builder(this, 0, 1.0f).setMessage("www").settime(1000).setTouchOutSideCancelable(false)
                .setonInputCodeListener(new PDialog1Builder.onProgressFinishListener() {
                    @Override
                    public void onProgressFinish() {
                        Dialog errordialog = new NDialogBuilder(NormalDialogActivity.this, 0, 1.0f)
                                .setTouchOutSideCancelable(false)
                                .setMessage("345tret", NDialogBuilder.MSG_LAYOUT_LEFT)
                                .setDialogAnimation(NDialogBuilder.DIALOG_ANIM_NORMAL)
                                .setBtnClickListener(true, "", new NDialogBuilder.onDialogbtnClickListener() {
                                    @Override
                                    public void onDialogbtnClick(Context context, Dialog dialog, int whichBtn) {
                                    }
                                })
                                .create();
                        errordialog.show();
                    }
                }).create().show();
    }

}
