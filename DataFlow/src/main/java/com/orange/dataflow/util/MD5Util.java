package com.orange.dataflow.util;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import sun.misc.BASE64Encoder;

public class MD5Util {

	// **����MD5���м���

	public static String EncoderByMd5(String str) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		// ȷ�����㷽��
		MessageDigest md5 = MessageDigest.getInstance("MD5");
		BASE64Encoder base64en = new BASE64Encoder();
		// ���ܺ���ַ���
		String newstr = base64en.encode(md5.digest(str.getBytes("utf-8")));

		return newstr;
	}

	public static String getMD5(String str) {
		try {
			// ����һ��MD5���ܼ���ժҪ
			MessageDigest md = MessageDigest.getInstance("MD5");
			// ����md5����
			md.update(str.getBytes());
			System.out.println(str);
			System.out.println(md.digest());
			// digest()���ȷ������md5 hashֵ������ֵΪ8Ϊ�ַ�������Ϊmd5 hashֵ��16λ��hexֵ��ʵ���Ͼ���8λ���ַ�
			// BigInteger������8λ���ַ���ת����16λhexֵ�����ַ�������ʾ���õ��ַ�����ʽ��hashֵ
			return new BigInteger(1, md.digest()).toString(16);
		} catch (Exception e) {
			e.printStackTrace();
			return str;
		}
	}

	public static String getMD5ByX32(String str) {
		try {
			// ����һ��MD5���ܼ���ժҪ
			MessageDigest md = MessageDigest.getInstance("MD5");
			// ����md5����
			md.update(str.getBytes());
			// digest()���ȷ������md5 hashֵ������ֵΪ8Ϊ�ַ�������Ϊmd5 hashֵ��16λ��hexֵ��ʵ���Ͼ���8λ���ַ�
			// BigInteger������8λ���ַ���ת����16λhexֵ�����ַ�������ʾ���õ��ַ�����ʽ��hashֵ
			String md5 = new BigInteger(1, md.digest()).toString(16);
			// BigInteger���0ʡ�Ե����貹ȫ��32λ
			return fillMD5(md5);
		} catch (Exception e) {
			throw new RuntimeException("MD5���ܴ���:" + e.getMessage(), e);
		}
	}

	public static String fillMD5(String md5) {
		return md5.length() == 32 ? md5 : fillMD5("0" + md5);
	}

}