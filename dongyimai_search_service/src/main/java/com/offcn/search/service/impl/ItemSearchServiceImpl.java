package com.offcn.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.promeg.pinyinhelper.Pinyin;
import com.offcn.pojo.TbItem;
import com.offcn.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ItemSearchServiceImpl implements ItemSearchService {

    @Autowired
    private SolrTemplate solrTemplate;

    @Autowired
    private RedisTemplate redisTemplate;



    @Override
    public void deleteByGoodsIds(List goodsIdList) {
        Query query  = new SimpleQuery();
        Criteria criteria = new Criteria("item_goodsid").in(goodsIdList);
        query.addCriteria(criteria);
        solrTemplate.delete(query);
        solrTemplate.commit();
    }

    @Override
    public void importList(List<TbItem> list) {
        for(TbItem item : list){
            // 将spec字段从字符串转为Map格式
            Map<String,String> specMap = JSON.parseObject(item.getSpec(), Map.class);
            // 动态域中需要将 键 改为 拼音
            Map map = new HashMap();
            for(String key : specMap.keySet()){
                map.put("item_spec_" + (Pinyin.toPinyin(key,"").toLowerCase()),specMap.get(key));

            }
            item.setSpecMap(map);

        }
        solrTemplate.saveBeans(list);
        solrTemplate.commit();
    }

    @Override
    public Map<String, Object> search(Map searchMap) {
        // 1.创建高亮查询
        SimpleHighlightQuery query = new SimpleHighlightQuery();

        // 删除 keywords中的空格
        String keywords = (String)searchMap.get("keywords");
        searchMap.put("keywords",keywords.replace(" ",""));

        // 过滤条件1.根据分类查询
        searchCategory(query,searchMap);
        // 过滤条件2 根据品牌
        searchBrand(query,searchMap);
        // 过滤条件3 根据规格查询
        searchSpec(query,searchMap);
        // 过滤条件4 根据价格
        searchPrice(query,searchMap);

        // 排序
        sortByField(query,searchMap);

        // 分页
        searhPage(query,searchMap);


        // 实现高亮查询
        Map<String,Object> map = searchHighLightList(query, searchMap);
        // 查询分类名
        List categoryList = searchCategoryList(searchMap);
        map.put("categoryList",categoryList);

        // 展示要显示的品牌和规格
        if(searchMap.get("category").equals("")) { //参数中没有category的选项，默认查询第一个
            map.putAll(searchBrandAndSpecList((String) categoryList.get(0)));
        }else{
            map.putAll(searchBrandAndSpecList((String) searchMap.get("category")) );
        }
        return map;
    }

    // 高亮查询
    private Map searchHighLightList(SimpleHighlightQuery query,Map searchMap){
        Map map = new HashMap();

        // 2.获取需要高亮处理字段
        HighlightOptions options = new HighlightOptions();
        options.addField("item_title");
        // 3.设定前缀和后缀
        options.setSimplePrefix("<em style='color:red'>");
        options.setSimplePostfix("</em>");
        // 4.将高亮选项和高亮查询结合
        query.setHighlightOptions(options);
        // 5.设定查询条件
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        // 6.进行查询
        query.addCriteria(criteria);
        HighlightPage<TbItem> page = solrTemplate.queryForHighlightPage(query, TbItem.class);
        // 7.获取高亮集合的入口
        List<HighlightEntry<TbItem>> highlighted = page.getHighlighted();
        // 8.遍历入口
        for(HighlightEntry<TbItem> entry : highlighted){
            TbItem tbItem = entry.getEntity();
            // 将tbItem中的title进行替换
            // 保证高亮域中有内容，并且高亮域中的片段有值
            if(entry.getHighlights().size()>0 && entry.getHighlights().get(0).getSnipplets().size() > 0){
                // 高亮域的集合
                List<HighlightEntry.Highlight> hightLightList = entry.getHighlights();
                // 获取当前高亮域中的片段
                List<String> snipplets = hightLightList.get(0).getSnipplets();
                // 第一个片段就是要进行存储的值
                tbItem.setTitle(snipplets.get(0));
            }
        }
        map.put("rows",page.getContent());
        map.put("totalPages",page.getTotalPages()); // 查询的总页数
        map.put("total",page.getTotalElements()); // 总的记录数
        return map;
    }

    // 获取分类 通过分组查询获取
    private List searchCategoryList(Map searchMap){
        List<String> list = new ArrayList();
        Query query = new SimpleQuery();
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        query.addCriteria(criteria);
        // 添加分组的选项
        // 根据 item_category 字段分组
        GroupOptions options = new GroupOptions().addGroupByField("item_category");
        query.setGroupOptions(options);
        // 查询
        GroupPage<TbItem> page = solrTemplate.queryForGroupPage(query, TbItem.class);
        // 获取分组结果
        GroupResult<TbItem> groupResult = page.getGroupResult("item_category");
        // 获取入口
        Page<GroupEntry<TbItem>> groupEntries = groupResult.getGroupEntries();
        // 分组入口的内容
        List<GroupEntry<TbItem>> content = groupEntries.getContent();
        for (GroupEntry<TbItem> entry : content){
            list.add(entry.getGroupValue()); // 分组查询的结果
        }
        return list;
    }

    // 基本查询
    private Map<String, Object> simplesearch(Map searchMap) {
        Map<String,Object> map = new HashMap<>();

        Query query = new SimpleQuery();
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));

        query.addCriteria(criteria);

        // 查询
        ScoredPage<TbItem> page = solrTemplate.queryForPage(query, TbItem.class);

        // 查询的结果
        map.put("rows",page.getContent());

        return map;
    }

    /**
     * @Author dsy
     * @Date 2020/9/11 15:05
     * @Description 根据分类名获取对应的品牌和规格
     * @Param [categoryName 分类名]
     * @Return java.util.Map
     * @Since version-1.0
     */
    private Map searchBrandAndSpecList(String categoryName){
       Map map= new HashMap();
       //通过categoryName来获取categoryId
        Long categoryId = (Long)redisTemplate.boundHashOps("itemCat").get(categoryName);
       if(categoryId != null) {
           // 通过categoryId来获取品牌
           List brandList = (List) redisTemplate.boundHashOps("brandList").get(categoryId);
           // 获取规格
           List specList = (List)redisTemplate.boundHashOps("specList").get(categoryId);
           // 将查询出来的brandList 和 specList放入到 map
            map.put("brandList",brandList);
            map.put("specList",specList);
       }
       return map;
    }

    private void searchCategory(SimpleHighlightQuery query,Map searchMap){
       if(!"".equals(searchMap.get("category"))){
           Criteria criteria = new Criteria("item_category").is(searchMap.get("category"));
           FilterQuery filterQuery = new SimpleFilterQuery();
           filterQuery.addCriteria(criteria);
           query.addFilterQuery(filterQuery);
       }
    }

    private void searchBrand(SimpleHighlightQuery query,Map searchMap){
        if(!"".equals(searchMap.get("brand"))){
            Criteria criteria = new Criteria("item_brand").is(searchMap.get("brand"));
            FilterQuery filterQuery = new SimpleFilterQuery();
            filterQuery.addCriteria(criteria);
            query.addFilterQuery(filterQuery);
        }
    }

    private void searchSpec(SimpleHighlightQuery query,Map searchMap){
        if(searchMap.get("spec") != null){
            Map<String,String> specMap = (Map)searchMap.get("spec");
            for(String key : specMap.keySet()){
                // key是汉字 转为拼音
                Criteria criteria = new Criteria("item_spec_" + (Pinyin.toPinyin(key, "").toLowerCase())).is(specMap.get(key));
                FilterQuery filterQuery = new SimpleFilterQuery();
                filterQuery.addCriteria(criteria);
                query.addFilterQuery(filterQuery);
            }
        }
    }

    private void searchPrice(SimpleHighlightQuery query,Map searchMap){
        if(!searchMap.get("price").equals("")){
            String priceStr = (String)searchMap.get("price");
            //格式： 1000-1500
            String[] prices = priceStr.split("-");
            // 0-1000
            // 1000-1500
            // 3000-*
            //第一个关注点 最大值
            if(!prices[1].equals("*")){ //小于等于最大值
                Criteria item_price_less = new Criteria("item_price").lessThanEqual(prices[1]);
                FilterQuery filterQuery = new SimpleFilterQuery(item_price_less);
                query.addFilterQuery(filterQuery);
            }
            if(!prices[0].equals("0")){ // 大于等于最小值
                Criteria item_price_great = new Criteria("item_price").greaterThanEqual(prices[0]);
                FilterQuery filterQuery = new SimpleFilterQuery(item_price_great);
                query.addFilterQuery(filterQuery);
            }
        }
    }

    private void searhPage(SimpleHighlightQuery query,Map searchMap){
        Integer pageNo = (Integer)searchMap.get("pageNo"); // 当前页
        if(pageNo == null){
            pageNo = 1;
        }
        Integer pageSize = (Integer)searchMap.get("pageSize"); // 每页显示的记录数
        if(pageSize == null){
            pageSize = 20;
        }
        query.setOffset((pageNo-1) * pageSize ); //从第几条数据开始查询
        query.setRows(pageSize); //显示多少条记录
    }

    private void sortByField(SimpleHighlightQuery query,Map searchMap){
        String sort = (String)searchMap.get("sort");
        String sortField = (String)searchMap.get("sortField");
        if("ASC".equalsIgnoreCase(sort)){
            Sort orders = new Sort(Sort.Direction.ASC, "item_" + sortField);
            query.addSort(orders);
        }
        if("DESC".equalsIgnoreCase(sort)){
            Sort orders = new Sort(Sort.Direction.DESC, "item_" + sortField);
            query.addSort(orders);
        }
    }
}
