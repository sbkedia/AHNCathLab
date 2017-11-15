package edu.cmu.ahncathlab;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TimeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    final Context context = this;
    private TextView mOutputText;
    private EditText datePick;
    private Calendar myCalendar;
    private DatePickerDialog.OnDateSetListener date;
    private DisplayTime mDisplayTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time);

        myCalendar = Calendar.getInstance();
        mOutputText = findViewById(R.id.timeView);
        datePick= findViewById(R.id.pickDate);

        Toolbar toolbar = findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);

        //Add navigation bar to screen
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout2);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer,toolbar,  R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        mDisplayTime = new DisplayTime();

        date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
                mDisplayTime.execute((Void) null);
            }

        };
        datePick.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(context, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

    }

    private void updateLabel() {
        String myFormat = "MM/dd/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        datePick.setText(sdf.format(myCalendar.getTime()));
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout2);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);

        //Update navigation bar details
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view2);
        navigationView.setNavigationItemSelectedListener(this);
        View header=navigationView.getHeaderView(0);
        TextView mEmail = (TextView)header.findViewById(R.id.emailID);
        TextView logInEmail = findViewById(R.id.email);
        mEmail.setText(LoginActivity.logInEmail);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            logout();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_cost) {
            Intent intent = new Intent(context, CostActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_time) {
            Intent intent = new Intent(context, TimeActivity.class);
            startActivity(intent);
        }else if (id == R.id.nav_home) {
            Intent intent = new Intent(context, MenuActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout2);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void logout() {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        LoginActivity.logInEmail = "";
        startActivity(intent);
        finish();
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class DisplayTime extends AsyncTask<Void, Void, List<String>> {

        @Override
        protected List<String> doInBackground(Void... params) {
            //Check if correct credentials
            List<String> response = doGet();
            return response;
        }

        @Override
        protected void onPostExecute(List<String> output) {

            if (output == null || output.size() == 0) {
                mOutputText.setText("No results returned.");
            } else {
                int count = 0;
                for(String dis : output){
                    count++;
                    mOutputText.append(dis +"\t");
                    if(count%3==0){
                        mOutputText.append("\n");
                    }
                }
//                mOutputText.setText(TextUtils.join("\n", output));
//                mOutputText.setText(TextUtils.join(" ", output));
            }
        }

        public List<String> doGet() {
            // Make an HTTP GET passing the name on the URL line
            List<String> response = new ArrayList<>();
            HttpURLConnection conn;
            int status = 0;

            try {
                System.out.println(datePick.getText().toString());
                // pass the userid,password
                URL url = new URL("https://ahncathlabserver.herokuapp.com/MongoDBAdd/?csvString=" + "FetchUser" +","+ LoginActivity.logInEmail +","+datePick.getText().toString()+","+ LoginActivity.logInEmail +","+ Build.MODEL +","+ Build.MANUFACTURER +","+ Build.VERSION.RELEASE);
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                // wait for response
                status = conn.getResponseCode();

                System.out.println(status);
                // If things went poorly, don't try to read any response, just return.
                if (status != 200) {
                    // not using msg
                    String msg = conn.getResponseMessage();
                    response.add(msg);
                    return response;
                }
                String output = "";
                // things went well so let's read the response
                BufferedReader br = new BufferedReader(new InputStreamReader(
                        (conn.getInputStream())));

                while ((output = br.readLine()) != null) {
                        System.out.println(output);
                        response.addAll(Arrays.asList(output.split(",")));
                }
                System.out.println(response);
                conn.disconnect();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return response;
        }

    }
}
