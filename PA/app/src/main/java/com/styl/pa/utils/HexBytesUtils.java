package com.styl.pa.utils;

import android.util.Log;

import androidx.annotation.VisibleForTesting;

public class HexBytesUtils {

	@VisibleForTesting
	HexBytesUtils() {
		throw new IllegalStateException("Utility class");
	}

	private static final String QPP_HEX_STR = "0123456789ABCDEF";

	/**
	 * Hex char convert to byte array
	 * 
	 * @version 1.0
	 * @createTime 2014-3-21,PM2:16:38
	 * @updateTime 2014-3-21,PM2:16:38
	 * @createAuthor Qn Sw team
	 * @updateAuthor Qn Sw team
	 * @updateInfo
	 * 
	 * @param hexString
	 * @return
	 */
	public static byte[] hexStr2Bytes(String hexString) {
		byte[] dataArr = new byte[0];

		if (hexString == null || hexString.isEmpty()) {
			return dataArr;
		}

		hexString = hexString.toUpperCase();

		int length = hexString.length() >> 1;
		char[] hexChars = hexString.toCharArray();

		int i = 0;
		Log.i("QnDbg", "hexString.length() : " + hexString.length());

		do {
			int checkChar = QPP_HEX_STR.indexOf(hexChars[i]);

			if (checkChar == -1)
				return dataArr;
			i++;
		} while (i < hexString.length());

		dataArr = new byte[length];

		for (i = 0; i < length; i++) {
			int strPos = i * 2;

			dataArr[i] = (byte) (charToByte(hexChars[strPos]) << 4 | charToByte(hexChars[strPos + 1]) & 0xff);
		}

		return dataArr;
	}

	public static byte[] hexStringToByteArray(String s) {
		int len = s.length();
		byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
					+ Character.digit(s.charAt(i+1), 16));
		}
		return data;
	}

	private static byte charToByte(char c) {
		return (byte) QPP_HEX_STR.indexOf(c);
	}

	protected static final char[] hexArray = QPP_HEX_STR.toCharArray();

	public static String bytesToHex(byte[] bytes) {
		char[] hexChars = new char[bytes.length * 2];
		for (int j = 0; j < bytes.length; j++) {
			int v = bytes[j] & 0xFF;
			hexChars[j * 2] = hexArray[v >>> 4];
			hexChars[j * 2 + 1] = hexArray[v & 0x0F];
		}
		return new String(hexChars);
	}

	public static String intToHex(int[] intdata) {
		byte[] data = new byte[intdata.length];
		for (int i = 0; i < intdata.length; i++) {
			data[i] = (byte) intdata[i];
		}
		return bytesToHex(data);
	}

}
