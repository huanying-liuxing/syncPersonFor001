package com.example.demo.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.demo.bean.Person;
import com.example.demo.entity.PlatMiddlePerson;
import com.hikvision.artemis.sdk.ArtemisHttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @version 2021-08-07
 */
@Service
@Slf4j
public class CollageAkService {

    //openapi调用后等待时长，单位毫秒
//    @Value("${waitTime}")
//    private String waitTime;

    @Value("${debugLevel}")
    boolean debugLevel;

    /**
     * 能力开放平台的网站路径
     * TODO 路径不用修改，就是/artemis
     */
    private static final String ARTEMIS_PATH = "/artemis";

    //向平台批量新增组织
    public synchronized boolean batchAddGroupToCol(List groupList){

        final String getCamsApi = ARTEMIS_PATH +"/api/resource/v1/org/batch/add";
        Map<String, String> path = new HashMap<String, String>(2) {
            {
                put("https://", getCamsApi);//根据现场环境部署确认是http还是https
            }
        };
//        String body = goupList.toString();
        String body = JSON.toJSONString(groupList);
        //[{"orgIndexCode": "1239017947124","orgName": "组织test1","parentIndexCode": "root000000"}]
        log.info("addGroupToCol param = " + body);
        //{"code": "0","msg": "success","data": {"successes": [{"clientId": 1,"orgIndexCode": "qyc8c894ch1y94c19y4c2"}],"failures": [{ "clientId": 22, "code": "-1","msg": "error"}]}}
        String result =ArtemisHttpUtil.doPostStringArtemis(path,body,null,null,"application/json",null);// post请求application/json类型参数
        Map map = JSON.parseObject(result, Map.class);
        String code = map.get("code").toString();
//        try {
//            Thread.sleep(Long.parseLong(waitTime));
//        } catch (Exception e) {
//            log.error("时间转换错误" , e);
//        }
        if(!"0".equals(code))
            return false;
        return true;
    }


    //向平台批量新增组织
    public synchronized boolean addSingleGroupToCol(Map group){
        final String getCamsApi = ARTEMIS_PATH +"/api/resource/v1/org/batch/add";
        Map<String, String> path = new HashMap<String, String>(2) {
            {
                put("https://", getCamsApi);//根据现场环境部署确认是http还是https
            }
        };
        List groupList = new ArrayList();
        groupList.add(group);
//        String body = goupList.toString();
        String body = JSON.toJSONString(groupList);
        //[{"orgIndexCode": "1239017947124","orgName": "组织test1","parentIndexCode": "root000000"}]
        log.info("addSingleGroupToCol param = " + body);
        //{"code": "0","msg": "success","data": {"successes": [{"clientId": 1,"orgIndexCode": "qyc8c894ch1y94c19y4c2"}],"failures": [{ "clientId": 22, "code": "-1","msg": "error"}]}}
        String result =ArtemisHttpUtil.doPostStringArtemis(path,body,null,null,"application/json",null);// post请求application/json类型参数
        log.info("addSingleGroupToCol result = " + result);
        Map map = JSON.parseObject(result, Map.class);
        String code = map.get("code").toString();
//        try {
//            Thread.sleep(Long.parseLong(waitTime));
//        } catch (Exception e) {
//            log.error("时间转换错误" , e);
//        }
        if(!"0".equals(code))
            return false;
        return true;
    }


    //向平台更新组织
    public synchronized boolean updateSingleGroupToCol(Map group){
        final String getCamsApi = ARTEMIS_PATH +"/api/resource/v1/org/single/update";
        Map<String, String> path = new HashMap<String, String>(2) {
            {
                put("https://", getCamsApi);//根据现场环境部署确认是http还是https
            }
        };
//        String body = group.toString();
        String body = JSON.toJSONString(group);
        //{"orgIndexCode": "1rwad89d-0ce6-4826-9146-6b71f037d81e","orgName": "组织test1"}
        log.info("updateSingleGroupToCol param = " + body);
        //{"code": "0","msg": "success","data": ""}
        String result =ArtemisHttpUtil.doPostStringArtemis(path,body,null,null,"application/json",null);// post请求application/json类型参数
        log.info("updateSingleGroupToCol result = " + result);
        Map map = JSON.parseObject(result, Map.class);
        String code = map.get("code").toString();
//        try {
//            Thread.sleep(Long.parseLong(waitTime));
//        } catch (Exception e) {
//            log.error("时间转换错误" , e);
//        }
        if(!"0".equals(code))
            return false;
        return true;
    }


    //向平台批量删除组织
    public synchronized boolean batchDeleteGroupToCol(List<String> groupIdList){
        final String getCamsApi = ARTEMIS_PATH +"/api/resource/v1/org/batch/delete";
        Map<String, String> path = new HashMap<String, String>(2) {
            {
                put("https://", getCamsApi);//根据现场环境部署确认是http还是https
            }
        };
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("indexCodes", groupIdList);
//        String body = jsonBody.toJSONString();
        String body = JSON.toJSONString(jsonBody);
        //{"indexCodes": ["b8d059a7-608a-4800-9b84-4eb8332978d2"]}
        log.info("batchDeleteGroupToCol param = " + body);
        //{"code": "0","msg": "success","data": [{"orgIndexCode": "8f0c821038cy090c9y3","code": "-1","msg": "error"}]}
        String result =ArtemisHttpUtil.doPostStringArtemis(path,body,null,null,"application/json",null);// post请求application/json类型参数
        log.info("batchDeleteGroupToCol result = " + result);
        Map map = JSON.parseObject(result, Map.class);
        String code = map.get("code").toString();
//        try {
//            Thread.sleep(Long.parseLong(waitTime));
//        } catch (Exception e) {
//            log.error("时间转换错误" , e);
//        }
        if(!"0".equals(code))
            return false;
        return true;
    }


    //向平台删除单个组织
    public synchronized boolean delSingleGroupToCol(String groupId){
        final String getCamsApi = ARTEMIS_PATH +"/api/resource/v1/org/batch/delete";
        Map<String, String> path = new HashMap<String, String>(2) {
            {
                put("https://", getCamsApi);//根据现场环境部署确认是http还是https
            }
        };
        ArrayList<String> groupIdList = new ArrayList();
        groupIdList.add(groupId);
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("indexCodes", groupIdList);
        //{"indexCodes": ["b8d059a7-608a-4800-9b84-4eb8332978d2"]}
//        String body = jsonBody.toJSONString();
        String body = JSON.toJSONString(jsonBody);
        log.info("delSingleGroupToCol param = " + body);
        //{"code": "0","msg": "success","data": [{"orgIndexCode": "8f0c821038cy090c9y3","code": "-1","msg": "error"}]}
        String result =ArtemisHttpUtil.doPostStringArtemis(path,body,null,null,"application/json",null);// post请求application/json类型参数
        log.info("delSingleGroupToCol result = " + result);
        Map map = JSON.parseObject(result, Map.class);
        String code = map.get("code").toString();
//        try {
//            Thread.sleep(Long.parseLong(waitTime));
//        } catch (Exception e) {
//            log.error("时间转换错误" , e);
//        }
        if(!"0".equals(code))
            return false;
        return true;
    }


    //向平台批量新增人员
    public boolean batchAddPersonToCol(List<Map> personList){
        final String getCamsApi = ARTEMIS_PATH +"/api/resource/v1/person/batch/add";
        Map<String, String> path = new HashMap<String, String>(2) {
            {
                put("https://", getCamsApi);//根据现场环境部署确认是http还是https
            }
        };
        //[{"personId": "y6a9fy8yh8cy0y010yc92y9y19c09","personName": "person0","orgIndexCode": "8861-4281","gender": 1,"jobNo": "111"}]
//        String body = personList.toString();
        String body = JSON.toJSONString(personList);
        log.info("batchAddPersonToCol param = " + body);
        //{"code": "0","msg": "success","data": {"successes": [{"clientId": 123456789,"personId": "5668f44c-a7fd41"}],"failures": [ {"clientId": 555, "code": "-1","message": "OrgIExists"}]}}
        String result =ArtemisHttpUtil.doPostStringArtemis(path,body,null,null,"application/json",null);// post请求application/json类型参数
        log.info("batchAddPersonToCol result = " + result);
        Map map = JSON.parseObject(result, Map.class);
        String code = map.get("code").toString();
//        try {
//            Thread.sleep(Long.parseLong(waitTime));
//        } catch (Exception e) {
//            log.error("时间转换错误" , e);
//        }
        if(!"0".equals(code))
            return false;
        return true;
    }


    //向平台新增人员
    public boolean addSinglePersonToCol(Map personMap){
        final String getCamsApi = ARTEMIS_PATH +"/api/resource/v2/person/single/add";
        Map<String, String> path = new HashMap<String, String>(2) {
            {
                put("https://", getCamsApi);//根据现场环境部署确认是http还是https
            }
        };
//        String body = personMap.toString();
        String body = JSON.toJSONString(personMap);
        log.info("addSinglePersonToCol param = " + body);
        String result =ArtemisHttpUtil.doPostStringArtemis(path,body,null,null,"application/json",null);// post请求application/json类型参数
        log.info("addSinglePersonToCol result = " + result);
        Map map = JSON.parseObject(result, Map.class);
        String code = map.get("code").toString();
//        try {
//            Thread.sleep(Long.parseLong(waitTime));
//        } catch (Exception e) {
//            log.error("时间转换错误" , e);
//        }
        if(!"0".equals(code))
            return false;
        return true;
    }


    //向平台更新人员
    public boolean updatePersonToCol(Map person){
        final String getCamsApi = ARTEMIS_PATH +"/api/resource/v1/person/single/update";
        Map<String, String> path = new HashMap<String, String>(2) {
            {
                put("https://", getCamsApi);//根据现场环境部署确认是http还是https
            }
        };
//        String body = person.toString();
        String body = JSON.toJSONString(person);
        log.info("updatePersonToCol param = " + body);
        String result =ArtemisHttpUtil.doPostStringArtemis(path,body,null,null,"application/json",null);// post请求application/json类型参数
        log.info("updatePersonToCol result = " + result);
        Map map = JSON.parseObject(result, Map.class);
        String code = map.get("code").toString();
//        try {
//            Thread.sleep(Long.parseLong(waitTime));
//        } catch (Exception e) {
//            log.error("时间转换错误" , e);
//        }
        if(!"0".equals(code))
            return false;
        return true;
    }

    //向平台批量删除人员
    public boolean batchDeletePersonFromCol(List<String> personIdList){
        final String getCamsApi = ARTEMIS_PATH +"/api/resource/v1/person/batch/delete";
        Map<String, String> path = new HashMap<String, String>(2) {
            {
                put("https://", getCamsApi);//根据现场环境部署确认是http还是https
            }
        };
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("personIds", personIdList);
        //{"personIds": ["d7228950bf29490484bc845dff756b95"]}
//        String body = jsonBody.toJSONString();
        String body = JSON.toJSONString(jsonBody);
        log.info("batchDeletePersonFromCol param = " + body);
        //{"code": "0","msg": "success","data": [ {"personId": "12803709iafhych8yr01c9","code": "-1","msg": "error"}]}
        String result =ArtemisHttpUtil.doPostStringArtemis(path,body,null,null,"application/json",null);// post请求application/json类型参数
        log.info("batchDeletePersonFromCol result = " + result);
        Map map = JSON.parseObject(result, Map.class);
        String code = map.get("code").toString();
//        try {
//            Thread.sleep(Long.parseLong(waitTime));
//        } catch (Exception e) {
//            log.error("时间转换错误" , e);
//        }
        if(!"0".equals(code))
            return false;
        return true;
    }

    //向平台删除人员
    public boolean delSinglePersonFromCol(String personId){
        final String getCamsApi = ARTEMIS_PATH +"/api/resource/v1/person/batch/delete";
        Map<String, String> path = new HashMap<String, String>(2) {
            {
                put("https://", getCamsApi);//根据现场环境部署确认是http还是https
            }
        };
        ArrayList personIdList = new ArrayList();
        personIdList.add(personId);
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("personIds", personIdList);
        //{"personIds": ["d7228950bf29490484bc845dff756b95"]}
//        String body = jsonBody.toJSONString();
        String body = JSON.toJSONString(jsonBody);
        log.info("delSinglePersonFromCol param = " + body);
        //{"code": "0","msg": "success","data": [ {"personId": "12803709iafhych8yr01c9","code": "-1","msg": "error"}]}
        String result =ArtemisHttpUtil.doPostStringArtemis(path,body,null,null,"application/json",null);// post请求application/json类型参数
        log.info("delSinglePersonFromCol result = " + result);
        Map map = JSON.parseObject(result, Map.class);
        String code = map.get("code").toString();
//        try {
//            Thread.sleep(Long.parseLong(waitTime));
//        } catch (Exception e) {
//            log.error("时间转换错误" , e);
//        }
        if(!"0".equals(code))
            return false;
        return true;
    }




    //批量查询人员v2
    public List<Person> queryPersonV2(Map query){
        List<Person> list = new ArrayList<>();
        final String getCamsApi = ARTEMIS_PATH +"/api/resource/v2/person/advance/personList";
        Map<String, String> path = new HashMap<String, String>(2) {
            {
                put("https://", getCamsApi);//根据现场环境部署确认是http还是https
            }
        };
//        String body = query.toString();
        String body = JSON.toJSONString(query);
        log.info("queryPersonV2 body = " + body);
        String result = ArtemisHttpUtil.doPostStringArtemis(path,body,null,null,"application/json",null);// post请求application/json类型参数
        log.info("queryPersonV2 result = " + result);
        Map map = JSON.parseObject(result, Map.class);
        String code = map.get("code").toString();
        if("0".equals(code)){
            String dataStr = map.get("data").toString();
            Map dataMap = JSON.parseObject(dataStr, Map.class);
            String listStr = dataMap.get("list").toString();
            List<Person> persons = JSON.parseArray(listStr, Person.class);
            return persons;
        }
        return null;
    }


    //从平台获取根组织
    public String getRootOrg(){
        final String getCamsApi = ARTEMIS_PATH +"/api/resource/v1/org/rootOrg";
        Map<String, String> path = new HashMap<String, String>(2) {
            {
                put("https://", getCamsApi);//根据现场环境部署确认是http还是https
            }
        };
        List goupList = new ArrayList();
        JSONObject jsonBody = new JSONObject();
//        String body = jsonBody.toJSONString();
        String body = JSON.toJSONString(jsonBody);
        log.info("getRootOrg param = " + body);
        String result =ArtemisHttpUtil.doPostStringArtemis(path,body,null,null,"application/json",null);// post请求application/json类型参数
        //{"code": "0","msg": "ok","data": {"orgIndexCode": "4CC7c89d-0ce6-4826-9146-6b71f037d81e","orgNo": "0000","orgName": "默认部门","orgPath": "默认部门","parentOrgIndexCode": "0","parentOrgName": "null","updateTime": "null"}}
        log.info("getRootOrg result = " + result);
        Map map = JSON.parseObject(result, Map.class);
        String code = map.get("code").toString();
//        try {
//            Thread.sleep(Long.parseLong(waitTime));
//        } catch (Exception e) {
//            log.error("时间转换错误" , e);
//        }
        if("0".equals(code)){
            return map.get("data").toString();
        }
        return null;
    }




    //向平台删除人员照片
    public boolean deleteImgToCol(String faceId){
        final String getCamsApi = ARTEMIS_PATH +"/api/resource/v1/face/single/delete";
        Map<String, String> path = new HashMap<String, String>(2) {
            {
                put("https://", getCamsApi);//根据现场环境部署确认是http还是https
            }
        };
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("faceId", faceId);
        //{"faceId": "fd1004e3-4b99-4a9f-bce5-3d00344c507"}
//        String body = jsonBody.toJSONString();
        String body = JSON.toJSONString(jsonBody);
        log.info("deleteImgToCol param = " + body);
        //{"code": "0","message": "success","data": ""}
        String result =ArtemisHttpUtil.doPostStringArtemis(path,body,null,null,"application/json",null);// post请求application/json类型参数
        log.info("deleteImgToCol result = " + result);
        Map map = JSON.parseObject(result, Map.class);
        String code = map.get("code").toString();
//        try {
//            Thread.sleep(Long.parseLong(waitTime));
//        } catch (Exception e) {
//            log.error("时间转换错误" , e);
//        }
        if(!"0".equals(code))
            return false;
        return true;
    }


    //向平台添加人员照片
    public Map addPersonFaceToCol(Map personMap){
        final String getCamsApi = ARTEMIS_PATH +"/api/resource/v1/face/single/add";
        Map<String, String> path = new HashMap<String, String>(2) {
            {
                put("https://", getCamsApi);//根据现场环境部署确认是http还是https
            }
        };
        //{"personId": "be7be3b49e4740c3bef45fc7cdd9fb40","faceData": "/9j/4AAQSkZJRgABAQEAAAAAAAD/4QBCRXhpZgAATU.."}
//        String body = personMap.toString();
        String body = JSON.toJSONString(personMap);
        log.info("addPersonFaceToCol start personId = {}", personMap.get("personId"));
        if(debugLevel)
            log.info("addPersonFaceToCol param = " + body);
        //{"code": "0","msg": "success","data": {"faceId": "fd1004e3-4b99-4a9f-bce5-3d00344c507e","faceUrl": "/pic?=d5=ia95z2452s441-c63428--de366289d69f9i7b7*=ids1*=idp1*=pd*m4i1t=5e5458691-25id06*e057fd2","personId": "be7be3b49e4740c3bef45fc7cdd9fb40"}}
        String result =ArtemisHttpUtil.doPostStringArtemis(path,body,null,null,"application/json",null);// post请求application/json类型参数
        log.info("addPersonFaceToCol personId = {}, result = {}", personMap.get("personId"), result);
        Map map = JSON.parseObject(result, Map.class);
        Object code = map.get("code");
//        try {
//            Thread.sleep(Long.parseLong(waitTime));
//        } catch (Exception e) {
//            log.error("时间转换错误" , e);
//        }
        if("0".equals(code)){
            String data = map.get("data").toString();
            //{"faceId": "fd1004e3-4c507e","faceUrl": "/pic?=d5=ia95z8452s741-c83428--de366289d69f9i7b7*=ids1*=idp2*=pd*m1i1t=5e5458991-88id06*e057fd2","personId": "be7be3b49e4740c3bef45fc7cdd9fb40"}
            //{"code":"0","msg":"SUCCESS","data":{"faceId":"f2f7bc11-890e-4478-b0f8-a334c97eb776","personId":"0006134195","faceUrl":"/pic?4d66=3041i29-=o4418p=9fc3*59-8e1b02eb4**811===spt**7114=0730961*5825=8l5o6fe46o-18*l94-4d08c85a4"}}
            Map dataMap = JSON.parseObject(data, Map.class);
            if(dataMap != null)
                return dataMap;
        }else{
//            String faceDataType = PicUtils.getImgBase64Type(personMap.get("faceData").toString());
//            log.info("addPersonFaceToCol upload fail personId = {}, faceDataType = {}", personMap.get("personId"), faceDataType);
            //将图片写入本地文件夹
//            String picBase64 = personMap.get("faceData").toString();
//            String fullPath = "/home/hik/sync/" + personMap.get("personId").toString() + ".jpg";
//            File file = new File(fullPath);
//            if(file.exists())
//                file.delete();
//            PicUtils.base64ToFile(picBase64, fullPath);
        }
        return null;
    }

    //向平台更新人员照片
    public Map updatePersonFaceToCol(PlatMiddlePerson person, Map personMap){
        final String getCamsApi = ARTEMIS_PATH +"/api/resource/v1/face/single/update";
        Map<String, String> path = new HashMap<String, String>(2) {
            {
                put("https://", getCamsApi);//根据现场环境部署确认是http还是https
            }
        };
        //{"faceId": "b8d059a7-608a-4800-9b84-4eb8332978ec","faceData": "/9j/4AAQSkZJRgABAQEAAAAAAAD/4QBCRXhpZgAATU..."}
        String body = JSON.toJSONString(personMap);
        log.info("updatePersonFaceToCol start personId = {}", person.getId());
        if(debugLevel)
            log.info("updatePersonFaceToCol param = " + body);
        //{"code": "0","msg": "success","data": {"faceId": "fd1004e3-4b99-4a9f-bce5-3d00344c507e","faceUrl": "/pic?=d5=ia95z8452s741-c83428--de366289d69f9i7b7*=ids1*=idp2*=pd*m1i1t=5e5458991-88id06*e057fd2","personId": "be7be3b49e4740c3bef45fc7cdd9fb40"}}
        String result =ArtemisHttpUtil.doPostStringArtemis(path,body,null,null,"application/json",null);// post请求application/json类型参数
        log.info("updatePersonFaceToCol finishi,personId = {}, result = {}", person.getId(), result);
        Map map = JSON.parseObject(result, Map.class);
        Object code = map.get("code");
//        try {
//            Thread.sleep(Long.parseLong(waitTime));
//        } catch (Exception e) {
//            log.error("时间转换错误" , e);
//        }
        if("0".equals(code)){
            String data = map.get("data").toString();
            //{"faceId": "fd1004e3-4b99-4a9f-bce5-3d00344c507e","faceUrl": "/pic?=d5=ia95z8452s741-c83428--de366289d69f9i7b7*=ids1*=idp2*=pd*m1i1t=5e5458991-88id06*e057fd2","personId": "be7be3b49e4740c3bef45fc7cdd9fb40"}
            Map dataMap = JSON.parseObject(data, Map.class);
            if(dataMap != null)
                return dataMap;
        }
        return null;
    }


    //向平台添加卡片
    public boolean addCardToCol(Map cardListInfo){
        final String getCamsApi = ARTEMIS_PATH +"/api/cis/v1/card/bindings";
        Map<String, String> path = new HashMap<String, String>(2) {
            {
                put("https://", getCamsApi);//根据现场环境部署确认是http还是https
            }
        };
//        String body = person.toString();
        String body = JSON.toJSONString(cardListInfo);
        //{"cardList":["cardNo":"10102929","personId":"230d9sdjsde"]}
        log.info("addCardToCol param = " + body);
        String result =ArtemisHttpUtil.doPostStringArtemis(path,body,null,null,"application/json",null);// post请求application/json类型参数
        log.info("addCardToCol result = " + result);
        Map map = JSON.parseObject(result, Map.class);
        String code = map.get("code").toString();
//        try {
//            Thread.sleep(Long.parseLong(waitTime));
//        } catch (Exception e) {
//            log.error("时间转换错误" , e);
//        }
        if(!"0".equals(code))
            return false;
        return true;
    }

    //向平台退卡
    public boolean delCardToCol(Map cardInfo){
        final String getCamsApi = ARTEMIS_PATH +"/api/cis/v1/card/deletion";
        Map<String, String> path = new HashMap<String, String>(2) {
            {
                put("https://", getCamsApi);//根据现场环境部署确认是http还是https
            }
        };
//        String body = person.toString();
        String body = JSON.toJSONString(cardInfo);
        //{"cardNumber": "100000002","personId": "370d303b-3294-428b-993b-07e6b3f09295"}
        log.info("delCardToCol param = " + body);
        String result =ArtemisHttpUtil.doPostStringArtemis(path,body,null,null,"application/json",null);// post请求application/json类型参数
        log.info("delCardToCol result = " + result);
        Map map = JSON.parseObject(result, Map.class);
        String code = map.get("code").toString();
//        try {
//            Thread.sleep(Long.parseLong(waitTime));
//        } catch (Exception e) {
//            log.error("时间转换错误" , e);
//        }
        if(!"0".equals(code))
            return false;
        return true;
    }

}
