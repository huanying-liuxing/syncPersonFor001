package com.example.demo.sync;

import com.example.demo.service.SyncService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@EnableScheduling
public class SyncTask {

    @Autowired
    private SyncService syncService;

    @Value("${rootIndex}")
    String rootIndex;


    @GetMapping("/ssyncPersonStart")
    @Scheduled(cron = "${sync_person_cron}")
    public String syncPersonStart(){
        try{
            log.info("开始同步人员。。。。。。");
            String res = syncService.syncStart(null);
            log.info("同步人员结束。。。。。。");
            return res;
        }catch (Exception e){
            log.error("同步人员失败", e);
            return "同步人员失败";
        }
    }

    @GetMapping("/ssyncPersonCardStart")
    @Scheduled(cron = "${sync_person_card_cron}")
    public String syncPersonCardStart(){
        try{
            log.info("开始同步人员卡片。。。。。。");
            String res = syncService.syncPersonCardStart(null);
            log.info("同步人员卡片结束。。。。。。");
            return res;
        }catch (Exception e){
            log.error("同步人员卡片失败", e);
            return "同步人员卡片失败";
        }
    }

    @GetMapping("/ssyncPicStart")
    @Scheduled(cron = "${sync_pic_cron}")
    public String syncPicStart(){
        try{
            log.info("开始照片同步。。。。。。");
            String res = syncService.syncPicStart(null);
            log.info("同步照片结束。。。。。。");
            return res;
        }catch (Exception e){
            log.error("同步照片失败", e);
            return "同步照片失败";
        }
    }


    @GetMapping("/stest")
    public String test(){
        try{
            System.out.println("root:" + rootIndex);
            String res = syncService.test();
            return res;
        }catch (Exception e){
            log.error("同步失败", e);
            return rootIndex;
        }
    }


    @GetMapping("/stestSave")
    public String testSave(){
        try{
            return syncService.testSave();
        }catch (Exception e){
            log.error("保存失败", e);
            return "保存失败";
        }
    }


    @GetMapping("/stestReplace")
    public String testReplace(){
        try{

            return syncService.testReplace();
        }catch (Exception e){
            log.error("保存失败", e);
            return "保存失败";
        }
    }
}
