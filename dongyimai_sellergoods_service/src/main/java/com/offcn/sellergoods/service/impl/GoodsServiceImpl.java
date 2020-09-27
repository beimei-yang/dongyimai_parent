package com.offcn.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.offcn.entity.PageResult;
import com.offcn.group.Goods;
import com.offcn.mapper.*;
import com.offcn.pojo.*;
import com.offcn.pojo.TbGoodsExample.Criteria;
import com.offcn.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
@Transactional
public class GoodsServiceImpl implements GoodsService {

	@Autowired
	private TbGoodsMapper goodsMapper;

	@Autowired
	private TbGoodsDescMapper goodsDescMapper;

	@Autowired
	private TbItemMapper itemMapper;

	@Autowired
	private TbBrandMapper brandMapper;

	@Autowired
	private TbItemCatMapper itemCatMapper;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbGoods> findAll() {
		return goodsMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbGoods> page=   (Page<TbGoods>) goodsMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(Goods goods) {
		// 该商品没有审核
		goods.getTbGoods().setAuditStatus("0");
		// 插入商品表
		goodsMapper.insert(goods.getTbGoods());
		// 插入商品描述表
		goods.getTbGoodsDesc().setGoodsId(goods.getTbGoods().getId());
		goodsDescMapper.insert(goods.getTbGoodsDesc());

		// int i = 1 / 0;

		// 插入 SKU的数据
		addItemList(goods);
	}

	private void addItemList(Goods goods){
		if("1".equals(goods.getTbGoods().getIsEnableSpec())) {

			// 商品SKU
			for (TbItem item : goods.getItemList()) {
				//标题 是 goods标题+当前的选项
				String title = goods.getTbGoods().getGoodsName();
				Map<String, Object> specMap = JSON.parseObject(item.getSpec(), Map.class);
				for (String key : specMap.keySet()) {
					title = title + specMap.get(key);
				}
				// 最终的item名字
				item.setTitle(title);
				item.setCategoryid(goods.getTbGoods().getCategory3Id());
				item.setCreateTime(new Date());
				item.setUpdateTime(new Date());
				item.setGoodsId(goods.getTbGoods().getId());
				item.setSellerId(goods.getTbGoods().getSellerId());
				item.setCategory(itemCatMapper.selectByPrimaryKey(item.getCategoryid()).getName());

				// 添加品牌
				TbBrand brand = brandMapper.selectByPrimaryKey(goods.getTbGoods().getBrandId());
				item.setBrand(brand.getName());

				// 图片
				List<Map> maps = JSON.parseArray(goods.getTbGoodsDesc().getItemImages(), Map.class);
				if (maps.size() > 0) {
					item.setImage((String) maps.get(0).get("url"));
				}

				itemMapper.insert(item);
			}

		}else{

			TbItem item = new TbItem();
			item.setTitle(goods.getTbGoods().getGoodsName());
			item.setPrice(goods.getTbGoods().getPrice());
			item.setStatus("1"); // 状态
			item.setIsDefault("1"); // 默认
			item.setNum(9999);
			item.setSpec("{}");
			item.setCategoryid(goods.getTbGoods().getCategory3Id());
			item.setCreateTime(new Date());
			item.setUpdateTime(new Date());
			item.setSellerId(goods.getTbGoods().getSellerId());
			item.setGoodsId(goods.getTbGoods().getId());
			item.setCategory(itemCatMapper.selectByPrimaryKey(item.getCategoryid()).getName());

			// 添加品牌
			TbBrand brand = brandMapper.selectByPrimaryKey(goods.getTbGoods().getBrandId());
			item.setBrand(brand.getName());

			// 图片
			List<Map> maps = JSON.parseArray(goods.getTbGoodsDesc().getItemImages(), Map.class);
			if (maps.size() > 0) {
				item.setImage((String) maps.get(0).get("url"));
			}
			itemMapper.insert(item);
		}
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(Goods goods){
		// 一共三张表
		// 商品的状态依然是0
		goods.getTbGoods().setAuditStatus("0");
		// 修改tbGoods表
		goodsMapper.updateByPrimaryKey(goods.getTbGoods());
		// 修改tbGoodsDesc表
		goodsDescMapper.updateByPrimaryKey(goods.getTbGoodsDesc());
		// 修改 item表
		// 先删除
		TbItemExample example = new TbItemExample();
		TbItemExample.Criteria criteria = example.createCriteria();
		criteria.andGoodsIdEqualTo(goods.getTbGoods().getId());
		itemMapper.deleteByExample(example);
		// 后添加
		addItemList(goods);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public Goods findOne(Long id){
		Goods goods = new Goods();
		// 填入goods的三个属性
		goods.setTbGoods(goodsMapper.selectByPrimaryKey(id));
		goods.setTbGoodsDesc(goodsDescMapper.selectByPrimaryKey(id));
		// 查询 SKU的数据 tbItem表
		TbItemExample example = new TbItemExample();
		TbItemExample.Criteria criteria = example.createCriteria();
		criteria.andGoodsIdEqualTo(id);
		List<TbItem> tbItems = itemMapper.selectByExample(example);
		goods.setItemList(tbItems);
		return goods;
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
//			goodsMapper.deleteByPrimaryKey(id);
			TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
			tbGoods.setIsDelete("1"); //删除
			goodsMapper.updateByPrimaryKey(tbGoods);
		}		
	}
	
	
		@Override
	public PageResult findPage(TbGoods goods, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbGoodsExample example=new TbGoodsExample();
		Criteria criteria = example.createCriteria();

		//criteria.andIsDeleteNotEqualTo("1");

		criteria.andIsDeleteIsNull(); // 当前字段为空
		
		if(goods!=null){			
						if(goods.getSellerId()!=null && goods.getSellerId().length()>0){
				criteria.andSellerIdEqualTo(goods.getSellerId());
			}			if(goods.getGoodsName()!=null && goods.getGoodsName().length()>0){
				criteria.andGoodsNameLike("%"+goods.getGoodsName()+"%");
			}			if(goods.getAuditStatus()!=null && goods.getAuditStatus().length()>0){
				criteria.andAuditStatusLike("%"+goods.getAuditStatus()+"%");
			}			if(goods.getIsMarketable()!=null && goods.getIsMarketable().length()>0){
				criteria.andIsMarketableLike("%"+goods.getIsMarketable()+"%");
			}			if(goods.getCaption()!=null && goods.getCaption().length()>0){
				criteria.andCaptionLike("%"+goods.getCaption()+"%");
			}			if(goods.getSmallPic()!=null && goods.getSmallPic().length()>0){
				criteria.andSmallPicLike("%"+goods.getSmallPic()+"%");
			}			if(goods.getIsEnableSpec()!=null && goods.getIsEnableSpec().length()>0){
				criteria.andIsEnableSpecLike("%"+goods.getIsEnableSpec()+"%");
			}
		}
		
		Page<TbGoods> page= (Page<TbGoods>)goodsMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}

	@Override
	public void updateSetatus(Long[] ids, String status) {
		for(Long id : ids){
			// 状态主要修改两张表 tbGoods  tbItems
			// 修改tbGoods表
			TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
			tbGoods.setAuditStatus(status);
			goodsMapper.updateByPrimaryKey(tbGoods);

			// 修改tbItems表
			TbItemExample example = new TbItemExample();
			TbItemExample.Criteria criteria = example.createCriteria();
			criteria.andGoodsIdEqualTo(id);
			List<TbItem> tbItems = itemMapper.selectByExample(example);
			for(TbItem item : tbItems){
				item.setStatus(status);
				itemMapper.updateByPrimaryKey(item);
			}
		}

	}

	@Override
	public List<TbItem> findItemByGoodsIdsAndStatus(Long[] ids, String status) {
		TbItemExample example = new TbItemExample();
		TbItemExample.Criteria criteria = example.createCriteria();
		criteria.andGoodsIdIn(Arrays.asList(ids));
		criteria.andStatusEqualTo(status);
		return itemMapper.selectByExample(example);
	}
}
