package com.ittest.appupdate.network;


import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.ittest.appupdate.R;
import com.ittest.appupdate.network.DownloadManager.UpdateShared;
import com.ittest.appupdate.po.Const;
import com.ittest.appupdate.utils.IntentUtil;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


/**
 * @author： aokunsang
 * @date： 2012-12-18
 */
public class DownloadInstall implements DownloadCallback {

	private Context mContext;
	private String apkPath,apkVersion;
	private LayoutInflater inflater;
	
	private TextView textView;
	private ProgressBar progressView;
	private AlertDialog downloadDialog;    //下载弹出框
	private boolean interceptFlag = false;  //是否取消下载
	
	public DownloadInstall(Context mContext,String apkPath,String apkVersion) {
		this.mContext = mContext;
		this.apkPath = apkPath;
		this.apkVersion = apkVersion;
		inflater = LayoutInflater.from(mContext);
	}
	
	@Override
	public boolean onCancel() {
		return interceptFlag;
	}

	@Override
	public void onChangeProgress(int progress) {
		progressView.setProgress(progress);   //设置下载进度
		textView.setText("进度："+progress+"%");
	}

	@Override
	public void onCompleted(boolean success, String errorMsg) {
		if(downloadDialog!=null){
			downloadDialog.dismiss();
		}
		if(success){  //更新成功
			installApk();
		}else{
			Toast.makeText(mContext, errorMsg, Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onDownloadPreare() {
		if(IntentUtil.checkSoftStage(mContext)){
			File file = new File(Const.apkSavepath);
			if(!file.exists()){
				file.mkdir();
			}
			Builder builder = new AlertDialog.Builder(mContext);
			builder.setIcon(R.drawable.ic_launcher).setTitle("正在下载新版本");
			//---------------------------- 设置在对话框中显示进度条 --------------------
			View view = inflater.inflate(R.layout.upgrade_apk, null);
			textView = (TextView)view.findViewById(R.id.progressCount_text);
			textView.setText("进度：0");
			progressView = (ProgressBar)view.findViewById(R.id.progressbar);
			builder.setView(view);
			
			builder.setNegativeButton("取消", new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					interceptFlag = true; 
				}
			});
			downloadDialog = builder.create();
			downloadDialog.show();
		}
	}
	
	/**
	 * 安装apk
	 */
	private void installApk(){
		File file = new File(apkPath);
		if(!file.exists()){
			return;
		}
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
		mContext.startActivity(intent);
	}
}
