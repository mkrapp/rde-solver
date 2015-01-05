package utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class Parser {

	public static int getInteger(Properties p, String s) {
		return Integer.parseInt(p.getProperty(s));
	}

	public static int getInteger(String s) {
		return Integer.parseInt(s);
	}

	public static double getDouble(Properties p, String s) {
		return Double.parseDouble(p.getProperty(s));
	}

	public static double getDouble(String s) {
		return Double.parseDouble(s);
	}

	public static String getString(File file) {
		StringBuilder sb = new StringBuilder();

		try {
			BufferedReader input = new BufferedReader(new FileReader(file));
			try {
				String line = null; //not declared within while loop
				while ((line = input.readLine()) != null) {
					sb.append(line + " ");
				}
			} finally {
				input.close();
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		return sb.toString();
	}

}
