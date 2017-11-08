package edu.cmu.ahncathlab;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class CostActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cost);

        Button mDashboardButton = (Button) findViewById(R.id.homeButton);
        mDashboardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToDashboard();
            }
        });

        Button mTimeButton = (Button) findViewById(R.id.timeButton);
        mTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToTimeScreen();
            }
        });
    }

    private void goToDashboard() {
        final Context context = this;
        Intent intent = new Intent(context, MenuActivity.class);
        startActivity(intent);
    }

    private void goToTimeScreen() {
        final Context context = this;
        Intent intent = new Intent(context, TimeActivity.class);
        startActivity(intent);
    }
}
