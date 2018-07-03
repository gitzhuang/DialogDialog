package x.com.dialogmobile.CheckUpdate;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
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
import x.com.dialogmobile.ProgressDialog.PDialog2Builder;
import x.com.dialogmobile.R;


public class DownloadHelper {

	private static final int DOWNLOAD_ING = 1;
	private static final int DOWNLOAD_OVER = 2;
	private String mVersionName;
	private String mDownloadUrl;
	private Activity mContext;
	private String mSavePath;
	private int mProgress;
	private Boolean mIsCancle = false;
	private Handler mProgressHandler;
	private DownloadCallBack mDownloadCallBack;

	private int mIsForce = 0;
	private PDialog2Builder mPDialog2Builder;
	private Dialog mDialog;

	private NotificationHelper mNotificationHelper;

	public interface DownloadCallBack{
		void downloadCancel();//取消下载时回调
		void installCancel();
	}

	public DownloadHelper(Activity context, final String downloadUrl, int isForce, DownloadCallBack downloadCallBack) {
		mContext = context;
		mIsForce = isForce;
		mDownloadUrl = downloadUrl;
		mDownloadCallBack = downloadCallBack;
		mVersionName = BuildConfig.APPLICATION_ID + ".checkUp";
		mSavePath = Environment.getExternalStorageDirectory() + "/" + "deanDownload";

		mProgressHandler = new MyHandle(mContext) {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
					case DOWNLOAD_ING:
						mPDialog2Builder.setProgress(mProgress);
						mNotificationHelper.setProgress(mProgress, null);
						break;
					case DOWNLOAD_OVER:
						mPDialog2Builder.setBtnCancle("下次再说", new View.OnClickListener() {
							@Override
							public void onClick(View view) {
								mDialog.dismiss();
								mDownloadCallBack.installCancel();
							}
						});
						mPDialog2Builder.setBtnVisity(mIsForce == 0, true);
						mPDialog2Builder.setProgress(100);
						mNotificationHelper.setProgress(100, getInstallApkIntent(new File(mSavePath, mVersionName)));
						installAPK();
						break;
				}
			}
		};

		//显示下载对话框
		showDownloadDialog();
		//显示通知栏下载进度
		mNotificationHelper = new NotificationHelper(mContext, "小鸡炖蘑菇", mSavePath, mVersionName);
	}

	/*
     * 显示正在下载对话框
     */
	public void showDownloadDialog() {
		mPDialog2Builder =  new PDialog2Builder(mContext, R.layout.download_progress_layout, 1f)
				.setMessage("正在下载：")
				.setTouchOutSideCancelable(false)
				.setBtnCancle("取消下载", new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						mIsCancle = true;
						mDialog.dismiss();
						mDownloadCallBack.downloadCancel();
					}
				}).setBtnSure("立即安装", new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						installAPK();
					}
				}).setBtnVisity(false ,false );
		mDialog = mPDialog2Builder.create();
		mDialog.show();

		downloadAPK();
	}

		/*
    * 开启新线程下载文件
    */
	private void downloadAPK() {
		System.out.println(mDownloadUrl);
		new Thread(new Runnable() {
			@Override
			public void run() {
			try {
				if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
					File file = new File(mSavePath);
					if (!file.exists()) {
						file.mkdir();
					}
					URL url = new URL(mDownloadUrl);
					HttpURLConnection connection = (HttpURLConnection) url.openConnection();
					connection.connect();
					InputStream is = connection.getInputStream();
					int len = connection.getContentLength();
					File apkFile = new File(mSavePath, mVersionName);
					FileOutputStream fos = new FileOutputStream(apkFile);
					int count = 0;
					byte[] buffer = new byte[1024];
					while (!mIsCancle) {
						int numread = is.read(buffer);
						count += numread;
						// 计算进度条的当前位置
						mProgress = (int) (((float) count / len) * 100);
						// 更新进度条
						mProgressHandler.sendEmptyMessage(DOWNLOAD_ING);
						// 下载完成
						if (numread < 0 ) {
							mProgressHandler.sendEmptyMessage(DOWNLOAD_OVER);
							break;
						}
						fos.write(buffer, 0, numread);
					}
				} else {
					System.out.println("00000000000000000000000000000");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			}
		}).start();
	}

	/*
     * 下载到本地后执行安装
     */
	protected void installAPK() {
		try {
			File apkFile = new File(mSavePath, mVersionName);
			if (!apkFile.exists()) {
				return;
			}
			mContext.startActivity(getInstallApkIntent(apkFile));
		}catch (Exception e){
			Toast.makeText(mContext, "安装失败", Toast.LENGTH_SHORT);
		}
	}

	/**
	 * 获取安装apk的Intent
	 * @param apkFile
	 * @return
	 */
	public Intent getInstallApkIntent(File apkFile){
		Intent intent = new Intent(Intent.ACTION_VIEW);
		//版本在7.0以上是不能直接通过uri访问的
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

			//参数1 上下文, 参数2 在AndroidManifest中的android:authorities值, 参数3  共享的文件
			Uri apkUri = FileProvider.getUriForFile(mContext, BuildConfig.APPLICATION_ID+".fileProvider", apkFile);
			//由于没有在Activity环境下启动Activity,设置下面的标签
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			//添加这一句表示对目标应用临时授权该Uri所代表的文件
			intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
			intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
		}else {
			Intent install = new Intent(Intent.ACTION_VIEW);
			install.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
			install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		}
		return intent;
	}


	static class MyHandle extends Handler{
		final WeakReference<Activity> mActivity;

		MyHandle(Activity activity) {
			mActivity = new WeakReference<Activity>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
		}
	}
}
