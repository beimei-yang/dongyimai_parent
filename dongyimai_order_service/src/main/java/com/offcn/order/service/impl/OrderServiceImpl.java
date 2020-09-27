package com.offcn.order.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.offcn.common.utils.IdWorker;
import com.offcn.entity.PageResult;
import com.offcn.group.Cart;
import com.offcn.mapper.TbOrderItemMapper;
import com.offcn.mapper.TbOrderMapper;
import com.offcn.order.service.OrderService;
import com.offcn.pojo.TbOrder;
import com.offcn.pojo.TbOrderExample;
import com.offcn.pojo.TbOrderExample.Criteria;
import com.offcn.pojo.TbOrderItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class OrderServiceImpl implements OrderService {

	@Autowired
	private TbOrderMapper orderMapper;

	@Autowired
	private TbOrderItemMapper orderItemMapper;

	@Autowired
	private IdWorker idWorker;

	@Autowired
	private RedisTemplate redisTemplate;




	/**
	 * 增加
	 */
	@Override
	public void add(TbOrder order) {
		// 获取购物车中的数据
		List<Cart> cartList = (List<Cart>)redisTemplate.boundHashOps("cartList").get(order.getUserId());
		for(Cart cart : cartList){
			TbOrder tbOrder = new TbOrder();
			// 设置订单ID
			tbOrder.setOrderId(idWorker.nextId());
			// 用户名
			tbOrder.setUserId(order.getUserId());
			// 支付类型
			tbOrder.setPaymentType(order.getPaymentType());
			// 订单时间
			tbOrder.setCreateTime(new Date());
			tbOrder.setUpdateTime(new Date());
			// 订单状态
			tbOrder.setStatus("1"); // 未付款
			// 收件人信息
			tbOrder.setReceiver(order.getReceiver());
			tbOrder.setReceiverAreaName(order.getReceiverAreaName());
			tbOrder.setReceiverMobile(order.getReceiverMobile());
			// 卖家ID
			tbOrder.setSellerId(cart.getSellerId());

			double money = 0;  // 本Cart中的总费用

			// 订单明细（订单项）
			for(TbOrderItem item : cart.getOrderItemList()){
				item.setId(idWorker.nextId());
				item.setOrderId(tbOrder.getOrderId());
				// 卖家ID
				item.setSellerId(cart.getSellerId());
				money += item.getTotalFee().doubleValue();
				// 插入订单明细表
				orderItemMapper.insert(item);
			}

			// money 就是当前Cart的总费用
			tbOrder.setPayment(new BigDecimal(money));
			orderMapper.insert(tbOrder);


		}

		// 删除redis中的数据
		redisTemplate.boundHashOps("cartList").delete(order.getUserId());

	}


	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbOrder> findAll() {
		return orderMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbOrder> page=   (Page<TbOrder>) orderMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}



	
	/**
	 * 修改
	 */
	@Override
	public void update(TbOrder order){
		orderMapper.updateByPrimaryKey(order);
	}	
	
	/**
	 * 根据ID获取实体
	 * @return
	 */
	@Override
	public TbOrder findOne(Long orderId){
		return orderMapper.selectByPrimaryKey(orderId);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] orderIds) {
		for(Long orderId:orderIds){
			orderMapper.deleteByPrimaryKey(orderId);
		}		
	}
	
	
		@Override
	public PageResult findPage(TbOrder order, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbOrderExample example=new TbOrderExample();
		Criteria criteria = example.createCriteria();
		
		if(order!=null){			
						if(order.getPaymentType()!=null && order.getPaymentType().length()>0){
				criteria.andPaymentTypeLike("%"+order.getPaymentType()+"%");
			}			if(order.getPostFee()!=null && order.getPostFee().length()>0){
				criteria.andPostFeeLike("%"+order.getPostFee()+"%");
			}			if(order.getStatus()!=null && order.getStatus().length()>0){
				criteria.andStatusLike("%"+order.getStatus()+"%");
			}			if(order.getShippingName()!=null && order.getShippingName().length()>0){
				criteria.andShippingNameLike("%"+order.getShippingName()+"%");
			}			if(order.getShippingCode()!=null && order.getShippingCode().length()>0){
				criteria.andShippingCodeLike("%"+order.getShippingCode()+"%");
			}			if(order.getUserId()!=null && order.getUserId().length()>0){
				criteria.andUserIdLike("%"+order.getUserId()+"%");
			}			if(order.getBuyerMessage()!=null && order.getBuyerMessage().length()>0){
				criteria.andBuyerMessageLike("%"+order.getBuyerMessage()+"%");
			}			if(order.getBuyerNick()!=null && order.getBuyerNick().length()>0){
				criteria.andBuyerNickLike("%"+order.getBuyerNick()+"%");
			}			if(order.getBuyerRate()!=null && order.getBuyerRate().length()>0){
				criteria.andBuyerRateLike("%"+order.getBuyerRate()+"%");
			}			if(order.getReceiverAreaName()!=null && order.getReceiverAreaName().length()>0){
				criteria.andReceiverAreaNameLike("%"+order.getReceiverAreaName()+"%");
			}			if(order.getReceiverMobile()!=null && order.getReceiverMobile().length()>0){
				criteria.andReceiverMobileLike("%"+order.getReceiverMobile()+"%");
			}			if(order.getReceiverZipCode()!=null && order.getReceiverZipCode().length()>0){
				criteria.andReceiverZipCodeLike("%"+order.getReceiverZipCode()+"%");
			}			if(order.getReceiver()!=null && order.getReceiver().length()>0){
				criteria.andReceiverLike("%"+order.getReceiver()+"%");
			}			if(order.getInvoiceType()!=null && order.getInvoiceType().length()>0){
				criteria.andInvoiceTypeLike("%"+order.getInvoiceType()+"%");
			}			if(order.getSourceType()!=null && order.getSourceType().length()>0){
				criteria.andSourceTypeLike("%"+order.getSourceType()+"%");
			}			if(order.getSellerId()!=null && order.getSellerId().length()>0){
				criteria.andSellerIdLike("%"+order.getSellerId()+"%");
			}	
		}
		
		Page<TbOrder> page= (Page<TbOrder>)orderMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}
	
}
