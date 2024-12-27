package com.example.demo.dao;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.demo.entity.CardInfo;
import com.example.demo.entity.PlatOrg;
import com.example.demo.mapper.PlatCardMapper;
import com.example.demo.mapper.PlatOrgMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 *  表数据服务层接口实现类 学校推送的卡片信息mapper
 * @author liuhuan51
 * @since 2021/6/18 14:57
 * @version 1.0
 */
@Service
public class PlatCardDaoImpl {

    @Resource
    private PlatCardMapper mapper;

    public void savePlatCard(CardInfo card){
        mapper.insert(card);
    }

    public void updatePlatOrg(CardInfo card){
        mapper.updateById(card);
    }

    public List<CardInfo> queryCardsByPersonId(String personId) {
        QueryWrapper<CardInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("xgh", personId);
        List<CardInfo> cards = mapper.selectList(wrapper);
        return cards;
    }


    public List<CardInfo> getAllCards() {
        QueryWrapper<CardInfo> wrapper = new QueryWrapper<>();
        List<CardInfo> cards = mapper.selectList(wrapper);
        return cards;
    }

}
