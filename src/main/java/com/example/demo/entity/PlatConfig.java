package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 同步成功的组织表
 * @author liuhuan51
 * @since 2021/5/27 14:55
 * @version 1.0

 */
@Data
@TableName(value = "tb_config")
public class PlatConfig {

    /**
     * 配置序号
     */
    @TableField("id")
    private Integer id;

    /**
     * 记录的上一次人员表updateTime,做增量查询
     */
    @TableField("last_person_update_time")
    private Date lastUpdateTime;

    /**
     * 最新更新uuid
     */
    @TableField("update_tag")
    private String updateTag;

}
