package project.astix.com.ltfoodsfaindirect;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import static project.astix.com.ltfoodsfaindirect.R.id.btn_refresh;
import static project.astix.com.ltfoodsfaindirect.R.id.ll_map;
import static project.astix.com.ltfoodsfaindirect.R.id.txt_rfrshCmnt;

public class DistributorMapActivity extends AppCompatActivity implements OnMapReadyCallback
{
    String imei, fDate;
    LinearLayout ll_locationDetails,ll_map;
    EditText et_gstNo;
    RadioButton rb_gstYes,rb_gstNo,rb_gstPending;
    FragmentManager manager;
    FragmentTransaction fragTrans;
    MapFragment mapFrag;

    String LattitudeFromLauncher="NA";
    String LongitudeFromLauncher="NA";
    String StoreName="NA";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_distributor_map);

        ll_locationDetails= (LinearLayout) findViewById(R.id.ll_locationDetails);
        ll_map= (LinearLayout) findViewById(R.id.ll_map);
        et_gstNo= (EditText) findViewById(R.id.et_gstNo);
        rb_gstYes= (RadioButton) findViewById(R.id.rb_gstYes);
        rb_gstNo= (RadioButton) findViewById(R.id.rb_gstNo);
        rb_gstPending= (RadioButton) findViewById(R.id.rb_gstPending);

        Intent i=getIntent();
        imei=i.getStringExtra("imei");
        fDate=i.getStringExtra("fDate");

        View viewLoc=getLayoutInflater().inflate(R.layout.location_details_layout, null);

        ll_locationDetails.addView(viewLoc);

        ll_map.setVisibility(View.VISIBLE);
        manager= getFragmentManager();
        mapFrag = (MapFragment)manager.findFragmentById(
                R.id.map);
        mapFrag.getMapAsync(DistributorMapActivity.this);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.show(mapFrag);

        GstRadioBtns();

    }

    public void GstRadioBtns()
    {
        rb_gstYes.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    et_gstNo.setVisibility(View.VISIBLE);
                }
            }
        });

        rb_gstNo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    et_gstNo.setVisibility(View.GONE);
                }
            }
        });

        rb_gstPending.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    et_gstNo.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        if(!LattitudeFromLauncher.equals("NA") && !LattitudeFromLauncher.equals("0.0"))
        {
            googleMap.clear();
            googleMap.setMyLocationEnabled(false);
            MarkerOptions marker = new MarkerOptions().position(new LatLng(Double.parseDouble(LattitudeFromLauncher), Double.parseDouble(LongitudeFromLauncher)));
            Marker locationMarker=googleMap.addMarker(marker);
            locationMarker.showInfoWindow();
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(LattitudeFromLauncher), Double.parseDouble(LongitudeFromLauncher)), 15));

        }

        else
        {

            googleMap.setMyLocationEnabled(true);
            googleMap.moveCamera(CameraUpdateFactory.zoomIn());
            googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {

                    marker.setTitle(StoreName);
                }
            });

        }

    }
}
