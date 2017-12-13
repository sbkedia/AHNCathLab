package edu.cmu.ahncathlab;

import android.accounts.AccountManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.Manifest;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
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

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static edu.cmu.ahncathlab.TimeActivity.*;

public class GoogleSheetsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    GoogleAccountCredential mCredential;
    private TextView mOutputText;

    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;

    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String[] SCOPES = { SheetsScopes.SPREADSHEETS_READONLY };
    private final Context context = this;
    private GoogleSheetsActivity googleSheetsActivity;

    String ID = "";
    List<String> sheetValues = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        googleSheetsActivity = this;
        setContentView(R.layout.activity_google_sheets);

        Toolbar toolbar = findViewById(R.id.toolbar3);
        setSupportActionBar(toolbar);

        //Add navigation bar to screen
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout3);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        Bundle bundle = getIntent().getExtras();
        ID = bundle.getString("FileID");

        mOutputText = findViewById(R.id.mOutputText);
        mCredential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff());
        getResultsFromApi();
    }

    private void getValues(List<String> output) {
        sheetValues = output;
    }

    private List<String> computeCost() {

        String user = LoginActivity.logInEmail;

        String startDate = String.valueOf(CostActivity.datePickStart.getText());

        HashMap<String, List<Double>> varSalary = new HashMap<>();
        HashMap<String, List<Double>> varSupply = new HashMap<>();
        HashMap<String, List<Double>> fixedSalary = new HashMap<>();
        HashMap<String, List<Double>> rates = new HashMap<>();

        for (String row: sheetValues) {
            String rowData[] = row.split("#");
            System.out.println("I'm in here." + startDate + " " + rowData[1]);
            if (rowData[1].equals(startDate)) {
                if (!varSalary.containsKey(rowData[0])) {    // If selected date in sheet row
//
                    List<Double> listVarSalary = new ArrayList<>();
                    listVarSalary.add(Double.parseDouble(rowData[2].replace(",","")));
                    varSalary.put(rowData[0], listVarSalary);

                    List<Double> listVarSupply = new ArrayList<>();
                    listVarSupply.add(Double.parseDouble(rowData[3].replace(",","")));
                    varSupply.put(rowData[0], listVarSupply);

                    List<Double> listFixedSalary = new ArrayList<>();
                    listFixedSalary.add(Double.parseDouble(rowData[4].replace(",","")));
                    fixedSalary.put(rowData[0], listFixedSalary);

                    List<Double> listRate = new ArrayList<>();
                    listRate.add(Double.parseDouble(rowData[5].replace(",","")));
                    rates.put(rowData[0], listRate);
                } else {
                    varSalary.get(rowData[0]).add(Double.parseDouble(rowData[2].replace(",","")));
                    varSupply.get(rowData[0]).add(Double.parseDouble(rowData[3].replace(",","")));
                    fixedSalary.get(rowData[0]).add(Double.parseDouble(rowData[4].replace(",","")));
                    rates.get(rowData[0]).add(Double.parseDouble(rowData[5].replace(",","")));
                }
            }
        }

        double totalVarSalary = 0, totalVarSupply = 0, totalFixedSalary = 0, rate = 0;

        List<String> result = new ArrayList<>();
//        result.add("\n");
        int count = 0;

        for (String key: varSalary.keySet()) {
            for (Double cost: varSalary.get(key)) {
                totalVarSalary += cost;
                count++;
            }
            for (Double cost: varSupply.get(key))
                totalVarSupply += cost;
            for (Double cost: fixedSalary.get(key))
                totalFixedSalary += cost;
            for (Double cost: rates.get(key))
                rate = cost;
            result.add(startDate + "," + key + "," + totalVarSalary/count + "," + totalVarSupply/count + "," + totalFixedSalary/count + "," + rate);
            count = 0;
        }

//        mOutputText.setText(TextUtils.join("\n", result));
        return(result);
    }


    /**
     * Attempt to call the API, after verifying that all the preconditions are
     * satisfied. The preconditions are: Google Play Services installed, an
     * account was selected and the device currently has online access. If any
     * of the preconditions are not satisfied, the app will prompt the user as
     * appropriate.
     */
    private void getResultsFromApi() {
        if (! isGooglePlayServicesAvailable()) {
            acquireGooglePlayServices();
        } else if (mCredential.getSelectedAccountName() == null) {
            chooseAccount();
        } else if (! isDeviceOnline()) {
            mOutputText.setText("No network connection available.");
        } else {
            new MakeRequestTask(mCredential).execute();
        }
    }

    /**
     * Attempts to set the account used with the API credentials. If an account
     * name was previously saved it will use that one; otherwise an account
     * picker dialog will be shown to the user. Note that the setting the
     * account to use with the credentials object requires the app to have the
     * GET_ACCOUNTS permission, which is requested here if it is not already
     * present. The AfterPermissionGranted annotation indicates that this
     * function will be rerun automatically whenever the GET_ACCOUNTS permission
     * is granted.
     */
    @AfterPermissionGranted(REQUEST_PERMISSION_GET_ACCOUNTS)
    private void chooseAccount() {
        if (EasyPermissions.hasPermissions(
                this, Manifest.permission.GET_ACCOUNTS)) {
            String accountName = getPreferences(Context.MODE_PRIVATE)
                    .getString(PREF_ACCOUNT_NAME, null);
            if (accountName != null) {
                mCredential.setSelectedAccountName(accountName);
                getResultsFromApi();
            } else {
                // Start a dialog from which the user can choose an account
                startActivityForResult(
                        mCredential.newChooseAccountIntent(),
                        REQUEST_ACCOUNT_PICKER);
            }
        } else {
            // Request the GET_ACCOUNTS permission via a user dialog
            EasyPermissions.requestPermissions(
                    this,
                    "This app needs to access your Google account (via Contacts).",
                    REQUEST_PERMISSION_GET_ACCOUNTS,
                    Manifest.permission.GET_ACCOUNTS);
        }
    }

    /**
     * Called when an activity launched here (specifically, AccountPicker
     * and authorization) exits, giving you the requestCode you started it with,
     * the resultCode it returned, and any additional data from it.
     * @param requestCode code indicating which activity result is incoming.
     * @param resultCode code indicating the result of the incoming
     *     activity result.
     * @param data Intent (containing result data) returned by incoming
     *     activity result.
     */
    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != RESULT_OK) {
                    mOutputText.setText(
                            "This app requires Google Play Services. Please install " +
                                    "Google Play Services on your device and relaunch this app.");
                } else {
                    getResultsFromApi();
                }
                break;
            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null &&
                        data.getExtras() != null) {
                    String accountName =
                            data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        SharedPreferences settings =
                                getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(PREF_ACCOUNT_NAME, accountName);
                        editor.apply();
                        mCredential.setSelectedAccountName(accountName);
                        getResultsFromApi();
                    }
                }
                break;
            case REQUEST_AUTHORIZATION:
                if (resultCode == RESULT_OK) {
                    getResultsFromApi();
                }
                break;
        }
    }

    /**
     * Respond to requests for permissions at runtime for API 23 and above.
     * @param requestCode The request code passed in
     *     requestPermissions(android.app.Activity, String, int, String[])
     * @param permissions The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *     which is either PERMISSION_GRANTED or PERMISSION_DENIED. Never null.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(
                requestCode, permissions, grantResults, this);
    }

    /**
     * Checks whether the device currently has a network connection.
     * @return true if the device has a network connection, false otherwise.
     */
    private boolean isDeviceOnline() {
        ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    /**
     * Check that Google Play services APK is installed and up to date.
     * @return true if Google Play Services is available and up to
     *     date on this device; false otherwise.
     */
    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }

    /**
     * Attempt to resolve a missing, out-of-date, invalid or disabled Google
     * Play Services installation via a user dialog, if possible.
     */
    private void acquireGooglePlayServices() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
        }
    }


    /**
     * Display an error dialog showing that Google Play Services is missing
     * or out of date.
     * @param connectionStatusCode code describing the presence (or lack of)
     *     Google Play Services on this device.
     */
    void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = apiAvailability.getErrorDialog(
                GoogleSheetsActivity.this,
                connectionStatusCode,
                REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }

    /**
     * An asynchronous task that handles the Google Sheets API call.
     * Placing the API calls in their own task ensures the UI stays responsive.
     */
    private class MakeRequestTask extends AsyncTask<Void, Void, List<String>> {
        private com.google.api.services.sheets.v4.Sheets mService = null;
        private Exception mLastError = null;
        TableLayout stk = (TableLayout) findViewById(R.id.costTable);

        MakeRequestTask(GoogleAccountCredential credential) {
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mService = new com.google.api.services.sheets.v4.Sheets.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName("Google Sheets API Android Quickstart")
                    .build();
        }

        /**
         * Background task to call Google Sheets API.
         * @param params no parameters needed for this task.
         */
        @Override
        protected List<String> doInBackground(Void... params) {
            try {
                return getDataFromApi();
            } catch (Exception e) {
                mLastError = e;
                cancel(true);
                return null;
            }
        }

        /**
         * Fetch a list of names and majors of students in a sample spreadsheet:
         * https://docs.google.com/spreadsheets/d/1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms/edit
         * @return List of names and majors
         * @throws IOException
         */
        private List<String> getDataFromApi() throws IOException {
            System.out.println("Here in sheets.");
            String spreadsheetId = ID;
            String range = "Sheet2!A1:AC";
            List<String> results = new ArrayList<>();
            ValueRange response = this.mService.spreadsheets().values()
                    .get(spreadsheetId, range)
                    .execute();
            List<List<Object>> values = response.getValues();
            int i = 0;
            if (values != null) {
                for (List row: values) {
                    // email, start (admission) date, DV Salary, DV Supply, DF Salary, rate
                    results.add(row.get(7) + "#" + row.get(3) + "#"
                            + row.get(15) + "#" + row.get(16) + "#" + row.get(18) + "#"
                            + row.get(28));
                }
            }

            System.out.println("Results" + results);
            return results;
        }

        //Create table view
        public void createTimeTable(List<String> output) {
            List<String> response = new ArrayList<>();

            System.out.println("In create time table");
            String[] val = new String[6];
            System.out.println("In create time table222" + output);
            for (String row : output) {
                System.out.println("In for loop");
                val = row.split(",");
                System.out.println("Values:" + val[0]);
            }
            System.out.print("Valuesssss" + val[0]);
            response = doGet(val[0]);

            TableRow tbrow0 = new TableRow(googleSheetsActivity);
            TextView tv0 = new TextView(googleSheetsActivity);
            tv0.setText(" PHYSICIAN ");
            tv0.setTextColor(Color.GREEN);
            tv0.setGravity(Gravity.CENTER);
            tbrow0.addView(tv0);
            TextView tv1 = new TextView(googleSheetsActivity);
            tv1.setText(" COST ");
            tv1.setTextColor(Color.GREEN);
            tv1.setGravity(Gravity.CENTER);
            tbrow0.addView(tv1);
            TextView tv2 = new TextView(googleSheetsActivity);
            tv2.setText(" AVG VAR SALARY ");
            tv2.setTextColor(Color.GREEN);
            tv2.setGravity(Gravity.CENTER);
            tbrow0.addView(tv2);
            TextView tv3 = new TextView(googleSheetsActivity);
            tv3.setText(" AVG VAR SUPPLY ");
            tv3.setTextColor(Color.GREEN);
            tv3.setGravity(Gravity.CENTER);
            tbrow0.addView(tv3);
            TextView tv4 = new TextView(googleSheetsActivity);
            tv4.setText(" AVG FIXED SALARY ");
            tv4.setTextColor(Color.GREEN);
            tv4.setGravity(Gravity.CENTER);
            tbrow0.addView(tv4);
            stk.addView(tbrow0);

            String prevDate = "";
            String currentDate = "";
            long totalTime = 0;

            String userId = "";

            String[] costRecords = new String[6];
            //Display user-wise time
            for (String row : output) {
                costRecords = row.split(",");
                userId = costRecords[1];
//                System.out.println("Userrrrrr" + userId);

                try {
                    SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
                    Date dateIn = format.parse("00:00:00");
                    Date dateOut = format.parse("00:00:00");

                    for (int i = 0; i < response.size(); i = i + 4) {
                        System.out.println("User: " + response);
                        if (response.get(i).equalsIgnoreCase(userId)) {
                            System.out.println("User: " + userId);

                            //Calculate total time
                            if (response.get(i + 3).equalsIgnoreCase("in")) {
                                dateIn = format.parse(response.get(i + 2));
                            } else {
                                dateOut = format.parse(response.get(i + 2));
                                totalTime = totalTime + (dateOut.getTime() - dateIn.getTime());
                            }
                        }
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                // Add total row
                    TableRow tbrow = new TableRow(googleSheetsActivity);
                    TextView t0v = new TextView(googleSheetsActivity);
                    t0v.setText(" $" + costRecords[1] + " ");
                    t0v.setTextColor(Color.WHITE);
                    t0v.setGravity(Gravity.CENTER);
                    tbrow.addView(t0v);

                    TextView t1v = new TextView(googleSheetsActivity);
                    t1v.setText(" " + String.format("$%.2f", ((TimeUnit.MILLISECONDS.toMinutes(totalTime) * Double.parseDouble(costRecords[5]))/60)) + " ");
                    t1v.setTextColor(Color.WHITE);
                    t1v.setGravity(Gravity.CENTER);
                    tbrow.addView(t1v);

                    TextView t2v = new TextView(googleSheetsActivity);
                    t2v.setText(" $" + costRecords[2] + " ");
                    t2v.setTextColor(Color.WHITE);
                    t2v.setGravity(Gravity.CENTER);
                    tbrow.addView(t2v);

                    TextView t3v = new TextView(googleSheetsActivity);
                    t3v.setText(" $" + costRecords[3] + " ");
                    t3v.setTextColor(Color.WHITE);
                    t3v.setGravity(Gravity.CENTER);
                    tbrow.addView(t3v);

                    TextView t4v = new TextView(googleSheetsActivity);
                    t4v.setText(" $" + costRecords[4] + " ");
                    t4v.setTextColor(Color.WHITE);
                    t4v.setGravity(Gravity.CENTER);
                    tbrow.addView(t4v);

                    stk.addView(tbrow);
            }
        }

        public List<String> doGet(String dateStart) {
            // Make an HTTP GET passing the name on the URL line
            List<String> response = new ArrayList<>();
            HttpURLConnection conn;
            int status = 0;

            try {

                SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
                Date date1 = formatter.parse(dateStart);
                Calendar c = Calendar.getInstance();
                c.setTime(date1);
                c.add(Calendar.DATE, 1);
                String dateEnd = formatter.format(c.getTime());

                URL url = new URL("https://ahncathlabserver.herokuapp.com/MongoDBAdd/?csvString=" + "FetchManager" +","+dateStart+","+dateEnd+","+ LoginActivity.logInEmail +","+ Build.MODEL +","+ Build.MANUFACTURER +","+ Build.VERSION.RELEASE);
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
            } catch (ParseException e) {
                e.printStackTrace();
            }

            return response;
        }

        @Override
        protected void onPreExecute() {
            mOutputText.setText("");
        }

        @Override
        protected void onPostExecute(List<String> output) {
            if (output == null || output.size() == 0) {
                mOutputText.setText("No results returned.");
            } else {
                getValues(output);
                output = computeCost();
                createTimeTable(output);
            }
        }

        @Override
        protected void onCancelled() {
            if (mLastError != null) {
                if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                    showGooglePlayServicesAvailabilityErrorDialog(
                            ((GooglePlayServicesAvailabilityIOException) mLastError)
                                    .getConnectionStatusCode());
                } else if (mLastError instanceof UserRecoverableAuthIOException) {
                    startActivityForResult(
                            ((UserRecoverableAuthIOException) mLastError).getIntent(),
                            GoogleSheetsActivity.REQUEST_AUTHORIZATION);
                } else {
                    mOutputText.setText("The following error occurred:\n"
                            + mLastError.getMessage());
                }
            } else {
                mOutputText.setText("Request cancelled.");
            }
        }
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout3);
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
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view3);
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

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout3);
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
}

// Citation - https://developers.google.com/sheets/api/quickstart/android (Google Sheets API | Google Developers)