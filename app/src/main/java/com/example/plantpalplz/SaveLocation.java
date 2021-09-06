package com.example.plantpalplz;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class SaveLocation extends Activity {
    //create string tester
    String city;
    String value;
    //create private String array savedlocations
    private String[] savedlocations;
    //create RelativeLayout object rl and rlbg
    RelativeLayout rlbg;
    TextView location, interval;
    ImageView background;

    //API key
    String OPEN_WEATHER_MAP_API = "8ee3713483d0844cffe6a2689db6b17f";

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
                    //Create new Date object T from json * 1000
                    Date T = new Date(json.getLong("dt") * 1000);
                    //create new DateFormat df
                    DateFormat df = DateFormat.getDateTimeInstance();

                    //set variables equal to values returned from json objects
                    location.setText(json.getString("name").toUpperCase(Locale.US) + ", " + json.getJSONObject("sys").getString("country"));
                    city = json.getString("name").toUpperCase(Locale.US) + ", " + json.getJSONObject("sys").getString("country");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    //onCreate() method
    @SuppressLint({"ResourceAsColor", "ClickableViewAccessibility"})
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        //setcontentView to locations_list
        setContentView(R.layout.locations_list);

        //create Intent intent
        Intent intent = getIntent();

        //set rlgb equal to equivalent xml object
        rlbg = findViewById(R.id.RelativeBG);

        background = findViewById(R.id.settingsBG);

        location = findViewById(R.id.location);
        interval = findViewById(R.id.interval);

        final ArrayList<String> savedsettings = intent.getStringArrayListExtra("city");
        final ArrayList<String> savedsettings2 = intent.getStringArrayListExtra("interval");

        Log.e("Tester", savedsettings + " " + savedsettings2);

        city = savedsettings.get(0);
        value = savedsettings2.get(0);

        if (city == null || value == null)
        {
            //Default Location
            city = "Galashiels";
            value = "6";
        }

        location.setText(city);
        interval.setText(value);

        //set/create onClickListener for cityField
        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //create new AlertDialog.Builder object alertDialog
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(SaveLocation.this);
                //call setTitle method of alertDialog, sending string as parameter
                alertDialog.setTitle("Change Location");
                //create final EditText object input
                final EditText input = new EditText(SaveLocation.this);
                //set text of input to city variable
                input.setText(city);
                //create LayoutParams object lp and set params
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                //set inputs layoutparams to lp
                input.setLayoutParams(lp);
                //setView to input
                alertDialog.setView(input);

                //set positive button for alert dialog, set value to change and create onClickListener
                alertDialog.setPositiveButton("Change",
                        new DialogInterface.OnClickListener() {
                            //onclick method
                            public void onClick(DialogInterface dialog, int which) {
                                //set city equal to string from input
                                city = input.getText().toString();
                                //call taskLoadUp method, sending city as parameter
                                if (Function.isNetworkAvailable(getApplicationContext())) {
                                    //Create object DownloadWeather called task
                                    taskLoadUp(city);

                                } else {
                                    //toast message to tell the user that they dont have an internet connection
                                    Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                //set negative button for alert dialog, set value to cancel and create onclicklistener
                alertDialog.setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            //onclick method
                            public void onClick(DialogInterface dialog, int which) {
                                //cancel dialog
                                dialog.cancel();
                            }
                        });
                //show alert dialog
                alertDialog.show();
            }
        });

        //set/create onClickListener for cityField
        interval.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //create new AlertDialog.Builder object alertDialog
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(SaveLocation.this);
                //call setTitle method of alertDialog, sending string as parameter
                alertDialog.setTitle("Change Interval");
                //create final EditText object input
                final EditText input = new EditText(SaveLocation.this);
                //set text of input to city variable
                input.setText(value);
                //create LayoutParams object lp and set params
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                //set inputs layoutparams to lp
                input.setLayoutParams(lp);
                //setView to input
                alertDialog.setView(input);

                //set positive button for alert dialog, set value to change and create onClickListener
                alertDialog.setPositiveButton("Change",
                        new DialogInterface.OnClickListener() {
                            //onclick method
                            public void onClick(DialogInterface dialog, int which) {
                                //set city equal to string from input
                                value = input.getText().toString();
                                try
                                {
                                    int temp = Integer.parseInt(value);
                                    if (temp < 1)
                                    {
                                        value = "1";
                                        Toast.makeText(getApplicationContext(), "Minimum interval is 1 hour.", Toast.LENGTH_LONG).show();
                                    }
                                    else if (temp > 24)
                                    {
                                        value = "24";
                                        Toast.makeText(getApplicationContext(), "Maximum interval is 24 hours.", Toast.LENGTH_LONG).show();
                                    }
                                }
                                catch (Exception e)
                                {
                                    Toast.makeText(getApplicationContext(), "Interval must be a number between 1-24.", Toast.LENGTH_LONG).show();
                                    value = "6";
                                }
                                interval.setText(value);
                            }
                        });
                //set negative button for alert dialog, set value to cancel and create onclicklistener
                alertDialog.setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            //onclick method
                            public void onClick(DialogInterface dialog, int which) {
                                //cancel dialog
                                dialog.cancel();
                            }
                        });
                //show alert dialog
                alertDialog.show();
            }
        });

        background.setOnTouchListener(new OnSwipeTouchListener(SaveLocation.this) {
            public void onSwipeTop() {
            }
            public void onSwipeRight() {
            }
            public void onSwipeLeft() {
            }
            public void onSwipeBottom() {
                //Create string returned equal to tester
                String returned = city;
                String retinterval = value;
                //create Intent returnIntent
                Intent returnIntent = new Intent();
                //send extra data with name String and data returned
                returnIntent.putExtra("city", returned);
                returnIntent.putExtra("interval", retinterval);

                if(city == savedsettings.get(0) && value == savedsettings2.get(0))
                {
                    setResult(1, returnIntent);
                }
                else if (value == savedsettings2.get(0))
                {
                    setResult(2, returnIntent);
                }
                else
                {
                    setResult(3, returnIntent);
                }


                //call finish() to return to mainactivity
                finish();
            }
        });

    }
    //taskLoadUp() method taking String parameter
    public void taskLoadUp(String query) {
        //if statement to determine if the user has an internet connection
        if (Function.isNetworkAvailable(getApplicationContext())) {
            //Create object DownloadWeather called task
            DownloadWeather task = new DownloadWeather();

            task.execute(query);

        } else {
            //toast message to tell the user that they dont have an internet connection
            Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_LONG).show();
        }
    }
}
