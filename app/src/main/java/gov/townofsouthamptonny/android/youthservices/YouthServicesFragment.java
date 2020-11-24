package gov.townofsouthamptonny.android.youthservices;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.telecom.Call;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.esri.arcgisruntime.geometry.GeometryEngine;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.Polygon;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.Callout;
import com.esri.arcgisruntime.mapping.view.Callout.Style.LeaderPosition;
import com.esri.arcgisruntime.mapping.view.DefaultMapViewOnTouchListener;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;

import java.text.DecimalFormat;
import java.util.UUID;

import androidx.fragment.app.Fragment;



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


    private MapView  mMapView;
    private ArcGISMap mMap;

    private int mMapType;
    private String mMapChoice = "satellite";

    MenuItem mStreetsMenuItem   = null;
    MenuItem mSatelliteMenuItem = null;
    MenuItem mTopoMenuItem      = null;
    MenuItem mHybridMenuItem    = null;
    MenuItem mNatGeoMenuItem    = null;

    Polygon mCurrentMapExtent = null;


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

        mMapType = item.getItemId();
        mCurrentMapExtent = mMapView.getVisibleArea();

        switch(mMapType)  {
            case R.id.menu_item_streets:
                mMapChoice = "streets";
                mMap = new ArcGISMap(Basemap.Type.STREETS, 40.862549,-72.511397,12);
                mMapView.setMap(mMap);
                mStreetsMenuItem.setChecked(true);
                break;

            case R.id.menu_item_topo:
                mMapChoice = "topo";
                mMap = new ArcGISMap(Basemap.Type.TOPOGRAPHIC, 40.862549,-72.511397,12);
                mMapView.setMap(mMap);
                mTopoMenuItem.setChecked(true);
                break;

            case R.id.menu_item_satellite:
                mMapChoice = "satellite";
                mMap = new ArcGISMap(Basemap.Type.IMAGERY, 40.862549,-72.511397,12);
                mMapView.setMap(mMap);
                mSatelliteMenuItem.setChecked(true);
                break;

            case R.id.menu_item_hybrid:
                mMapChoice = "hybrid";
                mMap = new ArcGISMap(Basemap.Type.TERRAIN_WITH_LABELS, 40.862549,-72.511397,12);
                mMapView.setMap(mMap);
                mHybridMenuItem.setChecked(true);
                break;

            case R.id.menu_item_natgeo:
                mMapChoice = "natgeo";
                mMap = new ArcGISMap(Basemap.Type.NATIONAL_GEOGRAPHIC, 40.862549,-72.511397,12);
                mMapView.setMap(mMap);
                mNatGeoMenuItem.setChecked(true);
                break;
        }

        mMapView.setViewpointGeometryAsync(mCurrentMapExtent);

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
        //mMap.pause();
    }

    @Override
    public void onResume() {
        super.onResume();
        //mMap.unpause();

    }


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View v = inflater.inflate(R.layout.fragment_ys, container, false);
        int mCalloutShown = 1;
        Point projectedPoint = null;
        mMapView = (MapView) v.findViewById(R.id.map);
          GraphicsOverlay graphicsLayer = new GraphicsOverlay();
        mMap = new ArcGISMap(Basemap.Type.STREETS, 40.862549,-72.511397,12);

        mMapView.setMap(mMap);
        mMapView.getMap().setReferenceScale(5000);
        mMapView.getGraphicsOverlays().add(graphicsLayer);

        double lat,lon;
        final DecimalFormat decimalFormat = new DecimalFormat("#.00000");
        try {

            lat = Double.parseDouble(mYouthService.getLat());
            lon = Double.parseDouble(mYouthService.getLon());

            Point point = new Point(lon,lat, mMap.getSpatialReference());

            projectedPoint = (Point) GeometryEngine.project(point, SpatialReferences.getWgs84());
            mMapView.setViewpointCenterAsync(projectedPoint, 5000);


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



            // create a textView for the content of the callout
            TextView calloutContent = new TextView(getContext());
            calloutContent.setTextColor(Color.BLACK);
            calloutContent.setText(
                    mYouthService.getF_Name() + "\n"
                            + "---------------------------------------------" + "\n"
                            + attrs + "\n"
            );

            // create callout
            callout = mMapView.getCallout();

            callout.setLocation(projectedPoint);
            callout.setContent(calloutContent);
            callout.show();

        } catch (Exception ex) {
            lat = 40.8876;
            lon = -72.3853;
            Point home= new Point(lon,lat, SpatialReferences.getWgs84());
            mMapView.setViewpointCenterAsync(home,5000);

            Toast.makeText(getActivity(), "Because of incorrect coordinate information this facility could not be mapped.", Toast.LENGTH_LONG).show();
        } finally    {

        }

        final SimpleMarkerSymbol markerSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, Color.rgb(226,119,40), 20.0f);
        markerSymbol.setOutline(new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.BLUE, 2.0f));
        Graphic pointGraphic = new Graphic(projectedPoint, markerSymbol);
        graphicsLayer.getGraphics().add(pointGraphic);

        mMapView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                                              @Override
                                              public void onFocusChange(View v, boolean hasFocus) {
                                                  String foo = "bar";
                                              };
                                          }

        );


        Point finalProjectedPoint = projectedPoint;
        mMapView.setOnTouchListener(new DefaultMapViewOnTouchListener(getContext(),mMapView)  {

                    private static final long serialVersionUID = 1L;

                    public boolean onTouchEvent(MotionEvent event)  {
                        return true;
                    }

                    @Override
                    public boolean onSingleTapConfirmed(MotionEvent e)  {
                        if (callout.isShowing()) {
                            callout.dismiss();
                        }
                        else {
                            callout.show();
                        }
                        mMapView.setViewpointCenterAsync(finalProjectedPoint);

                        return super.onSingleTapConfirmed(e);
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