package com.example.sensorapp;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Arrays;
import java.util.List;

import static com.example.sensorapp.SensorDetailsActivity.EXTRA_SENSOR_TYPE_PARAMETER;

public class SensorActivity extends AppCompatActivity {
    private SensorManager sensorManager;
    private List<Sensor> sensorList;
    private RecyclerView recyclerView;
    private SensorAdapter adapter;
    private boolean contains=false;
    private static final String SENSOR_APP_TAG = "SENSOR_APP_TAG";
    public static final int SENSOR_DETAILS_ACTIVITY_REQUEST_CODE = 1;
    public static final int LOCATION_ACTIVITY_REQUEST_CODE = 1;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.fragment_sensor_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        String string = getString(R.string.sensors_count, sensorList.size());
        getSupportActionBar().setSubtitle(string);
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sensor_activity);

        recyclerView = findViewById(R.id.sensor_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorList = sensorManager.getSensorList(Sensor.TYPE_ALL);
        sensorList.forEach(sensor -> {
            Log.d(SENSOR_APP_TAG, "Sensor name:" + sensor.getName());
            Log.d(SENSOR_APP_TAG, "Sensor vendor:" + sensor.getVendor());
            Log.d(SENSOR_APP_TAG, "Sensor max range:" + sensor.getMaximumRange());
        });
        sensorList.forEach(sensor -> {
            if(sensor.getType()==2){

                contains=true;
            }
        });
        if (adapter == null) {
            adapter = new SensorAdapter(sensorList);
            recyclerView.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }

    }

    private class SensorAdapter extends RecyclerView.Adapter<SensorAdapter.SensorHolder> {

        private final List<Sensor> mValues;

        public SensorAdapter(List<Sensor> items) {
            mValues = items;
        }

        @Override
        public SensorHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflate = LayoutInflater.from(parent.getContext());
            return new SensorHolder(inflate, parent);
        }

        @Override
        public void onBindViewHolder(final SensorHolder holder, int position) {
            Sensor sensor = sensorList.get(position);
            holder.bind(sensor);
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public class SensorHolder extends RecyclerView.ViewHolder {
            private final TextView sensorNameTextView;
            private final TextView sensorTypeTextView;

            public SensorHolder(LayoutInflater inflater, ViewGroup parent) {
                super(inflater.inflate(R.layout.sensor_list_item, parent, false));
                sensorNameTextView = itemView.findViewById(R.id.sensor_name);
                sensorTypeTextView = itemView.findViewById(R.id.sensor_type);
            }

            public void bind(Sensor sensor) {
                sensorNameTextView.setText(sensor.getName());
                sensorTypeTextView.setText(String.valueOf(sensor.getType()));
                View itemContainer = itemView.findViewById(R.id.list_item_sensor);
                if (sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                    itemContainer.setBackgroundColor(getResources().getColor(R.color.green_default));
                    itemContainer.setOnClickListener(v -> {
                        Intent intent = new Intent(SensorActivity.this, LocationActivity.class);
                        startActivityForResult(intent,LOCATION_ACTIVITY_REQUEST_CODE);
                    });
                }
                if (sensor.getType()== Sensor.TYPE_LIGHT||sensor.getType()==Sensor.TYPE_RELATIVE_HUMIDITY) {
                    itemContainer.setBackgroundColor(getResources().getColor(R.color.green_default));
                    itemContainer.setOnClickListener(v -> {
                        Intent intent = new Intent(SensorActivity.this, SensorDetailsActivity.class);
                        intent.putExtra(EXTRA_SENSOR_TYPE_PARAMETER, sensor.getType());
                        startActivityForResult(intent, SENSOR_DETAILS_ACTIVITY_REQUEST_CODE);
                    });
                }

            }
        }
    }
}