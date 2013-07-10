package edu.berkeley.delphi.utils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class WebUtils {


	public static int checkURL( String pathToCheck ) {
		int response = HttpURLConnection.HTTP_NOT_FOUND;
		try {
			URL url = new URL(pathToCheck);
			URLConnection connection = url.openConnection();
			if (connection instanceof HttpURLConnection) {
				HttpURLConnection httpConnection = (HttpURLConnection)connection;
				httpConnection.connect();
				response = httpConnection.getResponseCode();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return response;
	}


	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if( checkURL("http://pahma.berkeley.edu/delphi") != HttpURLConnection.HTTP_OK )
			System.out.println("Error: could not see PAHMA delphi home.");
		else
			System.out.println("Correctly saw PAHMA delphi home.");
		if( checkURL("http://pahma.berkeley.edu/not_there") != HttpURLConnection.HTTP_NOT_FOUND )
			System.out.println("Error: Seeing files that cannot be there.");
		else
			System.out.println("Correctly did not see file that is not there.");
	}

}
