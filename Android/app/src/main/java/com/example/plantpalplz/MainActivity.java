package com.example.plantpalplz;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity {

    //Declare objects/variables
    TextView date, time, temp, humidity, light, soil;
    RelativeLayout background;
    ImageView bg, rightb, leftb;

    //API key
    String OPEN_WEATHER_MAP_API = "8ee3713483d0844cffe6a2689db6b17f";

    String city;

    //increment
    int increm = 0;
    int interval;

    //list
    ArrayList<String> field1 = new ArrayList<String>();

    //Declare ArrayList containing type String
    ArrayList<String> savedsettings = new ArrayList<>();

    //Declare ArrayList containing type String
    ArrayList<String> savedsettings2 = new ArrayList<>();

    boolean daynight;

    //JSON
    JSONArray fields;

    //Declare SwipeRefreshLayout
    //SwipeRefreshLayout pullToRefresh;

    //Declare DatabaseManipulator object
    DatabaseManipulator dm;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Hide ActionBar and set contentView to activity_main.xml
        getSupportActionBar().hide();
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);

        Log.e("City", savedsettings.toString());
        Log.e("Interval", savedsettings2.toString());

        //set values of variables to equivalent xml objects
        date = findViewById(R.id.date);
        time = findViewById(R.id.time);
        temp = findViewById(R.id.temp);
        humidity = findViewById(R.id.humidity);
        light = findViewById(R.id.light);
        soil = findViewById(R.id.soil);
        rightb = findViewById(R.id.rightb);
        leftb = findViewById(R.id.leftb);

        //set values of variables to equivalent xml objects
        background = findViewById(R.id.RelativeBG);
        bg = findViewById(R.id.imgBG);

        //Instantiate DatabaseManipulator dm
        dm = new DatabaseManipulator(getApplicationContext());
        savedsettings.clear();
        savedsettings2.clear();
        //Create list of String arrays testers, equal to value returned from dm.selectAll() method
        List<String[]> testers = dm.selectAll();

        //foreach loop that runs through every string array in testers
        for (String[] name : testers)
        {
            //add item at position 1 in current string array from testers into savedlocations ArrayList of Strings
            savedsettings.add(name[1]);
            savedsettings2.add(name[2]);
        }

        try
        {
            city = savedsettings.get(0);
            interval = Integer.parseInt(savedsettings2.get(0));
        }
        catch (Exception e)
        {
            city = "Galashiels";
            interval = 6;
            dm.insert(city, String.valueOf(interval));
            Log.e("New DM", "Default");
        }

        //call taskLoadUp method, sending city variable as parameter
        taskLoadUp(city);

        background.setOnTouchListener(new OnSwipeTouchListener(MainActivity.this) {
            public void onSwipeTop() {
                Log.e("up", "1");
                //Instantiate DatabaseManipulator dm
                dm = new DatabaseManipulator(getApplicationContext());
                savedsettings.clear();
                savedsettings2.clear();

                //Create list of String arrays testers, equal to value returned from dm.selectAll() method
                List<String[]> testers = dm.selectAll();

                //foreach loop that runs through every string array in testers
                for (String[] name : testers)
                {
                    //add item at position 1 in current string array from testers into savedlocations ArrayList of Strings
                    savedsettings.add(name[1]);
                    savedsettings2.add(name[2]);
                }

                Intent settings = new Intent(getApplicationContext(), SaveLocation.class);
                //send extra information to new intent, with key Test and value message variable
                Log.e("up", "4");
                settings.putExtra("city", savedsettings);
                Log.e("up", "5");
                settings.putExtra("interval", savedsettings2);
                Log.e("up", "6");
                //call startActivityForResult method, sending intent and request code 1
                startActivityForResult(settings, 1);


            }
            public void onSwipeRight() {
                increm --;
                if (increm < 0)
                {
                    increm = 0;
                    Toast.makeText(getApplicationContext(), "Sorry, there are no entries before this date.", Toast.LENGTH_SHORT).show();
                }
                try {
                    date.setText(fields.getJSONObject(increm).getString("created_at").substring(0,10));
                    time.setText(fields.getJSONObject(increm).getString("created_at").substring(11,16));
                    temp.setText(fields.getJSONObject(increm).getString("field1").substring(0,4));
                    humidity.setText(fields.getJSONObject(increm).getString("field2").substring(0,4));
                    light.setText(fields.getJSONObject(increm).getString("field3"));
                    soil.setText(fields.getJSONObject(increm).getString("field4"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            public void onSwipeLeft() {
                if (increm >= fields.length() -1)
                {
                    Toast.makeText(getApplicationContext(), "Sorry, there are no entries after this date.", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    increm ++;
                }
                //put try and catch within else statement so it doesnt set even when increm doesnt increment
                try {
                    date.setText(fields.getJSONObject(increm).getString("created_at").substring(0,10));
                    time.setText(fields.getJSONObject(increm).getString("created_at").substring(11,16));
                    temp.setText(fields.getJSONObject(increm).getString("field1").substring(0,4));
                    humidity.setText(fields.getJSONObject(increm).getString("field2").substring(0,4));
                    light.setText(fields.getJSONObject(increm).getString("field3"));
                    soil.setText(fields.getJSONObject(increm).getString("field4"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            public void onSwipeBottom() {
            }
        });

        rightb.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                if (increm >= fields.length() -1)
                {
                    Toast.makeText(getApplicationContext(), "Sorry, there are no entries after this date.", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    increm ++;
                    Log.e("Test", String.valueOf(increm));
                }
                //put try and catch within else statement so it doesnt set even when increm doesnt increment
                try {
                    date.setText(fields.getJSONObject(increm).getString("created_at").substring(0,10));
                    time.setText(fields.getJSONObject(increm).getString("created_at").substring(11,16));
                    temp.setText(fields.getJSONObject(increm).getString("field1").substring(0,4));
                    humidity.setText(fields.getJSONObject(increm).getString("field2").substring(0,4));
                    light.setText(fields.getJSONObject(increm).getString("field3"));
                    soil.setText(fields.getJSONObject(increm).getString("field4"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        rightb.setOnLongClickListener(new View.OnLongClickListener(){
            @Override
            public boolean onLongClick(View v) {
                if (increm >= fields.length() -1)
                {
                    Toast.makeText(getApplicationContext(), "Sorry, there are no entries after this date.", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    increm = fields.length() -1;
                }
                try {
                    date.setText(fields.getJSONObject(increm).getString("created_at").substring(0,10));
                    time.setText(fields.getJSONObject(increm).getString("created_at").substring(11,16));
                    temp.setText(fields.getJSONObject(increm).getString("field1").substring(0,4));
                    humidity.setText(fields.getJSONObject(increm).getString("field2").substring(0,4));
                    light.setText(fields.getJSONObject(increm).getString("field3"));
                    soil.setText(fields.getJSONObject(increm).getString("field4"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return true;
            }
        });

        leftb.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                increm --;
                Log.e("Test", String.valueOf(increm));
                if (increm < 0)
                {
                    increm = 0;
                    Log.e("Test", String.valueOf(increm));
                    Toast.makeText(getApplicationContext(), "Sorry, there are no entries before this date.", Toast.LENGTH_SHORT).show();
                }
                try {
                    date.setText(fields.getJSONObject(increm).getString("created_at").substring(0,10));
                    time.setText(fields.getJSONObject(increm).getString("created_at").substring(11,16));
                    temp.setText(fields.getJSONObject(increm).getString("field1").substring(0,4));
                    humidity.setText(fields.getJSONObject(increm).getString("field2").substring(0,4));
                    light.setText(fields.getJSONObject(increm).getString("field3"));
                    soil.setText(fields.getJSONObject(increm).getString("field4"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        leftb.setOnLongClickListener(new View.OnLongClickListener(){
            @Override
            public boolean onLongClick(View v) {
                if (increm < 0)
                {
                    Toast.makeText(getApplicationContext(), "Sorry, there are no entries before this date.", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    increm = 0;
                }
                try {
                    date.setText(fields.getJSONObject(increm).getString("created_at").substring(0,10));
                    time.setText(fields.getJSONObject(increm).getString("created_at").substring(11,16));
                    temp.setText(fields.getJSONObject(increm).getString("field1").substring(0,4));
                    humidity.setText(fields.getJSONObject(increm).getString("field2").substring(0,4));
                    light.setText(fields.getJSONObject(increm).getString("field3"));
                    soil.setText(fields.getJSONObject(increm).getString("field4"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return true;
            }
        });

    }

    //method onActivityResult() takes two integers and an Intent object as parameters, runs after Intent SaveLocation closes
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //if statement to determine if request code is equal to 1
        if (requestCode == 1) {
            //if statement to determine if request code is 1
            if (resultCode == 1)
            {
                Log.e("Tester", "No changes");
            }
            //else if statement to determine if resultCode is equal to 2
            else if (resultCode == 2)
            {
                String settings = data.getStringExtra("city");
                String settings2 = data.getStringExtra("interval");
                dm = new DatabaseManipulator(getApplicationContext());
                dm.deletecity();
                dm.insert(settings, settings2);
                Log.e("Tester", "Changes");
                Toast.makeText(getApplicationContext(), "Your settings have been saved.", Toast.LENGTH_LONG).show();
                city = settings;
                interval = Integer.parseInt(settings2);
                taskLoadUp(city);
            }
            else if (resultCode == 3)
            {
                String settings = data.getStringExtra("city");
                String settings2 = data.getStringExtra("interval");
                dm = new DatabaseManipulator(getApplicationContext());
                dm.deletecity();
                dm.insert(settings, settings2);
                Log.e("Tester", "Changes");
                Toast.makeText(getApplicationContext(), "Your settings have been saved.", Toast.LENGTH_LONG).show();
                city = settings;
                interval = Integer.parseInt(settings2);
                taskLoadUp(city);
                updateInterval(interval);
            }
        }
    }

    //taskLoadUp() method taking String parameter
    public void taskLoadUp(String query) {
        //if statement to determine if the user has an internet connection
        if (Function.isNetworkAvailable(getApplicationContext())) {
            //Create object DownloadWeather called task
            DownloadWeather task = new DownloadWeather();
            DownloadData task2 = new DownloadData();
            task2.execute();
            task.execute(query);

        } else {
            //toast message to tell the user that they dont have an internet connection
            Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_LONG).show();
        }
    }

    public void updateInterval(int interval){

        if (Function.isNetworkAvailable(getApplicationContext()))
        {
            //Function.executeGetW("https://api.thingspeak.com/update?api_key=BQI3PU4GC4C26BGA&field5=", interval);
            SendData dl = new SendData();
            dl.execute("");

        }
        else
        {
            //toast message to tell the user that they dont have an internet connection
            Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_LONG).show();
        }
    }

    //class DownloadWeather, extending Asynchronus task
    class DownloadWeather extends AsyncTask< String, Void, String > {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        //method doInBackground
        protected String doInBackground(String...args) {
            //Create string xml
            String xml;
            //if statement to determine if args length is less than 2
            //set xml to String returned from Function.executeGet() method sending API request to http address as parameter
            xml = Function.excuteGet("http://api.openweathermap.org/data/2.5/weather?q=" + args[0] + "&units=metric&appid=" + OPEN_WEATHER_MAP_API);

            return xml;
        }

        //method onPostExecute
        @Override
        protected void onPostExecute(String xml) {
            //try catch block, to catch exceptions
            try {
                //create new JSONObject json from xml
                JSONObject json = new JSONObject(xml);
                Log.e("TEST", json.toString());
                //if statement to determine if json is not null
                if (json != null) {
                    JSONObject details = json.getJSONArray("weather").getJSONObject(0);
                    JSONObject main = json.getJSONObject("main");
                    JSONObject wind = json.getJSONObject("wind");

                    //create longs equal to json objects sunrise and sunset values * 1000
                    long sunrise = json.getJSONObject("sys").getLong("sunrise") * 1000;
                    long sunset = json.getJSONObject("sys").getLong("sunset") * 1000;
                    //create long currenttime equal to current time from device
                    long currentTime = new Date().getTime();
                    //create int cloudiness equal to value from json object
                    int cloudiness = json.getJSONObject("clouds").getInt("all");

                    if(currentTime>=sunrise && currentTime<sunset) {
                        //set backgroundResource of background to color daytime
                        background.setBackgroundResource(R.color.daytime);
                        bg.setImageResource(R.drawable.planter);
                    }
                    else
                    {
                        //set backgroundResource of background to color nighttime
                        background.setBackgroundResource(R.color.nighttime);
                        bg.setImageResource(R.drawable.planter2);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    //class DownloadWeather, extending Asynchronus task
    class DownloadData extends AsyncTask < String, Void, String > {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        //method doInBackground
        protected String doInBackground(String...args) {
            //Create string xml
            String xml;
            //if statement to determine if args length is less than 2
            //set xml to String returned from Function.executeGet() method sending API request to http address as parameter
            xml = Function.excuteGet("https://api.thingspeak.com/channels/908469/feeds.json?api_key=EBSFHFBU7EHYGPH8");
            Log.e("Test", xml);
            return xml;
        }
        //method onPostExecute
        @Override
        protected void onPostExecute(String xml) {
            //try catch block, to catch exceptions
            try {
                //create new JSONObject json from xml
                JSONObject json = new JSONObject(xml);
                Log.e("Test2", json.toString());
                //if statement to determine if json is not null
                if (json != null) {

                    //create new JSONObjects from data in json
                    fields = json.getJSONArray("feeds");
                    for(int i=0;i<fields.length();i++)
                    {
                        field1.add(fields.getJSONObject(i).getString("field1"));
                        date.setText(fields.getJSONObject(i).getString("created_at").substring(0,10));
                        time.setText(fields.getJSONObject(i).getString("created_at").substring(11,16));
                        temp.setText(fields.getJSONObject(i).getString("field1").substring(0,4));
                        humidity.setText(fields.getJSONObject(i).getString("field2").substring(0,4));
                        light.setText(fields.getJSONObject(i).getString("field3"));
                        soil.setText(fields.getJSONObject(increm).getString("field4"));
                        increm = i;
                    }

                    Log.e("TEST3", field1.toString());
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    //class DownloadWeather, extending Asynchronus task
    class SendData extends AsyncTask < String, Void, String > {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        //method doInBackground
        protected String doInBackground(String...args) {
            //Create string xml
            String xml;
            //if statement to determine if args length is less than 2
            //set xml to String returned from Function.executeGet() method sending API request to http address as parameter
            xml = Function.excuteGet("https://api.thingspeak.com/update?api_key=4BXJ5ZH6Q0U5K5FA&field1=" + interval);
            Log.e("Test", xml);
            return xml;
        }
        //method onPostExecute
        @Override
        protected void onPostExecute(String s) {
            //try catch block, to catch exceptions
            //Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
            Log.e("Response", s);
        }
    }
}
