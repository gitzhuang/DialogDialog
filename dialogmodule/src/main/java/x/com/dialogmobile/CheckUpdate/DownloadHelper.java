package x.com.dialogmobile.CheckUpdate;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
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
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

import androidx.core.content.FileProvider;
import x.com.dialogmobile.BuildConfig;
import x.com.dialogmobile.NotificationHelper;
import x.com.dialogmobile.ProgressDialog.PDialog2Builder;
import x.com.dialogmobile.R;


class DownloadHelper {
    private final String TAG = "download";
    private static final int DOWNLOAD_ING = 1;
    private static final int DOWNLOAD_OVER = 2;
    private static final int DOWNLOAD_AGAIN = 3;
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


    public interface DownloadCallBack {
        void downloadCancel();//取消下载时回调

        void installCancel();//取消安装时回调

        void downloadFail();//下载失败时回调
    }

    @SuppressLint("HandlerLeak")
    DownloadHelper(Activity activity, final String downloadUrl, int isForce, int notificationIconId, String notificationTitle, DownloadCallBack downloadCallBack) {
        mActivity = activity;
        mIsForce = isForce;
        mDownloadUrl = downloadUrl;
        mDownloadCallBack = downloadCallBack;
        mVersionName = BuildConfig.APPLICATION_ID + ".checkUp";
        mSavePath = Environment.getExternalStorageDirectory() + "/" + "deanDownload";
        mNotificationIconId = notificationIconId;
        mNotificationTitle = notificationTitle;
        mProgressHandler = new MyHandle(mActivity) {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case DOWNLOAD_ING:
                        mPDialog2Builder.setProgress(mProgress);
                        mNotificationHelper.setProgress(mProgress, null);
                        break;
                    case DOWNLOAD_OVER:
                        if (mNotificationHelper != null) {
                            mNotificationHelper.setProgress(100, getInstallApkIntent(new File(mSavePath, mVersionName)));
                        }
                        installAPK();
                        break;
                    case DOWNLOAD_AGAIN:
                        showReDownload();
                        break;
                }
            }
        };
        startDownload();
    }

    /**
     * 开启下载
     */
    void startDownload() {
        if (mNotificationHelper == null) {
            mNotificationHelper = new NotificationHelper(mActivity, mNotificationTitle);
            mNotificationHelper.setType(NotificationHelper.NOTIFICATION_TYPE_DOWNLOAD);
            mNotificationHelper.setProgress(0, null);
        }
        //显示下载对话框
        showDialog();
        //获取网络资源，判断下载或直接安装
        getApkResource();
    }

    /*
     * 显示正在下载对话框
     */
    private void showDialog() {
        if (mDialog == null || mPDialog2Builder == null) {
            mPDialog2Builder = new PDialog2Builder(mActivity, R.layout.download_progress_layout, 1f)
                    .setMessage("正在下载：")
                    .setTouchOutSideCancelable(false)
                    .setBtnCancel("取消下载", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mIsCancle = true;
                            mDialog.dismiss();
                            mDownloadCallBack.downloadCancel();
                        }
                    }).setBtnCancelVisity(false);
            mDialog = mPDialog2Builder.create();
        }
        mDialog.show();
    }

    /*
     * 开启新线程
     * 获取网络apk资源文件，判断下载或直接安装
     */
    private void getApkResource() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    File file = new File(mSavePath);
                    File apkFile = new File(mSavePath, mVersionName);
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
                        downloadApk(is, len, apkFile);
                    } catch (Exception e) {
                        downloadFail(apkFile);
                        Log.e(TAG, "getApk: " + e.toString());
                        //mProgressHandler.sendEmptyMessage(DOWNLOAD_AGAIN);
                    }
                } else {
                    Log.e(TAG, "getApk: SD卡读写权限受限");
                }
            }
        }).start();
    }

    /**
     * 下载apk
     *
     * @param is      资源流
     * @param len     资源大小
     * @param apkFile 安装包
     */
    private void downloadApk(InputStream is, int len, File apkFile) {
        //显示通知栏下载进度

        mProgress = 0;
        int mMiddlerogress = 0;
        try {
            FileOutputStream fos = new FileOutputStream(apkFile);
            int count = 0;
            byte[] buffer = new byte[1024];
            while (!mIsCancle) {
                int numread = is.read(buffer);
                count += numread;
                // 计算进度条的当前位置
                mMiddlerogress = (int) (((float) count / len) * 100);
                if (mMiddlerogress > mProgress) {
                    // 更新进度条
                    mProgressHandler.sendEmptyMessage(DOWNLOAD_ING);
                    mProgress = mMiddlerogress;
                }
                // 下载完成
                if (numread < 0) {
                    mProgressHandler.sendEmptyMessage(DOWNLOAD_OVER);
                    break;
                }
                fos.write(buffer, 0, numread);
            }
        } catch (Exception e) {
            downloadFail(apkFile);
            Log.e(TAG, "download: " + e.toString());
        }
    }

    /**
     * @param apkFile 安装包
     */
    private void downloadFail(File apkFile) {
        if (apkFile.exists()) {
            apkFile.delete();
        }
        if (mDialog != null) mDialog.dismiss();
        if (mDownloadCallBack != null) mDownloadCallBack.downloadFail();
    }

    /**
     * 重新下载
     */
    private void showReDownload() {
        if (mPDialog2Builder != null) {
            mPDialog2Builder.setBtnSure("重新下载", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    view.setVisibility(View.GONE);
                    getApkResource();
                }
            });
            mPDialog2Builder.setBtnSureVisity(true);
        }
    }

	/*
     * 执行安装apk
     */
    private void installAPK() {
        if (mPDialog2Builder != null) {
            mPDialog2Builder.setProgress(100);
            mPDialog2Builder.setBtnCancel("下次再说", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mDialog != null) mDialog.dismiss();
                    if (mDownloadCallBack != null) mDownloadCallBack.installCancel();
                }
            });
            mPDialog2Builder.setBtnSure("立即安装", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    installAPK();
                }
            });
            mPDialog2Builder.setBtnVisity(mIsForce == 0, true);
        }

		try {
			File apkFile = new File(mSavePath, mVersionName);
			if (!apkFile.exists()) {
				return;
			}
			Intent installApkIntent = getInstallApkIntent(apkFile);
			if(mNotificationHelper != null) mNotificationHelper.setProgress(100, installApkIntent);
			mActivity.startActivity(installApkIntent);
		}catch (Exception e){
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
}
