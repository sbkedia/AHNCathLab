package edu.cmu.ahncathlab;

import android.os.AsyncTask;

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
    String response;
    NewUserActivity newUserActivity;
    MenuActivity menuActivity;

    // Add new user info to database
    public String registerUser(String csvString, NewUserActivity nua) {
        newUserActivity = nua;
        new doPost().execute(csvString);
        return "";
    }

    public String addTimeTrack(String csvString, MenuActivity ma){
        menuActivity=ma;
        new doPost().execute(csvString);
        return "";
    }

    // Perform get to service and retrive required information
    // return either the value read or an error message

//    public static String read(String getVariables) {
//        int status = 0;
//        if ((status = doGet(getVariables)) != 200) {
//            return "Error from server " + status;
//        }
//        return null;
//    }

    private class doPost extends AsyncTask<String, Void, Integer> {

        @Override
        protected Integer doInBackground(String... strings) {
           int response = performPost(strings[0]);
            return response;
        }

//        protected void onPostExecute(int postResponse){ newUserActivity.setPostResponse(postResponse);
//        }

        public int doGet(String variableString) {

            // Make an HTTP GET passing the name on the URL line
            String response = "";
            HttpURLConnection conn;
            int status = 0;

            try {

                // pass the name on the URL line
                URL url = new URL("http://10.0.2.2:8090/MongoDBFetchandAdd/MongoDBAdd/" + "/" + variableString);
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

        // Low level routine to make an HTTP POST request
        // Note, POST does not use the URL line for its message to the server
        public int performPost(String csvString) {

            int status = 0;
            String output;

            try {
                System.out.println("In post");
                // Make call to a particular URL
                URL url = new URL("http://10.0.2.2:8070/MongoDBFetchandAdd/MongoDBAdd/ ");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                // set request method to POST and send name value pair
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                // write to POST data area
                OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
                out.write(csvString);
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
}