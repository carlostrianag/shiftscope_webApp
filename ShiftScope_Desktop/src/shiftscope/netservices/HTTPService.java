/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shiftscope.netservices;

import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.Response;

import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import shiftscope.util.Constants;

/**
 *
 * @author carlos
 */
public class HTTPService {

    public static Response HTTPGet(String targetURL) {
        AsyncHttpClient client = new AsyncHttpClient();
        try {
            Response r = client.prepareGet(Constants.SERVER_URL + targetURL).execute().get();
            //System.out.println(r.getResponseBody() + " from: " + Constants.SERVER_URL+targetURL);
            return r;
        } catch (InterruptedException ex) {
            Logger.getLogger(HTTPService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ExecutionException ex) {
            System.out.println("Se ha producido un error de conexion");
        }
        return null;
    }

    public static Response HTTPPost(String targetURL, String urlParameters) {
        AsyncHttpClient client = new AsyncHttpClient();
        try {
            Response r = client.preparePost(Constants.SERVER_URL + targetURL)
                    .addHeader("content-type", "application/json; charset=utf-8")
                    .setBody(urlParameters)
                    .execute()
                    .get();
            return r;
        } catch (InterruptedException ex) {
            Logger.getLogger(HTTPService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ExecutionException ex) {
            System.out.println("Se ha producido un error de conexion");
        }
        return null;
    }
}
