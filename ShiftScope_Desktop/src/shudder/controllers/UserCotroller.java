/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shudder.controllers;

import com.google.gson.Gson;
import com.ning.http.client.AsyncCompletionHandler;
import com.ning.http.client.Response;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import shudder.listeners.LoginListener;
import shudder.criteria.DeviceCriteria;
import shudder.criteria.LibraryCriteria;
import shudder.main.Main;
import shudder.model.Device;
import shudder.model.Library;
import shudder.model.User;
import shudder.netservices.HTTPService;
import shudder.util.LoginCredentials;
import shudder.util.SessionConstants;
import shudder.views.dialogs.LoginDialog;

/**
 *
 * @author carlos
 */
public class UserCotroller {

    private static ArrayList<LoginListener> listeners = new ArrayList<>();
    private static Gson JSONParser;
    
    public static void addListener(LoginListener listener) {
        listeners.add(listener);
    }
    
    public static void login(LoginCredentials credentials) {
        AsyncCompletionHandler<Void> responseHandler = new AsyncCompletionHandler<Void>() {

            @Override
            public Void onCompleted(Response response) throws Exception {
                if (response.getStatusCode() == 200) {
                    JSONParser = new Gson();
                    try {
                        User user = JSONParser.fromJson(response.getResponseBody(), User.class);
                        SessionConstants.USER_ID = user.getId();
                        verifyDeviceExistence();
                        for (LoginListener listener : listeners) {
                            listener.OnSuccessfulLogin();
                        }
                    } catch (IOException ex) {
                        Logger.getLogger(LoginDialog.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IllegalStateException ex) {
                        Logger.getLogger(LoginDialog.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    
                }
                return null;
            }

            @Override
            public void onThrowable(Throwable t) {
                for (LoginListener listener : listeners) {
                    listener.OnError(t.getMessage());
                }
            }
            
            
        };
        JSONParser = new Gson();
        String object = JSONParser.toJson(credentials);
        HTTPService.HTTPPost("/user/login", object, responseHandler);
    }

    public static Response createUser(User user) {
        JSONParser = new Gson();
        String object = JSONParser.toJson(user);
        return HTTPService.HTTPSyncPost("/user/create", object); 
    }
    
    private static void verifyDeviceExistence() {
        String uuid;
        String pcName;
        Device device;
        Device createdDevice;
        Response response;
        Library library;
        Library createdLibrary;
        List<String> lines;
        DeviceCriteria criteria;
        Device returnedDevice;
        Library returnedLibrary;
        LibraryCriteria libraryCriteria;
        File f = new File("secure-key_"+SessionConstants.USER_ID+".shft");
        JSONParser = new Gson();
        if (f.exists()) {
            try {
                lines = Files.readAllLines(Paths.get("secure-key_"+SessionConstants.USER_ID+".shft"));
                uuid = lines.get(0);
                criteria = new DeviceCriteria();
                criteria.setUUID(uuid);
                response = DeviceController.getDeviceByUUID(criteria);
                returnedDevice = JSONParser.fromJson(response.getResponseBody(), Device.class);
                
                SessionConstants.DEVICE_ID = returnedDevice.getId();
                criteria = new DeviceCriteria();
                criteria.setId(SessionConstants.DEVICE_ID);
                criteria.setOnline(true);
                DeviceController.connectDevice(criteria);                
                libraryCriteria = new LibraryCriteria();
                libraryCriteria.setDevice(SessionConstants.DEVICE_ID);
                response = LibraryController.getLibraryByDeviceId(libraryCriteria);
                returnedLibrary = JSONParser.fromJson(response.getResponseBody(), Library.class);
                SessionConstants.LIBRARY_ID = returnedLibrary.getId();

            } catch (IOException ex) {
                Logger.getLogger(LoginDialog.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            uuid = UUID.randomUUID().toString();
            try {
                do {
                    pcName = InetAddress.getLocalHost().getHostName();
                    f.createNewFile();
                    PrintWriter writer = new PrintWriter("secure-key_"+SessionConstants.USER_ID+".shft", "UTF-8");
                    writer.println(uuid);
                    writer.close();
                    f.setReadOnly();
                    device = new Device();
                    device.setOnline(true);
                    device.setOwnerUser(SessionConstants.USER_ID);
                    device.setUUID(uuid);
                    device.setName(pcName);
                } while ((response = DeviceController.createDevice(device)).getStatusCode() != 200);

                createdDevice = JSONParser.fromJson(response.getResponseBody(), Device.class);
                SessionConstants.DEVICE_ID = createdDevice.getId();
                library = new Library();
                library.setDevice(SessionConstants.DEVICE_ID);
                library.setUser(SessionConstants.USER_ID);
                response = LibraryController.createLibrary(library);
                createdLibrary = JSONParser.fromJson(response.getResponseBody(), Library.class);
                SessionConstants.LIBRARY_ID = createdLibrary.getId();
            } catch (UnknownHostException ex) {
                Logger.getLogger(LoginDialog.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(LoginDialog.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        for(LoginListener listener : listeners) {
            listener.OnInit();
        }
    }
}
