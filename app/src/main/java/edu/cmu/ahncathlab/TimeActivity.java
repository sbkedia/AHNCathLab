package edu.cmu.ahncathlab;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.squareup.timessquare.CalendarPickerView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.squareup.timessquare.CalendarPickerView.SelectionMode.RANGE;

public class TimeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    final Context context = this;
    private TextView mOutputText;
    private EditText datePickStart;
    private EditText datePickEnd;
    private Calendar myCalendarStart;
    private Calendar myCalendarEnd;
    private DatePickerDialog.OnDateSetListener dateStart;
    private DatePickerDialog.OnDateSetListener dateEnd;
    private DisplayTime mDisplayTime;
    private TimeActivity timeActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        timeActivity = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time);

        myCalendarStart = Calendar.getInstance();
        myCalendarEnd = Calendar.getInstance();
        mOutputText = findViewById(R.id.timeView);
        datePickStart = findViewById(R.id.pickDateStart);
        datePickEnd = findViewById(R.id.pickDateEnd);

        Toolbar toolbar = findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);

        //Add navigation bar to screen
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout2);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer,toolbar,  R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        mDisplayTime = new DisplayTime();

        dateStart = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                mDisplayTime = new DisplayTime();
                myCalendarStart.set(Calendar.YEAR, year);
                myCalendarStart.set(Calendar.MONTH, monthOfYear);
                myCalendarStart.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabelStart();
            }

        };
        datePickStart.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(context, dateStart, myCalendarStart
                        .get(Calendar.YEAR), myCalendarStart.get(Calendar.MONTH),
                        myCalendarStart.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        dateEnd = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {

                myCalendarEnd.set(Calendar.YEAR, year);
                myCalendarEnd.set(Calendar.MONTH, monthOfYear);
                myCalendarEnd.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabelEnd();
            }

        };
        datePickEnd.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new DatePickerDialog(context, dateEnd, myCalendarEnd
                        .get(Calendar.YEAR), myCalendarEnd.get(Calendar.MONTH),
                        myCalendarEnd.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        Button mShowCost = (Button) findViewById(R.id.showTime);
        mShowCost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDisplayTime = new DisplayTime();
                mDisplayTime.execute((Void) null);
            }
        });

    }

    private void updateLabelStart() {
        String myFormat = "MM/dd/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        datePickStart.setText(sdf.format(myCalendarStart.getTime()));
    }

    private void updateLabelEnd() {
        String myFormat = "MM/dd/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        datePickEnd.setText(sdf.format(myCalendarEnd.getTime()));
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

        if(!LoginActivity.role.equalsIgnoreCase("Manager")) {
            MenuItem it = navigationView.getMenu().findItem(R.id.nav_cost);
            it.setVisible(false);
        }
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

        TableLayout stk = (TableLayout) findViewById(R.id.timeTable);

        @Override
        protected List<String> doInBackground(Void... params) {
            //Check if correct credentials
            List<String> response = doGet();
            return response;
        }

        @Override
        protected void onPostExecute(List<String> output) {

            stk.removeAllViews();
            mOutputText.setText("");

            if (output == null || output.size() == 1) {
                mOutputText.setText("No results returned.");
            } else {
                createTimeTable(output);
//                int count = 0;
//                for(String dis : output){
//                    count++;
//                    mOutputText.append(dis +"\t");
//                    if(count%3==0){
//                        mOutputText.append("\n");
//                    }
//                }
//                mOutputText.setText(TextUtils.join("\n", output));
//                mOutputText.setText(TextUtils.join(" ", output));
            }
        }

        //Create table view
        public void createTimeTable(List<String> output) {

            if(LoginActivity.role.toLowerCase().equals("physician")) {
                TableRow tbrow0 = new TableRow(timeActivity);
                TextView tv0 = new TextView(timeActivity);
                tv0.setText(" DATE ");
                tv0.setTextColor(Color.GREEN);
                tv0.setGravity(Gravity.CENTER);
                tbrow0.addView(tv0);
                TextView tv1 = new TextView(timeActivity);
                tv1.setText(" TIME ");
                tv1.setTextColor(Color.GREEN);
                tv1.setGravity(Gravity.CENTER);
                tbrow0.addView(tv1);
                TextView tv2 = new TextView(timeActivity);
                tv2.setText(" MOVEMENT ");
                tv2.setTextColor(Color.GREEN);
                tv2.setGravity(Gravity.CENTER);
                tbrow0.addView(tv2);
                stk.addView(tbrow0);

                String prevDate = "";
                String currentDate = "";
                long totalTime = 0;
                try {
                    SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
                    Date dateIn = format.parse("00:00:00");
                    Date dateOut = format.parse("00:00:00");


                    for (int i = 0; i < output.size(); i = i + 3) {
                        currentDate = output.get(i);

                        //Calculate total time
                        if (output.get(i + 2).equalsIgnoreCase("in")) {
                            dateIn = format.parse(output.get(i + 1));
                        } else {
                            dateOut = format.parse(output.get(i + 1));
                            totalTime = totalTime + (dateOut.getTime() - dateIn.getTime());
                        }

                        // Add total row
                        if(!currentDate.equalsIgnoreCase(prevDate) && !prevDate.equalsIgnoreCase("")){
                            TableRow tbrow = new TableRow(timeActivity);
                            TextView t1v = new TextView(timeActivity);
                            t1v.setText(" " + "Total Time" + " ");
                            t1v.setTextColor(Color.CYAN);
                            t1v.setGravity(Gravity.CENTER);
                            tbrow.addView(t1v);
                            TextView t2v = new TextView(timeActivity);
                            t2v.setText(" " + totalTime + " ");
                            t2v.setTextColor(Color.CYAN);
                            t2v.setGravity(Gravity.CENTER);
                            tbrow.addView(t2v);
                            stk.addView(tbrow);

                            //Add blank row after total
                            stk.addView(new TableRow(timeActivity));
                        }


                        TableRow tbrow = new TableRow(timeActivity);
                        TextView t1v = new TextView(timeActivity);
                        t1v.setText(" " + output.get(i) + " ");
                        t1v.setTextColor(Color.WHITE);
                        t1v.setGravity(Gravity.CENTER);
                        tbrow.addView(t1v);
                        TextView t2v = new TextView(timeActivity);
                        t2v.setText(" " + output.get(i+1) + " ");
                        t2v.setTextColor(Color.WHITE);
                        t2v.setGravity(Gravity.CENTER);
                        tbrow.addView(t2v);
                        TextView t3v = new TextView(timeActivity);
                        t3v.setText(" " + output.get(i+2) + " ");
                        t3v.setTextColor(Color.WHITE);
                        t3v.setGravity(Gravity.CENTER);
                        tbrow.addView(t3v);
                        stk.addView(tbrow);
                    }
                }
                catch (ParseException e) {
                    e.printStackTrace();
                }

                //Add final total time
                String tTime = String.format("%d min, %d sec",
                        TimeUnit.MILLISECONDS.toMinutes(totalTime),
                        TimeUnit.MILLISECONDS.toSeconds(totalTime) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(totalTime))
                );
                TableRow tbrow = new TableRow(timeActivity);
                TextView t1v = new TextView(timeActivity);
                t1v.setText(" " + "Total Time" + " ");
                t1v.setTextColor(Color.CYAN);
                t1v.setGravity(Gravity.CENTER);
                tbrow.addView(t1v);
                TextView t2v = new TextView(timeActivity);
                t2v.setText(" " + tTime + " ");
                t2v.setTextColor(Color.CYAN);
                t2v.setGravity(Gravity.CENTER);
                tbrow.addView(t2v);
                stk.addView(tbrow);

                //Add blank row after total
                stk.addView(new TableRow(timeActivity));
            }

            // Display time for manager
            else {
                TableRow tbrow0 = new TableRow(timeActivity);
                TextView tv0 = new TextView(timeActivity);
                tv0.setText(" PHYSICIAN ");
                tv0.setTextColor(Color.GREEN);
                tv0.setGravity(Gravity.CENTER);
                tbrow0.addView(tv0);

                TextView tv1 = new TextView(timeActivity);
                tv1.setText(" DATE ");
                tv1.setTextColor(Color.GREEN);
                tv1.setGravity(Gravity.CENTER);
                tbrow0.addView(tv1);

                TextView tv2 = new TextView(timeActivity);
                tv2.setText(" TIME ");
                tv2.setTextColor(Color.GREEN);
                tv2.setGravity(Gravity.CENTER);
                tbrow0.addView(tv2);

                TextView tv3 = new TextView(timeActivity);
                tv3.setText(" MOVEMENT ");
                tv3.setTextColor(Color.GREEN);
                tv3.setGravity(Gravity.CENTER);
                tbrow0.addView(tv3);
                stk.addView(tbrow0);

                String prevDate = "";
                String currentDate = "";
                long totalTime = 0;

                //Store each user ID
                HashSet<String> userIds=new HashSet<String>();
                for(int k = 0; k<output.size(); k = k +4){
                    userIds.add(output.get(k));
                }
                Iterator<String> itr=userIds.iterator();
                String userId;

                //Display user-wise time
                while(itr.hasNext()){
                userId= itr.next();
                System.out.println("Userrrrrr" + userId);

                try {
                    SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
                    Date dateIn = format.parse("00:00:00");
                    Date dateOut = format.parse("00:00:00");

                    for (int i = 0; i < output.size(); i = i + 4) {

                        if (output.get(i).equalsIgnoreCase(userId)) {
                            currentDate = output.get(i);

                            //Calculate total time
                            if (output.get(i + 3).equalsIgnoreCase("in")) {
                                dateIn = format.parse(output.get(i + 2));
                            } else {
                                dateOut = format.parse(output.get(i + 2));
                                totalTime = totalTime + (dateOut.getTime() - dateIn.getTime());
                            }

                            // Add total row
                            if (!currentDate.equalsIgnoreCase(prevDate) && !prevDate.equalsIgnoreCase("")) {
                                TableRow tbrow = new TableRow(timeActivity);
                                TextView t1v = new TextView(timeActivity);
                                t1v.setText(" " + "Total Time" + " ");
                                t1v.setTextColor(Color.CYAN);
                                t1v.setGravity(Gravity.CENTER);
                                tbrow.addView(t1v);
                                TextView t2v = new TextView(timeActivity);
                                t2v.setText(" " + totalTime + " ");
                                t2v.setTextColor(Color.CYAN);
                                t2v.setGravity(Gravity.CENTER);
                                tbrow.addView(t2v);
                                stk.addView(tbrow);

                                //Add blank row after total
                                stk.addView(new TableRow(timeActivity));
                            }


                            TableRow tbrow = new TableRow(timeActivity);
                            TextView t0v = new TextView(timeActivity);
                            t0v.setText(" " + output.get(i) + " ");
                            t0v.setTextColor(Color.WHITE);
                            t0v.setGravity(Gravity.CENTER);
                            tbrow.addView(t0v);

                            TextView t1v = new TextView(timeActivity);
                            t1v.setText(" " + output.get(i + 1) + " ");
                            t1v.setTextColor(Color.WHITE);
                            t1v.setGravity(Gravity.CENTER);
                            tbrow.addView(t1v);

                            TextView t2v = new TextView(timeActivity);
                            t2v.setText(" " + output.get(i + 2) + " ");
                            t2v.setTextColor(Color.WHITE);
                            t2v.setGravity(Gravity.CENTER);
                            tbrow.addView(t2v);

                            TextView t3v = new TextView(timeActivity);
                            t3v.setText(" " + output.get(i + 3) + " ");
                            t3v.setTextColor(Color.WHITE);
                            t3v.setGravity(Gravity.CENTER);
                            tbrow.addView(t3v);
                            stk.addView(tbrow);
                        }
                    }
                }
                catch (ParseException e) {
                    e.printStackTrace();
                }

                //Add final total time
                String tTime = String.format("%d min, %d sec",
                        TimeUnit.MILLISECONDS.toMinutes(totalTime),
                        TimeUnit.MILLISECONDS.toSeconds(totalTime) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(totalTime))
                );
                TableRow tbrow = new TableRow(timeActivity);
                TextView t1v = new TextView(timeActivity);
                t1v.setText(" " + "Total Time" + " ");
                t1v.setTextColor(Color.CYAN);
                t1v.setGravity(Gravity.CENTER);
                tbrow.addView(t1v);
                TextView t2v = new TextView(timeActivity);
                t2v.setText(" " + tTime + " ");
                t2v.setTextColor(Color.CYAN);
                t2v.setGravity(Gravity.CENTER);
                tbrow.addView(t2v);
                stk.addView(tbrow);

                //Add blank row after total
                stk.addView(new TableRow(timeActivity));
            }
            }

        }

        public List<String> doGet() {
            // Make an HTTP GET passing the name on the URL line
            List<String> response = new ArrayList<>();
            HttpURLConnection conn;
            int status = 0;

            try {

                if(LoginActivity.role.equalsIgnoreCase("physician")) {

                    System.out.println(datePickStart.getText().toString());
                    System.out.println(datePickEnd.getText().toString());

                    // pass the userid,password
                    URL url = new URL("https://ahncathlabserver.herokuapp.com/MongoDBAdd/?csvString=" + "FetchUser" +","+ LoginActivity.logInEmail +","+datePickStart.getText().toString()+ ","+datePickEnd.getText().toString()+","+ LoginActivity.logInEmail +","+ Build.MODEL +","+ Build.MANUFACTURER +","+ Build.VERSION.RELEASE);
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
                }

                else {

                    URL url = new URL("https://ahncathlabserver.herokuapp.com/MongoDBAdd/?csvString=" + "FetchManager" +","+datePickStart.getText().toString()+","+datePickEnd.getText().toString()+","+ LoginActivity.logInEmail +","+ Build.MODEL +","+ Build.MANUFACTURER +","+ Build.VERSION.RELEASE);
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
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return response;
        }

    }
}
