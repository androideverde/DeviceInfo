package com.androideverde.deviceinfo;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Point;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;

import java.util.ArrayList;



public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private RecyclerAdapter mAdapter;
    private ArrayList<RecyclerItem> myDataSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler);
        // content will not change size of rows, so we can optimise
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        myDataSet = new ArrayList<>();
        //loadMockData();
        loadData();
        //TODO: add ImageView to Adapter, ViewHolder and data Model
        //TODO: add custom icon for ImageView depending on item

        mAdapter = new RecyclerAdapter(myDataSet);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void loadData() {
        //Summary with progress bars
        //RAM, Internal storage, [External storage], CPU load, Battery
        //Export report: text dump of all data except summary

        loadDeviceData();
        loadSystemData();
        loadCPUData();
        loadDisplayData();
        loadBatteryData();
        loadMemoryData();
        loadStorageData();
        loadSensorsData();
    }

    private void loadDeviceData() {
        //TODO: add IP address IPv4, IPv6
        //TODO: add wifi MAC address
        //TODO: add bluetooth MAC address
        myDataSet.add(new RecyclerItem("Device", Build.DEVICE));
        myDataSet.add(new RecyclerItem("Manufacturer", Build.MANUFACTURER));
        myDataSet.add(new RecyclerItem("Model", Build.MODEL));
        myDataSet.add(new RecyclerItem("Brand", Build.BRAND));
        myDataSet.add(new RecyclerItem("Product", Build.PRODUCT));
        myDataSet.add(new RecyclerItem("Board", Build.BOARD));
        myDataSet.add(new RecyclerItem("Hardware", Build.HARDWARE));
        myDataSet.add(new RecyclerItem("Serial", Build.SERIAL));
    }


    private void loadSystemData() {
        //TODO: add android runtime
        //TODO: add system uptime
        myDataSet.add(new RecyclerItem("OS Version", Build.VERSION.RELEASE + " (API level " + Build.VERSION.SDK_INT + ")"));
        myDataSet.add(new RecyclerItem("Codename", Build.VERSION.CODENAME));
        myDataSet.add(new RecyclerItem("Bootloader", Build.BOOTLOADER));

        String radio = Build.RADIO;
        if (Build.VERSION.SDK_INT >= 14) {
            radio = Build.getRadioVersion();
        }
        myDataSet.add(new RecyclerItem("Radio", radio));

        myDataSet.add(new RecyclerItem("Base OS", (Build.VERSION.SDK_INT >= 23) ? Build.VERSION.BASE_OS : "N/A"));
        myDataSet.add(new RecyclerItem("Fingerprint", Build.FINGERPRINT));
        myDataSet.add(new RecyclerItem("Display ID", Build.DISPLAY));
        myDataSet.add(new RecyclerItem("Build ID", Build.ID));
        myDataSet.add(new RecyclerItem("Kernel", System.getProperty("os.name") + "-" + System.getProperty("os.arch") + "-" + System.getProperty("os.version")));
    }

    private void loadCPUData() {
        //TODO: add CPU hardware
        //TODO: add CPU amount of cores
        //TODO: add CPU clock speed
        //TODO: add instant clock speed for each core
        //TODO: add CPU load %
    }

    private void loadDisplayData() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        Point point = new Point();
        if (Build.VERSION.SDK_INT >= 17) {
            getWindowManager().getDefaultDisplay().getRealSize(point);
        } else {
            point.set(0, 0);
        }
        double diagonal = Math.sqrt(point.x * point.x + point.y * point.y) / metrics.densityDpi;
        String density = "N/A";
        if (metrics.densityDpi < DisplayMetrics.DENSITY_LOW) {
            density = "N/A";
        } else if (metrics.densityDpi < DisplayMetrics.DENSITY_MEDIUM) {
            density = "ldpi";
        } else if (metrics.densityDpi < DisplayMetrics.DENSITY_HIGH) {
            density = "mdpi";
        } else if (metrics.densityDpi < DisplayMetrics.DENSITY_XHIGH) {
            density = "hdpi";
        } else if (metrics.densityDpi < DisplayMetrics.DENSITY_XXHIGH) {
            density = "xhdpi";
        } else if (metrics.densityDpi < DisplayMetrics.DENSITY_XXXHIGH) {
            density = "xxhdpi";
        } else if (metrics.densityDpi >= DisplayMetrics.DENSITY_XXXHIGH) {
            density = "xxxhdpi";
        } else {
            density = "N/A";
        }
        myDataSet.add(new RecyclerItem("Display size", metrics.heightPixels + " x " + metrics.widthPixels + " pixels (" + String.format("%.2f", diagonal) + " inches)"));
        myDataSet.add(new RecyclerItem("Display density", metrics.densityDpi + " dpi (" + density + ")"));
        myDataSet.add(new RecyclerItem("Refresh rate", String.format("%.0f", getWindowManager().getDefaultDisplay().getRefreshRate()) + " Hz"));
    }

    private void loadBatteryData() {
        //getBatteryCharge();
        //TODO: use a BroadcastReceiver for getting BatteryStatus updates
        Intent intent = this.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        int batteryCharge = level / scale * 100;
        myDataSet.add(new RecyclerItem("Battery level", batteryCharge + "%")); //FIXME: not working, always reports 0

        int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL;
        myDataSet.add(new RecyclerItem("Battery status", (isCharging ? "charging" : "discharging")));

        int plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        String plug;
        if (plugged == BatteryManager.BATTERY_PLUGGED_AC) {
            plug = "AC";
        } else if (plugged == BatteryManager.BATTERY_PLUGGED_USB) {
            plug = "USB";
        } else if (plugged == BatteryManager.BATTERY_PLUGGED_WIRELESS) {
            plug = "Wireless";
        } else {
            plug = "Unknown";
        }
        myDataSet.add(new RecyclerItem("Charging through", plug));

        int health = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, -1);
        String healthState;
        if (health == BatteryManager.BATTERY_HEALTH_GOOD) {
            healthState = "Good";
        } else if (health == BatteryManager.BATTERY_HEALTH_COLD) {
            healthState = "Cold";
        } else if (health == BatteryManager.BATTERY_HEALTH_DEAD) {
            healthState = "Dead";
        } else if (health == BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE) {
            healthState = "Over voltage";
        } else if (health == BatteryManager.BATTERY_HEALTH_OVERHEAT) {
            healthState = "Overheating";
        } else if (health == BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE) {
            healthState = "Unspecified failure";
        } else {
            healthState = "Unknown";
        }
        myDataSet.add(new RecyclerItem("Battery health", healthState));

        int temp = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1);
        float realTemp = temp / 10.f;
        myDataSet.add(new RecyclerItem("Battery temperature", realTemp + " C"));

        int voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1);
        myDataSet.add(new RecyclerItem("Battery voltage", voltage + " mV"));

        String tech = intent.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY);
        myDataSet.add(new RecyclerItem("Battery technology", tech));

        int currentCapacity = (Build.VERSION.SDK_INT >= 21) ? BatteryManager.BATTERY_PROPERTY_CAPACITY : -1;
        int currentCharging = (Build.VERSION.SDK_INT >= 21) ? BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER : -1;
        myDataSet.add(new RecyclerItem("Battery capacity", currentCapacity + " capacity, " + currentCharging + " mAh")); //FIXME: not really working
    }

    private void loadMemoryData() {
        //TODO: add free RAM %
        ActivityManager.MemoryInfo memInfo = new ActivityManager.MemoryInfo();
        ActivityManager activityManager = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
        activityManager.getMemoryInfo(memInfo);
        long totalRAM = -1;
        if (Build.VERSION.SDK_INT >= 16) {
            totalRAM = memInfo.totalMem / 1024 / 1024;
        }
        long freeRAM = memInfo.availMem / 1024 / 1024;
        myDataSet.add(new RecyclerItem("RAM", freeRAM + " MB available out of " + totalRAM + " MB"));
        myDataSet.add(new RecyclerItem("Currently in low-memory condition?", memInfo.lowMemory ? "yes" : "no"));
    }

    private void loadStorageData() {
        //TODO: add internal free %
        //TODO: add internal used abs + %
        //TODO: add internal storage path (/storage/emulated/0)
        //TODO: add amount of external storage
        //TODO: for each external storage available:
        //TODO:     add free abs + %
        //TODO:     add used abs + %
        //TODO:     add total abs
        //TODO:     add external storage path

        StatFs fs = new StatFs(Environment.getRootDirectory().getAbsolutePath());
        long totalFs = -1;
        long freeFs = -1;
        if (Build.VERSION.SDK_INT >= 18) {
            totalFs = fs.getBlockCountLong() * fs.getBlockSizeLong() / 1024 / 1024;
            freeFs = fs.getAvailableBlocksLong() * fs.getBlockSizeLong() / 1024 / 1024;
        }
        myDataSet.add(new RecyclerItem("Disk space", freeFs + " MB available out of " + totalFs + " MB"));
    }

    private void loadSensorsData() {
        //TODO: add data from accelerometer
        //TODO: add data from light sensor
        //TODO: add data from gyroscope
        //TODO: add data from barometer
        //TODO: add data from step sensor
        //TODO: add data from magnetometer
        //TODO: add data from proximity sensor
    }

    //TODO: change BroadcastReceiver to be alive and updating data while app is in foreground
    //TODO: discriminate case when is full
    private void getBatteryCharge() {
        BroadcastReceiver batteryLevelReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                context.unregisterReceiver(this); //this makes it actually only check once on startup and stop listening to changes
                int currentLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
                if (currentLevel >= 0 && scale > 0) {
                    //mBatteryCharge = currentLevel / scale * 100;
                }
            }
        };
        IntentFilter batteryLevelFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(batteryLevelReceiver, batteryLevelFilter);
    }

    private void loadMockData() {
        for (int i = 1; i <= 20; i++) {
            myDataSet.add(new RecyclerItem("Title" + i,"Value" + i));
        }
    }
}
