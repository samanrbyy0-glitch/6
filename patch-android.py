import os, shutil, re

pkg_dir = "android/app/src/main/java/com/sami/dailyapp"
os.makedirs(pkg_dir, exist_ok=True)
for f in os.listdir("native"):
    if f.endswith(".java"):
        shutil.copy(os.path.join("native", f), os.path.join(pkg_dir, f))

mp = "android/app/src/main/AndroidManifest.xml"
s = open(mp, encoding="utf8").read()

perms = ["USE_FULL_SCREEN_INTENT", "FOREGROUND_SERVICE", "FOREGROUND_SERVICE_MEDIA_PLAYBACK",
         "WAKE_LOCK", "VIBRATE", "RECEIVE_BOOT_COMPLETED", "SCHEDULE_EXACT_ALARM",
         "USE_EXACT_ALARM", "POST_NOTIFICATIONS"]
add = ""
for p in perms:
    line = '<uses-permission android:name="android.permission.%s" />' % p
    if line not in s and ('android.permission.%s"' % p) not in s:
        add += "    " + line + "\n"
s = s.replace("<application", add + "    <application", 1)

comp = '''
        <receiver android:name="com.sami.dailyapp.AlarmReceiver" android:exported="false" />
        <receiver android:name="com.sami.dailyapp.BootReceiver" android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.BOOT_COMPLETED" />
                <action android:name="android.intent.action.MY_PACKAGE_REPLACED" />
            </intent-filter>
        </receiver>
        <service android:name="com.sami.dailyapp.AlarmService" android:exported="false" android:foregroundServiceType="mediaPlayback" />
        <activity android:name="com.sami.dailyapp.AlarmActivity" android:exported="false" android:showWhenLocked="true" android:turnScreenOn="true" android:excludeFromRecents="true" android:launchMode="singleInstance" android:theme="@android:style/Theme.DeviceDefault.NoActionBar" />
'''
if "AlarmReceiver" not in s:
    s = s.replace("</application>", comp + "    </application>", 1)
open(mp, "w", encoding="utf8").write(s)
print("patched manifest + java files")
