package com.orange.dataflow.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.orange.dataflow.service.IDataFlowService;



/**
 * @功能描述：流量充值 
 * @创建人： wuhao 
 * @创建时间： 2017-7-20 上午9:44:56
 */
@Controller
public class DataFlowController {

	protected static final Logger logger = Logger.getLogger(DataFlowController.class);

	@Autowired
	private IDataFlowService dataFlowService;

	/**
	 * 跳转流量超市列表
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "rechargeFlow")
	public void rechargeFlow(HttpServletRequest request, HttpServletResponse response) throws Exception{
		
		 String action = request.getParameter("Action");//宽带信息编码
		 
		 if("flowRecharge".equals(action)){//单号码充值
			 
			 dataFlowService.flowRecharge(request,response);
		 }else if("chargeBat".equals(action)){//获取流量包定义
			 dataFlowService.chargeBat(request,response);
		 }else if("getPackage".equals(action)){//获取流量包定义
			 dataFlowService.getPackage(request,response);
		 }else if("getBalance".equals(action)){//余额查询
			 dataFlowService.getBalance(request,response);
		 }else if("getReports".equals(action)){//查询状态
			 dataFlowService.getReports(request,response);
		 }else if("getOrder".equals(action)){//订单查询
			 dataFlowService.getOrder(request,response);
		 }

	}
	
	/**
	 * 
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "getState.do")
	public void getState(HttpServletRequest request, HttpServletResponse response) throws Exception {

		String taskID = request.getParameter("TaskID");// 任务
		String mobile = request.getParameter("Mobile");// 手机号
		String status = request.getParameter("Status");// 状态
		String reportTime = request.getParameter("ReportTime");// 时间
		String reportCode = request.getParameter("ReportCode");// 代码说明
		String outTradeNo = request.getParameter("OutTradeNo");// 订单号

		Map<String, String> map = new HashMap<String, String>();
		map.put("taskID", taskID);
		map.put("mobile", mobile);
		map.put("status", status);
		map.put("reportTime", reportTime);
		map.put("reportCode", reportCode);
		map.put("outTradeNo", outTradeNo);

		try {
			boolean flag = dataFlowService.getMsg(map);

			if (flag) {

				response.setContentType("text/html; charset=utf-8");
				PrintWriter out = response.getWriter();

				out.write("ok");
				out.flush();
				out.close();

			} else {

				response.setContentType("text/html; charset=utf-8");
				PrintWriter out = response.getWriter();

				out.write("false");
				out.flush();
				out.close();

			}
		} catch (Exception e) {
			response.setContentType("text/html; charset=utf-8");
			PrintWriter out = response.getWriter();

			out.write("false");
			out.flush();
			out.close();
		}

	}
}
