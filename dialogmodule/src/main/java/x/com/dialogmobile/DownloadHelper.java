package x.com.dialogmobile;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import x.com.dialogmobile.ProgressDialog.PDialog2Builder;


public class DownloadHelper {
    private final String TAG = "download";
    private static final int DOWNLOAD_ING = 1;
    private static final int DOWNLOAD_OVER = 2;
    private String mVersionName;
    private String mDownloadUrl;
    private Activity mActivity;
    private String mSavePath;
    private int mProgress;
    private Boolean mIsCancle = false;
    private Handler mProgressHandler;
    private DownloadCallBack mDownloadCallBack;
    private int mIsForce;
    private PDialog2Builder mPDialog2Builder;
    private Dialog mDialog;
    private NotificationHelper mNotificationHelper;
    private int mNotificationIconId;
    private String mNotificationTitle;
    private boolean mIsShowNotification;//是否显示下载进度通知
    private boolean mIsShowDialog;//是否显示下载进度弹框
    private boolean mIsCheckUp;//是否是更新，true则自动安装
    private String mBtnSureText;
    private String mBtnCancelText;
    private Thread thread;

    private static ArrayList<DownloadHelper> mDownloadManager = new ArrayList<>();

    public interface DownloadCallBack {

        void downloadSuccess(File file);//下载成功时回调

        void downloadFail();//下载失败时回调

        void installCancel();//取消安装时回调
    }

    @SuppressLint("HandlerLeak")
    public DownloadHelper(Activity activity, String downloadUrl, final DownloadCallBack downloadCallBack) {
        mActivity = activity;
        mIsForce = 0; //默认不强制
        mIsCheckUp = false;
        mDownloadUrl = downloadUrl;
        mDownloadCallBack = downloadCallBack;
        mVersionName = getFileName(downloadUrl);
        mSavePath = Environment.getExternalStorageDirectory() + "/" + "deanDownload";

        mProgressHandler = new MyHandle(mActivity) {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case DOWNLOAD_ING:
                        Log.i(TAG, "下载进度：" + mProgress);
                        if (mIsShowDialog && mProgress < 100) {
                            mPDialog2Builder.setProgress(mProgress);
                        }
                        if (mIsShowNotification && mProgress < 100) {
                            mNotificationHelper.setProgress(mProgress, null);
                        }
                        break;
                    case DOWNLOAD_OVER:
                        Log.i(TAG, "下载完成。");
                        //刷新进度
                        downloadSuccess();
                        //回调file
                        downloadCallBack.downloadSuccess(new File(mSavePath, mVersionName));
                        if (mIsCheckUp) {
                            //判断是否执行安装
                            installAPK();
                        }
                        break;
                }
            }
        };
    }

    /**
     * 根据url返回文件名
     *
     * @param downloadUrl 下载路径
     * @return this
     */
    private String getFileName(String downloadUrl) {
        String[] symbol = new String[]{"/", "//?", "=", "&"};
        String[] split;
        if (downloadUrl != null) {
            for (int i = 0; i < symbol.length && downloadUrl.length() > 0; i++) {
                split = downloadUrl.split(symbol[i]);
                downloadUrl = split[split.length - 1];
            }
            return downloadUrl;
        } else {
            return "";
        }
    }

    /**
     * 开启下载
     */
    private void startDownload() {
        mDownloadManager.add(this);
        if (mIsShowNotification && mNotificationHelper == null) {
            mNotificationHelper = new NotificationHelper(mActivity);
            if (mNotificationIconId != 0) {
                mNotificationHelper.setSmallIcon(mNotificationIconId);
            }
            mNotificationHelper.setTitle(mNotificationTitle);
            mNotificationHelper.setType(NotificationHelper.NOTIFICATION_TYPE_DOWNLOAD);
            mNotificationHelper.setNotificationGroup("下载");
            mNotificationHelper.setProgress(0, null);
        }
        //显示下载对话框
        if (mIsShowDialog) {
            showDialog();
        }
        //获取网络资源，判断下载或直接安装
        getApkResource();
    }

    /*
     * 显示正在下载对话框
     */
    private void showDialog() {
        if (mDialog == null || mPDialog2Builder == null) {
            mPDialog2Builder = new PDialog2Builder(mActivity, 0, 1f)
                    .setMessage("正在下载：")
                    .setTouchOutSideCancelable(false)
                    .setBtnCancel("取消下载", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mIsCancle = true;
                            mDialog.dismiss();
                            //mDownloadCallBack.downloadCancel();
                        }
                    })
                    .setBtnVisity(false, false);
            mDialog = mPDialog2Builder.create();
        }
        mDialog.show();
    }

    /*
     * 开启新线程
     * 获取网络apk资源文件，判断下载或直接安装
     */
    private void getApkResource() {
        //显示通知栏下载进度
        mProgress = 0;
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    int mMiddlerogress = 0;
                    File file = new File(mSavePath);
                    File apkFile = new File(mSavePath, mVersionName);
                    FileOutputStream fos = null;
                    try {
                        if (!file.exists()) {
                            file.mkdir();
                        }
                        URL url = new URL(mDownloadUrl);
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        connection.connect();
                        InputStream is = connection.getInputStream();
                        int len = connection.getContentLength();//获取网络apk大小
                        if (apkFile.exists()) {
                            apkFile.delete();
                        }
                        fos = new FileOutputStream(apkFile);
                        int count = 0;
                        byte[] buffer = new byte[1024];
                        while (!mIsCancle) {
                            int numread = is.read(buffer);
                            count += numread;
                            // 计算进度条的当前位置
                            mMiddlerogress = (int) (((float) count / len) * 100);
                            if (mMiddlerogress > mProgress && mMiddlerogress < 100) {
                                // 更新进度条
                                mProgress = mMiddlerogress;
                                mProgressHandler.sendEmptyMessage(DOWNLOAD_ING);
                            }
                            // 下载完成
                            if (numread < 0) {
                                mProgress = 100;
                                mProgressHandler.sendEmptyMessage(DOWNLOAD_OVER);
                                break;
                            }
                            fos.write(buffer, 0, numread);
                        }
                    } catch (Exception e) {
                        if (apkFile.exists()) {
                            apkFile.delete();
                        }
                        if (mDialog != null) {
                            mDialog.dismiss();
                        }
                        if (mDownloadCallBack != null) {
                            mDownloadCallBack.downloadFail();
                        }
                    } finally {
                        if (fos != null) {
                            try {
                                fos.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } else {
                    Log.e(TAG, "getApk: SD卡读写权限受限");
                }
            }
        });
        thread.start();
    }

    /*
     * 下载完成
     */
    private void downloadSuccess() {
        if (mNotificationHelper != null) {
            mNotificationHelper.setProgress(100, mIsCheckUp ? getInstallApkIntent(new File(mSavePath, mVersionName)) : null);
        }
        if (mPDialog2Builder != null) {
            mPDialog2Builder.setProgress(100);
            mPDialog2Builder.setBtnCancel(mBtnCancelText, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mDialog != null) mDialog.dismiss();
                    if (mDownloadCallBack != null) mDownloadCallBack.installCancel();
                }
            });
            mPDialog2Builder.setBtnSure(mBtnSureText, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    installAPK();
                }
            });
            mPDialog2Builder.setBtnVisity(mIsForce == 0, true);
        }
    }

    /**
     * 安装apk
     */
    private void installAPK() {
        try {
            File apkFile = new File(mSavePath, mVersionName);
            if (!apkFile.exists()) {
                return;
            }
            Intent installApkIntent = getInstallApkIntent(apkFile);
            if (mNotificationHelper != null) mNotificationHelper.setProgress(100, installApkIntent);
            mActivity.startActivity(installApkIntent);
        } catch (Exception e) {
            Toast.makeText(mActivity, "安装失败", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 获取安装apk的Intent
     *
     * @param apkFile 安装包
     * @return Intent 跳转意图
     */
    private Intent getInstallApkIntent(File apkFile) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        //版本在7.0以上是不能直接通过uri访问的
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //参数1 上下文, 参数2 在AndroidManifest中的android:authorities值, 参数3  共享的文件
            Uri apkUri = FileProvider.getUriForFile(mActivity, BuildConfig.APPLICATION_ID + ".fileProvider", apkFile);
            //由于没有在Activity环境下启动Activity,设置下面的标签
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //添加这一句表示对目标应用临时授权该Uri所代表的文件
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        return intent;
    }

    static class MyHandle extends Handler {
        final WeakReference<Activity> activity;

        MyHandle(Activity activity) {
            this.activity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    }

    /**
     * 设置是否强制更新
     *
     * @param isForce 是否强制
     * @return this
     */
    public DownloadHelper setIsForce(boolean isForce) {
        mIsForce = isForce ? 1 : 0;
        return this;
    }

    /**
     * 设置通知参数
     *
     * @return this
     */
    public DownloadHelper setNotificationShow(boolean isShow, @NonNull String title, int iconId) {
        mIsShowNotification = isShow;
        mNotificationTitle = title;
        mNotificationIconId = iconId;
        return this;
    }

    /**
     * 设置是否显示下载进度弹框
     *
     * @param isShow 是否显示
     * @return this
     */
    public DownloadHelper setDialogShow(boolean isShow, @NonNull String btnCancelText, @NonNull String btnSureText) {
        mIsShowDialog = isShow;
        mBtnSureText = btnSureText;
        mBtnCancelText = btnCancelText;
        return this;
    }

    /**
     * 是否是版本更新，true自动执行安装
     *
     * @param isCheckUp 是否为版本升级
     * @return this
     */
    public DownloadHelper setCheckUp(boolean isCheckUp) {
        mIsCheckUp = isCheckUp;
        return this;
    }

    /**
     * 设置保存路径，默认 Environment.getExternalStorageDirectory() + "/" + "deanDownload";
     *
     * @param path 路径
     * @return this
     */
    public DownloadHelper setSavePath(String path) {
        mSavePath = path;
        return this;
    }

    /**
     * 开启下载
     */
    public void start() {
        //判断文件是否存在
        File file = new File(mSavePath, mVersionName);
        if (file.exists()) {
            //已存在
            showAlreadyExistsDialog(file);
        } else {
            //不存在，执行下载
            startDownload();
        }
    }

    /**
     * 中断下载
     */
    public void stop() {
        mIsCancle = true;
        if (mNotificationHelper != null) {
            mNotificationHelper.cancel();
        }
        if (mDialog != null) {
            mDialog.dismiss();
        }
        thread = null;
        mProgressHandler = null;
        mDownloadCallBack = null;
        mDownloadManager.remove(this);
    }

    /**
     * 删除文件
     */
    public void deleteFile() {
        File file = new File(mSavePath, mVersionName);
        if (file.exists()) {
            file.delete();
        }
    }

    /**
     * 删除制定路径下的文件
     *
     * @param filePath 文件路径
     */
    public static void deleteFile(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            file.delete();
        }
    }

    /**
     * 清空所有下载
     */
    public static void cancelAll() {
        if (mDownloadManager != null) {
            ArrayList<DownloadHelper> list = new ArrayList<>(mDownloadManager);
            for (DownloadHelper helper : list) {
                if (helper != null) helper.stop();
            }
            mDownloadManager.clear();
            list.clear();
        }
    }

    public void showAlreadyExistsDialog(final File file) {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new NDialogBuilder(mActivity, 0, 1.0f)
                        .setTitle("文件已存在")
                        .setTouchOutSideCancelable(false)
                        .setMessage("文件已存在，是否继续？", NDialogBuilder.MSG_LAYOUT_LEFT)
                        .setDialogAnimation(NDialogBuilder.DIALOG_ANIM_NORMAL)
                        .setBtnClickListener(true, "重新下载", "继续", new NDialogBuilder.onDialogbtnClickListener() {
                            @Override
                            public void onDialogbtnClick(Context context, Dialog dialog, int whichBtn) {
                                if (whichBtn == BUTTON_CONFIRM) {
                                    startDownload();
                                } else {
                                    if (mIsCheckUp) {
                                        //判断是否执行安装
                                        installAPK();
                                    }
                                    mDownloadCallBack.downloadSuccess(file);
                                }
                            }
                        })
                        .create().show();
            }
        });
    }
}
