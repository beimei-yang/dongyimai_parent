package com.offcn.sellergoods.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.offcn.entity.PageResult;
import com.offcn.entity.Result;
import com.offcn.group.Goods;
import com.offcn.pojo.TbGoods;
import com.offcn.pojo.TbItem;
import com.offcn.service.GoodsService;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import java.util.Arrays;
import java.util.List;

/**
 * controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/goods")
public class GoodsController {

	@Reference
	private GoodsService goodsService;

	@Autowired
	private ActiveMQQueue queue;

	@Autowired
	private ActiveMQQueue queueDelete;

	@Autowired
	private ActiveMQTopic topic;

	@Autowired
	private JmsTemplate jmsTemplate;

	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findAll")
	public List<TbGoods> findAll(){			
		return goodsService.findAll();
	}
	
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findPage")
	public PageResult  findPage(int page,int rows){			
		return goodsService.findPage(page, rows);
	}
	
	/**
	 * 增加
	 * @param goods
	 * @return
	 */
	@RequestMapping("/add")
	public Result add(@RequestBody Goods goods){
		// 获取当前用户名
		String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
		// 加入到当前要添加的商品中
		goods.getTbGoods().setSellerId(sellerId);
		try {
			goodsService.add(goods);
			return new Result(true, "增加成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "增加失败");
		}
	}
	
	/**
	 * 修改
	 * @param goods
	 * @return
	 */
	@RequestMapping("/update")
	public Result update(@RequestBody Goods goods){
		// 数据验证，判断登录的用户ID 和 数据库中的用户ID 是否相同
		// 获取当前登录用户ID
		String sellerIdLogin = SecurityContextHolder.getContext().getAuthentication().getName();
		// 获取数据库中的用户ID
		Goods one = goodsService.findOne(goods.getTbGoods().getId());
		String sellerIdDB = one.getTbGoods().getSellerId();
		if(!sellerIdDB.equals(sellerIdLogin)){
			return new Result(false,"非法操作，当前的用户IP已被记录");
		}
		try {
			goodsService.update(goods);
			return new Result(true, "修改成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "修改失败");
		}
	}	
	
	/**
	 * 获取实体
	 * @param id
	 * @return
	 */
	@RequestMapping("/findOne")
	public Goods findOne(Long id){
		return goodsService.findOne(id);		
	}
	
	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
	@RequestMapping("/delete")
	public Result delete(Long[] ids){
		try {
			goodsService.delete(ids);

			// solr中的删除
			//itemSearchService.deleteByGoodsIds(Arrays.asList(ids));
			jmsTemplate.send(queueDelete, new MessageCreator() {
				@Override
				public Message createMessage(Session session) throws JMSException {
					return session.createObjectMessage(ids);
				}
			});

			return new Result(true, "删除成功"); 
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "删除失败");
		}
	}
	
		/**
	 * 查询+分页
	 * @param page
	 * @param rows
	 * @return
	 */
	@RequestMapping("/search")
	public PageResult search(@RequestBody TbGoods goods, int page, int rows  ){
		return goodsService.findPage(goods, page, rows);		
	}

	@RequestMapping("/updateStatus")
	public Result updateStatus(Long[] ids,String status){
		try {
			goodsService.updateSetatus(ids,status);

			if(status.equals("1")){ //审核通过
				// 1 查询数据
				List<TbItem> itemList = goodsService.findItemByGoodsIdsAndStatus(ids, status);
				// 2 导入到solr
				//itemSearchService.importList(itemList);
				String jsonString = JSON.toJSONString(itemList);
				jmsTemplate.send(queue, new MessageCreator() {
					@Override
					public Message createMessage(Session session) throws JMSException {
						return session.createTextMessage(jsonString);
					}
				});

				// 3.生成页面
				for(Long id : ids){
					//itemPageService.createHTML(id);
					jmsTemplate.send(topic, new MessageCreator() {
						@Override
						public Message createMessage(Session session) throws JMSException {
							return session.createTextMessage(id + "");
						}
					});
				}
			}

			return new Result(true,"修改成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false,"修改失败");
		}


	}
	
}
