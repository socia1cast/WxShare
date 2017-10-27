package com.wxutil;

import java.util.Map;
import org.springframework.context.ApplicationEvent; 

public class WxUtil extends ApplicationEvent {
	private static final long serialVersionUID = 1L;
	private static int times = 0;
	
	public WxUtil(Object source) {  
        super(source);  
    } 
	public static Map<String, String> getSign(String url){  
		JsApiTicket ticketJson = (JsApiTicket) ServletContextUtil.get().getAttribute("JSAPI_TICKET");
		if (null != ticketJson) {
			if (times != 0) times = 0 ;
			return result(ticketJson.getTicket(), url);
		} else if(++times < 5) { //连续获取Token 5次、失败后就停止获取
			AccessTokenUtil.initAndSetAccessToken();
			return getSign(url);
		}
        return null;
    }
	
	private static Map<String, String> result(String ticket, String url){
		Map<String, String> ret = Sign.sign(ticket, url);
        System.out.println("计算出的签名-----------------------");  
        for (Map.Entry entry : ret.entrySet()) {  
            System.out.println(entry.getKey() + ", " + entry.getValue());  
        }  
        System.out.println ("-----------------------");  
        return ret;
	}
}
