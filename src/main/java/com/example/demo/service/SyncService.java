package com.example.demo.service;

import com.alibaba.fastjson.JSON;
import com.example.demo.bean.Person;
import com.example.demo.dao.*;
import com.example.demo.entity.*;
import com.example.demo.utils.EncryptUtils;
import com.example.demo.utils.ImgConvert;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SyncService {

    @Autowired
    private CollageAkService collageAkService;

    @Autowired
    private PlatPersonDaoImpl platPersonDao;


    @Autowired
    private PlatMiddlePersonDaoImpl platMiddlePersonDao;

    @Autowired
    private PlatOrgDaoImpl platOrgDao;

    @Autowired
    private PlatConfigDaoImpl platConfigDao;

    @Autowired
    private PlatPersonPicDaoImpl platPersonPicDao;

    @Autowired
    private PlatCardDaoImpl platCardDao;

    @Value("${rootIndex}")
    String rootIndex;

    //token_url
    @Value("${token_url}")
    String tokenUrl;

    //pic_url
    @Value("${pic_url}")
    String picUrl;

    //APP_ID
    @Value("${app_id}")
    String appId;
    //USER_ID
    @Value("${user_id}")
    String userId;
    //SHARE_TYPE
    @Value("${share_type}")
    String shareType;
    //KEY
    @Value("${key}")
    String key;

    @Value("${debugLevel}")
    boolean debugLevel;

    ArrayBlockingQueue personSyncQueue = new ArrayBlockingQueue(200000);
    ArrayBlockingQueue personCardSyncQueue = new ArrayBlockingQueue(200000);
    ArrayBlockingQueue personFaceSyncQueue = new ArrayBlockingQueue(200000);

    ReentrantLock personInfoSyncLock = new ReentrantLock();

    ReentrantLock personPicSyncLock = new ReentrantLock();

    ReentrantLock personCardSyncLock = new ReentrantLock();

    /**
     * 人员信息同步
     * @param personIdList 选中的人员id
     * @return
     */
    public String syncStart(List personIdList){
        if(personSyncQueue.size() > 0){
            log.info("历史人员同步任务未执行完成，请稍后再试！！personSyncQueue.size = {}", personSyncQueue.size());
            return "历史人员同步任务未执行完成，请稍后再试！！";
        }
        boolean lockStatus = personInfoSyncLock.tryLock();
        if(!lockStatus){
            return "人员正在同步，请稍后再试！！";
        }
        try{
                //用来存放已校验过的orgId
                HashSet<String> orgIdSet = new HashSet<>();
                PlatConfig platConfig = platConfigDao.queryPlatOrgByOrgId(1);
                if(platConfig == null){
                    log.error("platConfig is null, skip!!");
                    return "fail,platConfig is null";
                }

                String uuid = UUID.randomUUID().toString();
                platConfig.setUpdateTag(uuid);
                platConfig.setLastUpdateTime(new Date());
                platConfigDao.updatePlatConfig(platConfig);

                //从中间库同步人员
            List<PlatPerson> allPerson;
            if(personIdList == null){
                allPerson = platPersonDao.getAllPerson();
            }else{
                allPerson = platPersonDao.queryPlatPersonByPersonIds(personIdList);
            }
                log.info("allPerson = " + allPerson);
            CountDownLatch countDownLatch = new CountDownLatch(allPerson.size());
                //获取根节点indexCode
                String rootOrg = collageAkService.getRootOrg();
                String rootOrgIndexCode = "root00000000";
                if(StringUtils.isNotBlank(rootOrg)){
                    try {
                        Gson gson = new Gson();
                        Map map = gson.fromJson(rootOrg, Map.class);
                        rootOrgIndexCode = map.get("orgIndexCode").toString();
                    } catch (JsonSyntaxException e) {
                        log.error("获取根节点失败,rootOrg = {}", rootOrg);
                    }
                }
                log.info("rootIndex = " + rootOrgIndexCode);

                //更新人员到平台
            ThreadPoolExecutor personSyncPool = new ThreadPoolExecutor(10, 10, 30, TimeUnit.SECONDS, personSyncQueue);

            //先获取到人员的组织,如果不存在,先添加组织,再添加人
                for(PlatPerson person: allPerson){
                    if(StringUtils.isBlank(person.getOrgName())){
                        countDownLatch.countDown();
                        continue;
                    }
                    person.setOrgName(ImgConvert.dealOrgName(person.getOrgName()));
                    person.setName(ImgConvert.dealPersonName(person.getName()));
                    if(person.getOrgId().startsWith("1")){
                        person.setOrgName("医学部" + person.getOrgName());
                    }
                    String orgId = person.getOrgId();
                    if (StringUtils.isBlank(orgId)) {
                        log.error("person orgId is null,person = {}", person);
                        countDownLatch.countDown();
                        continue;
                    }
                    if (!orgIdSet.contains(orgId)) {
                        log.info("start update org,orgId = {}", orgId);
                        //如果之前已经校验过组织了,就不再重复处理
                        PlatOrg platOrg = platOrgDao.queryPlatOrgByOrgId(orgId);
                        log.info("local plat org = " + platOrg);
                        if (platOrg == null) {
                            //dealOrgName
                            // add org to plat
                            Map<String, String> orgMap = new HashMap();
                            orgMap.put("orgIndexCode", orgId);
                            orgMap.put("orgName", person.getOrgName());
                            orgMap.put("parentIndexCode", rootOrgIndexCode);
                            platOrg = new PlatOrg(orgId, person.getOrgName(), rootOrgIndexCode);
                            boolean syncOrgRes = collageAkService.addSingleGroupToCol(orgMap);
                            if (syncOrgRes) {
                                platOrgDao.savePlatOrg(platOrg);
                                orgIdSet.add(orgId);
                            }
                        } else if (!Objects.equals(person.getOrgName(), platOrg.getOrgName())) {
                            Map orgMap = new HashMap();
                            orgMap.put("orgIndexCode", orgId);
                            orgMap.put("orgName", person.getOrgName());
                            boolean syncOrgRes = collageAkService.updateSingleGroupToCol(orgMap);
                            platOrg = new PlatOrg(orgId, person.getOrgName(), rootOrgIndexCode);
                            if (syncOrgRes) {
                                platOrgDao.updatePlatOrg(platOrg);
                                orgIdSet.add(orgId);
                            }
                        }
                    }
                    log.info("syncPersonStart PlatMiddlePerson = {}", person);
                    Thread syncInfoPersonThread = new Thread(() -> {
                        syncPersonPeer(person, uuid,countDownLatch);
                    });
                    personSyncPool.execute(syncInfoPersonThread);

                    /*
                    Map<String, Object> personMap = new HashMap();
                    personMap.put("personId", person.getId());
                    personMap.put("jobNo", person.getId());
                    personMap.put("personName", person.getName());
                    personMap.put("gender", "0");
                    personMap.put("orgIndexCode", person.getOrgId());
                    //更新人员信息
                    //1.根据人员id从成功的库查询人员
                    PlatMiddlePerson platMiddlePerson = platMiddlePersonDao.queryPlatMiddlePersonByPersonId(person.getId());
                    if(platMiddlePerson == null){
                        //新增人员
                        //1.1如果未查询到,则新增
                        boolean syncPersonRes = collageAkService.addSinglePersonToCol(personMap);
                        if(syncPersonRes){
                            PlatMiddlePerson mdp = new PlatMiddlePerson();
                            mdp.setId(person.getId());
                            mdp.setName(person.getName());
                            mdp.setOrgId(person.getOrgId());
                            mdp.setOrgName(person.getOrgName());
                            mdp.setUpdateTime(new Date());
                            mdp.setUpdateTag(uuid);
                            platMiddlePersonDao.saveMiddlePlatPerson(mdp);
                        }
                    }else{
                        //判断是否需要更新
                        if(Objects.equals(person.getId(), platMiddlePerson.getId()) && Objects.equals(person.getName(), platMiddlePerson.getName())
                                &&Objects.equals(person.getOrgId(),platMiddlePerson.getOrgId()) && Objects.equals(person.getOrgName(),platMiddlePerson.getOrgName())){
                            //无需更新，仅更新中间成功库的updateTag
                            platMiddlePerson.setUpdateTag(uuid);
                            platMiddlePersonDao.updateMiddlePlatPerson(platMiddlePerson);
                        }else{
                            //需要更新
                            //1.2查询到了,则更新
                            //1.1如果未查询到,则新增
                            boolean syncPersonRes = collageAkService.updatePersonToCol(personMap);
                            if(syncPersonRes){
                                platMiddlePerson.setId(person.getId());
                                platMiddlePerson.setName(person.getName());
                                platMiddlePerson.setOrgId(person.getOrgId());
                                platMiddlePerson.setOrgName(person.getOrgName());
                                platMiddlePerson.setUpdateTime(new Date());
                                platMiddlePerson.setUpdateTag(uuid);
                                platMiddlePersonDao.updateMiddlePlatPerson(platMiddlePerson);
                            }
                        }
                    }
                    */

                }
            countDownLatch.await();
                log.info("所有人员同步的线程都执行完成，继续执行主线程！！");
            //开始处理需要删除的人员
            //在中间成功库，但是本次未更新的
            List<PlatMiddlePerson> removePlatMiddlePersons = platMiddlePersonDao.queryPlatMiddlePersonByUpdateTag(uuid);
            List<String> personIds = removePlatMiddlePersons.stream().map(x -> x.getId()).collect(Collectors.toList());
            log.debug("需要删除的人员信息：removePlatMiddlePersons = {}", removePlatMiddlePersons);
        //        collageAkService.batchDeletePersonFromCol(personIds);
        //        platMiddlePersonDao.delByIds(personIds);

        }catch (Exception e){
            log.error("人员同步任务执行异常", e);
            return "人员同步任务执行异常";
        }finally {
            personInfoSyncLock.unlock();
        }
        return "同步完成!!!";
    }


    public String syncPersonCardStart(List personIdList){
        if(personCardSyncQueue.size() > 0){
            log.info("历史人员卡片同步任务未执行完成，请稍后再试！！personSyncQueue.size = {}", personCardSyncQueue.size());
            return "历史人员卡片同步任务未执行完成，请稍后再试！！";
        }
        boolean res = personCardSyncLock.tryLock();
        if(!res){
            return "卡片正在同步，请稍后再试！！";
        }
        try{
            //1.先查询到所有已经同步成功的人员
            List<PlatMiddlePerson> allMiddlePerson;
            if(personIdList == null){
                allMiddlePerson = platMiddlePersonDao.getAllMiddlePerson();
            }else{
                allMiddlePerson = platMiddlePersonDao.queryPlatMiddlePersonByPersonIds(personIdList);
            }
            log.info("syncPersonCardStart allMiddlePerson.size = {}", allMiddlePerson.size());
            //2.遍历人员，从学校推送库中查询到这个学生的卡信息，可能有多个
            ThreadPoolExecutor personSyncPool = new ThreadPoolExecutor(10, 10, 30, TimeUnit.SECONDS, personCardSyncQueue);
            for(PlatMiddlePerson mdp: allMiddlePerson){
                log.info("syncPersonCardStart PlatMiddlePerson = {}", mdp);
                Thread syncPersonCardThread = new Thread(() -> {
                    syncPersonCard(mdp);
                });
                personSyncPool.execute(syncPersonCardThread);
                /*
                Set<String> curPlatCards = new HashSet<>();
                Set<String> curSchoolCards = new HashSet<>();
                Set<String> addCards = new HashSet<>();
                Set<String> delCards = new HashSet<>();
                String cardNos = mdp.getCardNos();
                if(StringUtils.isNotBlank(cardNos)){
                    String[] split = cardNos.split(",");
                    curPlatCards = Arrays.stream(split).collect(Collectors.toSet());
                }
                //3.用三方卡信息和中间库做比对
                List<CardInfo> cardInfos = platCardDao.queryCardsByPersonId(mdp.getId());
                if (cardInfos != null && cardInfos.size() > 0) {
                    curSchoolCards = cardInfos.stream().map(x->x.getKh()).collect(Collectors.toSet());
                }
                log.info("syncPersonCardStart curPlatCards = {}", curPlatCards);
                log.info("syncPersonCardStart curSchoolCards = {}", curSchoolCards);
                //3.1在三方不在中间库 --- 新增
                for(String card: curSchoolCards){
                    if(!curPlatCards.contains(card)){
                        addCards.add(card);
                    }
                }
                //3.2在三方，也在中间库 --- 不动
                //3.3不在三方，在中间库--- 从平台删除
                for(String card: curPlatCards){
                    if(!curSchoolCards.contains(card)){
                        delCards.add(card);
                    }
                }
                //开卡
                if(addCards.size() > 0){
                    Map cardListInfo = new HashMap();
                    List cards = new ArrayList<>();
                    for(String card: addCards){
                        ////{"cardList":["cardNo":"10102929","personId":"230d9sdjsde"]}
                        Map cardMap = new HashMap();
                        cardMap.put("cardNo", card);
                        cardMap.put("personId", mdp.getId());
                        cards.add(cardMap);
                    }
                    cardListInfo.put("cardList", cards);
                    collageAkService.addCardToCol(cardListInfo);
                }
                //解卡
                if(delCards.size() > 0){
                    for(String card: delCards){
                        Map cardMap = new HashMap();
                        cardMap.put("cardNumber", card);
                        cardMap.put("personId", mdp.getId());
                        collageAkService.delCardToCol(cardMap);
                    }
                }
                if(addCards.size() > 0 || delCards.size() > 0){
                    curPlatCards.addAll(addCards);
                    curPlatCards.removeAll(delCards);
                    String curPlatCardsStr = curPlatCards.stream().collect(Collectors.joining(","));
                    mdp.setCardNos(curPlatCardsStr);
                    log.info("update middle person card info,person = {}", mdp);
                    platMiddlePersonDao.updateMiddlePlatPerson(mdp);
                }
                */
            }
        }catch (Exception e){
            log.error("同步人员卡片失败", e);
            return "同步人员卡片失败";
        }finally {
            personCardSyncLock.unlock();
        }
        return "同步人员卡片开始成功";
    }
    public String syncPicStart(List personIdList){
        if(personFaceSyncQueue.size() > 0){
            log.info("历史人员卡片同步任务未执行完成，请稍后再试！！personFaceSyncQueue.size = {}", personFaceSyncQueue.size());
            return "历史人员卡片同步任务未执行完成，请稍后再试！！";
        }
        boolean lockStatus = personPicSyncLock.tryLock();
        if(!lockStatus){
            return "人员照片正在同步，请稍后再试！！";
        }
        try{
            RestTemplate restTemplate = new RestTemplate();
            //http://data.pku.edu.cn/dataPubWS/svc/sharePicture/getShareToken.do?appId={APP_ID}&userId={USER_ID}&type={SHARE_TYPE}
            String tempTokenUrl = tokenUrl + "?appId=" + appId + "&userId=" +userId + "&type=" + shareType;
            log.info("getToken url = " + tempTokenUrl);
            String getTokenStr = restTemplate.getForObject(tempTokenUrl, String.class);
//            Map getToken = restTemplate.getForObject(tempTokenUrl, Map.class);
            log.info("getTokenStr = " + getTokenStr);
            Map getToken = JSON.parseObject(getTokenStr, Map.class);
            if((boolean)getToken.get("success") == true){
                String token = getToken.get("token").toString();
                log.info("获取token成功, token=" + token);
                //获取平台人员,开始更新照片
                //1.先查询到所有已经同步成功的人员
                List<PlatMiddlePerson> allMiddlePerson;
                if(personIdList == null){
                    allMiddlePerson = platMiddlePersonDao.getAllMiddlePerson();
                }else{
                    allMiddlePerson = platMiddlePersonDao.queryPlatMiddlePersonByPersonIds(personIdList);
                }
                log.info("syncPicStart allMiddlePerson.size = {}", allMiddlePerson.size());
                //2.遍历人员，从学校推送库中查询到这个学生的卡信息，可能有多个
                ThreadPoolExecutor personFaceSyncPool = new ThreadPoolExecutor(5, 5, 30, TimeUnit.SECONDS, personFaceSyncQueue);
                for(PlatMiddlePerson mdp: allMiddlePerson){
                    log.info("syncPersonCardStart PlatMiddlePerson = {}", mdp);
                    Thread syncFacePersonCardThread = new Thread(() -> {
                        syncPersonPicPeer(mdp, token);
                    });
                    personFaceSyncPool.execute(syncFacePersonCardThread);

                    /*
                    long timestamp = System.currentTimeMillis();
                    //MD5(personId+appId+token+timestamp+KEY)
                    String ss = person.getId() + appId + token + timestamp + key;
                    String msgAbs = EncryptUtils.md5(ss);
                    //根据工号查询照片
                    //http://data.pku.edu.cn/dataPubWS/svc/sharePicture/getPicture.do?
                    // personId={PERSON_ID}&appId={APP_ID}&token={TOKEN}&timestamp={TIMESTAMP}&msgAbs={MSG_ABS}&width={WIDTH}&height={HEIGHT}
                    String getPicUrl = picUrl + "?personId=" + person.getId() + "&appId=" + appId + "&token=" + token +
                            "&timestamp=" + timestamp + "&msgAbs=" + msgAbs;
                    log.debug("getPic url = " + tempTokenUrl);
                    Map picMap = restTemplate.getForObject(getPicUrl, Map.class);
//                    {"success":true,"picture":"照片 Base64 串"}
                    log.info("getPic result = " + picMap);
                    boolean success = (boolean)picMap.get("success");
                    if(success){
                        log.debug("get pic success, person = " + person);
                        Object picture = picMap.get("picture");
                        if(picture != null){
                            //获取本地的缓存照片
                            PlatPersonPic platPersonPic = platPersonPicDao.queryPlatPersonPicByPersonId(person.getId());
                            log.debug("person = {}, platPersonPic = {}" ,person, platPersonPic );
                            if(platPersonPic == null){
                                log.info("add pic,person = {}", person);
                                //新增照片
                                Map personFace = new HashMap();
                                personFace.put("personId", person.getId());
                                personFace.put("faceData",picture.toString());
                                Map map = collageAkService.addPersonFaceToCol(personFace);
                                PlatPersonPic face = new PlatPersonPic(person.getId(), picture.toString(), map.get("faceId") + "", new Date());
                                log.debug("save pic = {}", face);
                                platPersonPicDao.savePlatPersonPic(face);
                            }else if(!Objects.equals(picture.toString(), platPersonPic.getPicData())){
                                //更新照片
                                log.info("update pic,person = {}", person);
                                Map personFace = new HashMap();
                                personFace.put("faceId", platPersonPic.getFaceId());
                                personFace.put("faceData",picture.toString());
                                Map map = collageAkService.updatePersonFaceToCol(personFace);
                                platPersonPic.setFaceId(map.get("faceId") + "");
                                platPersonPic.setUpdateTime(new Date());
                                log.debug("update pic = {}", platPersonPic);
                                platPersonPicDao.updatePlatPersonPic(platPersonPic);
                            }
                        }
                    }
                    */

                }
            }
        }catch (Exception e){
            log.error("人员照片同步异常", e);
            return  "人員照片同步异常";
        }finally {
            personPicSyncLock.unlock();
        }
        return "人員照片同步任务开始完成";
    }



    public String test(){
        List<PlatOrg> allOrg = platOrgDao.getAllOrg();
        List<PlatPerson> allPerson = platPersonDao.getAllPerson();
        log.info("allPerson:{}", allPerson);
        System.out.println("allPerson=" + allPerson);
        log.info("allOrg:{}", allOrg);
        System.out.println("allOrg=" + allOrg);

        return "ok";

    }


    public String testReplace(){
        List<PlatOrg> allOrg = platOrgDao.getAllOrg();
        List<PlatPerson> allPerson = platPersonDao.getAllPerson();
        log.info("allPerson:{}", allPerson);
        System.out.println("allPerson=" + allPerson);
        log.info("allOrg:{}", allOrg);
        System.out.println("allOrg=" + allOrg);
        for(PlatPerson platPerson: allPerson){
            String orgName = ImgConvert.dealOrgName(platPerson.getOrgName());
            System.out.println("orgName = " + orgName);
        }

        return "ok";

    }


    public String testSave(){
        PlatOrg org = new PlatOrg();
        String ind = new Date().getTime() + "";
        org.setId(ind);
        org.setOrgName("name" + ind);
        org.setParentId("root000000");
        platOrgDao.savePlatOrg(org);

        PlatPerson p = new PlatPerson();
        p.setId(ind);
        platPersonDao.savePlatPerson(p);

        PlatPersonPic platPersonPic = new PlatPersonPic();
        platPersonPic.setUpdateTime(new Date());
        platPersonPic.setFaceId("faceid99999");
        platPersonPic.setId("999999");
        platPersonPic.setPicData("difjdifewfew0fjwf0ojwf0ewofewf0ojfioewjfei");
        platPersonPicDao.savePlatPersonPic(platPersonPic);

        return "ok";

    }

    private String syncPersonCard(PlatMiddlePerson mdp){
        log.info("syncPersonCard PlatMiddlePerson = {}", mdp);
        Set<String> curPlatCards = new HashSet<>();
        Set<String> curSchoolCards = new HashSet<>();
        Set<String> addCards = new HashSet<>();
        Set<String> delCards = new HashSet<>();
        String cardNos = mdp.getCardNos();
        if(StringUtils.isNotBlank(cardNos)){
            String[] split = cardNos.split(",");
            curPlatCards = Arrays.stream(split).collect(Collectors.toSet());
        }
        //3.用三方卡信息和中间库做比对
        List<CardInfo> cardInfos = platCardDao.queryCardsByPersonId(mdp.getId());
        if (cardInfos != null && cardInfos.size() > 0) {
            curSchoolCards = cardInfos.stream().map(x->x.getKh()).collect(Collectors.toSet());
        }
        log.info("syncPersonCardStart curPlatCards = {}", curPlatCards);
        log.info("syncPersonCardStart curSchoolCards = {}", curSchoolCards);
        //3.1在三方不在中间库 --- 新增
        for(String card: curSchoolCards){
            if(!curPlatCards.contains(card)){
                addCards.add(card);
            }
        }
        //3.2在三方，也在中间库 --- 不动
        //3.3不在三方，在中间库--- 从平台删除
        for(String card: curPlatCards){
            if(!curSchoolCards.contains(card)){
                delCards.add(card);
            }
        }
        //开卡
        if(addCards.size() > 0){
            Map cardListInfo = new HashMap();
            List cards = new ArrayList<>();
            for(String card: addCards){
                ////{"cardList":["cardNo":"10102929","personId":"230d9sdjsde"]}
                Map cardMap = new HashMap();
                cardMap.put("cardNo", card);
                cardMap.put("personId", mdp.getId());
                cards.add(cardMap);
            }
            cardListInfo.put("cardList", cards);
            collageAkService.addCardToCol(cardListInfo);
        }
        //解卡
        if(delCards.size() > 0){
            for(String card: delCards){
                Map cardMap = new HashMap();
                cardMap.put("cardNumber", card);
                cardMap.put("personId", mdp.getId());
                collageAkService.delCardToCol(cardMap);
            }
        }
        if(addCards.size() > 0 || delCards.size() > 0){
            curPlatCards.addAll(addCards);
            curPlatCards.removeAll(delCards);
            String curPlatCardsStr = curPlatCards.stream().collect(Collectors.joining(","));
            mdp.setCardNos(curPlatCardsStr);
            log.info("update middle person card info,person = {}", mdp);
            platMiddlePersonDao.updateMiddlePlatPerson(mdp);
        }
        return "卡片同步完成";
    }

    private String syncPersonPicPeer(PlatMiddlePerson person, String token){
        PlatPersonPic platPersonPic2 = platPersonPicDao.queryPlatPersonPicByPersonId(person.getId());
        if(platPersonPic2 != null && StringUtils.isNotBlank(platPersonPic2.getPicData())){
            log.info("middle db have pic,don't repeat! personId = {}", person.getId());
            return "middle db have pic,don't repeat!";
        }
        Map<String, Object> queryMap = new HashMap();
        queryMap.put("pageNo", 1);
        queryMap.put("pageSize", 1);
        queryMap.put("personIds", person.getId());
        //如果平台上已经有照片了，不再重复获取
        List<Person> people = collageAkService.queryPersonV2(queryMap);
        log.info("people = {}", people);
        if(people != null && people.size() > 0 && people.get(0).getPersonPhoto().size() > 0){
            log.info("person have face in plat,personId = {}", person.getId());
            //更新照片缓存表
            if(platPersonPic2 == null){
                platPersonPic2 = new PlatPersonPic();
                platPersonPic2.setPicData("plat");
                platPersonPic2.setId(person.getId());
                platPersonPic2.setUpdateTime(new Date());
                platPersonPic2.setFaceId("plat");
                platPersonPicDao.savePlatPersonPic(platPersonPic2);
            }else{
                platPersonPic2.setPicData("plat");
                platPersonPic2.setId(person.getId());
                platPersonPic2.setUpdateTime(new Date());
                platPersonPic2.setFaceId("plat");
                platPersonPicDao.updatePlatPersonPic(platPersonPic2);
            }
            return "person have face in plat,personId = {}"+ person.getId();
        }

        RestTemplate restTemplate = new RestTemplate();
        long timestamp = System.currentTimeMillis();
        //MD5(personId+appId+token+timestamp+KEY)
        String ss = person.getId() + appId + token + timestamp + key;
        String msgAbs = EncryptUtils.md5(ss);
        //根据工号查询照片
        //http://data.pku.edu.cn/dataPubWS/svc/sharePicture/getPicture.do?
        // personId={PERSON_ID}&appId={APP_ID}&token={TOKEN}&timestamp={TIMESTAMP}&msgAbs={MSG_ABS}&width={WIDTH}&height={HEIGHT}
        String getPicUrl = picUrl + "?personId=" + person.getId() + "&appId=" + appId + "&token=" + token +
                "&timestamp=" + timestamp + "&msgAbs=" + msgAbs;
        if(debugLevel)
            log.info("getPic url = " + picUrl);
        String picResStr = restTemplate.getForObject(getPicUrl, String.class);
        Map picMap = JSON.parseObject(picResStr, Map.class);
//                    {"success":true,"picture":"照片 Base64 串"}
        // {success=false, errCode=E10, errMsg=照片不存在,picture=difjiejfeiwrje}
        if(debugLevel)
            log.info("getPic result personId={}, picMap = {}", person.getId(), picMap);
        log.info("getPic result personId={}, success = {},errCode={},errMsg={}",
                person.getId(), picMap.get("success"),picMap.get("errCode"),picMap.get("errMsg"));
        boolean success = (boolean)picMap.get("success");
        if(success){
            log.info("get pic success, personId = " + person.getId());
            Object picture = picMap.get("picture");
            if(StringUtils.isNotBlank(picture + "")){
                //获取本地的缓存照片
                PlatPersonPic platPersonPic = platPersonPicDao.queryPlatPersonPicByPersonId(person.getId());
                if(debugLevel)
                    log.info("person = {}, platPersonPic = {}" ,person, platPersonPic );
                if(platPersonPic == null){
                    log.info("add pic,personId = {}", person.getId());
                    //新增照片
                    Map personFace = new HashMap();
                    personFace.put("personId", person.getId());
                    personFace.put("faceData",picture.toString());
                    Map map = collageAkService.addPersonFaceToCol(personFace);
                    if(map != null){
                        PlatPersonPic face = new PlatPersonPic(person.getId(), picture.toString(), map.get("faceId") + "", new Date());
                        if(debugLevel)
                            log.info("save pic = {}", face);
                        platPersonPicDao.savePlatPersonPic(face);
                    }
                }else if(!Objects.equals(picture.toString(), platPersonPic.getPicData())){
                    //更新照片
                    log.info("update pic,personId = {}", person.getId());
                    if(debugLevel)
                       log.info("update pic,person = {}", person);
                    Map personFace = new HashMap();
                    personFace.put("faceId", platPersonPic.getFaceId());
                    personFace.put("faceData",picture.toString());
                    Map map = collageAkService.updatePersonFaceToCol(person, personFace);
                    if(map != null){
                        platPersonPic.setFaceId(map.get("faceId") + "");
                        platPersonPic.setUpdateTime(new Date());
                        log.info("update faceId = {}", platPersonPic.getFaceId());
                        if(debugLevel)
                            log.info("update pic = {}", platPersonPic);
                        platPersonPicDao.updatePlatPersonPic(platPersonPic);
                    }
                }
            }
        }
        return "";
    }

    private String syncPersonPeer(PlatPerson person, String uuid, CountDownLatch latch){
        try {
            log.info("111需要同步人员：{}", person);
            Map<String, Object> personMap = new HashMap();
            personMap.put("personId", person.getId());
            personMap.put("jobNo", person.getId());
            personMap.put("personName", person.getName());
            personMap.put("gender", "0");
            personMap.put("orgIndexCode", person.getOrgId());
            //更新人员信息
            //1.根据人员id从成功的库查询人员
            PlatMiddlePerson platMiddlePerson = platMiddlePersonDao.queryPlatMiddlePersonByPersonId(person.getId());
//            log.info("111需要同步人员中间库信息：{}", platMiddlePerson);
            if(platMiddlePerson == null){
//                log.info("222需要新增人员：{}", person);
                //新增人员
                //1.1如果未查询到,则新增
                boolean syncPersonRes = collageAkService.addSinglePersonToCol(personMap);
                if(syncPersonRes){
                    PlatMiddlePerson mdp = new PlatMiddlePerson();
                    mdp.setId(person.getId());
                    mdp.setName(person.getName());
                    mdp.setOrgId(person.getOrgId());
                    mdp.setOrgName(person.getOrgName());
                    mdp.setUpdateTime(new Date());
                    mdp.setUpdateTag(uuid);
                    platMiddlePersonDao.saveMiddlePlatPerson(mdp);
                }
            }else{
//                log.info("222需要其他人员：{}", person);
                //判断是否需要更新
                if(Objects.equals(person.getId(), platMiddlePerson.getId()) && Objects.equals(person.getName(), platMiddlePerson.getName())
                        &&Objects.equals(person.getOrgId(),platMiddlePerson.getOrgId()) && Objects.equals(person.getOrgName(),platMiddlePerson.getOrgName())){
                    //无需更新，仅更新中间成功库的updateTag
                    log.info("222需要不更新人员：{}", person);
                    platMiddlePerson.setUpdateTag(uuid);
                    platMiddlePersonDao.updateMiddlePlatPerson(platMiddlePerson);
                }else{
//                    log.info("222需要更新人员：{}", person);
                    //需要更新
                    //1.2查询到了,则更新
                    //1.1如果未查询到,则新增
                    boolean syncPersonRes = collageAkService.updatePersonToCol(personMap);
                    if(syncPersonRes){
                        platMiddlePerson.setId(person.getId());
                        platMiddlePerson.setName(person.getName());
                        platMiddlePerson.setOrgId(person.getOrgId());
                        platMiddlePerson.setOrgName(person.getOrgName());
                        platMiddlePerson.setUpdateTime(new Date());
                        platMiddlePerson.setUpdateTag(uuid);
                        platMiddlePersonDao.updateMiddlePlatPerson(platMiddlePerson);
                    }
                }
            }
//            log.info("人员同步成功，person ={}", person);
        } catch (Exception e) {
            log.error("人员信息更新失败,person={}", person, e);
            return "人员同步失败";
        }finally {
            latch.countDown();
        }
        return "人员同步成功";
    }
}
