package com.sami.dailyapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.Calendar;

public class AlarmStore {
    static final String PREFS = "daily_alarm";
    static final String KEY = "alarms";

    static void save(Context c, String json) {
        c.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putString(KEY, json).apply();
    }

    static String load(Context c) {
        return c.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString(KEY, "[]");
    }

    static PendingIntent pi(Context c, int id, String title, String body) {
        Intent i = new Intent(c, AlarmReceiver.class);
        i.setAction("com.sami.dailyapp.ALARM");
        i.putExtra("id", id);
        i.putExtra("title", title);
        i.putExtra("body", body);
        int flags = PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE;
        return PendingIntent.getBroadcast(c, id, i, flags);
    }

    static void cancelAll(Context c) {
        try {
            AlarmManager am = (AlarmManager) c.getSystemService(Context.ALARM_SERVICE);
            JSONArray arr = new JSONArray(load(c));
            for (int k = 0; k < arr.length(); k++) {
                JSONObject a = arr.getJSONObject(k);
                am.cancel(pi(c, a.optInt("id"), "", ""));
            }
        } catch (Exception e) {
            // ignore
        }
    }

    static boolean contains(JSONArray arr, int v) {
        for (int k = 0; k < arr.length(); k++) {
            if (arr.optInt(k) == v) return true;
        }
        return false;
    }

    static long nextTime(JSONObject a) {
        long at = a.optLong("at", 0);
        long now = System.currentTimeMillis();
        if (at > 0) return at > now ? at : -1;
        int h = a.optInt("hour");
        int m = a.optInt("minute");
        JSONArray wd = a.optJSONArray("weekdays");
        for (int d = 0; d < 8; d++) {
            Calendar c = Calendar.getInstance();
            c.add(Calendar.DAY_OF_YEAR, d);
            c.set(Calendar.HOUR_OF_DAY, h);
            c.set(Calendar.MINUTE, m);
            c.set(Calendar.SECOND, 0);
            c.set(Calendar.MILLISECOND, 0);
            if (c.getTimeInMillis() <= now + 1000) continue;
            if (wd != null && wd.length() > 0 && !contains(wd, c.get(Calendar.DAY_OF_WEEK))) continue;
            return c.getTimeInMillis();
        }
        return -1;
    }

    static void scheduleOnce(Context c, int id, String title, String body, long when) {
        AlarmManager am = (AlarmManager) c.getSystemService(Context.ALARM_SERVICE);
        PendingIntent p = pi(c, id, title, body);
        Intent show = new Intent(c, MainActivity.class);
        PendingIntent s = PendingIntent.getActivity(c, 0, show,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        try {
            am.setAlarmClock(new AlarmManager.AlarmClockInfo(when, s), p);
        } catch (SecurityException e) {
            am.set(AlarmManager.RTC_WAKEUP, when, p);
        }
    }

    static void rescheduleAll(Context c) {
        try {
            JSONArray arr = new JSONArray(load(c));
            for (int k = 0; k < arr.length(); k++) {
                JSONObject a = arr.getJSONObject(k);
                long t = nextTime(a);
                if (t < 0) continue;
                scheduleOnce(c, a.optInt("id"), a.optString("title"), a.optString("body"), t);
            }
        } catch (Exception e) {
            // ignore
        }
    }
}
