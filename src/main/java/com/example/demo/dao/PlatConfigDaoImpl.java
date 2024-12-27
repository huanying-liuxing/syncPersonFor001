package com.example.demo.dao;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.demo.entity.PlatConfig;
import com.example.demo.mapper.PlatConfigMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * PlatConfigDaoImpl 表数据服务层接口实现类 现场配置config的mapper
 * @author liuhuan51
 * @since 2021/6/18 14:57
 * @version 1.0
 */
@Service
public class PlatConfigDaoImpl {

    @Resource
    private PlatConfigMapper mapper;

    public void savePlatOrg(PlatConfig platConfig){
        mapper.insert(platConfig);
    }

    public void updatePlatConfig(PlatConfig platConfig){
        mapper.updateById(platConfig);
    }

    public PlatConfig queryPlatOrgByOrgId(Integer configId) {
        QueryWrapper<PlatConfig> wrapper = new QueryWrapper<>();
        wrapper.eq("id", configId);
        List<PlatConfig> configs = mapper.selectList(wrapper);
        if(configs != null && configs.size() > 0)
            return configs.get(0);
        return null;
    }


}
