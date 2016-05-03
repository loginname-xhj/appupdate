package com.ittest.appupdate.network;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.ittest.appupdate.utils.IntentUtil;

import android.os.AsyncTask;
/**
 * 异步下载数据
 * @author： aokunsang
 * @date： 2012-12-17
 */
public class DownloadAsyncTask extends AsyncTask<String, Integer, String> {

	private DownloadCallback downCallBack;
	private HttpURLConnection urlConn;
	
	public DownloadAsyncTask(DownloadCallback downloadCallback){
		this.downCallBack = downloadCallback;
	}
	
	@Override
	protected void onPreExecute() {
		downCallBack.onDownloadPreare();
		super.onPreExecute();
	}
	
	@Override
	protected String doInBackground(String... args) {
		String apkDownloadUrl = args[0]; //apk下载地址
		String apkPath = args[1];   //apk在sd卡中的安装位置
		String result = "";
		if(!IntentUtil.checkURL(apkDownloadUrl)){
			result = "netfail";
		}else{
			InputStream is = null;
			FileOutputStream fos = null;
			try {
				URL url = new URL(apkDownloadUrl);
				urlConn = (HttpURLConnection)url.openConnection();
				is = urlConn.getInputStream();
				int length = urlConn.getContentLength();   //文件大小
				fos = new FileOutputStream(apkPath);
				
				int count = 0,numread = 0;
				byte buf[] = new byte[1024];
				
				while(!downCallBack.onCancel()&& (numread = is.read(buf))!=-1){
					count+=numread;
					int progressCount =(int)(((float)count / length) * 100);
					publishProgress(progressCount);
					fos.write(buf, 0, numread);
				}
				fos.flush();
				result = "success";
			} catch (Exception e) {
				e.printStackTrace();
				result = "fail";
			}finally{
				try {
					if(fos!=null)
						fos.close();
					if(is!=null)
						is.close();
				} catch (IOException e) {
					e.printStackTrace();
					result = "fail";
				}
			}
		}
		return result;
	}
	
	@Override
	protected void onProgressUpdate(Integer... values) {
		downCallBack.onChangeProgress(values[0]);
		super.onProgressUpdate(values);
	}
	
	@Override
	protected void onPostExecute(String result) {
		if(downCallBack.onCancel()){
			downCallBack.onCompleted(false, "版本更新下载已取消。");
		}else if("success".equals(result)){
			downCallBack.onCompleted(true, null);
		}else if("netfail".equals(result)){
			downCallBack.onCompleted(false, "连接服务器失败，请稍后重试。");
		}else{
			downCallBack.onCompleted(false, "版本更新失败，请稍后重试。");
		}
		super.onPostExecute(result);
	}
	
	@Override
	protected void onCancelled() {
		if(urlConn!=null){
			urlConn.disconnect();
		}
		super.onCancelled();
	}
}
