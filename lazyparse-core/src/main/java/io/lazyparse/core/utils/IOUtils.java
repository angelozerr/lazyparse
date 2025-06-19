package io.lazyparse.core.utils;

import java.io.InputStream;
import java.util.Scanner;

/**
 * IO utilities class
 * *
 */
public class IOUtils {

	/**
	 * Convert the given {@link InputStream} into a String. The source InputStream
	 * will then be closed.
	 * 
	 * @param is the input stream
	 * @return the given input stream in a String.
	 */
	public static String convertStreamToString(InputStream is) {
		try (Scanner s = new java.util.Scanner(is)) {
			s.useDelimiter("\\A");
			return s.hasNext() ? s.next() : "";
		}
	}
}