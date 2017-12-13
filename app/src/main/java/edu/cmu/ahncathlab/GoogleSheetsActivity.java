package edu.cmu.ahncathlab;

import android.accounts.AccountManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.Manifest;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
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

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

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

    String ID = "";
    List<String> sheetValues = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

    private void computeCost() {

        String startDate = String.valueOf(CostActivity.datePickStart.getText());
        String endDate = String.valueOf(CostActivity.datePickEnd.getText());

        HashMap<String, List<Double>> charges = new HashMap<>();
        HashMap<String, List<Double>> netRevEstimate = new HashMap<>();
        HashMap<String, List<Double>> varSalary = new HashMap<>();
        HashMap<String, List<Double>> varSupply = new HashMap<>();
        HashMap<String, List<Double>> varOther = new HashMap<>();
        HashMap<String, List<Double>> fixedSalary = new HashMap<>();
        HashMap<String, List<Double>> fixedOther = new HashMap<>();
        HashMap<String, List<Double>> allocationsCost = new HashMap<>();
        HashMap<String, List<Double>> interestDep = new HashMap<>();
        HashMap<String, List<Double>> hospitalOverhead = new HashMap<>();
        HashMap<String, List<Double>> fixedIndirectAcademic = new HashMap<>();
        HashMap<String, List<Double>> corporateOverhead = new HashMap<>();
        HashMap<String, List<Double>> dayTotal = new HashMap<>();
        HashMap<String, List<String>> physicianName = new HashMap<>();

        double totalCharges = 0, totalRevEstimate = 0, totalVarSalary = 0, totalVarSupply = 0, totalVarOther = 0,
                totalFixedSalary = 0, totalFixedOther = 0, totalAllocationsCost = 0, totalInterestDep = 0, totalHospitalOverhead = 0,
                totalFixedIndirectAcad = 0, totalCorporateOverhead = 0;

        for (String row: sheetValues) {
            if (row.contains("01/01/16")) {
                String rowData[] = row.split(",");
                if (!charges.containsKey(rowData[2])) {    // If selected date in sheet row
                    List<Double> listCharges = new ArrayList<>();
                    listCharges.add(Double.parseDouble(rowData[4]));
                    charges.put(rowData[2], listCharges);

                    List<Double> listNet = new ArrayList<>();
                    listNet.add(Double.parseDouble(rowData[5]));
                    netRevEstimate.put(rowData[2], listNet);

                    List<Double> listVarSalary = new ArrayList<>();
                    listVarSalary.add(Double.parseDouble(rowData[6]));
                    varSalary.put(rowData[2], listVarSalary);

                    List<Double> listVarSupply = new ArrayList<>();
                    listVarSupply.add(Double.parseDouble(rowData[7]));
                    varSupply.put(rowData[2], listVarSupply);

                    List<Double> listOther = new ArrayList<>();
                    listOther.add(Double.parseDouble(rowData[8]));
                    varOther.put(rowData[2], listOther);

                    List<Double> listFixedSalary = new ArrayList<>();
                    listFixedSalary.add(Double.parseDouble(rowData[9]));
                    fixedSalary.put(rowData[2], listFixedSalary);

                    List<Double> listFixedOther = new ArrayList<>();
                    listFixedOther.add(Double.parseDouble(rowData[10]));
                    fixedOther.put(rowData[2], listFixedOther);

                    List<Double> listAllocationsCost = new ArrayList<>();
                    listAllocationsCost.add(Double.parseDouble(rowData[11]));
                    allocationsCost.put(rowData[2], listAllocationsCost);

                    List<Double> listInterestDep = new ArrayList<>();
                    listInterestDep.add(Double.parseDouble(rowData[12]));
                    interestDep.put(rowData[2], listInterestDep);

                    List<Double> listHospitalOverhead = new ArrayList<>();
                    listHospitalOverhead.add(Double.parseDouble(rowData[13]));
                    hospitalOverhead.put(rowData[2], listHospitalOverhead);

                    List<Double> listFixedIndirectAcad = new ArrayList<>();
                    listFixedIndirectAcad.add(Double.parseDouble(rowData[14]));
                    fixedIndirectAcademic.put(rowData[2], listFixedIndirectAcad);

                    List<Double> listCorporateOverhead = new ArrayList<>();
                    listCorporateOverhead.add(Double.parseDouble(rowData[15]));
                    corporateOverhead.put(rowData[2], listCorporateOverhead);

                    List<String> listNames = new ArrayList<>();
                    listNames.add(rowData[0]);
                    physicianName.put(rowData[2], listNames);
                }
                else {
                    charges.get(rowData[2]).add(Double.parseDouble(rowData[4]));
                    netRevEstimate.get(rowData[2]).add(Double.parseDouble(rowData[5]));
                    varSalary.get(rowData[2]).add(Double.parseDouble(rowData[6]));
                    varSupply.get(rowData[2]).add(Double.parseDouble(rowData[7]));
                    varOther.get(rowData[2]).add(Double.parseDouble(rowData[8]));
                    fixedSalary.get(rowData[2]).add(Double.parseDouble(rowData[9]));
                    fixedOther.get(rowData[2]).add(Double.parseDouble(rowData[10]));
                    allocationsCost.get(rowData[2]).add(Double.parseDouble(rowData[11]));
                    interestDep.get(rowData[2]).add(Double.parseDouble(rowData[12]));
                    hospitalOverhead.get(rowData[2]).add(Double.parseDouble(rowData[13]));
                    fixedIndirectAcademic.get(rowData[2]).add(Double.parseDouble(rowData[14]));
                    corporateOverhead.get(rowData[2]).add(Double.parseDouble(rowData[15]));
                    physicianName.get(rowData[2]).add(rowData[0]);
                }
            }
        }

        List<String> result = new ArrayList<>();
        result.add("\n");

        for (String key: charges.keySet()) {
            result.add("Date: " + key);
            for (Double cost: charges.get(key))
                totalCharges += cost;
            for (Double cost: netRevEstimate.get(key))
                totalRevEstimate += cost;
            for (Double cost: varSalary.get(key))
                totalVarSalary += cost;
            for (Double cost: varSupply.get(key))
                totalVarSupply += cost;
            for (Double cost: varOther.get(key))
                totalVarOther += cost;
            for (Double cost: fixedSalary.get(key))
                totalFixedSalary += cost;
            for (Double cost: fixedOther.get(key))
                totalFixedOther += cost;
            for (Double cost: allocationsCost.get(key))
                totalAllocationsCost += cost;
            for (Double cost: interestDep.get(key))
                totalInterestDep += cost;
            for (Double cost: hospitalOverhead.get(key))
                totalHospitalOverhead += cost;
            for (Double cost: fixedIndirectAcademic.get(key))
                totalFixedIndirectAcad += cost;
            for (Double cost: corporateOverhead.get(key))
                totalCorporateOverhead += cost;
            result.add("Total charges: " + totalCharges);
            result.add("Total net revenue estimate: " + totalRevEstimate);
            result.add("Total variable salary: " + totalVarSalary+"\n");
//            patientTotals.put(key, Arrays.asList(totalIndirect, totalDirect, totalMaterial));
        }

        System.out.println("Total charges:");
        System.out.println(totalCharges + "\n");

        mOutputText.setText(TextUtils.join("\n", result));
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
            String spreadsheetId = ID;
            String range = "Sheet1!A1:AB";
            List<String> results = new ArrayList<String>();
            ValueRange response = this.mService.spreadsheets().values()
                    .get(spreadsheetId, range)
                    .execute();
            List<List<Object>> values = response.getValues();
            if (values != null) {
                for (List row : values) {
                    results.add(row.get(7) + "," + row.get(1) + ","
                            + row.get(26) + "," + row.get(9) + "," + row.get(13) + ","
                            + row.get(14) + "," + row.get(15) + "," + row.get(16) + ","
                            + row.get(17) + "," + row.get(18) + "," + row.get(19) + ","
                            + row.get(20) + "," + row.get(21) + "," + row.get(22)
                            + "," + row.get(23) + "," + row.get(24));
                }
            }
            return results;
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
                computeCost();
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