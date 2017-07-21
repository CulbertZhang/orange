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
 * Http���󹤾���
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
	 * ����
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
	 * ����http�����ȡ���ؽ��
	 * 
	 * @param req_url
	 *            �����ַ
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

			// �����ص�������ת�����ַ���
			InputStream inputStream = httpUrlConn.getInputStream();
			InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

			String str = null;
			while ((str = bufferedReader.readLine()) != null) {
				buffer.append(str);
			}
			bufferedReader.close();
			inputStreamReader.close();
			// �ͷ���Դ
			inputStream.close();
			inputStream = null;
			httpUrlConn.disconnect();

		} catch (Exception e) {
			System.out.println(e.getStackTrace());
		}
		return buffer.toString();
	}

	/**
	 * ����http����ȡ�÷��ص�������
	 * 
	 * @param requestUrl
	 *            �����ַ
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
			// ��÷��ص�������
			inputStream = httpUrlConn.getInputStream();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return inputStream;
	}

	/**
	 * ��ָ��URL����GET����������
	 * 
	 * @param url
	 *            ���������URL
	 * @param param
	 *            ����������������Ӧ���� name1=value1&name2=value2 ����ʽ��
	 * @return URL ������Զ����Դ����Ӧ���
	 */
	public static String sendGet(String url, String param) {
		String result = "";
		BufferedReader in = null;
		try {
			String urlNameString = url + "?" + param;
			URL realUrl = new URL(urlNameString);
			// �򿪺�URL֮�������
			URLConnection connection = realUrl.openConnection();
			// ����ͨ�õ���������
			connection.setRequestProperty("accept", "*/*");
			connection.setRequestProperty("connection", "Keep-Alive");
			connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			// ����ʵ�ʵ�����
			connection.connect();
			// ��ȡ������Ӧͷ�ֶ�
			Map<String, List<String>> map = connection.getHeaderFields();
			// �������е���Ӧͷ�ֶ�
			for (String key : map.keySet()) {
				System.out.println(key + "--->" + map.get(key));
			}
			// ���� BufferedReader����������ȡURL����Ӧ
			in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}
		} catch (Exception e) {
			System.out.println("����GET��������쳣��" + e);
			e.printStackTrace();
		}
		// ʹ��finally�����ر�������
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
	 * ��ָ�� URL ����POST����������
	 * 
	 * @param url
	 *            ��������� URL
	 * @param param
	 *            ����������������Ӧ���� name1=value1&name2=value2 ����ʽ��
	 * @param isproxy
	 *            �Ƿ�ʹ�ô���ģʽ
	 * @return ������Զ����Դ����Ӧ���
	 */
	public static String sendPost(String url, String param, boolean isproxy) {
		OutputStreamWriter out = null;
		BufferedReader in = null;
		String result = "";
		try {
			URL realUrl = new URL(url);
			HttpURLConnection conn = null;
			if (isproxy) {// ʹ�ô���ģʽ
				@SuppressWarnings("static-access")
				Proxy proxy = new Proxy(Proxy.Type.DIRECT.HTTP, new InetSocketAddress(proxyHost, proxyPort));
				conn = (HttpURLConnection) realUrl.openConnection(proxy);
			} else {
				conn = (HttpURLConnection) realUrl.openConnection();
			}
			// �򿪺�URL֮�������

			// ����POST�������������������
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setRequestMethod("POST"); // POST����

			// ����ͨ�õ���������

			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			 conn.setRequestProperty("contentType", "UTF-8");  
			conn.connect();

			// ��ȡURLConnection�����Ӧ�������
			out = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
			
			// �����������
			out.write(param);
			// flush������Ļ���
			out.flush();
			// ����BufferedReader����������ȡURL����Ӧ
			in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}
		} catch (Exception e) {
			System.out.println("���� POST ��������쳣��" + e);
			e.printStackTrace();
		}
		// ʹ��finally�����ر��������������
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
			// ��������
			URL urls = new URL(url);
			HttpURLConnection httpConn = (HttpURLConnection) urls.openConnection();
			// ���ò���
			httpConn.setDoOutput(true); // ��Ҫ���
			httpConn.setDoInput(true); // ��Ҫ����
			httpConn.setUseCaches(false); // ��������
			httpConn.setRequestMethod("POST"); // ����POST��ʽ����
			// ������������
			httpConn.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
			httpConn.setRequestProperty("Connection", "Keep-Alive");// ά�ֳ�����
			httpConn.setRequestProperty("Charset", "UTF-8");
			
			// ����,Ҳ���Բ�������connect��ʹ�������httpConn.getOutputStream()���Զ�connect
			httpConn.connect();
			// ��������������ָ���URL�������
			DataOutputStream dos = new DataOutputStream(httpConn.getOutputStream());
			System.out.println("--param--"+param.trim());
			dos.writeBytes(param);
			dos.flush();
			dos.close();
			// �����Ӧ״̬
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
     * ��� inputsream �ֽ���
     * @param args
     */
    public static byte[] readInputStream(InputStream is) throws Exception {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while ((len = is.read(buffer)) != -1) {
            outStream.write(buffer, 0, len);
        }
        byte[] data = outStream.toByteArray();//��ҳ�Ķ���������        
        outStream.close();
        is.close();
        return data;
    }
	public static String getSign(String params) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		String key = "88172d1116e0440e964af71ddc79797c";
		String para = "account=jinankunshi&cmpackage=YD10&mobile=13573151855,13864196813";
		// para= para.toLowerCase()+"&key="+key;
		para = para + "&key=" + key;
		System.out.println("������-----" + para);

		return MD5Util.getMD5ByX32(para);
	}

	public static void main(String[] args) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		/*
		 * demo:�Ǵ������ Ӧ��ID��jinankunshi Ӧ��KEY��88172d1116e0440e964af71ddc79797c
		 * YD100
		 * 
		 * post���� ����url��http://api.ucpaas.com/flux/api.aspx��
		 * post���壺V=1.1&Action=flowRecharge&Account=account&Mobile=13510869250&
		 * package=YD10&Sign=da5b38d05fbf7775242f76b9a251080b
		 * ��Ӧ��{"TaskID":"146978488584981","Message":"��ֵ�ύ�ɹ�","Code":"0"}
		 */

		String url = "http://api.ucpaas.com/flux/api.aspx";
		String para = "Action=chargeBat&V=1.1&Account=jinankunshi&Range=0&OutTradeNo=201707211817081112&Mobile=13573151855,13864196813&CMPackage=YD10&CUPackage=&CTPackage=";
		String sign = getSign(para);
		
		System.out.println("ǩ���ַ���-----" + sign);
		para = para + "&Sign=" + sign;
		System.out.println("������-----" + para);

		String sr = HttpRequestUtil.sendPost(url, para);
		System.out.println("sr��-----" + sr);
		/*String key = "88172d1116e0440e964af71ddc79797c";
		//String para = "account=jinankunshi&mobile=13605310990&package=YD10";
		
		//String key = "88172d1116e0440e964af71ddc00000c";
		
		String para = "account=jinankunshi&outtradeno=201707211517080001";
		para = para + "&key=" + key;
		System.out.println("������-----" + para);

		System.out.println( MD5Util.getMD5ByX32(para));*/
	}

}
