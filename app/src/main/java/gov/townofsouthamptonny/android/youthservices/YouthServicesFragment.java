package gov.townofsouthamptonny.android.youthservices;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.esri.android.map.Callout;
import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.MapOptions;
import com.esri.android.map.MapOptions.MapType;
//import com.esri.android.map.MapView;
import com.esri.arcgisruntime.geometry.Polygon;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.android.map.event.OnSingleTapListener;
import com.esri.android.map.event.OnStatusChangedListener;
import com.esri.core.geometry.GeometryEngine;
import com.esri.core.geometry.Point;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.SimpleMarkerSymbol;


import java.util.UUID;

/**
 * Created by JDaly on 2/25/2016.
 */
public class YouthServicesFragment extends Fragment {

    private static final String ARG_YOUTH_SERVICES_ID = "youthservices_id";
    private static final String TAG = YouthServicesFragment.class.getSimpleName();

    private ServicesItem mYouthService;
    private TextView mFacilityField;
    private TextView mAddressField;
    private TextView mFeeField;
    private TextView mContactField;
    private TextView mTitleField;
    private TextView mEmailField;
    private TextView mPhone1Field;
    private TextView mWeblinkField;
    private TextView mCategoryField;
    private TextView mDescriptionField;
    private ViewGroup mCalloutContent;
    private Callout callout;
    private MapView  mMap;


    private int mMapType;
    private int mCalloutShown;
    private String mMapChoice = "satellite";

    MenuItem mStreetsMenuItem   = null;
    MenuItem mSatelliteMenuItem = null;
    MenuItem mTopoMenuItem      = null;
    MenuItem mHybridMenuItem    = null;
    MenuItem mNatGeoMenuItem    = null;

    Polygon mCurrentMapExtent = null;

    final MapOptions mTopoBasemap      = new MapOptions(MapType.TOPO);
    final MapOptions mStreetsBasemap   = new MapOptions(MapType.STREETS);
    final MapOptions mSatelliteBasemap = new MapOptions(MapType.SATELLITE);
    final MapOptions mHybridBasemap    = new MapOptions(MapType.HYBRID);
    final MapOptions mNatGeoBasemap    = new MapOptions(MapType.NATIONAL_GEOGRAPHIC);

    private Callbacks mCallbacks;

    public interface Callbacks {
        void onYouthServiceUpdated(ServicesItem item);
    }

    @Override
    public void onAttach(Context context)  {
        super.onAttach(context);
        mCallbacks = (Callbacks)context;
    }

    public void onDetach()  {
        super.onDetach();
        mCallbacks = null;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)  {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.basemaps, menu);

        mSatelliteMenuItem   = menu.getItem(0);
        mStreetsMenuItem     = menu.getItem(1);
        mTopoMenuItem        = menu.getItem(2);
        mHybridMenuItem      = menu.getItem(3);
        mNatGeoMenuItem      = menu.getItem(4);

        mStreetsMenuItem.setChecked(true);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        mCurrentMapExtent = mMap.getVisibleArea();
        mMapType = item.getItemId();

        switch(mMapType)  {
            case R.id.menu_item_streets:
                mMapChoice = "streets";
                mMap.setMapOptions(mStreetsBasemap);
                mStreetsMenuItem.setChecked(true);
                break;

            case R.id.menu_item_topo:
                mMapChoice = "topo";
                mMap.setMapOptions(mTopoBasemap);
                mTopoMenuItem.setChecked(true);
                break;

            case R.id.menu_item_satellite:
                mMapChoice = "satellite";
                mMap.setMapOptions(mSatelliteBasemap);
                mSatelliteMenuItem.setChecked(true);
                break;

            case R.id.menu_item_hybrid:
                mMapChoice = "hybrid";
                mMap.setMapOptions(mHybridBasemap);
                mHybridMenuItem.setChecked(true);
                break;

            case R.id.menu_item_natgeo:
                mMapChoice = "natgeo";
                mMap.setMapOptions(mNatGeoBasemap);
                mNatGeoMenuItem.setChecked(true);
                break;

        }
        return true;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState)  {
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setRetainInstance(true);
        UUID serviceID = (UUID)getArguments().getSerializable(ARG_YOUTH_SERVICES_ID);
        //mYouthService = ServicesGenerator.get(getActivity()).getService(serviceID);
        mYouthService = ServicesGenerator.getService(serviceID);
    }

    @Override
    public void onPause() {
        super.onPause();
        mMap.pause();
    }

    @Override
    public void onResume() {
        super.onResume();
        mMap.unpause();

    }


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View v = inflater.inflate(R.layout.fragment_ys, container, false);
        mCalloutShown = 1;

        mMap = (MapView) v.findViewById(R.id.map);

        final GraphicsLayer graphicsLayer = new GraphicsLayer();

                mMap.setOnStatusChangedListener(new OnStatusChangedListener() {

                    private static final long serialVersionUID = 1L;

                    @Override
                    public void onStatusChanged(Object source, STATUS status) {

                        mMap.setMap(mCurrentMapExtent);

                        if (OnStatusChangedListener.STATUS.INITIALIZED == status && source == mMap) {
                            double lat, lon;
                            try {


                                lat = Double.parseDouble(mYouthService.getLat());
                                lon = Double.parseDouble(mYouthService.getLon());


                                MapOptions type = new MapOptions(MapOptions.MapType.STREETS, lat, lon, 18);

                                if (mMapChoice.equals("streets")) {
                                    type = new MapOptions(MapOptions.MapType.STREETS, lat, lon, 18);
                                } else if (mMapChoice.equals("topo")) {
                                    type = new MapOptions(MapType.TOPO, lat, lon, 18);
                                } else if (mMapChoice.equals("satellite")) {
                                    type = new MapOptions(MapType.SATELLITE, lat, lon, 18);
                                } else if (mMapChoice.equals("hybrid")) {
                                    type = new MapOptions(MapType.HYBRID, lat, lon, 18);
                                } else if (mMapChoice.equals("natgeo")) {
                                    type = new MapOptions(MapType.NATIONAL_GEOGRAPHIC, lat, lon, 18);
                                }

                                mMap.setMapOptions(type);

                                mMap.setScale(50000, false);
                                mMap.addLayer(graphicsLayer);

                                Point point = GeometryEngine.project(lon, lat, mMap.getSpatialReference());

                                int color = Color.rgb(255,0,0);

                                SimpleMarkerSymbol symbol = new SimpleMarkerSymbol(color, 20, SimpleMarkerSymbol.STYLE.CROSS);
                                Graphic marker = new Graphic(point, symbol);
                                graphicsLayer.addGraphic(marker);

                                callout = mMap.getCallout();
                                callout.hide();
                                callout.setOffset(0, -3);
                                callout.setCoordinates(point);
                                callout.setStyle(R.xml.callout);
                                callout.setMaxHeight(450);
                                callout.setMaxWidth(600);

                                TextView tv = new TextView(getActivity());
                                tv.setMaxWidth(500);

                                String attrs = "";

                                if (!mYouthService.getTitle().equals("NULL"))
                                    attrs += "Title: " + mYouthService.getTitle() + "\n";
                                if (!mYouthService.getAddress().equals("NULL"))
                                    attrs += "Address: " + mYouthService.getAddress() + "\n";
                                if (!mYouthService.getCategory().equals("NULL"))
                                    attrs += "Category: " + mYouthService.getCategory() + "\n";
                                if (!mYouthService.getFee().equals("NULL"))
                                    attrs += "Fee: " + mYouthService.getFee() + "\n";
                                if (!mYouthService.getEmail().equals("NULL"))
                                    attrs += "Email: " + mYouthService.getEmail() + "\n\n";
                                else attrs += "\n\n";

                                if (!mYouthService.getDesc().equals("NULL"))
                                    attrs += "Description: " + mYouthService.getDesc() + "\n";

                                tv.setText(
                                        mYouthService.getF_Name() + "\n"
                                                + "---------------------------------------------" + "\n"
                                                + attrs + "\n"
                                );

                                callout.setContent(tv);
                                callout.show();

                            } catch (Exception ex) {
                                lat = 40.8876;
                                lon = -72.3853;
                                mMap.setScale(50000, false);
                                mMap.centerAt(lat, lon, false);
                                Toast.makeText(getActivity(), "Because of incorrect coordinate information this facility could not be mapped.", Toast.LENGTH_LONG).show();
                            } finally {

                                mMap.setOnSingleTapListener(new OnSingleTapListener() {
                                    @Override
                                    public void onSingleTap(float v, float v1) {
                                        int [] ids = graphicsLayer.getGraphicIDs(v,v1,10,1);

                                        if(ids != null && ids.length > 0)  {
                                            if (callout.isShowing()) {
                                                callout.hide();
                                            }  else  {
                                                callout.show();
                                            }
                                        }
                                    }
                                });
                            }
                        }
                    }

                });


        return v;
    }

    public static YouthServicesFragment newInstance(UUID YouthServiceId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_YOUTH_SERVICES_ID, YouthServiceId);

        YouthServicesFragment fragment = new YouthServicesFragment();
        fragment.setArguments(args);

        return fragment;
    }


}