package com.example.mapapplication;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;


public class GoogleMapActivity extends AppCompatActivity {

    private static final String SHOW_URL = "http://10.0.2.2/localisation/getPosition.php";
    private static final String REQUEST_TAG = "positions";

    private MapView map;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Configuration.getInstance().load(
                getApplicationContext(),
                getSharedPreferences("prefs", MODE_PRIVATE)
        );
        Configuration.getInstance().setUserAgentValue(getPackageName());

        setContentView(R.layout.activity_google_map);

        map = findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);
        map.getController().setZoom(15.0);
        map.getController().setCenter(new GeoPoint(37.272525, -122.12106));

        requestQueue = Volley.newRequestQueue(getApplicationContext());
        loadPositions();
    }

    private void loadPositions() {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                SHOW_URL,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray positions = response.getJSONArray("positions");
                            map.getOverlays().clear();

                            for (int i = 0; i < positions.length(); i++) {
                                JSONObject position = positions.getJSONObject(i);
                                double lat = position.getDouble("latitude");
                                double lng = position.getDouble("longitude");
                                String date = position.optString("date", "");

                                addMarker(lat, lng, "Marker " + (i + 1), date);

                                if (i == 0) {
                                    map.getController().setCenter(new GeoPoint(lat, lng));
                                }
                            }

                            map.invalidate();
                            Toast.makeText(
                                    getApplicationContext(),
                                    getString(R.string.positions_loaded, positions.length()),
                                    Toast.LENGTH_SHORT
                            ).show();
                        } catch (JSONException e) {
                            Toast.makeText(
                                    getApplicationContext(),
                                    R.string.positions_load_error,
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(
                                getApplicationContext(),
                                R.string.positions_load_error,
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                }
        );

        jsonObjectRequest.setTag(REQUEST_TAG);
        requestQueue.add(jsonObjectRequest);
    }

    private void addMarker(double lat, double lng, String title, String date) {
        Marker marker = new Marker(map);
        marker.setPosition(new GeoPoint(lat, lng));
        marker.setTitle(title);
        marker.setSnippet(date);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);

        Drawable original = ContextCompat.getDrawable(this, R.drawable.marker);
        if (original instanceof BitmapDrawable) {
            Bitmap bitmap = ((BitmapDrawable) original).getBitmap();
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, 80, 80, false);
            marker.setIcon(new BitmapDrawable(getResources(), scaledBitmap));
        } else if (original != null) {
            marker.setIcon(original);
        }

        map.getOverlays().add(marker);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (map != null) {
            map.onResume();
        }
    }

    @Override
    protected void onPause() {
        if (map != null) {
            map.onPause();
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (requestQueue != null) {
            requestQueue.cancelAll(REQUEST_TAG);
        }
        super.onDestroy();
    }
}
