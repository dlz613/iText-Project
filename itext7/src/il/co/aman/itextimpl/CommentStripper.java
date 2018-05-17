package il.co.aman.itextimpl;

import java.io.IOException;
import java.lang.StringBuilder;

public class CommentStripper {
	
	final static char[] STARTC = "/*".toCharArray();
	final static char[] ENDC = "*/".toCharArray();
	
	public static String strip(String text) {
		StringBuilder res = new StringBuilder();
		char[] chars = text.toCharArray(); // this will probably only work for UTF-16
		for (int i = 0; i < chars.length; i++) {
			if (starting(chars, i)) {
				int end = endingIndex(chars, i);
				if (end < 0) {
					break;
				} else {
					i = end + ENDC.length - 1;
				}
			} else {
				res.append(chars[i]);
			}
		}
		
		return res.toString();
	}

	private static boolean starting(char[] chars, int pos) {
		boolean res = false;
		for (int i = 0; i < STARTC.length && (pos + i < chars.length); i++) {
			if (chars[pos + i] != STARTC[i]) {
				res = false;
				break;
			}
			res = i == STARTC.length - 1;
		}		
		return res;
	}
	
	private static boolean ending(char[] chars, int pos) {
		boolean res = false;
		for (int i = 0; i < ENDC.length && (pos + i < chars.length); i++) {
			if (chars[pos + i] != ENDC[i]) {
				res = false;
				break;
			}
			res = i == ENDC.length - 1;
		}
		return res;
	}
	
	private static int endingIndex(char[] chars, int pos) {
		int res = -1;
		for (int i = pos; i < chars.length; i++) {
			if (chars[i] == ENDC[0] && ending(chars, i)) {
				return i;
			}
		}
		
		return res;
	}
	
	public static void main(String[] args) throws IOException {
		String text = "hello /*the/* /////*r\ne *****/ /*sdfsdfs*/everyone how /*is*/ar/*r*/e you all /*2*/today?*/";
		System.out.println(text);
		System.out.println(strip(text));
		/*text = il.co.aman.apps.Misc.readFile("C:\\Users\\davidz\\Desktop\\metadata.css");
		System.out.println(strip(text));*/		
	}

}
