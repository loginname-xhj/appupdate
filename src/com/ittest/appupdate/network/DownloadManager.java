package com.ittest.appupdate.network;

import java.io.File;

import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Handler;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.ittest.appupdate.R;
import com.ittest.appupdate.domain.UpdateInfo;
import com.ittest.appupdate.engine.XMLSParser;
import com.ittest.appupdate.po.Const;
import com.ittest.appupdate.utils.DownLoadUtils;
import com.ittest.appupdate.utils.HttpRequestUtil;
import com.ittest.appupdate.utils.IntentUtil;

/**
 * 下载管理
 * 
 * @author： aokunsang
 * @date： 2012-12-18
 */
public class DownloadManager {

	private Context mContext;

	final static int CHECK_FAIL = 0;
	final static int CHECK_SUCCESS = 1;
	final static int CHECK_NOUPGRADE = 2;
	final static int CHECK_NETFAIL = 3;

	private UpdateInfo info;
	private AlertDialog noticeDialog; // 提示弹出框
	private ProgressDialog progressDialog;

	public DownloadManager(Context mContext) {
		this.mContext = mContext;
	}

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (progressDialog != null) {
				progressDialog.dismiss();
			}
			switch (msg.what) {
			case CHECK_SUCCESS: {
				showNoticeDialog();
				break;
			}
			case CHECK_NOUPGRADE: { // 不需要更新
				Toast.makeText(mContext, "网络连接不正常。", Toast.LENGTH_SHORT).show();
				break;
			}
			case CHECK_NETFAIL: {
				Toast.makeText(mContext, "网络连接不正常。", Toast.LENGTH_SHORT).show();
				break;
			}
			case CHECK_FAIL: {
				Toast.makeText(mContext, "从服务器获取更新数据失败。", Toast.LENGTH_SHORT)
						.show();
				break;
			}
			}
		};
	};

	/* 检查下载更新 [apk下载入口] */
	public void checkDownload() {
		progressDialog = ProgressDialog.show(mContext, "", "请稍后，正在检查更新...");
		new Thread() {
			@Override
			public void run() {
				if (!IntentUtil.isConnect(mContext)) { // 检查网络连接是否正常
					handler.sendEmptyMessage(CHECK_NETFAIL);
				} else {
					String result = HttpRequestUtil.getSourceResult(mContext
							.getResources().getString(R.string.server_url),
							null, mContext);
					Log.d("config", "解析的xml:"+result);
					info = XMLSParser.getUpdateInfo(result);
					try {
						if (info != null && checkApkVercode()) { // 检查版本号
							handler.sendEmptyMessage(CHECK_SUCCESS);
						} else {
							handler.sendEmptyMessage(CHECK_NOUPGRADE);
						}
					} catch (Exception e) {
						e.printStackTrace();
						handler.sendEmptyMessage(CHECK_FAIL);
					}
				}
			}
		}.start();
	}

	/* 弹出软件更新提示对话框 */
	private void showNoticeDialog() {
		StringBuffer sb = new StringBuffer();
		sb.append("版本号：" + info.getVersion() + "\n").append(
				"更新日志：\n" + info.getDescription());
		Builder builder = new AlertDialog.Builder(mContext);
		builder.setIcon(R.drawable.ic_launcher).setTitle("版本更新")
				.setMessage(sb.toString());
		builder.setPositiveButton("下载", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String apkPath = Const.apkSavepath + File.separator
						+ DownLoadUtils.getFileName(info.getApkurl());
				DownloadCallback downCallback = new DownloadInstall(mContext,
						apkPath, info.getVersion());
				DownloadAsyncTask request = new DownloadAsyncTask(downCallback);
				request.execute(info.getApkurl(), apkPath);
				dialog.dismiss();
			}
		});
		builder.setNegativeButton("以后再说",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		noticeDialog = builder.create();
		noticeDialog.getWindow().setType(
				WindowManager.LayoutParams.TYPE_SYSTEM_ALERT); // 设置最顶层Alertdialog
		noticeDialog.show();
	}

	/**
	 * 检查版本是否需要更新
	 * 
	 * @return
	 */
	private boolean checkApkVercode() {
		// 比对当前客户端的版本号 跟服务器的版本号是否一致.
		if (getAppVersion().equals(info.getVersion())) {
			return false;
		} else {
			return true;
		}
	}

	static interface UpdateShared {
		String SETTING_UPDATE_APK_INFO = "cbt_upgrade_setting";
		String UPDATE_DATE = "updatedate";
		String APK_VERSION = "apkversion";
		String APK_VERCODE = "apkvercode";
		String CHECK_DATE = "checkdate";
	}

	// 获取当前应用程序的版本号
	public String getAppVersion() {
		// 获取手机的包管理者
		PackageManager pm = mContext.getPackageManager();
		try {
			PackageInfo packInfo = pm.getPackageInfo(mContext.getPackageName(),
					0);
			return packInfo.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			// 不可能发生.
			// can't reach
			return "";

		}
	}
}
