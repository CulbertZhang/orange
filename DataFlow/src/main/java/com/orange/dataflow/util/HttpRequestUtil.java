package com.orange.dataflow.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;

/**
 * Http请求工具类
 * 
 * @author snowfigure
 * @since 2014-8-24 13:30:56
 * @version v1.0.1
 */
public class HttpRequestUtil {
	static boolean proxySet = false;
	static String proxyHost = "127.0.0.1";
	static int proxyPort = 8087;

	/**
	 * 编码
	 * 
	 * @param source
	 * @return
	 */
	public static String urlEncode(String source, String encode) {
		String result = source;
		try {
			result = java.net.URLEncoder.encode(source, encode);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return "0";
		}
		return result;
	}

	public static String urlEncodeGBK(String source) {
		String result = source;
		try {
			result = java.net.URLEncoder.encode(source, "GBK");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return "0";
		}
		return result;
	}

	/**
	 * 发起http请求获取返回结果
	 * 
	 * @param req_url
	 *            请求地址
	 * @return
	 */
	public static String httpRequest(String req_url) {
		StringBuffer buffer = new StringBuffer();
		try {
			URL url = new URL(req_url);
			HttpURLConnection httpUrlConn = (HttpURLConnection) url.openConnection();

			httpUrlConn.setDoOutput(false);
			httpUrlConn.setDoInput(true);
			httpUrlConn.setUseCaches(false);

			httpUrlConn.setRequestMethod("GET");
			httpUrlConn.connect();

			// 将返回的输入流转换成字符串
			InputStream inputStream = httpUrlConn.getInputStream();
			InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

			String str = null;
			while ((str = bufferedReader.readLine()) != null) {
				buffer.append(str);
			}
			bufferedReader.close();
			inputStreamReader.close();
			// 释放资源
			inputStream.close();
			inputStream = null;
			httpUrlConn.disconnect();

		} catch (Exception e) {
			System.out.println(e.getStackTrace());
		}
		return buffer.toString();
	}

	/**
	 * 发送http请求取得返回的输入流
	 * 
	 * @param requestUrl
	 *            请求地址
	 * @return InputStream
	 */
	public static InputStream httpRequestIO(String requestUrl) {
		InputStream inputStream = null;
		try {
			URL url = new URL(requestUrl);
			HttpURLConnection httpUrlConn = (HttpURLConnection) url.openConnection();
			httpUrlConn.setDoInput(true);
			httpUrlConn.setRequestMethod("GET");
			httpUrlConn.connect();
			// 获得返回的输入流
			inputStream = httpUrlConn.getInputStream();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return inputStream;
	}

	/**
	 * 向指定URL发送GET方法的请求
	 * 
	 * @param url
	 *            发送请求的URL
	 * @param param
	 *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
	 * @return URL 所代表远程资源的响应结果
	 */
	public static String sendGet(String url, String param) {
		String result = "";
		BufferedReader in = null;
		try {
			String urlNameString = url + "?" + param;
			URL realUrl = new URL(urlNameString);
			// 打开和URL之间的连接
			URLConnection connection = realUrl.openConnection();
			// 设置通用的请求属性
			connection.setRequestProperty("accept", "*/*");
			connection.setRequestProperty("connection", "Keep-Alive");
			connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			// 建立实际的连接
			connection.connect();
			// 获取所有响应头字段
			Map<String, List<String>> map = connection.getHeaderFields();
			// 遍历所有的响应头字段
			for (String key : map.keySet()) {
				System.out.println(key + "--->" + map.get(key));
			}
			// 定义 BufferedReader输入流来读取URL的响应
			in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}
		} catch (Exception e) {
			System.out.println("发送GET请求出现异常！" + e);
			e.printStackTrace();
		}
		// 使用finally块来关闭输入流
		finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return result;
	}

	/**
	 * 向指定 URL 发送POST方法的请求
	 * 
	 * @param url
	 *            发送请求的 URL
	 * @param param
	 *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
	 * @param isproxy
	 *            是否使用代理模式
	 * @return 所代表远程资源的响应结果
	 */
	public static String sendPost(String url, String param, boolean isproxy) {
		OutputStreamWriter out = null;
		BufferedReader in = null;
		String result = "";
		try {
			URL realUrl = new URL(url);
			HttpURLConnection conn = null;
			if (isproxy) {// 使用代理模式
				@SuppressWarnings("static-access")
				Proxy proxy = new Proxy(Proxy.Type.DIRECT.HTTP, new InetSocketAddress(proxyHost, proxyPort));
				conn = (HttpURLConnection) realUrl.openConnection(proxy);
			} else {
				conn = (HttpURLConnection) realUrl.openConnection();
			}
			// 打开和URL之间的连接

			// 发送POST请求必须设置如下两行
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setRequestMethod("POST"); // POST方法

			// 设置通用的请求属性

			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			 conn.setRequestProperty("contentType", "UTF-8");  
			conn.connect();

			// 获取URLConnection对象对应的输出流
			out = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
			
			// 发送请求参数
			out.write(param);
			// flush输出流的缓冲
			out.flush();
			// 定义BufferedReader输入流来读取URL的响应
			in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}
		} catch (Exception e) {
			System.out.println("发送 POST 请求出现异常！" + e);
			e.printStackTrace();
		}
		// 使用finally块来关闭输出流、输入流
		finally {
			try {
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return result;
	}

	public static String sendPost(String url, String param) {
		String urlPath = new String(url);
		StringBuffer sb2 = new StringBuffer();
		try {
			// 建立连接
			URL urls = new URL(url);
			HttpURLConnection httpConn = (HttpURLConnection) urls.openConnection();
			// 设置参数
			httpConn.setDoOutput(true); // 需要输出
			httpConn.setDoInput(true); // 需要输入
			httpConn.setUseCaches(false); // 不允许缓存
			httpConn.setRequestMethod("POST"); // 设置POST方式连接
			// 设置请求属性
			httpConn.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
			httpConn.setRequestProperty("Connection", "Keep-Alive");// 维持长连接
			httpConn.setRequestProperty("Charset", "UTF-8");
			
			// 连接,也可以不用明文connect，使用下面的httpConn.getOutputStream()会自动connect
			httpConn.connect();
			// 建立输入流，向指向的URL传入参数
			DataOutputStream dos = new DataOutputStream(httpConn.getOutputStream());
			System.out.println("--param--"+param.trim());
			dos.writeBytes(param);
			dos.flush();
			dos.close();
			// 获得响应状态
			int resultCode = httpConn.getResponseCode();
			
			if (HttpURLConnection.HTTP_OK == resultCode) {
				String readLine = new String();
				BufferedReader responseReader = new BufferedReader(
						new InputStreamReader(httpConn.getInputStream(), "UTF-8"));
				while ((readLine = responseReader.readLine()) != null) {
					sb2.append(readLine).append("\n");
				}
				responseReader.close();
				System.out.println(sb2.toString());
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sb2.toString();
	}
	
	/**
     * 解读 inputsream 字节流
     * @param args
     */
    public static byte[] readInputStream(InputStream is) throws Exception {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while ((len = is.read(buffer)) != -1) {
            outStream.write(buffer, 0, len);
        }
        byte[] data = outStream.toByteArray();//网页的二进制数据        
        outStream.close();
        is.close();
        return data;
    }
	public static String getSign(String params) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		String key = "88172d1116e0440e964af71ddc79797c";
		String para = "account=jinankunshi&cmpackage=YD10&mobile=13573151855,13864196813";
		// para= para.toLowerCase()+"&key="+key;
		para = para + "&key=" + key;
		System.out.println("加密体-----" + para);

		return MD5Util.getMD5ByX32(para);
	}

	public static void main(String[] args) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		/*
		 * demo:非代理访问 应用ID：jinankunshi 应用KEY：88172d1116e0440e964af71ddc79797c
		 * YD100
		 * 
		 * post请求 请求url：http://api.ucpaas.com/flux/api.aspx，
		 * post包体：V=1.1&Action=flowRecharge&Account=account&Mobile=13510869250&
		 * package=YD10&Sign=da5b38d05fbf7775242f76b9a251080b
		 * 响应：{"TaskID":"146978488584981","Message":"充值提交成功","Code":"0"}
		 */

		String url = "http://api.ucpaas.com/flux/api.aspx";
		String para = "Action=chargeBat&V=1.1&Account=jinankunshi&Range=0&OutTradeNo=201707211817081112&Mobile=13573151855,13864196813&CMPackage=YD10&CUPackage=&CTPackage=";
		String sign = getSign(para);
		
		System.out.println("签名字符串-----" + sign);
		para = para + "&Sign=" + sign;
		System.out.println("请求体-----" + para);

		String sr = HttpRequestUtil.sendPost(url, para);
		System.out.println("sr体-----" + sr);
		/*String key = "88172d1116e0440e964af71ddc79797c";
		//String para = "account=jinankunshi&mobile=13605310990&package=YD10";
		
		//String key = "88172d1116e0440e964af71ddc00000c";
		
		String para = "account=jinankunshi&outtradeno=201707211517080001";
		para = para + "&key=" + key;
		System.out.println("加密体-----" + para);

		System.out.println( MD5Util.getMD5ByX32(para));*/
	}

}
