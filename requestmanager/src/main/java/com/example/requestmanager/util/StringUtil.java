package com.example.requestmanager.util;

import android.support.annotation.NonNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Chris on 2015/9/2.
 */
public class StringUtil {
	public static boolean isNumber(@NonNull String str) {
		String num = "^[0-9]{1,}$";
		Pattern pattern2 = Pattern.compile(num);
		Matcher matcher2 = pattern2.matcher(str);
		return matcher2.matches();
	}
}
