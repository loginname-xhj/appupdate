package com.ittest.appupdate.engine;

import java.io.InputStream;

import org.xmlpull.v1.XmlPullParser;
import com.ittest.appupdate.domain.UpdateInfo;

import android.util.Xml;

/**
 * 更新信息xml的解析器
 * 
 * @author Administrator
 * 
 */
public class UpdateInfoParser {
	/**
	 * 返回服务器上的更新信息
	 * 
	 * @param is
	 *            服务器返回的流
	 * @return 更新信息 null代表解析失败
	 */
	public static UpdateInfo getUpdateInfo(InputStream is) {
		XmlPullParser parser = Xml.newPullParser();
		// 初始化解析器.
		try {
			parser.setInput(is, "utf-8");
			int type = parser.getEventType();
			UpdateInfo info = new UpdateInfo();
			while (type != XmlPullParser.END_DOCUMENT) {
				switch (type) {
				case XmlPullParser.START_TAG:
					if ("version".equals(parser.getName())) {
						String version = parser.nextText();
						info.setVersion(version);
					} else if ("description".equals(parser.getName())) {
						String description = parser.nextText();
						info.setDescription(description);
					} else if ("apkurl".equals(parser.getName())) {
						String apkurl = parser.nextText();
						info.setApkurl(apkurl);
					}
					break;
				}
				type = parser.next();
			}

			return info;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}
}
