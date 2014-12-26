/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shiftscope.netservices;

import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import shiftscope.util.Constants;

/**
 *
 * @author carlos
 */
public class HTTPService {

    private static Gson JSONParser;

    
    
    public static String parseContent(InputStream content){
        String responseContent = "";
        String line;
        BufferedReader rd = new BufferedReader(new InputStreamReader(content));
        try {
            while ((line = rd.readLine()) != null) {
                responseContent += line;
            }
        } catch (IOException ex) {
            Logger.getLogger(HTTPService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return responseContent;
    }
    
    public static HttpResponse HTTPGet(String targetURL) {
        HttpClient httpClient = new DefaultHttpClient();

        try {
            HttpGet request = new HttpGet(Constants.SERVER_URL+targetURL);
            request.addHeader("content-type", "application/json");
            request.addHeader("accept", "json");
            HttpResponse response = httpClient.execute(request);
            return response;
        } catch (Exception ex) {
            return null;
        } finally {
            httpClient.getConnectionManager().shutdown();
        }
    }   
    
    public static HttpResponse HTTPPost(String targetURL, String urlParameters) {
        HttpClient httpClient = new DefaultHttpClient();
        HttpResponse response;
        try {
            HttpPost request = new HttpPost(Constants.SERVER_URL+targetURL);
            StringEntity params = new StringEntity(urlParameters, "UTF-8");
            request.addHeader("content-type", "application/json");
            request.setEntity(params);
            response = httpClient.execute(request);
        } catch (Exception ex) {
            return null;
        } finally {
            httpClient.getConnectionManager().shutdown();
        }
        return response;
    }
}
