package com.ittest.appupdate.utils;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.content.Context;


/**
 * 网络操作类
 * @author： aokunsang
 * @date： 2012-12-18
 */
public class HttpRequestUtil {
	/**
	 * 获取远程数据
	 * @param url
	 * @param paramMap
	 * @return
	 */
	public static String getSourceResult(String url,Map<String,String> paramMap,Context mContext){
		try {
    		HttpPost httpPostRequest = new HttpPost(url);		
			List<NameValuePair> httpParams = new ArrayList<NameValuePair>();	
			if(paramMap!=null && !paramMap.isEmpty()){
		        for (Iterator<Map.Entry<String,String>> it = paramMap.entrySet().iterator(); it.hasNext();) {
		            String key = it.next().getKey();
		            String value = it.next().getValue();
		            httpParams.add(new BasicNameValuePair(key,value));	
		        }
				httpPostRequest.setEntity(new UrlEncodedFormEntity(httpParams,HTTP.UTF_8));
			}
			DefaultHttpClient defaultHttpClient =new DefaultHttpClient();
			defaultHttpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 6000);  
			defaultHttpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 6000);
			HttpResponse httpResponse = defaultHttpClient.execute(httpPostRequest);
			if(httpResponse.getStatusLine().getStatusCode()==200){
				return EntityUtils.toString(httpResponse.getEntity(),HTTP.UTF_8);
			}else{
				return null;
			}
        } catch (Exception e) {
        	e.printStackTrace();
        	return null;
		}
	}
}
