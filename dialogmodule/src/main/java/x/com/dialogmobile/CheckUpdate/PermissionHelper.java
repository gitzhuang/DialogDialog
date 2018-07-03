package x.com.dialogmobile.CheckUpdate;

import android.Manifest;
import android.content.Context;
import android.os.Build;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class PermissionHelper {

    private final String[] mPermissionList = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
    };

    private final String[] mPermissionList26 = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.REQUEST_INSTALL_PACKAGES,
    };

    public interface RequestPermissionCallBack{
        void requestPermissionSuccess();
        void requestPermissionFail();
    }

    private static final int REQUESTCODE = 1;
    private RequestPermissionCallBack mPermissionCallBack;
    private static PermissionHelper permissionHelper;

    private PermissionHelper(){}

    public static PermissionHelper getInstance(){
        if (permissionHelper == null) {
            permissionHelper = new PermissionHelper();
        }
        return permissionHelper;
    }

    /**
     * 动态权限申请
     */
    public void requestSavePermission(Fragment fragment, RequestPermissionCallBack permissionCallBack) {
        mPermissionCallBack = permissionCallBack;

        //判断是否已经赋予权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
                ? ContextCompat.checkSelfPermission(fragment.getContext(), Manifest.permission.REQUEST_INSTALL_PACKAGES) != PERMISSION_GRANTED
                : ContextCompat.checkSelfPermission(fragment.getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PERMISSION_GRANTED) {

            //如果应用之前请求过此权限但用户拒绝了请求，此方法将返回 true。
            if (fragment.shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    || fragment.shouldShowRequestPermissionRationale(Manifest.permission.REQUEST_INSTALL_PACKAGES)) {

                //todo 此处可以提醒说明申请权限说明，再调用权限申请
                Toast.makeText(fragment.getContext(), "此功能需要允许存储权限，才能正常使用。", Toast.LENGTH_SHORT).show();
                getPermission(fragment);

            } else {
                //申请权限，字符串数组内是一个或多个要申请的权限，1是申请权限结果的返回参数，在onRequestPermissionsResult可以得知申请结果
                getPermission(fragment);
            }
        }else {
            mPermissionCallBack.requestPermissionSuccess();
        }
    }

    /**
     * 申请权限
     * @param fragment
     */
    private void getPermission(Fragment fragment){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            fragment.requestPermissions(
                    mPermissionList26, REQUESTCODE);
        }else {
            fragment.requestPermissions(
                    mPermissionList, REQUESTCODE);
        }
    }

    /**
     * 申请结果回掉
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    public void requestPermissionsResult(Context context, int requestCode, String[] permissions, int[] grantResults){
        if (requestCode == REQUESTCODE) {
            if (grantResults[0] == PERMISSION_GRANTED && grantResults[1] == PERMISSION_GRANTED) {
                //申请成功
                mPermissionCallBack.requestPermissionSuccess();
            } else {
                //被拒绝了
                Toast.makeText(context, "存储权限申请失败，无法升级", Toast.LENGTH_SHORT).show();
                mPermissionCallBack.requestPermissionFail();
            }

        }
    }

}
