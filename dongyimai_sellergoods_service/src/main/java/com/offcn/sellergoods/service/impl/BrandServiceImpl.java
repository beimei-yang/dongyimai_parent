package com.offcn.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.offcn.entity.PageResult;
import com.offcn.mapper.TbBrandMapper;
import com.offcn.pojo.TbBrand;
import com.offcn.pojo.TbBrandExample;
import com.offcn.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

@Service
public class BrandServiceImpl implements BrandService {

    @Autowired
    private TbBrandMapper brandMapper;

    @Override
    public List<TbBrand> findAll() {

        return brandMapper.selectByExample(null);
    }

    @Override
    public PageResult findByPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        Page<TbBrand> page = (Page<TbBrand>)brandMapper.selectByExample(null);
        return new PageResult(page.getTotal(),page.getResult());
    }

    @Override
    public void add(TbBrand brand) {
        brandMapper.insert(brand);
    }

    @Override
    public void update(TbBrand brand) {
        brandMapper.updateByPrimaryKey(brand);
    }

    @Override
    public TbBrand findById(Long id) {
        return brandMapper.selectByPrimaryKey(id);
    }

    @Override
    public void delete(Long[] ids) {
        for(Long id : ids){
            brandMapper.deleteByPrimaryKey(id);
        }
    }

    @Override
    public PageResult findBySelectPage(TbBrand brand, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        TbBrandExample example = new TbBrandExample();
        if(brand != null){
            TbBrandExample.Criteria criteria = example.createCriteria();
            if(brand.getFirstChar() != null && !brand.getFirstChar().isEmpty()){
                criteria.andFirstCharEqualTo(brand.getFirstChar());
            }
            if(brand.getName() != null && !brand.getName().isEmpty()){
                criteria.andNameLike("%" +brand.getName()+ "%");
            }

        }
        Page<TbBrand> page = (Page<TbBrand>)brandMapper.selectByExample(example);
        return new PageResult(page.getTotal(),page.getResult());
    }

    @Override
    public List<Map> selectBrandList() {
        return brandMapper.selectBrandList();
    }
}
