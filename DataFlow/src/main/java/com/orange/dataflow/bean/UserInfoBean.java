package com.orange.dataflow.bean;

import java.io.Serializable;

/**
 * 用户配置表
 * 
 * @author wuhao
 *
 */
public class UserInfoBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	private String account;
	private String cipherCode;
	private String returnUrl;
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	
	public String getCipherCode() {
		return cipherCode;
	}
	public void setCipherCode(String cipherCode) {
		this.cipherCode = cipherCode;
	}
	public String getReturnUrl() {
		return returnUrl;
	}
	public void setReturnUrl(String returnUrl) {
		this.returnUrl = returnUrl;
	}
	
}
