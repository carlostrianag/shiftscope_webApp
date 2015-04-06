/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shudder.netservices;

import com.ning.http.client.AsyncCompletionHandler;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.Response;

import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import shudder.util.Constants;

/**
 *
 * @author carlos
 */
public class HTTPService {

    public static void HTTPGet(String targetURL, AsyncCompletionHandler<Void> responseHandler) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.prepareGet(Constants.SERVER_URL + targetURL).execute(responseHandler);
    }
    
    public static Response HTTPSyncGet(String targetURL) {
        AsyncHttpClient client = new AsyncHttpClient();
        Response r;
        try {
            r = client.prepareGet(Constants.SERVER_URL + targetURL).execute().get();
            return r;
        } catch (InterruptedException ex) {
            Logger.getLogger(HTTPService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ExecutionException ex) {
            Logger.getLogger(HTTPService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
        //System.out.println(r.getResponseBody() + " from: " + Constants.SERVER_URL+targetURL);
    }

    public static void HTTPPost(String targetURL, String urlParameters, AsyncCompletionHandler<Void> responseHandler) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.preparePost(Constants.SERVER_URL + targetURL)
            .addHeader("content-type", "application/json; charset=utf-8")
            .setBody(urlParameters)
            .execute(responseHandler);
    }
    
    public static Response HTTPSyncPost(String targetURL, String urlParameters) {
        AsyncHttpClient client = new AsyncHttpClient();
        Response r;
        try {
            r = client.preparePost(Constants.SERVER_URL + targetURL)
                    .addHeader("content-type", "application/json; charset=utf-8")
                    .setBody(urlParameters)
                    .execute().get();
            return r;
        } catch (InterruptedException ex) {
            Logger.getLogger(HTTPService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ExecutionException ex) {
            Logger.getLogger(HTTPService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public static void HTTPDelete(String targetURL, AsyncCompletionHandler<Void> responseHandler) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.prepareDelete(Constants.SERVER_URL + targetURL).execute(responseHandler);
    }    
}
