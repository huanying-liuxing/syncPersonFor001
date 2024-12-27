package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 同步成功的组织表
 * @author liuhuan51
 * @since 2021/5/27 14:55
 * @version 1.0

 sql:
    -- Drop table
    DROP TABLE public.tb_sync_plat_org;
    CREATE TABLE public.tb_sync_plat_org (
    id varchar NOT null PRIMARY KEY,
    org_name varchar NULL,
    parent_id varchar NULL
    );
    -- Permissions
    ALTER TABLE public.tb_sync_plat_org OWNER TO postgres;
    GRANT ALL ON TABLE public.tb_sync_plat_org TO postgres;
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "tb_sync_plat_org")
public class PlatOrg {


    /**
     * 组织id，在平台的唯一标识
     */
    @TableField("id")
    private String id;

    /**
     * 组织名称
     */
    @TableField("org_name")
    private String orgName;

    /**
     * 父组织id
     */
    @TableField("parent_id")
    private String parentId;

}
