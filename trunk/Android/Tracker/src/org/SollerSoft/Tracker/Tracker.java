package org.SollerSoft.Tracker;

import java.util.List;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

public class Tracker extends Activity implements SensorEventListener {
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
        sm = (SensorManager)getSystemService(SENSOR_SERVICE);
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
		for(int i = 0; i < sensors.size(); i++){
			Sensor s = sensors.get(i);
			if(s.getName() == sensor){
	        	sm.registerListener(this, s, SensorManager.SENSOR_DELAY_NORMAL);
	        	print("Registered listener for " + s.getName() + "\n");
	    		print("\tSensor power: " + s.getPower() + " mA\n");
	    		print("\tSensor resolution: " + s.getResolution() + "\n");
	    		print("\tSensor range: " + s.getMaximumRange() + "\n");
			}
		}
    	running = true;
    }
    public void SensorKill(){
		sm.unregisterListener(this);
		print("Unregistered listeners.\n");
		running = false;
    }
    public void SensorPopulate(){
    	clear();
    	sensors = sm.getSensorList(Sensor.TYPE_ALL);
    	ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item);
    	adapter.add("None");
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
    	Float x = new Float(0);
    	Float y = new Float(0);
    	Float z = new Float(0);
    	if(values.length>0) x = values[0];
    	if(values.length>1) y = values[1];
    	if(values.length>2) z = values[2];
		txtx.setText("X:" + x.toString());
		txty.setText("Y:" + y.toString());
		txtz.setText("Z:" + z.toString());
    	for(int i = 0; i < values.length; i++){
        	print("\t" + values[i] + "\n");	
    	}
    }
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}
}