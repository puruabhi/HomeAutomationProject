package com.example.android.bluetoothapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    TextView homeAutomationTextView;
    Button getStartedButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        homeAutomationTextView = (TextView)findViewById(R.id.homeAutomationTextView);
        getStartedButton = (Button)findViewById(R.id.getStartedButton);
        getStartedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                homeAutomationTextView.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),android.R.anim.fade_in));
                Intent intent = new Intent(getApplicationContext(),ApplianceController.class);
                intent.putExtra("btStatus","0");
                startActivity(intent);
            }
        });
    }

}
