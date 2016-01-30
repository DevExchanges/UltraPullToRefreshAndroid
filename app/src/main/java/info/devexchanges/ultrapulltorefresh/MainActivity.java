package info.devexchanges.ultrapulltorefresh;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.RoundingMode;

import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;

public class MainActivity extends AppCompatActivity implements PtrHandler {

    private PtrClassicFrameLayout pullToRefreshLayout;
    private TextView location;
    private TextView city;
    private TextView pressure;
    private TextView humidity;
    private TextView temperature;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        temperature = (TextView) findViewById(R.id.temperature);
        city = (TextView) findViewById(R.id.city);
        location = (TextView) findViewById(R.id.location);
        humidity = (TextView) findViewById(R.id.humidity);
        pressure = (TextView) findViewById(R.id.pressure);
        pullToRefreshLayout = (PtrClassicFrameLayout) findViewById(R.id.pt_frame);

        //set handler for pull to refresh layout
        pullToRefreshLayout.setPtrHandler(this);
        //set last update time in header
        pullToRefreshLayout.setLastUpdateTimeRelateObject(this);
    }

    @SuppressLint("SetTextI18n")
    public void parsingJSON(String data) {
        // parsing json and set the custom dialog components - text, image and button
        try {
            JSONObject jsonObject = new JSONObject(data);

            //get location
            JSONObject locationObject = jsonObject.getJSONObject("coord");
            location.setText("[ " + locationObject.getString("lon") + ", " + locationObject.getString("lat") + " ]");

            //get temperature, humidity and pressure
            JSONObject tempObject = jsonObject.getJSONObject("main");
            temperature.setText(kelvinToCelcius(tempObject.getString("temp")) + " " + (char) 0x00B0 + "C");
            humidity.setText(tempObject.getString("humidity") + "%");
            pressure.setText(hPaToatm(tempObject.getString("pressure")) + " atm");

            //get city name
            city.setText(jsonObject.getString("name"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        pullToRefreshLayout.refreshComplete(); //stop Refreshing
    }

    @Override
    public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
        return true;
    }

    @Override
    public void onRefreshBegin(PtrFrameLayout frame) {
        new GetWeatherTask(this).execute();
    }

    public double kelvinToCelcius(String kelvin) {
        BigDecimal bd = new BigDecimal(Double.parseDouble(kelvin) - 273);
        bd = bd.setScale(2, RoundingMode.HALF_UP);

        return bd.doubleValue();
    }

    public double hPaToatm(String hPa) {
        BigDecimal bd = new BigDecimal((Double.parseDouble(hPa) / 1013.25));
        bd = bd.setScale(2, RoundingMode.HALF_UP);

        return bd.doubleValue();
    }
}
