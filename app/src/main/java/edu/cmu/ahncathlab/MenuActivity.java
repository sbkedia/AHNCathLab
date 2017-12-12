package edu.cmu.ahncathlab;

import android.bluetooth.BluetoothDevice;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;


public class MenuActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Chronometer mChronometer;
    Button start, stop, restart;
    final Context context = this;
    private BleWrapper mBleWrapper = null;
    private TextView bleIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Add navigation bar to screen
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        setBleText();

//        mChronometer = (Chronometer) findViewById(R.id.timerCh);
//        start = (Button) findViewById(R.id.start_button);
//        restart = (Button) findViewById(R.id.restart_button);
//
//        restart.setEnabled(false);

//        start.setOnClickListener(new View.OnClickListener() {
//            long timeStopped;
////            boolean isStarted = false;
//            @Override
//            public void onClick(View view) {
//                String email = LoginActivity.logInEmail;
//                DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
//                DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
//                Date date = new Date();
//                String todayDate = dateFormat.format(date);
//                String  todayTime = timeFormat.format(date);
//
//                if(!restart.isEnabled()){
//                    timeStopped = 0;
//                    mChronometer.setBase(SystemClock.elapsedRealtime());
//                    mChronometer.start();
//                    start.setText("Pause");
//                    restart.setEnabled(true);
//
//                    //Add 'in' time in track_info table
//                    String csvString = "AddTrack" +","+ email +","+ todayDate +","+ todayTime +","+ "In";
//                    //Add logging variables
//                    System.out.println(Build.MODEL + ", " + Build.MANUFACTURER + ", " + Build.VERSION.RELEASE + ", ");
//                    csvString = csvString +","+ LoginActivity.logInEmail +","+ Build.MODEL +","+ Build.MANUFACTURER +","+ Build.VERSION.RELEASE;
//                    new ExecuteTask().execute(csvString);
//
//                }
//                else{
//                   if(start.getText().equals("Pause")){
//                      timeStopped = mChronometer.getBase() - SystemClock.elapsedRealtime();
//                      mChronometer.stop();
//                      start.setText("Start");
//
//                      //Add 'out' time in track_info table
//                       String csvString = "AddTrack" +","+ email +","+ todayDate +","+ todayTime +","+ "Out";
//                       csvString = csvString +","+ LoginActivity.logInEmail +","+ Build.MODEL +","+ Build.MANUFACTURER +","+ Build.VERSION.RELEASE;
//                    new ExecuteTask().execute(csvString);
//                   }
//                   else{
//                       mChronometer.setBase(SystemClock.elapsedRealtime() + timeStopped);
//                       mChronometer.start();
//                       start.setText("Pause");
//
//                       //Add 'in' time in track_info table
//                       String csvString = "AddTrack" +","+ email +","+ todayDate +","+ todayTime +","+ "In";
//                       csvString = csvString +","+ LoginActivity.logInEmail +","+ Build.MODEL +","+ Build.MANUFACTURER +","+ Build.VERSION.RELEASE;
//                       new ExecuteTask().execute(csvString);
//
//                   }
//                }
//            }
//        });


//        restart.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                restart.setEnabled(false);
//                mChronometer.setBase(SystemClock.elapsedRealtime());
//            }
//        });
    }

    public void setBleText(){

        mBleWrapper = new BleWrapper(this, new BleWrapperUiCallbacks.Null());
        boolean bleOn = mBleWrapper.isBtEnabled();
        System.out.println("BLEEEEEEEEE: " + bleOn);
        bleIndicator = findViewById(R.id.bleIndicator);
        if(bleOn){
            bleIndicator.setText("Your Bluetooth is on");
            bleIndicator.setBackgroundColor(Color.GREEN);
        }
        else{
            bleIndicator.setText("Your Bluetooth is off! Please turn it on to correctly use this app.");
            bleIndicator.setBackgroundColor(Color.RED);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
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
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
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
            final Context context = this;
            Intent intent = new Intent(context, CostActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_time) {
            final Context context = this;
            Intent intent = new Intent(context, TimeActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_home) {
            Intent intent = new Intent(context, MenuActivity.class);
            startActivity(intent);
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
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


//    class ExecuteTask extends AsyncTask<String, Integer, String> {
//
//        @Override
//        protected String doInBackground(String... params) {
//            System.out.println("In doInBackground");
//            try {
//                PostData(params);
//            } catch (UnsupportedEncodingException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(String s) {
////            mprogressBar1.setVisibility(View.GONE);
//        }
//
//    }

//    public void PostData(String[] values) throws IOException {
//        int status = 0;
//        String output;
//
//        try {
//            System.out.println("In post");
//            // Make call to a particular URL
//            URL url = new URL("https://ahncathlabserver.herokuapp.com/MongoDBAdd/");
//            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//
//            // set request method to POST and send name value pair
//            conn.setRequestMethod("POST");
//            conn.setDoOutput(true);
//            // write to POST data area
//            OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
//            out.write(values[0]);
//            out.close();
//
//            // get HTTP response code sent by server
//            status = conn.getResponseCode();
//
//            //close the connection
//            conn.disconnect();
//        } // handle exceptions
//        catch (MalformedURLException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//    }
}
