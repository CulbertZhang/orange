package com.orange.dataflow.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.orange.dataflow.bean.OrderInfoBean;
import com.orange.dataflow.bean.UserInfoBean;

@Repository
public interface IDataFlowMapper {

	/**
	 * 订单表数据插入
	 * @param bean
	 */
	public void insertOrderInfo(@Param("orderInfoBean")OrderInfoBean orderInfoBean);
	
	/**
	 * 查询订单
	 * @param bean
	 */
	public List<OrderInfoBean> queryOrderInfo(@Param("orderInfoBean")OrderInfoBean orderInfoBean);
	
	/**
	 * 根据taskId更新订单表
	 * @param bean
	 */
	public void upOrderInfoByTaskId(@Param("orderInfoBean")OrderInfoBean orderInfoBean);
	
	/**
	 * 根据Id更新订单表
	 * @param bean
	 */
	public void upOrderInfoById(@Param("orderInfoBean")OrderInfoBean orderInfoBean);
	/**
	 * 更新订单表
	 * @param bean
	 */
	public void upOrderInfo(@Param("orderInfoBean")OrderInfoBean orderInfoBean);
	
	/**
	 * 根据orderId查询订单信息
	 * @param bean
	 * @return
	 */
	public List<OrderInfoBean> queryOrderListById(@Param("orderInfoBean")OrderInfoBean orderInfoBean);
	
	/**
	 * 根据商户名称查询商户信息
	 * @param bean
	 * @return
	 */
	public  UserInfoBean queryUserInfo(@Param("account")String account);
	
	/**
	 * 根据taskId查询account
	 * @param bean
	 * @return
	 */
	public  String queryAccountByTaskId(@Param("taskId")String taskId);
	
	/**
	 * 根据商户名称查询商户信息
	 * @param bean
	 * @return
	 */
	public  String queryUrlByAccount(@Param("account")String account);
}
