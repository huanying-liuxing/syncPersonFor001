package com.example.demo.sync;

import com.example.demo.service.SyncService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@Slf4j
public class SyncController {

    @Autowired
    private SyncService syncService;

    @Value("${rootIndex}")
    String rootIndex;

    @Value("${debugLevel}")
    boolean debugLevel;


    @GetMapping("/syncPersonStart")
    public String syncPersonStart(String personIds){
        try{
            List personIdList = null;
            log.info("开始同步人员。。。。。。");
            if(StringUtils.isNotBlank(personIds)){
                String[] split = personIds.split(",");
                personIdList = Arrays.asList(split);
            }
            String res = syncService.syncStart(personIdList);
            log.info("同步人员结束。。。。。。");
            return res;
        }catch (Exception e){
            log.error("同步人员失败", e);
            return "同步人员失败";
        }
    }

    @GetMapping("/syncPersonCardStart")
    public String syncPersonCardStart(String personIds){
        try{
            List personIdList = null;
            log.info("开始同步人员卡片。。。。。。");
            if(StringUtils.isNotBlank(personIds)){
                String[] split = personIds.split(",");
                personIdList = Arrays.asList(split);
            }
            String res = syncService.syncPersonCardStart(personIdList);
            log.info("同步人员卡片结束。。。。。。");
            return res;
        }catch (Exception e){
            log.error("同步人员卡片失败", e);
            return "同步人员卡片失败";
        }
    }

    @GetMapping("/syncPicStart")
    public String syncPicStart(String personIds){
        try{
            List personIdList = null;
            log.info("开始照片同步。。。。。。");
            if(StringUtils.isNotBlank(personIds)){
                String[] split = personIds.split(",");
                personIdList = Arrays.asList(split);
            }
            String res = syncService.syncPicStart(personIdList);
            log.info("同步照片结束。。。。。。");
            return res;
        }catch (Exception e){
            log.error("同步照片失败", e);
            return "同步照片失败";
        }
    }


    @GetMapping("/test")
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


    @GetMapping("/testSave")
    public String testSave(){
        try{
            return syncService.testSave();
        }catch (Exception e){
            log.error("保存失败", e);
            return "保存失败";
        }
    }


    @GetMapping("/testReplace")
    public String testReplace(){
        try{

            return syncService.testReplace();
        }catch (Exception e){
            log.error("保存失败", e);
            return "保存失败";
        }
    }


    @GetMapping("/testBool")
    public Object testBool(){
        try{
            System.out.println(debugLevel);
        }catch (Exception e){
        }
            return debugLevel;
    }
}
