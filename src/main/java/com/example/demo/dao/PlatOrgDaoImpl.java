package com.example.demo.dao;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.demo.entity.PlatOrg;
import com.example.demo.mapper.PlatOrgMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * PlatOrgDaoImpl 表数据服务层接口实现类 同步成功的组织mapper
 * @author liuhuan51
 * @since 2021/6/18 14:57
 * @version 1.0
 */
@Service
public class PlatOrgDaoImpl {

    @Resource
    private PlatOrgMapper mapper;

    public void savePlatOrg(PlatOrg platOrg){
        mapper.insert(platOrg);
    }

    public void updatePlatOrg(PlatOrg platOrg){
        mapper.updateById(platOrg);
    }

    public PlatOrg queryPlatOrgByOrgId(String orgId) {
        QueryWrapper<PlatOrg> wrapper = new QueryWrapper<>();
        wrapper.eq("id", orgId);
        List<PlatOrg> orgs = mapper.selectList(wrapper);
        if(orgs != null && orgs.size() > 0)
            return orgs.get(0);
        return null;
    }


    public List<PlatOrg> getAllOrg() {
        QueryWrapper<PlatOrg> wrapper = new QueryWrapper<>();
        List<PlatOrg> orgs = mapper.selectList(wrapper);
        return orgs;
    }

}
