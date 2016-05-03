package com.ittest.appupdate.po;

import android.os.Environment;


/**
 * 常量类
 * @author： aokunsang
 * @date： 2012-12-18
 */
public final class Const {

	/* 检查是否升级url*/
	public final static String apkCheckUpdateUrl = "http://ip:port/xxxx";
	/* 在多少天内不检查升级 */
	public final static int defaultMinUpdateDay = 50;
	public static String apkSavepath = Environment
			.getExternalStorageDirectory().getAbsolutePath();
}
