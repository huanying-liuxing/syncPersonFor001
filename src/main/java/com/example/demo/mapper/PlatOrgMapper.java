package com.example.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.entity.PlatOrg;
import org.apache.ibatis.annotations.Mapper;

/**
 * 同步成功的组织表dao接口
 * @author liuhuan
 * @since 2021/6/18 14:57
 * @version 1.0
 */
@Mapper
public interface PlatOrgMapper extends BaseMapper<PlatOrg> {
}
