package com.sami.dailyapp;

import android.os.Bundle;
import com.getcapacitor.BridgeActivity;

public class MainActivity extends BridgeActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        registerPlugin(DailyAlarmPlugin.class);
        super.onCreate(savedInstanceState);
    }
}
