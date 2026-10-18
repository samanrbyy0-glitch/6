package com.sami.dailyapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextClock;
import android.widget.TextView;

public class AlarmActivity extends Activity {

    private String title = "";
    private String body = "";

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        if (Build.VERSION.SDK_INT >= 27) {
            setShowWhenLocked(true);
            setTurnScreenOn(true);
        } else {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                    | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                    | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        }
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        Intent in = getIntent();
        String t = in.getStringExtra("title");
        String bd = in.getStringExtra("body");
        if (t != null) title = t;
        if (bd != null) body = bd;

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER);
        root.setBackgroundColor(Color.parseColor("#0f141c"));
        int p = dp(24);
        root.setPadding(p, p, p, p);

        TextClock clock = new TextClock(this);
        clock.setFormat24Hour("HH:mm");
        clock.setFormat12Hour("HH:mm");
        clock.setTextSize(72);
        clock.setTextColor(Color.WHITE);
        clock.setGravity(Gravity.CENTER);
        root.addView(clock);

        TextView tt = new TextView(this);
        tt.setText(title);
        tt.setTextSize(26);
        tt.setTextColor(Color.parseColor("#7b93ff"));
        tt.setGravity(Gravity.CENTER);
        tt.setPadding(0, dp(24), 0, dp(8));
        root.addView(tt);

        TextView bt = new TextView(this);
        bt.setText(body);
        bt.setTextSize(18);
        bt.setTextColor(Color.parseColor("#8d98aa"));
        bt.setGravity(Gravity.CENTER);
        bt.setPadding(0, 0, 0, dp(40));
        root.addView(bt);

        Button stop = new Button(this);
        stop.setText("توقف");
        stop.setTextSize(22);
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopAlarm();
                finish();
            }
        });
        root.addView(stop, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp(72)));

        Button snooze = new Button(this);
        snooze.setText("۵ دقیقه بعد");
        snooze.setTextSize(18);
        snooze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlarmStore.scheduleOnce(AlarmActivity.this, 8888, title, body,
                        System.currentTimeMillis() + 5 * 60 * 1000L);
                stopAlarm();
                finish();
            }
        });
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp(60));
        lp.topMargin = dp(16);
        root.addView(snooze, lp);

        setContentView(root);
    }

    private void stopAlarm() {
        Intent s = new Intent(this, AlarmService.class);
        s.setAction("STOP");
        startService(s);
    }

    private int dp(int v) {
        return (int) (v * getResources().getDisplayMetrics().density);
    }
}
