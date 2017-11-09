package edu.cmu.ahncathlab;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;


public class NewUserActivity extends Activity {
    String user_ID, fName, Lname, Email, password, role;
    Button mlogin,mRegisterButton;
    ProgressBar progressBar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);
        user_ID = ((TextView) findViewById(R.id.userid)).getText().toString();
        fName = ((TextView) findViewById(R.id.FName)).getText().toString();
        Lname = ((TextView) findViewById(R.id.LName)).getText().toString();
        Email = ((TextView) findViewById(R.id.emailID)).getText().toString();
        password = ((TextView) findViewById(R.id.password)).getText().toString();
        RadioGroup rg = (RadioGroup) findViewById(R.id.RadioGroup1);
        role =
                ((RadioButton)findViewById(rg.getCheckedRadioButtonId()))
                        .getText().toString();
        mlogin = (Button) findViewById(R.id.btnLinkToLoginScreen);
        mRegisterButton = (Button) findViewById(R.id.register_button);
        progressBar = (ProgressBar) findViewById(R.id.progressBar1);


        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                new ExecuteTask().execute(user_ID, fName, Lname, Email, password, role);
                attemptLogin();
            }
        });

        Button mbacktologinscreenButton = (Button) findViewById(R.id.btnLinkToLoginScreen);
        mbacktologinscreenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigatetoLoginActivity(view);
            }


        });
    }

    private void attemptLogin() {
        final Context context = this;
        Intent intent = new Intent(context, LoginActivity.class);
        startActivity(intent);
    }

    public void navigatetoLoginActivity(View view){
        Intent homeIntent = new Intent(getApplicationContext(),LoginActivity.class);
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeIntent);
    }


//    public void onRadioButtonClicked(View view) {
//     // Is the button now checked?
//     boolean checked = ((RadioButton) view).isChecked();
//
//    // Check which radio button was clicked
//     switch(view.getId()) {
//         case R.id.Physician:
//             if (checked)
//
//             break;
//         case R.id.Manager:
//             if (checked)
//
//                 break;
//     }
//    }

    class ExecuteTask extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
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
            progressBar.setVisibility(View.GONE);
        }

    }

    public void PostData(String[] values) throws IOException {
        HttpClient hc = new DefaultHttpClient();
        HttpPost hp = new HttpPost("http://localhost:8080/MongoDBFetchandAdd/MongoDBAdd/");
        List<NameValuePair> user = new ArrayList<NameValuePair>();
        user.add(new BasicNameValuePair("user_ID", values[0]));
        user.add(new BasicNameValuePair("FName", values[1]));
        user.add(new BasicNameValuePair("LName", values[2]));
        user.add(new BasicNameValuePair("Email", values[3]));
        user.add(new BasicNameValuePair("password", values[4]));
        user.add(new BasicNameValuePair("role", values[5]));
        hp.setEntity(new UrlEncodedFormEntity(user));
        hc.execute(hp);
    }


}
