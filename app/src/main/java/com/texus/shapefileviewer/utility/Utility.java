package com.texus.shapefileviewer.utility;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


public class Utility {

	
	public static String readFromAssets(String filename, Context context) {
	 	String content = "";
	 	
	 	BufferedReader br = null;
	     try {
	         br = new BufferedReader(new InputStreamReader(context.getAssets().open(filename))); //throwing a FileNotFoundException?
	         String word;
	         while((word=br.readLine()) != null)
	         	content = content  + word;
	     }
	         catch(IOException e) {
	             e.printStackTrace();
	         }
	         finally {
	             try {
	                 br.close(); //stop reading
	             }
	             catch(IOException ex) {
	                 ex.printStackTrace();
	             }
	         }
	 	
	 	
	 	
	 	return content;
	 }

	public static int parseInt(String number) {
		int num = 0;
		try {
			num = Integer.parseInt(number);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return num;
	}
	
	public static float parseFloat(String number) {
		float num = 0;
		try {
			num = Float.parseFloat(number);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return num;
	}

    public static double parseDouble(String number) {
        double num = 0;
        try {
            num = Double.parseDouble(number);
        } catch (Exception e) {
            // TODO: handle exception
        }
        return num;
    }
	
	public static boolean parseBoolean(String value) {
		boolean boolValue = false;
		try {
			boolValue = Boolean.parseBoolean(value.toLowerCase());
		} catch (Exception e) {
		}
		return boolValue;
	}
	
	
	
}
