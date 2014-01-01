package com.example.HomeMonitor;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

public class MyActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        final Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                URL url = null;
                try {
                    url = new URL("http://192.168.0.198:8085/weather/api.php");
                } catch (MalformedURLException mue) {
                    Log.e("MyActivity", mue.toString());
                }
                new UpdateData().execute(url);
            }
        });
    }

    private class UpdateData extends AsyncTask<URL, Integer, dataObject> {

        @Override
        protected dataObject doInBackground(URL... urls) {
            dataObject data = new dataObject();
            for (URL url : urls) {
                Log.i("Update", "update");
                JSONObject jsonObject = null;
                try {
                    HttpGet get = new HttpGet(url.toURI());
                    HttpClient client = new DefaultHttpClient();
                    HttpResponse response = client.execute(get);
                    String content = new BasicResponseHandler().handleResponse(response);
                    jsonObject = new JSONObject(content);
                } catch (IOException ioe) {
                    Log.e("MainActivity", "IO Exception - " + ioe);
                } catch (URISyntaxException use) {
                    Log.e("MyActivity", use.toString());
                } catch (JSONException je) {
                    Log.e("MyActivity", je.toString());
                }
                if (jsonObject != null) {
                    try {
                        data.setTemp(jsonObject.getString("temp"));
                        data.setHum(jsonObject.getString("hum"));
                        data.setDate(jsonObject.getString("date"));
                    } catch (JSONException je) {
                        Log.e("MyActivity", je.toString());
                    }
                } else {
                    Log.i("MyActivity", "Null response");
                }
            }
            return data;
        }

        protected void onPostExecute(dataObject result) {
            TextView dateView = (TextView) findViewById(R.id.dateValue);
            dateView.setText(result.getDate());
            TextView tempView = (TextView) findViewById(R.id.tempValue);
            tempView.setText(result.getTemp());
            TextView humView = (TextView) findViewById(R.id.humValue);
            humView.setText(result.getHum());
        }
    }

    private class dataObject {
        private String date;
        private String temp;
        private String hum;

        public void setTemp(String temp) {
            this.temp = temp;
        }

        public void setHum(String hum) {
            this.hum = hum;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getTemp() {
            return temp;
        }

        public String getHum() {
            return hum;
        }

        public String getDate() {
            return date;
        }

    }

}