package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 同步成功的人员照片表
 * @author liuhuan51
 * @since 2021/5/27 14:55
 * @version 1.0
 * sql

 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "tb_person_pic")
public class PlatPersonPic {


    /**
     * 人员标识，在平台中唯一标识一个人员,职工号
     */
    @TableField("id")
    private String id;

    /**
     * 人员照片信息,用来比对是否需要更新平台
     */
    @TableField("pic_data")
    private String picData;

    /**
     * 人员照片id
     */
    @TableField("face_id")
    private String faceId;

    /**
     * 数据更新时间
     */
    @TableField("update_time")
    private Date updateTime;

}
