package com.ittest.appupdate.activity;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources.NotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ittest.appupdate.R;
import com.ittest.appupdate.domain.UpdateInfo;
import com.ittest.appupdate.engine.UpdateInfoParser;
import com.ittest.appupdate.network.DownloadAsyncTask;
import com.ittest.appupdate.network.DownloadCallback;
import com.ittest.appupdate.network.DownloadInstall;
import com.ittest.appupdate.po.Const;
import com.ittest.appupdate.utils.DownLoadUtils;

/**
 * @ClassName: SplashActivity
 * @Description: 描述软件自动更新
 * @date 2016年4月26日 上午10:54:49
 */
public class SplashActivity extends Activity {
	public static final int PARSE_SUCCESS = 10;
	public static final int PARSE_ERROR = 11;
	public static final int SERVER_ERROR = 12;
	public static final int URL_ERROR = 13;
	public static final int NETWORK_ERROR = 14;
	public static final int DOWNLOAD_SUCCESS = 15;
	public static final int DOWNLOAD_ERROR = 16;
	protected static final String TAG = "SplashActivity";
	private TextView tv_splash_version;
	private RelativeLayout rl_splash_bg;
	private UpdateInfo updateInfo;
	private AlertDialog noticeDialog; // 提示弹出框
	// 定义一个消息处理器 在主线程里面创建
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case PARSE_SUCCESS:
				// 比对当前客户端的版本号 跟服务器的版本号是否一致.
				if (getAppVersion().equals(updateInfo.getVersion())) {
					 loadMainUI();
				} else {
					// 版本不同,提示用户升级.
					//Logger.i(TAG, "版本不同,提示用户升级");
					showNoticeDialog();

				}

				break;
			case PARSE_ERROR:
				Toast.makeText(getApplicationContext(), "解析失败", 0).show();
				 loadMainUI();
				break;
			case SERVER_ERROR:
				Toast.makeText(getApplicationContext(), "服务器异常", 0).show();
				 loadMainUI();
				break;
			case URL_ERROR:
				Toast.makeText(getApplicationContext(), "网络路径错误", 0).show();
				 loadMainUI();
				break;
			case NETWORK_ERROR:
				Toast.makeText(getApplicationContext(), "网络连接异常", 0).show();
				loadMainUI();
				break;
			case DOWNLOAD_ERROR:
				Toast.makeText(getApplicationContext(), "下载失败.", 0).show();
			     loadMainUI();
				break;
			case DOWNLOAD_SUCCESS:
				Toast.makeText(getApplicationContext(), "下载成功,安装apk", 0).show();

				break;
			}

		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		tv_splash_version = (TextView) findViewById(R.id.tv_splash_version);
		tv_splash_version.setText("版本:" + getAppVersion());
		rl_splash_bg = (RelativeLayout) findViewById(R.id.rl_splash_bg);

		AlphaAnimation aa = new AlphaAnimation(0.2f, 1.0f);
		aa.setDuration(3000);
		rl_splash_bg.startAnimation(aa);

		// 连接服务器 获取更新信息,在子线程操作.
		new Thread(new CheckVersionTask()).start();
	}

	public class CheckVersionTask implements Runnable {
		long startTime;// 记录任务开启之前的时间
		public void run() {
			startTime = System.currentTimeMillis();
			Message msg = Message.obtain();
			// 获取服务器上的配置信息.
			try {
				URL url = new URL(getResources().getString(R.string.server_url));
				HttpURLConnection conn = (HttpURLConnection) url
						.openConnection();
				conn.setRequestMethod("GET");
				conn.setConnectTimeout(2000);
				int code = conn.getResponseCode();
				if (code == 200) {
					// 请求成功.
					InputStream is = conn.getInputStream();
					updateInfo = UpdateInfoParser.getUpdateInfo(is);
					if (updateInfo != null) {
						// 解析成功
						msg.what = PARSE_SUCCESS;
					} else {
						// 解析失败..
						msg.what = PARSE_ERROR;
					}
				} else {
					// TODO:连接服务器请求失败.
					msg.what = SERVER_ERROR;
				}
			} catch (MalformedURLException e) {
				e.printStackTrace();
				msg.what = URL_ERROR;
			} catch (NotFoundException e) {
				e.printStackTrace();
				msg.what = URL_ERROR;
			} catch (IOException e) {
				e.printStackTrace();
				msg.what = NETWORK_ERROR;
			} finally {
				long endTime = System.currentTimeMillis();
				long dTime = endTime - startTime;// 访问网络的间隔时间.
				if (dTime < 2000) {
					try {
						Thread.sleep(2000 - dTime);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				// 发送消息给主线程.
				handler.sendMessage(msg);
			}
		}

	}

	// 获取当前应用程序的版本号
	public String getAppVersion() {
		// 获取手机的包管理者
		PackageManager pm = getPackageManager();
		try {
			PackageInfo packInfo = pm.getPackageInfo(getPackageName(), 0);
			return packInfo.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			// 不可能发生.
			// can't reach
			return "";

		}
	}
	/* 弹出软件更新提示对话框 */
	private void showNoticeDialog() {
		StringBuffer sb = new StringBuffer();
		sb.append("版本号：" + updateInfo.getVersion() + "\n")
				.append("更新日志：\n" + updateInfo.getDescription());
		Builder builder = new AlertDialog.Builder(SplashActivity.this);
		builder.setIcon(R.drawable.ic_launcher).setTitle("版本更新")
				.setMessage(sb.toString());
		builder.setPositiveButton("下载", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String apkPath = Const.apkSavepath +File.separator+ DownLoadUtils.getFileName(updateInfo.getApkurl());
				DownloadCallback downCallback = new DownloadInstall(SplashActivity.this,
						apkPath, updateInfo.getVersion());
				DownloadAsyncTask request = new DownloadAsyncTask(downCallback);
				request.execute(updateInfo.getApkurl(), apkPath);
				dialog.dismiss();
			}
		});
		builder.setNegativeButton("以后再说",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						loadMainUI();
					}
				});
		noticeDialog = builder.create();
		noticeDialog.getWindow().setType(
				WindowManager.LayoutParams.TYPE_SYSTEM_ALERT); // 设置最顶层Alertdialog
		noticeDialog.show();
		
	}
	/**
	 * 进入主界面.
	 */
	public void loadMainUI() {
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
		// 把splash界面给关闭掉
		this.finish();
	}
}
