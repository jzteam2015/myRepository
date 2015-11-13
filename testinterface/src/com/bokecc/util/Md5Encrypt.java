package com.bokecc.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 功能：md5加密处理类 版本：1.0 日期：2010-12-21 作者：feng
 **/
public class Md5Encrypt {

	/**
	 * 功能：计算字符串的md5值
	 * 
	 * @param src
	 * @return
	 */
	public static String md5(String src) {
		return digest(src, "MD5");
	}

	/**
	 * 功能：根据指定的散列算法名，得到字符串的散列结果。
	 * 
	 * @param src
	 * @param name
	 * @return
	 */
	private static String digest(String src, String name) {
		try {
			MessageDigest alg = MessageDigest.getInstance(name);
			byte[] result = alg.digest(src.getBytes());
			return BinaryConvert.byte2hex(result);
		} catch (NoSuchAlgorithmException ex) {
			return null;
		}
	}

	public static String getFileMD5(File file) {
		if (!file.isFile()) {
			return null;
		}
		MessageDigest digest = null;
		FileInputStream in = null;
		byte[] buffer = new byte[1024];
		int len = 0;
		try {
			digest = MessageDigest.getInstance("MD5");
			in = new FileInputStream(file);
			while ((len = in.read(buffer, 0, 1024)) != -1) {
				digest.update(buffer, 0, len);
			}
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		BigInteger bigInt = new BigInteger(1,digest.digest());
		return bigInt.toString(16);

	}
}
