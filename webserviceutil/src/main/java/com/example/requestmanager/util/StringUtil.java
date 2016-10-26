package com.example.requestmanager.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Chris on 2015/9/2.
 */
public class StringUtil {

	/**
	 * check Object obj is null
	 * 
	 * @param obj
	 * @return
	 */
	public static boolean isEmpty(final Object obj) {
		if (null != obj && !"".equals(obj)) {
			return false;
		}
		return true;
	}

	/**
	 * check Object obj is not null
	 * 
	 * @param obj
	 * @return
	 */
	public static boolean isNotEmpty(final Object obj) {
		return !isEmpty(obj);
	}


	public static boolean isNumber(String str) {
		String num = "^[0-9]{1,}$";
		Pattern pattern2 = Pattern.compile(num);
		Matcher matcher2 = pattern2.matcher(str);
		boolean isNumber = matcher2.matches();
		return isNumber;
	}
}
