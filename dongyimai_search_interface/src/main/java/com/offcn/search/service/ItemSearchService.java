package com.offcn.search.service;

import com.offcn.pojo.TbItem;

import java.util.List;
import java.util.Map;

public interface ItemSearchService {

    public Map<String,Object> search(Map searchMap);

    // 定义一个导入到solr中的方法
    public void importList(List<TbItem> list);

    // s删除solr中的数据
    public void deleteByGoodsIds(List goodsIdList);
}
