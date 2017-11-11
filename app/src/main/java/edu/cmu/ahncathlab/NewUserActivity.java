package edu.cmu.ahncathlab;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.view.View.OnClickListener;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;


public class NewUserActivity extends AppCompatActivity {
    TextView mFName, mLName, mEmail, mPassword;
    RadioButton mRole;
    Button mRegisterButton1, mBackToLoginButton;
    RadioGroup rg;
//    ProgressBar mProgressBar1;
    HttpURLConnection conn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_new_user);
//
//        //Get view variables to be worked on ahead
//        mFName = findViewById(R.id.rFName);
//        mLName = findViewById(R.id.rLName);
//        mEmail = findViewById(R.id.rEmailID);
//        mPassword = findViewById(R.id.rPassword);
//        rg = findViewById(R.id.rRadioGroup1);
//        mRole = findViewById(rg.getCheckedRadioButtonId());
//        mRegisterButton1 = findViewById(R.id.rRegister_button);
//        mBackToLoginButton = findViewById(R.id.rAlreadyAUser_button);
//        mProgressBar1 = findViewById(R.id.progressBar1);

        //Get MongoDb connection
        ServerComm mDB = new ServerComm();
        conn = mDB.getConnection();


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
                attemptLogin();
            }
        });

    }

    private void attemptRegister(){
//        mprogressBar1.setVisibility(View.VISIBLE);

        //Get information from register form
        String fName, Lname, Email, password, role;
        fName = mFName.getText().toString();
        Lname = mLName.getText().toString();
        Email = mEmail.getText().toString();
        password = mPassword.getText().toString();
        role = mRole.getText().toString();


        System.out.println(fName);
        System.out.println(Lname);
        System.out.println(Email);
        System.out.println(password);
        System.out.println(role);

        //Register user by adding user info in the db


//        new ExecuteTask().execute("agt", "Aritra", "Guha", "a@gmail", "dsgvds", role);
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
//            mprogressBar1.setVisibility(View.GONE);
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
