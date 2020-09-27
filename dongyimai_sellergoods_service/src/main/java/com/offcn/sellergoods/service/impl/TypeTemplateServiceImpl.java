package com.offcn.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.offcn.entity.PageResult;
import com.offcn.mapper.TbSpecificationOptionMapper;
import com.offcn.mapper.TbTypeTemplateMapper;
import com.offcn.pojo.TbSpecificationOption;
import com.offcn.pojo.TbSpecificationOptionExample;
import com.offcn.pojo.TbTypeTemplate;
import com.offcn.pojo.TbTypeTemplateExample;
import com.offcn.pojo.TbTypeTemplateExample.Criteria;
import com.offcn.service.TypeTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;
import java.util.Map;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service(timeout = 3000)
public class TypeTemplateServiceImpl implements TypeTemplateService {

	@Autowired
	private TbTypeTemplateMapper typeTemplateMapper;
	@Autowired
	private TbSpecificationOptionMapper optionMapper;
	@Autowired
	private RedisTemplate redisTemplate;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbTypeTemplate> findAll() {
		return typeTemplateMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbTypeTemplate> page=   (Page<TbTypeTemplate>) typeTemplateMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbTypeTemplate typeTemplate) {

		typeTemplateMapper.insert(typeTemplate);
		saveToRedis();
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(TbTypeTemplate typeTemplate){
		typeTemplateMapper.updateByPrimaryKey(typeTemplate);
		saveToRedis();
	}
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbTypeTemplate findOne(Long id){
		return typeTemplateMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {

		for(Long id:ids){
			typeTemplateMapper.deleteByPrimaryKey(id);
		}
		saveToRedis();
	}
	
	
		@Override
	public PageResult findPage(TbTypeTemplate typeTemplate, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbTypeTemplateExample example=new TbTypeTemplateExample();
		Criteria criteria = example.createCriteria();
		
		if(typeTemplate!=null){			
						if(typeTemplate.getName()!=null && typeTemplate.getName().length()>0){
				criteria.andNameLike("%"+typeTemplate.getName()+"%");
			}			if(typeTemplate.getSpecIds()!=null && typeTemplate.getSpecIds().length()>0){
				criteria.andSpecIdsLike("%"+typeTemplate.getSpecIds()+"%");
			}			if(typeTemplate.getBrandIds()!=null && typeTemplate.getBrandIds().length()>0){
				criteria.andBrandIdsLike("%"+typeTemplate.getBrandIds()+"%");
			}			if(typeTemplate.getCustomAttributeItems()!=null && typeTemplate.getCustomAttributeItems().length()>0){
				criteria.andCustomAttributeItemsLike("%"+typeTemplate.getCustomAttributeItems()+"%");
			}	
		}
		
		Page<TbTypeTemplate> page= (Page<TbTypeTemplate>)typeTemplateMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * @Author dsy
	 * @Date 2020/9/7 10:33
	 * @Description This is description of method
	 * @Param id 是模板的ID
	 * @Return java.util.List<java.util.Map>
	 * @Since version-1.0
	 */

	@Override
	public List<Map> findSpecList(Long id) {
		TbTypeTemplate tbTypeTemplate = typeTemplateMapper.selectByPrimaryKey(id);
		//tbTypeTemplate.getSpecIds(); //json类型
		List<Map> list = JSON.parseArray(tbTypeTemplate.getSpecIds(), Map.class);
		if(list != null){
			for(Map map : list){
				// {"id":35,"text":"电子书",options:[规格项]}
				 Long specId = new Long((Integer)map.get("id"));
				TbSpecificationOptionExample example = new TbSpecificationOptionExample();
				TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
				criteria.andSpecIdEqualTo(specId);
				List<TbSpecificationOption> tbSpecificationOptions = optionMapper.selectByExample(example);
				map.put("options",tbSpecificationOptions);
			}
		}
		return list;
	}

	private void saveToRedis(){
		// 获取模板中的所有的数据
		List<TbTypeTemplate> allTypeTemplate = findAll();
		for(TbTypeTemplate template : allTypeTemplate) {
			// 获取品牌
			List<Map> brands = JSON.parseArray(template.getBrandIds(),Map.class);
			// 存储到redis中
			redisTemplate.boundHashOps("brandList").put(template.getId(),brands);
			// 获取规格
			List<Map> specList = findSpecList(template.getId());
			redisTemplate.boundHashOps("specList").put(template.getId(),specList);
		}

	}

}
