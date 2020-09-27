package com.offcn.service;

import com.offcn.entity.PageResult;
import com.offcn.pojo.TbBrand;

import java.util.List;
import java.util.Map;

public interface BrandService {

    public List<TbBrand> findAll();

    public PageResult findByPage(int pageNum,int pageSize);

    public void add(TbBrand brand);

    public void update(TbBrand brand);

    public TbBrand findById(Long id);

    public void delete(Long[] ids);

    public PageResult findBySelectPage(TbBrand brand,int pageNum,int pageSize);

    public List<Map> selectBrandList();
}
