package com.androideverde.deviceinfo;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
//        loadMockData();
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

        //Device
        //model, manufacturer, device, board, hardware, brand
        //Hardware serial, IP address IPv4+IPv6, Wifi Mac address, Bluetooth Mac address, build fingerprint
        loadDeviceData();
        //System
        //version, api number+codename, bootloader, build number, radio version, kernel, android runtime, uptime
        loadSystemData();
        //CPU
        //CPU hardware, cores, clock speed, Running CPUs (instant clock speed for each core), CPU load
        loadCPUData();
        //Display
        //Resolution in pixels, density value+dpi category, font scale, physical size inches, refresh rate
        loadDisplayData();
        //Battery
        //level, status, power source, health, technology, temperature, voltage, capacity
        loadBatteryData();
        //Memory/Storage
        //RAM: free abs MB+%; used abs+%; total abs, ROM == internal storage (free, used, total), Internal storage path (/storage/emulated/0), External storage path + free, used, total
        loadMemoryData();
        loadStorageData();
        //Sensors
        //Accel, light, gyro, barometer, step, magnetometer, proximity, ...
        loadSensorsData();
    }

    private void loadDeviceData() {
        myDataSet.add(new RecyclerItem("Device", Build.DEVICE));
        myDataSet.add(new RecyclerItem("Manufacturer", Build.MANUFACTURER));
        myDataSet.add(new RecyclerItem("Model", Build.MODEL));
        myDataSet.add(new RecyclerItem("Product", Build.PRODUCT));
    }


    private void loadSystemData() {
        myDataSet.add(new RecyclerItem("OS Version", Build.VERSION.RELEASE + " (API level " + Build.VERSION.SDK_INT + ")"));
    }

    private void loadCPUData() {
        //TODO: add stuff here
    }

    private void loadDisplayData() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        myDataSet.add(new RecyclerItem("Display size", metrics.heightPixels + " x " + metrics.widthPixels + " pixels"));
        myDataSet.add(new RecyclerItem("Display density", metrics.densityDpi + " dpi"));
    }

    private void loadBatteryData() {
        //getBatteryCharge();
        //TODO: use a BroadcastReceiver for getting BatteryStatus updates
        Intent intent = this.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        //charge level
        int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        int batteryCharge = level / scale * 100;
        myDataSet.add(new RecyclerItem("Battery level", batteryCharge + "%")); //FIXME: not working, always reports 0
        //status
        int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL;
        myDataSet.add(new RecyclerItem("Battery status", (isCharging ? "charging" : "discharging")));
        //charging method
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
        //health
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
        //temp
        int temp = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1);
        float realTemp = temp / 10.f;
        myDataSet.add(new RecyclerItem("Battery temperature", realTemp + " C"));
        //volt
        int voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1);
        myDataSet.add(new RecyclerItem("Battery voltage", voltage + " mV"));
        //tech
        String tech = intent.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY);
        myDataSet.add(new RecyclerItem("Battery technology", tech));
        //capacity
        int currentCapacity = BatteryManager.BATTERY_PROPERTY_CAPACITY; //TODO: protect this with a version_code check
        int currentCharging = BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER; //TODO: protect this with a version_code check
        myDataSet.add(new RecyclerItem("Battery capacity", currentCapacity + " capacity, " + currentCharging + " mAh")); //FIXME: not really working
    }

    private void loadMemoryData() {
        ActivityManager.MemoryInfo memInfo = new ActivityManager.MemoryInfo();
        ActivityManager activityManager = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
        activityManager.getMemoryInfo(memInfo);
        long totalRAM = memInfo.totalMem / 1024 / 1024; //TODO: protect this with a version_code check
        long freeRAM = memInfo.availMem / 1024 / 1024;
        myDataSet.add(new RecyclerItem("RAM", freeRAM + " MB available out of " + totalRAM + " MB"));
        myDataSet.add(new RecyclerItem("Currently in low-memory condition?", memInfo.lowMemory ? "yes" : "no"));
    }

    private void loadStorageData() {
        StatFs fs = new StatFs(Environment.getRootDirectory().getAbsolutePath());
        long totalFs = fs.getBlockCountLong() * fs.getBlockSizeLong() / 1024 / 1024; //TODO: protect this with a version_code check
        long freeFs = fs.getAvailableBlocksLong() * fs.getBlockSizeLong() / 1024 / 1024; //TODO: protect this with a version_code check
        myDataSet.add(new RecyclerItem("Disk space", freeFs + " MB available out of " + totalFs + " MB"));
    }

    private void loadSensorsData() {
        //TODO: add stuff here
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
