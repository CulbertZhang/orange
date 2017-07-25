package com.orange.dataflow.service;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface IDataFlowService {

	public void flowRecharge(HttpServletRequest request, HttpServletResponse response);
	
	public void chargeBat(HttpServletRequest request, HttpServletResponse response);
	
	public void getPackage(HttpServletRequest request, HttpServletResponse response);
	
	public void getBalance(HttpServletRequest request, HttpServletResponse response);
	
	public void getReports(HttpServletRequest request, HttpServletResponse response);
	
	public void getOrder(HttpServletRequest request, HttpServletResponse response);
	
	public void other(HttpServletRequest request, HttpServletResponse response);
	
	public boolean getMsg(Map<String,String> map);
}
