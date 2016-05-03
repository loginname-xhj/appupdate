package com.ittest.appupdate.engine;

import java.io.ByteArrayInputStream;
import org.apache.http.protocol.HTTP;
import org.xmlpull.v1.XmlPullParser;
import android.util.Xml;

import com.ittest.appupdate.domain.UpdateInfo;

/**
 * @Description:字符串的xml解析(采用的是pull解析)
 * @date 2016年4月27日 上午10:03:00
 */
public class XMLSParser {
	public static UpdateInfo getUpdateInfo(String xml) {
		ByteArrayInputStream bInputStream;
		UpdateInfo updateInfo = null;
		// xml的内容为空
		if (xml == null || ("".equals(xml)))
			return null;
	
		bInputStream = new ByteArrayInputStream(xml.getBytes());
		XmlPullParser pullParser = Xml.newPullParser();
		updateInfo = new UpdateInfo();
		try {
			pullParser.setInput(bInputStream, HTTP.UTF_8);
			int eventType = pullParser.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				switch (eventType) {
				case XmlPullParser.START_DOCUMENT:// 文档开始的事件
					
					break;
				case XmlPullParser.START_TAG:
					String name = pullParser.getName();
					if ("version".equals(name)) {
						String version = pullParser.nextText();
						updateInfo.setVersion(version);
					} else if ("description".equals(name)) {
						String description = pullParser.nextText();
						updateInfo.setDescription(description);
					} else if ("apkurl".equals(name)) {
						String apkurl = pullParser.nextText();
						updateInfo.setApkurl(apkurl);
					}
					break;
				case XmlPullParser.END_TAG:
					break;
				}
				eventType = pullParser.next();
			}
			bInputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return updateInfo;
	}
}
