package com.offcn.page.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.offcn.mapper.TbGoodsDescMapper;
import com.offcn.mapper.TbGoodsMapper;
import com.offcn.mapper.TbItemCatMapper;
import com.offcn.mapper.TbItemMapper;
import com.offcn.page.service.ItemPageService;
import com.offcn.pojo.*;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;

import java.io.FileWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ItemPageServiceImpl implements ItemPageService {

    @Value("${pagedir}")
    private String pagedir;

    @Autowired
    private FreeMarkerConfig freeMarkerConfig;

    @Autowired
    private TbGoodsMapper tbGoodsMapper;

    @Autowired
    private TbGoodsDescMapper tbGoodsDescMapper;

    @Autowired
    private TbItemCatMapper itemCatMapper;

    @Autowired
    private TbItemMapper itemMapper;


    @Override
    public boolean createHTML(Long goodsId) {

        try {
            Configuration configuration = freeMarkerConfig.getConfiguration();
            Template template = configuration.getTemplate("item.ftl");
            // 数据模型
            Map map = new HashMap();
            // 查询tbGoods中的数据
            TbGoods tbGoods = tbGoodsMapper.selectByPrimaryKey(goodsId);
            map.put("tbGoods",tbGoods);
            // 查询tbGoodsDesc中的数据
            TbGoodsDesc tbGoodsDesc = tbGoodsDescMapper.selectByPrimaryKey(goodsId);
            map.put("tbGoodsDesc",tbGoodsDesc);
            // 商品分类
            String itemCat1= itemCatMapper.selectByPrimaryKey(tbGoods.getCategory1Id()).getName();
            String itemCat2= itemCatMapper.selectByPrimaryKey(tbGoods.getCategory2Id()).getName();
            String itemCat3= itemCatMapper.selectByPrimaryKey(tbGoods.getCategory3Id()).getName();
            map.put("itemCat1",itemCat1);
            map.put("itemCat2",itemCat2);
            map.put("itemCat3",itemCat3);
            // 获取SKU表中的记录
            TbItemExample example = new TbItemExample();
            TbItemExample.Criteria criteria = example.createCriteria();
            criteria.andStatusEqualTo("1"); //通过审核的商品
            criteria.andGoodsIdEqualTo(goodsId); //指定商品的所有item数据
            // 根据默认进行排序
            example.setOrderByClause("is_default desc");
            List<TbItem> items = itemMapper.selectByExample(example);
            map.put("itemList",items);

            // 生成的结果 每个文件名就是商品的id
            FileWriter fileWriter = new FileWriter(pagedir + goodsId + ".html");
            template.process(map,fileWriter);
            fileWriter.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }


    }
}
