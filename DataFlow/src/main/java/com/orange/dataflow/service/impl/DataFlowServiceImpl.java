package com.orange.dataflow.service.impl;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.orange.dataflow.bean.OrderInfoBean;
import com.orange.dataflow.bean.UserInfoBean;
import com.orange.dataflow.dao.IDataFlowMapper;
import com.orange.dataflow.service.IDataFlowService;
import com.orange.dataflow.util.HttpRequestUtil;
import com.orange.dataflow.util.MD5Util;

import net.sf.json.JSONObject;


@Service("dataFlowService")
public class DataFlowServiceImpl implements IDataFlowService {

	private static final Logger logger = Logger.getLogger(DataFlowServiceImpl.class);
	private final String url = "http://api.ucpaas.com/flux/api.aspx";
	private final String myKey = "88172d1116e0440e964af71ddc79797c";
	@Autowired
	private IDataFlowMapper dataFlowMapper;

	/**
	 * 流量单冲
	 * 
	 * @author wuhao
	 *
	 */
	@Override
	public void flowRecharge(HttpServletRequest request, HttpServletResponse response) {

		Map<String, String> outMap = new HashMap<String, String>();

		String v = request.getParameter("V");// 版本号
		String range = request.getParameter("Range");// 流量类型
		String outTradeNo = request.getParameter("OutTradeNo");// 商户订单号
		String account = request.getParameter("Account");// 帐号 (签名)
		String mobile = request.getParameter("Mobile");// 号码 (签名)
		String pckage = request.getParameter("Package");// 套餐 (签名)
		String sign = request.getParameter("Sign");// 签名

		// 查询商户秘钥
		UserInfoBean user = dataFlowMapper.queryUserInfo(account);

		if (null != user) {

			// 拼接需要加密的参数串

			String enParam = "account=" + account + "&mobile=" + mobile + "&package=" + pckage;
			enParam = enParam + "&key=" + user.getCipherCode();
			String enSign = MD5Util.getMD5ByX32(enParam);

			OrderInfoBean orderInfoBean = new OrderInfoBean();
			orderInfoBean.setOrderId(outTradeNo);
			orderInfoBean.setMobile(mobile);
			orderInfoBean.setDateType(Integer.parseInt(range));
			orderInfoBean.setPackageId(pckage);
			orderInfoBean.setAccount(account);
			orderInfoBean.setState(0);
			orderInfoBean.setOrderTime(new Date());
			// 下单数据存储
			dataFlowMapper.insertOrderInfo(orderInfoBean);
			if (enSign.equals(sign)) {
				// 验签成功
				logger.info("---flowRecharge验签成功---账户："+account);

				String myParam = "account=jinankunshi&mobile=" + mobile + "&package=" + pckage;
				myParam = myParam + "&key=" + myKey;
				String mySign = MD5Util.getMD5ByX32(myParam);

				String para = "V=" + v + "&Action=flowRecharge&Range=" + range + "&OutTradeNo=" + outTradeNo
						+ "&Account=jinankunshi&Mobile=" + mobile + "&Package=" + pckage;

				para = para + "&Sign=" + mySign;

				String resultMsg = HttpRequestUtil.sendPost(url, para);
				logger.info("---flowRecharge--resultMsg---" + resultMsg);
				JSONObject returnJsonObj = JSONObject.fromObject(resultMsg);
				if ("0".equals(returnJsonObj.get("Code"))) {

					orderInfoBean.setTaskId(returnJsonObj.get("TaskID").toString());
					orderInfoBean.setState(1);
					orderInfoBean.setErrorCode(returnJsonObj.get("Code").toString());
					dataFlowMapper.upOrderInfoById(orderInfoBean);
					outMap.put("TaskID", returnJsonObj.get("TaskID").toString());
				}
				outMap.put("Code", returnJsonObj.get("Code").toString());
				outMap.put("Message", returnJsonObj.get("Message").toString());

			} else {
				// 验签失败
				outMap.put("TaskID", "");
				outMap.put("Code", "100");
				outMap.put("Message", "签名错误");
			}
			orderInfoBean.setTaskId(outMap.get("TaskID"));
			orderInfoBean.setErrorCode(outMap.get("Code"));
			dataFlowMapper.upOrderInfoById(orderInfoBean);
			logger.info("---upOrderInfo更新成功---");
		} else {
			outMap.put("Code", "013");
			outMap.put("Message", "账号已经失效");
		}
		try {

			JSONObject jsonObject = JSONObject.fromObject(outMap);
			response.setContentType("text/html; charset=utf-8");
			PrintWriter out = response.getWriter();

			out.write(jsonObject.toString());
			out.flush();
			out.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * 流量批量冲
	 * 
	 * @author wuhao
	 *
	 */
	@Override
	public void chargeBat(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> outMap = new HashMap<String, Object>();

		String v = request.getParameter("V");// 版本号
		String range = request.getParameter("Range");// 流量类型
		String outTradeNo = request.getParameter("OutTradeNo");// 商户订单号
		String account = request.getParameter("Account");// 帐号 (签名)
		String mobile = request.getParameter("Mobile");// 号码 (签名)
		String cmpckage = request.getParameter("CMPackage");// 移动套餐 (签名)
		String cupckage = request.getParameter("CUPackage");// 联通套餐 (签名)
		String ctpckage = request.getParameter("CTPackage");// 电信套餐 (签名)
		String sign = request.getParameter("Sign");// 签名
		String pckage = "";
		// 查询商户秘钥
		UserInfoBean user = dataFlowMapper.queryUserInfo(account);
		if (null != user) {

			// 获取流量包类型
			if (!"".equals(cmpckage) || null != cmpckage) {
				pckage = cmpckage;
			} else if (!"".equals(cupckage) || null != cupckage) {
				pckage = cupckage;
			} else if (!"".equals(ctpckage) || null != ctpckage) {
				pckage = ctpckage;
			}

			// 拼接需要加密的参数串

			String enParam = "account=" + account + "&cmpackage=" + cmpckage + "&ctpackage=" + ctpckage + "&cupackage="
					+ cupckage;
			enParam = enParam + "&mobile=" + mobile;
			enParam = enParam + "&key=" + user.getCipherCode();
			logger.info("-enParam--" + enParam);
			String enSign = MD5Util.getMD5ByX32(enParam);
			logger.info("-enSign--" + enSign);
			String[] mobiles = mobile.split(",");

			for (int i = 0; i < mobiles.length; i++) {

				OrderInfoBean orderInfoBean = new OrderInfoBean();
				orderInfoBean.setOrderId(outTradeNo);
				orderInfoBean.setMobile(mobiles[i]);
				orderInfoBean.setDateType(Integer.parseInt(range));
				orderInfoBean.setPackageId(pckage);
				orderInfoBean.setAccount(account);
				orderInfoBean.setState(0);
				orderInfoBean.setOrderTime(new Date());
				// 下单数据存储
				dataFlowMapper.insertOrderInfo(orderInfoBean);

			}

			if (enSign.equals(sign)) {
				// 验签成功
				logger.info("---验签成功---");

				String myParam = "account=jinankunshi&cmpackage=" + cmpckage + "&ctpackage=" + ctpckage + "&cupackage="
						+ cupckage;
				myParam = myParam + "&mobile=" + mobile;

				myParam = myParam + "&key=" + myKey;
				logger.info("--beMyParam--" + myParam);
				String mySign = MD5Util.getMD5ByX32(myParam);

				String para = "V=" + v + "&Action=chargeBat&Range=" + range + "&OutTradeNo=" + outTradeNo
						+ "&Account=jinankunshi&CMPackage=" + cmpckage + "&CUPackage=" + cupckage + "&CTPackage="
						+ ctpckage;
				para = para + "&Mobile=" + mobile;
				para = para + "&Sign=" + mySign;

				String resultMsg = HttpRequestUtil.sendPost(url, para);
				logger.info("--批量充值resultMsg--" + resultMsg);
				JSONObject returnJsonObj = JSONObject.fromObject(resultMsg);

				outMap.put("TaskID", returnJsonObj.get("TaskID"));
				outMap.put("Code", returnJsonObj.get("Code"));
				outMap.put("Message", returnJsonObj.get("Message"));

			} else {
				// 验签失败
				outMap.put("TaskID", "");
				outMap.put("Code", "100");
				outMap.put("Message", "签名错误");
			}

			// 多数据源查询订单信息,逐条存储返回信息
			OrderInfoBean orderInfoBean = new OrderInfoBean();
			orderInfoBean.setOrderId(outTradeNo);
			List<OrderInfoBean> orderList = dataFlowMapper.queryOrderListById(orderInfoBean);

			String code = outMap.get("Code").toString();
			String[] taskId = null;
			if ("0".equals(code)) {
				taskId = mobile.split(",");
			}

			for (int i = 0; i < orderList.size(); i++) {

				if ("0".equals(code)) {
					orderList.get(i).setTaskId(taskId[i]);
				}
				orderList.get(i).setErrorCode(code);
				dataFlowMapper.upOrderInfoById(orderList.get(i));
			}
		} else {
			outMap.put("Code", "013");
			outMap.put("Message", "账号已经失效");
		}

		try {

			JSONObject jsonObject = JSONObject.fromObject(outMap);
			response.setContentType("text/html; charset=utf-8");
			PrintWriter out = response.getWriter();
			out.write(jsonObject.toString());
			out.flush();
			out.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * 获取充值包
	 * 
	 * @author wuhao
	 *
	 */
	@Override
	public void getPackage(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> outMap = new HashMap<String, Object>();

		String v = request.getParameter("V");// 版本号
		String account = request.getParameter("Account");// 帐号 (签名)
		String type = request.getParameter("Type");// 类型(签名)
		String sign = request.getParameter("Sign");// 签名
		//查询商户秘钥
		UserInfoBean user=dataFlowMapper.queryUserInfo(account);
		
		if(null!=user){

			
			// 拼接需要加密的参数串
			String enParam="account="+account+"&type="+type;
			enParam = enParam + "&key=" + user.getCipherCode();
			String enSign=MD5Util.getMD5ByX32(enParam);
			
			if(enSign.equals(sign)){
				//验签成功
				logger.info("---验签成功---account："+account);
				
				String myParam="account=jinankunshi&type="+type;
				myParam = myParam + "&key=" + myKey;
				String mySign=MD5Util.getMD5ByX32(myParam);
				
				String para = "V="+v+"&Action=getPackage&Account=jinankunshi&Type="+type;
				
				para = para + "&Sign=" + mySign;
				
				String resultMsg = HttpRequestUtil.sendPost(url, para);
				logger.info("---获取充值包---"+resultMsg);
				
				JSONObject returnJsonObj = JSONObject.fromObject(resultMsg);
				
				//成功返回List
				if("0".equals(returnJsonObj.get("Code"))){
					List<Map<String, Object>> list=(List) returnJsonObj.get("Packages");
					
					outMap.put("Packages", list);
				}
				
				outMap.put("Code", returnJsonObj.get("Code"));
				outMap.put("Message", returnJsonObj.get("Message"));
				
				
			}else{
				//验签失败
				
				outMap.put("Code", "100");
				outMap.put("Message", "签名错误");
			}
		}else{
			outMap.put("Code", "013");
			outMap.put("Message", "账号已经失效");
		}
		
		
		try {
			
			JSONObject jsonObject = JSONObject.fromObject(outMap);
		    response.setContentType("text/html; charset=utf-8"); 
			PrintWriter out = response.getWriter();
			out.write(jsonObject.toString());
			out.flush();
			out.close();
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	/**
	 * 查询余额
	 * 
	 * @author wuhao
	 *
	 */
	@Override
	public void getBalance(HttpServletRequest request, HttpServletResponse response) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * 查询状态
	 * 
	 * @author wuhao
	 *
	 */
	@Override
	public void getReports(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> outMap = new HashMap<String, Object>();

		String v = request.getParameter("V");// 版本号
		String account = request.getParameter("Account");// 帐号 (签名)
		String count = request.getParameter("Count");// 一次取数量(签名)
		String sign = request.getParameter("Sign");// 签名
		//查询商户秘钥
		UserInfoBean user=dataFlowMapper.queryUserInfo(account);
		
		// 拼接需要加密的参数串
		String enParam="account="+account+"&count="+count;
		enParam = enParam + "&key=" + user.getCipherCode();
		String enSign=MD5Util.getMD5ByX32(enParam);
		
		if(enSign.equals(sign)){
			//验签成功
			logger.info("---验签成功---account："+account);
			
			String myParam="account=jinankunshi&count="+count;
			myParam = myParam + "&key=" + myKey;
			String mySign=MD5Util.getMD5ByX32(myParam);
			
			String para = "V="+v+"&Action=getReports&Account=jinankunshi&Count="+count;
			
			para = para + "&Sign=" + mySign;
			
			String resultMsg=HttpRequestUtil.sendPost(url, para);
			logger.info("---resultMsg---"+resultMsg);
			JSONObject returnJsonObj = JSONObject.fromObject(resultMsg);
			
			//成功返回List
			if("0".equals(returnJsonObj.get("Code"))){
				List<Map<String, Object>> list=(List) returnJsonObj.get("Reports");
				
				outMap.put("Reports", list);
			}
			
			outMap.put("Code", returnJsonObj.get("Code"));
			outMap.put("Message", returnJsonObj.get("Message"));
			
			
		}else{
			//验签失败
			
			outMap.put("Code", "100");
			outMap.put("Message", "签名错误");
		}
		
		try {
			
			JSONObject jsonObject = JSONObject.fromObject(outMap);
			response.setContentType("text/html; charset=utf-8"); 
			PrintWriter out = response.getWriter();
			
			out.write(jsonObject.toString());
			out.flush();
			out.close();
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}

	/**
	 * 查询充值记录
	 * 
	 * @author wuhao
	 *
	 */
	@Override
	public void getOrder(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> outMap = new HashMap<String, Object>();

		String v = request.getParameter("V");// 版本号
		String account = request.getParameter("Account");// 帐号 (签名)
		String outTradeNo = request.getParameter("OutTradeNo");// 外部订单号(签名)
		String sign = request.getParameter("Sign");// 签名
		// 查询商户秘钥
		UserInfoBean user = dataFlowMapper.queryUserInfo(account);
		if (null != user) {
			// 拼接需要加密的参数串
			String enParam = "account=" + account + "&outtradeno=" + outTradeNo;
			enParam = enParam + "&key=" + user.getCipherCode();
			String enSign = MD5Util.getMD5ByX32(enParam);

			if (enSign.equals(sign)) {
				// 验签成功
				logger.info("---验签成功---");

				String myParam = "account=jinankunshi&outtradeno=" + outTradeNo;
				myParam = myParam + "&key=" + myKey;
				String mySign = MD5Util.getMD5ByX32(myParam);

				String para = "V=" + v + "&Action=getOrder&Account=jinankunshi&OutTradeNo=" + outTradeNo;

				para = para + "&Sign=" + mySign;

				String resultMsg = HttpRequestUtil.sendPost(url, para);

				JSONObject returnJsonObj = JSONObject.fromObject(resultMsg);

				// 成功返回List
				if ("0".equals(returnJsonObj.get("Code"))) {
					Map<String, Object> map = (Map<String, Object>) returnJsonObj.get("Data");

					outMap.put("Data", map);
				}

				outMap.put("Code", returnJsonObj.get("Code"));
				outMap.put("Message", returnJsonObj.get("Message"));

			} else {
				// 验签失败

				outMap.put("Code", "100");
				outMap.put("Message", "签名错误");
			}
		} else {
			outMap.put("Code", "013");
			outMap.put("Message", "账号已经失效");
		}
		try {

			JSONObject jsonObject = JSONObject.fromObject(outMap);
			response.setContentType("text/html; charset=utf-8");
			PrintWriter out = response.getWriter();

			out.write(jsonObject.toString());
			out.flush();
			out.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	public boolean getMsg(Map<String, String> map) {
		
		try {
			OrderInfoBean orderInfoBean=new OrderInfoBean();
			orderInfoBean.setTaskId(URLDecoder.decode(map.get("taskID")));
			orderInfoBean.setUdpateTime(new Date());
			orderInfoBean.setState(Integer.parseInt(map.get("status")));
			dataFlowMapper.upOrderInfoByTaskId(orderInfoBean);
		} catch (Exception e) {
			return false;
		}
		
		//发送下游状态
		String account=dataFlowMapper.queryAccountByTaskId(map.get("taskID"));
		String returnUrl=dataFlowMapper.queryUrlByAccount(account);
		JSONObject returnJsonObj = JSONObject.fromObject(map);
		String resultMsg=HttpRequestUtil.sendPost(returnUrl, returnJsonObj.toString());
		logger.info("---回调resultMsg---"+resultMsg);
		//下游反馈状态存储
		if(resultMsg.contains("ok")){
			logger.info("---下游反馈---ok");
			OrderInfoBean orderInfoBean=new OrderInfoBean();
			orderInfoBean.setTaskId(URLDecoder.decode(map.get("taskID")));
			orderInfoBean.setState(2);
			dataFlowMapper.upOrderInfoByTaskId(orderInfoBean);
			return true;
		}else{
			logger.info("---下游反馈---no");
			OrderInfoBean orderInfoBean=new OrderInfoBean();
			orderInfoBean.setTaskId(URLDecoder.decode(map.get("taskID")));
			orderInfoBean.setState(3);
			dataFlowMapper.upOrderInfoByTaskId(orderInfoBean);
			
			return false;
		}
		
	}

	@Override
	public void other(HttpServletRequest request, HttpServletResponse response) {
		try {
			Map<String, Object> outMap = new HashMap<String, Object>();
			outMap.put("Code", "001");
			outMap.put("Message", "参数错误");
			JSONObject jsonObject = JSONObject.fromObject(outMap);
			response.setContentType("text/html; charset=utf-8");
			PrintWriter out = response.getWriter();

			out.write(jsonObject.toString());
			out.flush();
			out.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

		
	}

	
	
}
