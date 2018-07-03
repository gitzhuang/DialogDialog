package x.com.dialogdialog;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import x.com.dialogmobile.CheckUpdate.CheckDialogFragment;

public class Main2Activity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new CheckDialogFragment(this,
                0,
                "http://imtt.dd.qq.com/16891/337A49BBE7A8A0B42E2312893903BBB3.apk?fsname=com.coolsnow.screenshot_5.6.0_56000.apk",
                0,
                "ghjjkj",
                new CheckDialogFragment.OnCheckcallback() {
                    @Override
                    public void onCancel() {
                        Toast.makeText(Main2Activity.this, "345", Toast.LENGTH_SHORT).show();
                    }
                }).show(getSupportFragmentManager(), "checkup");

    }

}
