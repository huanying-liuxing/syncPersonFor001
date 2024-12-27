package com.example.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.entity.PlatPerson;
import com.example.demo.entity.PlatPersonPic;
import org.apache.ibatis.annotations.Mapper;

/**
 * 同步成功的人员照片表dao接口
 * @author liuhuan
 * @since 2021/6/18 14:57
 * @version 1.0
 */
@Mapper
public interface PlatPersonPicMapper extends BaseMapper<PlatPersonPic> {
}
