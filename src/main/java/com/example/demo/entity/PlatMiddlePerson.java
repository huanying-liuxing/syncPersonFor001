package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 成功同步到平台的人员
 */
@Data
@TableName(value = "tb_middle_person_info")
public class PlatMiddlePerson {

    /**
     * 人员标识，在平台中唯一标识一个人员,职工号
     */
    @TableField("id")
    private String id;

    /**
     * 职工姓名
     */
    @TableField("person_name")
    private String name;

    /**
     * 单位代码
     */
    @TableField("org_id")
    private String orgId;

    /**
     * 单位名称
     */
    @TableField("org_name")
    private String orgName;

    /**
     * 数据更新时间
     */
    @TableField("update_time")
    private Date updateTime;

    /**
     * 人员更新标记
     */
    @TableField("update_tag")
    private String updateTag;

    /**
     * 人员已经开的卡，多个用逗号分隔
     */
    @TableField("card_nos")
    private String cardNos;

}
