package com.ittest.appupdate.utils;


import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.ProgressDialog;

public class DownLoadUtils {

	/**
	 * ���صĹ��߷���
	 * 
	 * @param filePath
	 *            �������ļ���·��
	 * @param savePath
	 *            ���ر����·��
	 * @param pd
	 *            ������Ի���
	 * @return
	 */
	public static File download(String filePath, String savePath,
			ProgressDialog pd) {
		try {
			URL url = new URL(filePath);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(5000);
			int code = conn.getResponseCode();
			if (code == 200) {
				File file = new File(savePath);
				FileOutputStream fos = new FileOutputStream(file);
				InputStream is = conn.getInputStream();

				int max = is.available();
				pd.setMax(max);
				int len = 0;
				byte[] buffer = new byte[1024];
				int total = 0;
				while ((len = is.read(buffer)) != -1) {

					fos.write(buffer, 0, len);
					total += len;
					pd.setProgress(total);// ���½�����Ի���Ľ��
					Thread.sleep(30);
				}
				fos.flush();
				fos.close();
				is.close();

				return file;
			} else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}
	
	/**
	 * ��ȡ�������ϵ��ļ����
	 */
	
	public static String getFileName(String filePath){
		int index = filePath.lastIndexOf("/")+1;
		return filePath.substring(index);
	}
	
	
}
