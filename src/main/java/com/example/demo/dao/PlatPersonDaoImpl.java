package com.example.demo.dao;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.demo.entity.PlatPerson;
import com.example.demo.mapper.PlatPersonMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * PlatPersonDaoImpl 表数据服务层接口实现类 学校推送到中间库的人员dao
 * @author liuhuan51
 * @since 2021/6/18 14:57
 * @version 1.0
 */
@Service
public class PlatPersonDaoImpl {

    @Resource
    private PlatPersonMapper mapper;

    public void savePlatPerson(PlatPerson platPerson){
        mapper.insert(platPerson);
    }

    public void updatePlatPerson(PlatPerson platPerson){
        mapper.updateById(platPerson);
    }

    public PlatPerson queryPlatPersonByPersonId(String personId) {
        QueryWrapper<PlatPerson> wrapper = new QueryWrapper<>();
        wrapper.eq("id", personId);
        List<PlatPerson> persons = mapper.selectList(wrapper);
        if(persons != null && persons.size() > 0)
            return persons.get(0);
        return null;
    }

    public List<PlatPerson> queryPlatPersonByPersonIds(List personIds) {
        QueryWrapper<PlatPerson> wrapper = new QueryWrapper<>();
        wrapper.in("id", personIds);
        List<PlatPerson> persons = mapper.selectList(wrapper);
        return persons;
    }

    public List<PlatPerson> getAllPerson() {
        QueryWrapper<PlatPerson> wrapper = new QueryWrapper<>();
        List<PlatPerson> persons = mapper.selectList(wrapper);
        return persons;
    }

    public List<PlatPerson> getPersonsByTime(Date lastUpdateTime) {
        QueryWrapper<PlatPerson> wrapper = new QueryWrapper<>();
        wrapper.gt("update_time", lastUpdateTime);
        List<PlatPerson> persons = mapper.selectList(wrapper);
        return persons;
    }



}
