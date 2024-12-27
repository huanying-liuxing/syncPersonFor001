package com.example.demo.dao;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.demo.entity.PlatPerson;
import com.example.demo.entity.PlatPersonPic;
import com.example.demo.mapper.PlatPersonMapper;
import com.example.demo.mapper.PlatPersonPicMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * PlatPersonDaoImpl 表数据服务层接口实现类 同步成功的人员照片dao
 * @author liuhuan51
 * @since 2021/6/18 14:57
 * @version 1.0
 */
@Service
public class PlatPersonPicDaoImpl {

    @Resource
    private PlatPersonPicMapper mapper;

    public void savePlatPersonPic(PlatPersonPic platPersonPic){
        mapper.insert(platPersonPic);
    }

    public void updatePlatPersonPic(PlatPersonPic platPersonPic){
        mapper.updateById(platPersonPic);
    }

    public PlatPersonPic queryPlatPersonPicByPersonId(String personId) {
        QueryWrapper<PlatPersonPic> wrapper = new QueryWrapper<>();
        wrapper.eq("id", personId);
        List<PlatPersonPic> personPics = mapper.selectList(wrapper);
        if(personPics != null && personPics.size() > 0)
            return personPics.get(0);
        return null;
    }



}
