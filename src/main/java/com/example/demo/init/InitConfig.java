package com.example.demo.init;

import com.example.demo.service.SyncService;
import com.hikvision.artemis.sdk.config.ArtemisConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class InitConfig implements CommandLineRunner {

    @Value("${colHost}")
    String colHost;

    @Value("${client_id}")
    String clientId;

    @Value("${client_secret}")
    String clientSecret;

    @Autowired
    private SyncService syncService;

    @Override
    public void run(String... args) throws Exception {
        //配置artemis信息
        ArtemisConfig.host = colHost;// 代理API网关nginx服务器ip端口
        ArtemisConfig.appKey = clientId;// 秘钥appkey
        ArtemisConfig.appSecret = clientSecret;// 秘钥appSecret
/*
        log.info("启动线程开始同步。。。。。。");
        new Thread(()->{
            while(true){
                try{
                    String res = syncService.syncStart();
                    log.debug("线程执行同步结果={}", res);
                }catch (Exception e){
                    log.error("同步失败", e);
                }
            }
        });
        log.info("线程启动结束。。。。。。");
        */

    }
}
