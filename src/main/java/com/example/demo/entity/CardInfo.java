package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 学校同步到中间表的卡片，会有一人多卡的情况
 */
@Data
@TableName(value = "tb_card_info")
public class CardInfo {

    /**
     * 账号
     */
    @TableField("ZH")
    private String zh;

    /**
     * 学工号，和人员表的id字段对应
     */
    @TableField("XGH")
    private String xgh;

    /**
     * 物理卡号
     */
    @TableField("KH")
    private String kh;

    /**
     * 卡类型
     */
    @TableField("KLX")
    private String klx;

}
