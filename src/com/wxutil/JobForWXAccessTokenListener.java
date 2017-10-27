package com.wxutil;

import java.util.concurrent.Executors;  
import java.util.concurrent.ScheduledExecutorService;  
import java.util.concurrent.TimeUnit;  
  
import org.springframework.context.ApplicationListener;  
import org.springframework.context.event.ContextRefreshedEvent;  
import org.springframework.stereotype.Component;
 
@Component
public class JobForWXAccessTokenListener implements ApplicationListener<ContextRefreshedEvent>  {  
    @Override  
    public void onApplicationEvent(ContextRefreshedEvent event) { 
        if(event.getApplicationContext().getParent() == null){  
              
            Runnable runnable = new Runnable() {  
                public void run() {  
                    /** 
                     * 定时设置accessToken 
                     */  
                    AccessTokenUtil.initAndSetAccessToken();  
                }  
            };  
              
            ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();  
            service.scheduleAtFixedRate(runnable, 1, 7000, TimeUnit.SECONDS);  
        }  
    }  
}
