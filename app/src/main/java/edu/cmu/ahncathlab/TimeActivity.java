package edu.cmu.ahncathlab;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class TimeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time);

        Button mDashboardButton = (Button) findViewById(R.id.homeButton);
        mDashboardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToDashboard();
            }
        });

        Button mCostButton = (Button) findViewById(R.id.costButton);
        mCostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToCostScreen();
            }
        });
    }

    private void goToDashboard() {
        final Context context = this;
        Intent intent = new Intent(context, MenuActivity.class);
        startActivity(intent);
    }

    private void goToCostScreen() {
        final Context context = this;
        Intent intent = new Intent(context, CostActivity.class);
        startActivity(intent);
    }
}
