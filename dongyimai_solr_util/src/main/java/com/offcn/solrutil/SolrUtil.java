package com.offcn.solrutil;

import com.alibaba.fastjson.JSON;
import com.github.promeg.pinyinhelper.Pinyin;
import com.offcn.mapper.TbItemMapper;
import com.offcn.pojo.TbItem;
import com.offcn.pojo.TbItemExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class SolrUtil {

    @Autowired
    private TbItemMapper itemMapper;

    @Autowired
    private SolrTemplate solrTemplate;

    public void importData(){

        // 获取tbItem表中的所有数据 （满足条件：审核通过的商品）
        TbItemExample example = new TbItemExample();
        TbItemExample.Criteria criteria = example.createCriteria();
        criteria.andStatusEqualTo("1"); //审核通过
        List<TbItem> tbItems = itemMapper.selectByExample(example);


        for(TbItem item : tbItems){
            System.out.println(item.getTitle());
            // 获取每条记录的 spec字段中的内容
            // 键是汉字
            Map<String,String> map = JSON.parseObject(item.getSpec(), Map.class);
            // 值相同，但是键改为拼音
            Map<String,String> pinyinMap = new HashMap<String, String>();
            // 遍历map
            for(String key : map.keySet()){
                // 值  map.get(key)
                String pinyinKey = Pinyin.toPinyin(key,"").toLowerCase();
                pinyinMap.put(pinyinKey,map.get(key));
            }
            item.setSpecMap(pinyinMap);
        }

        // 批量插入
        solrTemplate.saveBeans(tbItems);
        solrTemplate.commit();

    }

    public static void main(String[] args) {

        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath*:spring/applicationContext-*.xml");

        SolrUtil solrUtil = (SolrUtil)context.getBean("solrUtil");

        solrUtil.importData();
    }

}
