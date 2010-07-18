package org.SollerSoft.Tracker;

import java.util.List;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

public class Tracker extends Activity implements SensorEventListener,LocationListener{
    /** Called when the activity is first created. */
	boolean running = false;
	Button btngps;
	TextView txtcon;
	TextView txtx;
	TextView txty;
	TextView txtz;
	ScrollView scroller;
	Spinner senspin;
	List<Sensor> sensors;
	SensorManager sm;
	LocationManager lm;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        senspin = (Spinner)findViewById(R.id.SpinnerSensors);
        btngps = (Button)findViewById(R.id.ButtonGet);
        scroller = (ScrollView)findViewById(R.id.ScrollViewConsole);
        txtcon = (TextView)findViewById(R.id.TextViewConsole);
        txtx = (TextView)findViewById(R.id.TextViewX);
        txty = (TextView)findViewById(R.id.TextViewY);
        txtz = (TextView)findViewById(R.id.TextViewZ);
        try{
        	sm = (SensorManager)getSystemService(SENSOR_SERVICE);
        	lm = (LocationManager)getSystemService(LOCATION_SERVICE);
        }catch(Exception ex){
        	print(ex.getMessage());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item);
    	adapter.add("None");
	    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    senspin.setAdapter(adapter);
        senspin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				if(running) SensorKill();
				if(senspin.getSelectedItem().toString() != "None")
					SensorRegister(senspin.getSelectedItem().toString());
			}
			public void onNothingSelected(AdapterView<?> arg0) {
			}
        });
        btngps.setOnClickListener(new Button.OnClickListener(){
        	public void onClick(View v){
        		SensorPopulate();
        	}
        });
    }
    public void clear(){
    	txtcon.setText("");
    	scroller.scrollTo(0, txtcon.getHeight());
    }
    public void print(String str){
    	txtcon.append(str);
    	scroller.scrollTo(0, txtcon.getHeight());
    }
    public void SensorRegister(String sensor){
    	clear();
    	running = true;
    	if(sensor == "Location"){
    		print("Registered listener for " + sensor + "\n");
    		try{
        		lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        		Location loc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        		if(loc != null) onLocationChanged(loc);
    		}catch(Exception ex){
				print(ex.getMessage());
    			running = false;
    		}
    	}else{
    		for(int i = 0; i < sensors.size(); i++){
				Sensor s = sensors.get(i);
				if(s.getName() == sensor){
					try{
						sm.registerListener(this, s, SensorManager.SENSOR_DELAY_NORMAL);
					}catch(Exception ex){
						print(ex.getMessage());
						running = false;
					}
	        		print("Registered listener for " + s.getName() + "\n");
	    			print("\tSensor power: " + s.getPower() + " mA\n");
	    			print("\tSensor resolution: " + s.getResolution() + "\n");
	    			print("\tSensor range: " + s.getMaximumRange() + "\n");
				}
			}
    	}
    }
    public void SensorKill(){
    	lm.removeUpdates(this);
		sm.unregisterListener(this);
		print("Unregistered listeners.\n");
		running = false;
    }
    public void SensorPopulate(){
    	clear();
    	sensors = sm.getSensorList(Sensor.TYPE_ALL);
    	ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item);
    	adapter.add("None");
    	adapter.add("Location");
    	for(int i = 0; i < sensors.size(); i++){
    		Sensor s = sensors.get(i);    		
    		print("Found sensor " + s.getName() + "\n");
    		adapter.add(s.getName());
    	}
	    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    senspin.setAdapter(adapter);
    }
    public void onSensorChanged(SensorEvent event){
    	Sensor s = event.sensor;
    	float[] values = event.values;
    	print("Data from sensor " + s.getName() + "\n");
    	if(values.length>0)
    		txtx.setText("X: " + values[0]);
    	if(values.length>1)
    		txty.setText("Y: " + values[1]);
    	if(values.length>2)
    		txtz.setText("Z: " + values[2]);
    	for(int i = 0; i < values.length; i++){
        	print("\t" + values[i] + "\n");	
    	}
    }
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onLocationChanged(Location location) {
		txtx.setText("Latitude: " + location.getLatitude());
		txty.setText("Longitude: " + location.getLongitude());
		txtz.setText("Altitude: " + location.getAltitude());
		print("Latitude: " + location.getLatitude() +
				"\nLongitude: " + location.getLongitude() +
				"\nAltitude: " + location.getAltitude() + "\n");
		
	}
	@Override
	public void onProviderDisabled(String provider) {
		print("GPS Disabled\n");
	}
	@Override
	public void onProviderEnabled(String provider) {
		print("GPS Enabled\n");
	}
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		print("GPS Status: " + status + "\n");
	}
}