package edu.cmu.ahncathlab;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;


public class NewUserActivity extends AppCompatActivity {
    TextView mFName, mLName, mEmail, mPassword;
    RadioButton mRole;
    Button mRegisterButton1, mBackToLoginButton;
    RadioGroup rg;
    ProgressBar mProgressBar1;
    NewUserActivity nua;
    final Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);
        nua = this;

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        //Get view variables to be worked on ahead
        mFName = findViewById(R.id.rFName);
        mLName = findViewById(R.id.rLName);
        mEmail = findViewById(R.id.rEmailID);
        mPassword = findViewById(R.id.rPassword);
        rg = findViewById(R.id.rRadioGroup1);
        mRegisterButton1 = findViewById(R.id.rRegister_button);
        mBackToLoginButton = findViewById(R.id.rAlreadyAUser_button);
        mProgressBar1 = findViewById(R.id.login_progress);


        mRegisterButton1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("In New User activity");
                attemptRegister();
            }
        });

        mBackToLoginButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, LoginActivity.class);
                startActivity(intent);
            }
        });

    }

    private void attemptRegister(){
        mProgressBar1.setVisibility(View.VISIBLE);

        //Get information from register form
        String fName, lName, email, password, role;
        fName = mFName.getText().toString();
        lName = mLName.getText().toString();
        email = mEmail.getText().toString();
        password = calculateHash();
        mRole = findViewById(rg.getCheckedRadioButtonId());
        role = mRole.getText().toString();



        //Register user by adding user info in the db
        String csvString = "AddUser" +","+ fName +","+ lName +","+ email +","+ password +","+ role;
        //Add logging variables
        csvString = csvString +","+ email +","+ Build.MODEL +","+ Build.MANUFACTURER +","+ Build.VERSION.RELEASE;
        new ExecuteTask().execute(csvString);
//        String response = serverComm.registerUser(csvString, nua);
//        attemptLogin();
    }

    class ExecuteTask extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
            System.out.println("In doInBackground");
            try {
                PostData(params);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            mProgressBar1.setVisibility(View.GONE);
            Intent intent = new Intent(context, LoginActivity.class);
            startActivity(intent);
        }

    }

    public void PostData(String[] values) throws IOException {
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
            out.write(values[0]);
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

    }

    public void setPostResponse(int i){
        System.out.println(i);
    }


    //Hash password
    public java.lang.String calculateHash() {
        //Salt to be combined with password before hashing
        String salt = "MASS";
        String hashThis = mPassword.getText().toString() + salt;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(hashThis.getBytes());
            byte[] digest = md.digest();

            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i=0; i<digest.length; i++)
                hexString.append(Integer.toHexString(0xFF & digest[i]));
            return hexString.toString();

        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(mEmail.getText().toString()).log(Level.SEVERE, null, ex);
        }
        return "";
    }

}
