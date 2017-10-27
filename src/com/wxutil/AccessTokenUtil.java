package com.wxutil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.URL;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import javax.servlet.ServletContext;

import net.sf.json.JSONObject;

import java.util.Properties;

public class AccessTokenUtil {
    
    public static final  String ACCESS_TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET";  
    private static final String JSAPI_TICKET_URL = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token=ACCESS_TOKEN&type=jsapi";  
      
    /** 
     * @Description: //初始化access_token 
     */  
    public static void initAndSetAccessToken() {  
        System.out.println("初始化Token : "+System.currentTimeMillis());  
        Properties prop = new Properties();  
        try {  
            InputStream in = AccessTokenUtil.class.getResourceAsStream("/wechat.properties");  
            prop.load(in);  
        } catch (IOException e) {  
        	System.out.println("初始化Token错误："+ e.getMessage());  
        }  
        String appid = prop.getProperty("APPID");  
        String appsecret = prop.getProperty("APPSECRET"); 
        ServletContext sc = ServletContextUtil.get();
        if(!appid.isEmpty() && !appsecret.isEmpty()) {  
            AccessToken accessToken = getAccessToken(appid,appsecret); 
            if(null != accessToken) {  
                /** 
                 * cache access_token 
                 */  
                sc.removeAttribute("ACCESS_TOKEN");  
                sc.setAttribute("ACCESS_TOKEN", accessToken);  
                  
                /** 
                 * cache jsapi_ticket 
                 */  
                JsApiTicket jsApiTicket = getJsApiTicket(accessToken.getAccess_token());  
                if(null != jsApiTicket) {  
                    sc.removeAttribute("JSAPI_TICKET");  
                    sc.setAttribute("JSAPI_TICKET", jsApiTicket);  
                } else {
                	sc.removeAttribute("JSAPI_TICKET");
                    sc.setAttribute("JSAPI_TICKET", null);
                    System.out.println("获取Token成功，获取Ticket错误");
                }
            } else {
                sc.removeAttribute("JSAPI_TICKET");
                sc.setAttribute("JSAPI_TICKET", null);
                System.out.println("获取Token错误");
            }
        } else {  
        	System.out.println("初始化Token错误： appid,appsecret 为空");  
        }  
        System.out.println("初始化Token结束："+System.currentTimeMillis());  
    }  
    
    /** 
     * 获取access_token 
     *  
     * @param appid 凭证 
     * @param appsecret 密钥 
     * @return 
     */  
    public static AccessToken getAccessToken(String appid, String appsecret) {  
        try {  
        	String requestUrl = ACCESS_TOKEN_URL.replace("APPID", appid).replace("APPSECRET", appsecret);
            String result = httpRequest(requestUrl, "GET", null); 
            System.out.println("微信服务器获取token:"+result);  
            JSONObject rqJsonObject = JSONObject.fromObject(result);  
            AccessToken tokenJson = (AccessToken) JSONObject.toBean(rqJsonObject,AccessToken.class);  
            return tokenJson;  
        } catch (Exception e) {  
            e.printStackTrace();  
            return null;  
        }  
    }  
      
    public static JsApiTicket getJsApiTicket(String accessToken) {  
    	try {  
	        String requestUrl = JSAPI_TICKET_URL.replace("ACCESS_TOKEN", accessToken);  
	        String result = httpRequest(requestUrl, "GET", null); 
	        System.out.println("微信服务器获取ticket:"+result);  
	        JSONObject ticketJson = JSONObject.fromObject(result);  
	        JsApiTicket ticket = (JsApiTicket) JSONObject.toBean(ticketJson,JsApiTicket.class);
	        return ticket;
    	} catch (Exception e) {  
            e.printStackTrace();  
            return null;  
        }  
    }  
  
    /** 
     * 发起https请求并获取结果 
     *  
     * @param requestUrl 请求地址 
     * @param requestMethod 请求方式（GET、POST） 
     * @param outputStr 提交的数据 
     * @return String
     */  
    public static String httpRequest(String requestUrl, String requestMethod, String outputStr) {  
    	String retString = "";
		StringBuffer buffer = new StringBuffer();
		OutputStream outputStream = null;
		InputStream inputStream= null;
		HttpsURLConnection httpUrlConn = null;
		try {
			URL url = new URL(requestUrl); 
			trustAllHttpsCertificates();
			HttpsURLConnection.setDefaultHostnameVerifier(hv);
			httpUrlConn =  (HttpsURLConnection) url.openConnection();
			httpUrlConn.setDoOutput(true);
			httpUrlConn.setDoInput(true);
			httpUrlConn.setUseCaches(false);
			httpUrlConn.setConnectTimeout(30000);
			System.setProperty("sun.net.client.defaultConnectTimeout", "30000");// 连接超时30秒
			System.setProperty("sun.net.client.defaultReadTimeout", "30000"); // 读取超时30秒
			
			// 设置请求方式（GET/POST）
			httpUrlConn.setRequestMethod(requestMethod);
			if ("GET".equalsIgnoreCase(requestMethod))
				httpUrlConn.connect();
			// 当有数据需要提交时
			if (null != outputStr) {
				outputStream = httpUrlConn.getOutputStream();
				// 注意编码格式，防止中文乱码
				outputStream.write(outputStr.getBytes("UTF-8"));
				outputStream.flush();
				outputStream.close();
			}
			
			// 将返回的输入流转换成字符串
			 inputStream = httpUrlConn.getInputStream();
				
			InputStreamReader inputStreamReader = new InputStreamReader(
					inputStream, "utf-8");
			BufferedReader bufferedReader = new BufferedReader(
					inputStreamReader);
			String str = null;
			while ((str = bufferedReader.readLine()) != null) {
				buffer.append(str);
			}
			
			bufferedReader.close();
			inputStreamReader.close();

			// 释放资源
			inputStream.close();
			inputStream = null;
			httpUrlConn.disconnect();
			retString = buffer.toString();

		} catch (ConnectException ce) {
			ce.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try{
				if(null != inputStream){
					inputStream.close();
				}
				if(null != outputStream){
					outputStream.close();
				}
				httpUrlConn.disconnect();
			}catch (Exception e) {
				System.out.println("关闭流失败");
			}
		}
		return retString;
    }
    
    /**
	 * 忽略java认证方法
	 * @throws Exception
	 */
	
	static HostnameVerifier hv = new HostnameVerifier() {

		public boolean verify(String hostname, SSLSession session) {
			return true;
		}
	};
	
	
	private static void trustAllHttpsCertificates() throws Exception {  
        javax.net.ssl.TrustManager[] trustAllCerts = new javax.net.ssl.TrustManager[1];  
        javax.net.ssl.TrustManager tm = new miTM();  
        trustAllCerts[0] = tm;  
        javax.net.ssl.SSLContext sc = javax.net.ssl.SSLContext  
                .getInstance("SSL");  
        sc.init(null, trustAllCerts, null);  
        javax.net.ssl.HttpsURLConnection.setDefaultSSLSocketFactory(sc  
                .getSocketFactory());  
    }  
  
    static class miTM implements javax.net.ssl.TrustManager,  
            javax.net.ssl.X509TrustManager {  
        public java.security.cert.X509Certificate[] getAcceptedIssuers() {  
            return null;  
        }  
  
        public boolean isServerTrusted(  
                java.security.cert.X509Certificate[] certs) {  
            return true;  
        }  
  
        public boolean isClientTrusted(  
                java.security.cert.X509Certificate[] certs) {  
            return true;  
        }  
  
        public void checkServerTrusted(  
                java.security.cert.X509Certificate[] certs, String authType)  
                throws java.security.cert.CertificateException {  
            return;  
        }  
  
        public void checkClientTrusted(  
                java.security.cert.X509Certificate[] certs, String authType)  
                throws java.security.cert.CertificateException {  
            return;  
        }  
    }  
}
