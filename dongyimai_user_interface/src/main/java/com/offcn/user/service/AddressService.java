package com.offcn.user.service;

import com.offcn.entity.PageResult;
import com.offcn.pojo.TbAddress;

import java.util.List;

/**
 * 服务层接口
 * @author Administrator
 *
 */
public interface AddressService {

	/**
	 * @Author dsy
	 * @Date 2020/9/19 09:26
	 * @Description 根据用户ID获取地址
	 * @Param
	 * @Return
	 * @Since version-1.0
	 */
	public List<TbAddress> findListByUserId(String userId);


	/**
	 * 返回全部列表
	 * @return
	 */
	public List<TbAddress> findAll();
	
	
	/**
	 * 返回分页列表
	 * @return
	 */
	public PageResult findPage(int pageNum, int pageSize);
	
	
	/**
	 * 增加
	*/
	public void add(TbAddress address);
	
	
	/**
	 * 修改
	 */
	public void update(TbAddress address);
	

	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	public TbAddress findOne(Long id);
	
	
	/**
	 * 批量删除
	 * @param ids
	 */
	public void delete(Long[] ids);

	/**
	 * 分页
	 * @param pageNum 当前页 码
	 * @param pageSize 每页记录数
	 * @return
	 */
	public PageResult findPage(TbAddress address, int pageNum, int pageSize);
	
}
