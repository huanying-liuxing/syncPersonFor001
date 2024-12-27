package com.example.demo.vo;

import lombok.Data;

/**
 * 卡片信息
 */
@Data
public class CardInfo {
    /**
     * 卡片标识
     */
    private String cardId;
    /**
     * 卡号
     */
    private String cardNo;
    /**
     * 持卡人员标识
     */
    private String personId;
}
