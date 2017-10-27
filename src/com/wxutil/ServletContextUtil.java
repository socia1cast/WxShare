package com.wxutil;

import javax.servlet.ServletContext;  

import org.springframework.web.context.ContextLoader;  
import org.springframework.web.context.WebApplicationContext;  
  
/** 
* @Description: 全局缓存servletcontext 
 */  
public final class ServletContextUtil {  
      
    private static ServletContext serveltContext = null;  
      
    private ServletContextUtil(){};  
      
    public synchronized static ServletContext get() {  
          
        if(null == serveltContext) {  
            WebApplicationContext webApplicationContext = ContextLoader.getCurrentWebApplicationContext();    
            serveltContext = webApplicationContext.getServletContext();   
        }   
        return serveltContext;  
    }  
}  
