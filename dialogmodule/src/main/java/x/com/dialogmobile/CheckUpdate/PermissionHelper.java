package x.com.dialogmobile.CheckUpdate;

import android.app.Activity;
import android.content.Context;
import android.os.Build;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class PermissionHelper {

    public interface RequestPermissionCallBack{
        void requestPermissionAgainHint();
        void requestPermissionSuccess();
        void requestPermissionFail();
    }

    private static PermissionHelper permissionHelper;
    private final int REQUESTCODE = 1;
    private RequestPermissionCallBack mPermissionCallBack;

    public static PermissionHelper getInstance(){
        if (permissionHelper == null) {
            permissionHelper = new PermissionHelper();
        }
        return permissionHelper;
    }

    /**
     * 基于Fragment动态权限申请
     */
    public void requestPermission(Fragment fragment, String[] permissionList, RequestPermissionCallBack permissionCallBack) {
        requestPermission(null, fragment, permissionList, permissionCallBack);
    }

    /**
     * 基于Fragment动态权限申请
     */
    public void requestPermission(Activity activity, String[] permissionList, RequestPermissionCallBack permissionCallBack) {
        requestPermission(activity, null, permissionList, permissionCallBack);
    }

    private void requestPermission(Activity activity, Fragment fragment, String[] permissionList, RequestPermissionCallBack permissionCallBack){
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.M && (fragment != null || activity != null)){
            mPermissionCallBack = permissionCallBack;
            boolean isNeedApply = false;
            for(int i = 0; i < permissionList.length; i++){
                if(ContextCompat.checkSelfPermission(activity == null ? fragment.getContext() : activity, permissionList[i]) != PERMISSION_GRANTED){
                    isNeedApply = true;
                    break;
                }
            }
            if(isNeedApply){
                //如果应用之前请求过此权限但用户拒绝了请求，此方法将返回 true。
                for(int i = 0; i < permissionList.length; i++){
                    if(activity == null ? fragment.shouldShowRequestPermissionRationale(permissionList[i])
                            : activity.shouldShowRequestPermissionRationale(permissionList[i])){
                        //此处可以提醒说明申请权限说明，再调用权限申请
                        mPermissionCallBack.requestPermissionAgainHint();
                        break;
                    };
                }
                //申请权限，字符串数组内是一个或多个要申请的权限，1是申请权限结果的返回参数，在onRequestPermissionsResult可以得知申请结果
                if(activity == null){
                    fragment.requestPermissions(permissionList, REQUESTCODE);
                }else {
                    activity.requestPermissions(permissionList, REQUESTCODE);
                }
            }else {
                mPermissionCallBack.requestPermissionSuccess();
            }
        }
    }

    /**
     * 申请结果回调
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    public void requestPermissionsResult(Context context, int requestCode, String[] permissions, int[] grantResults){
        if (requestCode == REQUESTCODE) {
            boolean isApplyFail = false;
            for(int i = 0; i < grantResults.length; i++){
                if(grantResults[i] != PERMISSION_GRANTED){
                    //存在权限申请被拒绝
                    isApplyFail = true;
                    break;
                }
            }
            if(isApplyFail){
                //申请成功
                mPermissionCallBack.requestPermissionSuccess();
            }else {
                //被拒绝了
                mPermissionCallBack.requestPermissionFail();
            }
        }
    }

}
