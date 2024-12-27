package com.example.demo.dao;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.demo.entity.PlatMiddlePerson;
import com.example.demo.entity.PlatPerson;
import com.example.demo.mapper.PlatMiddlePersonMapper;
import com.example.demo.mapper.PlatPersonMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * PlatPersonDaoImpl 表数据服务层接口实现类 同步成功的人员dao
 * @author liuhuan51
 * @since 2021/6/18 14:57
 * @version 1.0
 */
@Service
public class PlatMiddlePersonDaoImpl {

    @Resource
    private PlatMiddlePersonMapper mapper;

    public void saveMiddlePlatPerson(PlatMiddlePerson platPerson){
        mapper.insert(platPerson);
    }

    public void updateMiddlePlatPerson(PlatMiddlePerson platPerson){
        mapper.updateById(platPerson);
    }

    public PlatMiddlePerson queryPlatMiddlePersonByPersonId(String personId) {
        QueryWrapper<PlatMiddlePerson> wrapper = new QueryWrapper<>();
        wrapper.eq("id", personId);
        List<PlatMiddlePerson> persons = mapper.selectList(wrapper);
        if(persons != null && persons.size() > 0)
            return persons.get(0);
        return null;
    }

    public List<PlatMiddlePerson> queryPlatMiddlePersonByPersonIds(List personId) {
        QueryWrapper<PlatMiddlePerson> wrapper = new QueryWrapper<>();
        wrapper.in("id", personId);
        List<PlatMiddlePerson> persons = mapper.selectList(wrapper);
        return persons;
    }

    public List<PlatMiddlePerson> queryPlatMiddlePersonByUpdateTag(String updateTag) {
        QueryWrapper<PlatMiddlePerson> wrapper = new QueryWrapper<>();
        wrapper.ne("update_tag", updateTag);
        List<PlatMiddlePerson> persons = mapper.selectList(wrapper);
        return persons;
    }

    public List<PlatMiddlePerson> getAllMiddlePerson() {
        QueryWrapper<PlatMiddlePerson> wrapper = new QueryWrapper<>();
        List<PlatMiddlePerson> persons = mapper.selectList(wrapper);
        return persons;
    }

    public List<PlatMiddlePerson> getMiddlePersonsByTime(Date lastUpdateTime) {
        QueryWrapper<PlatMiddlePerson> wrapper = new QueryWrapper<>();
        wrapper.gt("update_time", lastUpdateTime);
        List<PlatMiddlePerson> persons = mapper.selectList(wrapper);
        return persons;
    }

    public void delByIds(List<String> personIds){
        QueryWrapper<PlatMiddlePerson> wrapper = new QueryWrapper<>();
        wrapper.in("id", personIds);
        mapper.delete(wrapper);
    }



}
