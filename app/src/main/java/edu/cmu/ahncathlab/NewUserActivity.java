package edu.cmu.ahncathlab;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.view.View.OnClickListener;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class NewUserActivity extends AppCompatActivity {
    TextView muser_ID, mfName, mLname, mEmail, mpassword;
    RadioButton mrole;
    Button mRegisterButton1;
    ProgressBar mprogressBar1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);
        System.out.println("In New User activity");
        muser_ID = findViewById(R.id.userid);
        mfName = findViewById(R.id.FName);
        mLname = findViewById(R.id.LName);
        mEmail = findViewById(R.id.emailID);
        mpassword = findViewById(R.id.password);
        RadioGroup rg = findViewById(R.id.RadioGroup1);
        mrole = findViewById(rg.getCheckedRadioButtonId());
        mRegisterButton1 = findViewById(R.id.register_button);
        mprogressBar1 = findViewById(R.id.progressBar1);
        mRegisterButton1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRegister();
            }
        });
    }

    private void attemptRegister(){
        mprogressBar1.setVisibility(View.VISIBLE);
        String user_ID, fName, Lname, Email, password, role;
//        user_ID = muser_ID.getText().toString();
//        fName = mfName.getText().toString();
//        Lname = mLname.getText().toString();
//        Email = mEmail.getText().toString();
//        password = mpassword.getText().toString();
        role = "Physician";
        System.out.println("In registration method.");
        new ExecuteTask().execute("agt", "Aritra", "Guha", "a@gmail", "dsgvds", role);
        attemptLogin();
    }

    private void attemptLogin() {
        final Context context = this;
        Intent intent = new Intent(context, LoginActivity.class);
        startActivity(intent);
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
            mprogressBar1.setVisibility(View.GONE);
        }

    }

    public void PostData(String[] values) throws IOException {
//        HttpClient hc = new DefaultHttpClient();
//        HttpPost hp = new HttpPost("http://128.237.130.121:8080/MongoDBFetchandAdd");
//        List<NameValuePair> user = new ArrayList<NameValuePair>();
//        user.add(new BasicNameValuePair("user_ID", values[0]));
//        user.add(new BasicNameValuePair("FName", values[1]));
//        user.add(new BasicNameValuePair("LName", values[2]));
//        user.add(new BasicNameValuePair("Email", values[3]));
//        user.add(new BasicNameValuePair("password", values[4]));
//        user.add(new BasicNameValuePair("role", values[5]));
//        System.out.println(1);
//        hp.setEntity(new UrlEncodedFormEntity(user));
//        hc.execute(hp);
        

    }




}
