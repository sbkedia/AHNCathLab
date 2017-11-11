package edu.cmu.ahncathlab;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by sbked on 11/10/2017.
 */

public class ServerComm {

    public HttpURLConnection getConnection(){
        URL url = null;
        try {
            url = new URL("http://10.0.2.2:8090/MongoDBFetchandAdd/MongoDBAdd/");
            HttpURLConnection conn =(HttpURLConnection) url.openConnection();
            return conn;
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    // assign a string value to a string name
    public static String assign(String xmlString) {
        // Try to PUT, if that fails then try to POST
        if (doPut(xmlString) == 200) {
            return "";
        } else {
            if (doPost(xmlString) == 200) {
                return "";
            }
        }
        return "Error from server";
    }
    // read a value associated with a name from the server
    // return either the value read or an error message

    public static String read(String name) {
        int status = 0;
        if ((status = doGet(name)) != 200) {
            return "Error from server " + status;
        }
        return null;
    }

    public static int doGet(String name) {

        // Make an HTTP GET passing the name on the URL line
        String response = "";
        HttpURLConnection conn;
        int status = 0;

        try {

            // pass the name on the URL line
            URL url = new URL("http://10.0.2.2:8090/MongoDBFetchandAdd/MongoDBAdd/" + "//" + name);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            // tell the server what format we want back
            conn.setRequestProperty("Accept", "text/plain");
            // wait for response
            status = conn.getResponseCode();

            // If things went poorly, don't try to read any response, just return.
            if (status != 200) {
                // not using msg
                String msg = conn.getResponseMessage();
                return conn.getResponseCode();
            }
            String output = "";
            // things went well so let's read the response
            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

            while ((output = br.readLine()) != null) {
                response += output + "\n";

            }

            conn.disconnect();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // return value from server
        // set the response object
//        r.setValue(response);
        // return HTTP status to caller
        return status;
    }

    // Low level routine to make an HTTP PUT request
    // Note, PUT does not use the URL line for its message to the server
    public static int doPut(String xmlValue) {

        int status = 0;
        try {
            URL url = new URL("http://localhost:8090/Project3Task3Server/BlockChainServer/");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("PUT");
            conn.setDoOutput(true);
            OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());

            out.write(xmlValue);
            out.close();
            status = conn.getResponseCode();
            conn.disconnect();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return status;
    }

    // Low level routine to make an HTTP POST request
    // Note, POST does not use the URL line for its message to the server
    public static int doPost(String xmlValue) {

        int status = 0;
        String output;

        try {
            // Make call to a particular URL
            URL url = new URL("http://localhost:8090/Project3Task3Server/BlockChainServer/");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            // set request method to POST and send name value pair
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            // write to POST data area
            OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
            out.write(xmlValue);
            out.close();

            // get HTTP response code sent by server
            status = conn.getResponseCode();

            //close the connection
            conn.disconnect();
        } // handle exceptions
        catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // return HTTP status
        return status;
    }
}
