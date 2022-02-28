package com.its4u.buildfactory.utils;

public class FormatUtils {

	public static String format(String str,int length) {
    	return String.format("%1$"+length+ "s", str);    	
	}
	
	public static String leftpad(String text, int length) {
	    return String.format("%" + length + "." + length + "s", text);
	}

	public static String rightpad(String text, int length) {
	    return String.format("%-" + length + "." + length + "s", text);
	}
	
	public static String underline(String str) {
		 String und="";
		 for (int i=0;i<str.length();i++) {
			 und=und+"-";
		 }
		 System.out.println(und);
		 return und;
	}
}
