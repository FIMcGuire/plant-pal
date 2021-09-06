package com.example.plantpalplz;

import android.content.Context;
import android.net.ConnectivityManager;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class Function {

    //method isNetworkAvailable()
    public static boolean isNetworkAvailable(Context context) {
        //return ConnectivityManager object
        return ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo() != null;
    }

    //method executeGet() with String parameters
    public static String excuteGet(String targetURL) {
        //create URl object url
        URL url;
        //create HttpURLConnection object connection set equal to null
        HttpURLConnection connection = null;
        //try catch block to catch exceptions
        try {
            //Create connection to targetURL
            url = new URL(targetURL);
            connection = (HttpURLConnection) url.openConnection();
            //set request headers
            connection.setRequestProperty("content-type", "application/json;  charset=utf-8");
            connection.setRequestProperty("Content-Language", "en-US");
            //setUseChaches to false
            connection.setUseCaches(false);
            //setDoInput to true, i.e. read data from URL connection
            connection.setDoInput(true);
            //setDoOutput to false, i.e. do not write data to URL connection
            connection.setDoOutput(false);

            //create InputStream object is to read data from opened connection
            InputStream is;
            //create int status equal to response code from URL connection
            int status = connection.getResponseCode();
            //if statement to determine if status is not equal to value HTTP_OK (i.e. status == HttpURLConnection.HTTP_Unauthorized)
            if (status != HttpURLConnection.HTTP_OK)
                //set is equal to error stream if any, null if no errors/connection not connected or no useful data
                is = connection.getErrorStream();
            else
                //set is equal to input stream
                is = connection.getInputStream();
            //create BufferedReader object rd wrapped around InputStreamReader(is) for efficiency
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            //create string line
            String line;
            //create StringBuffer response
            StringBuffer response = new StringBuffer();
            //while line is not null
            while ((line = rd.readLine()) != null) {
                //append line to StringBuffer response
                response.append(line);
                //append '\r' to StringBuffer response (carriage return)
                response.append('\r');
            }
            //call rd.close() to close stream
            rd.close();
            //return StringBuffer response as String
            return response.toString();
        } catch (Exception e) {
            //return null
            return null;
        } finally {
            //if statement to determine if connection is not null
            if (connection != null) {
                //call connection.disconnect() to close socket
                connection.disconnect();
            }
        }
    }

    public static void executeGetW(String targetURL, int interval) {
        String urlString = targetURL; // URL to call
        String data = String.valueOf(interval); //data to post
        OutputStream out = null;

        try {
            URL url = new URL(urlString);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            out = new BufferedOutputStream(urlConnection.getOutputStream());

            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
            writer.write(data);
            writer.flush();
            writer.close();
            out.close();

            urlConnection.connect();
            Log.e("Tinternet", "here");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }
}
