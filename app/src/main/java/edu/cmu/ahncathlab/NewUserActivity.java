package edu.cmu.ahncathlab;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
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


public class NewUserActivity extends AppCompatActivity {
    TextView mFName, mLName, mEmail, mPassword;
    RadioButton mRole;
    Button mRegisterButton1, mBackToLoginButton;
    RadioGroup rg;
    ProgressBar mProgressBar1;
    ServerComm serverComm;
    NewUserActivity nua;
    final Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);
        nua = this;

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        serverComm = new ServerComm();

        //Get view variables to be worked on ahead
        mFName = findViewById(R.id.rFName);
        mLName = findViewById(R.id.rLName);
        mEmail = findViewById(R.id.rEmailID);
        mPassword = findViewById(R.id.rPassword);
        rg = findViewById(R.id.rRadioGroup1);
        mRole = findViewById(rg.getCheckedRadioButtonId());
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
        password = mPassword.getText().toString();
        role = mRole.getText().toString();


        System.out.println(fName);
        System.out.println(lName);
        System.out.println(email);
        System.out.println(password);
        System.out.println(role);

        //Register user by adding user info in the db
        String csvString = "AddUser" +","+ fName +","+ lName +","+ email +","+ password +","+ role;
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




}
