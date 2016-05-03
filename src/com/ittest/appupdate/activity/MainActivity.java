package com.ittest.appupdate.activity;

import com.ittest.appupdate.R;
import com.ittest.appupdate.network.DownloadManager;
import com.ittest.appupdate.utils.ActivityManager;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;

/**
 * @ClassName: MainActivity
 * @Description: 主页面
 * @date 2016年4月26日 下午3:18:29
 */
public class MainActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		/* 一级菜单 */
		SubMenu sub1 = menu.addSubMenu(1, 1, 0, "退出系统");
		sub1.setIcon(R.drawable.ic_launcher);
		sub1.setHeaderIcon(R.drawable.ic_launcher);
		/* 二级菜单 */
		SubMenu sub2 = menu.addSubMenu(1, 2, 0, "检查更新");
		sub2.setIcon(R.drawable.ic_launcher);
		sub2.setHeaderIcon(R.drawable.ic_launcher);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == 1) {
			new AlertDialog.Builder(this)
					/* 弹出窗口的最上头文字 */
					.setTitle("消息提示")
					.setIcon(R.drawable.ic_launcher)
					.setMessage("确定要退出吗？")
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								public void onClick(
										DialogInterface dialoginterface, int i) {
									ActivityManager.getInstance().exit();
								}
							})
					.setNegativeButton("取消",
							new DialogInterface.OnClickListener() {
								public void onClick(
										DialogInterface dialoginterface, int i) {
									dialoginterface.dismiss();
								}
							}).create().show();
		} else if (item.getItemId() == 2) {
			/* 升级程序[主动] */
			DownloadManager downManger = new DownloadManager(this);
			downManger.checkDownload();
		}
		return true;
	}
}
