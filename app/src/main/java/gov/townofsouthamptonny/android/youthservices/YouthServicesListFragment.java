package gov.townofsouthamptonny.android.youthservices;


import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


/**
 * Created by JDaly on 2/18/2016.
 */
public class  YouthServicesListFragment extends Fragment  {

    private static final String TAG = "YouthServicesListFragment";
    private static final int REQUEST_ERROR = 0;
    private static final int REQUEST_CODE_ASK_FINE_LOCATION = 110;
    private ServiceAdapter mAdapter;
    private RecyclerView mYouthServicesRecyclerView;
    private boolean mSubtitleVisible;
    private Callbacks mCallbacks;
    private GoogleApiClient mClient;
    private ProgressDialog progress;
    private double lat, lon;
    private String selectedService, selectedTown;

    boolean setItemSelected = false;

    PopupWindow popupWindow;
    TextView hotlines;

    private List<ServicesItem> mItems = new ArrayList<>();

    public interface Callbacks  {
        void onYouthServiceSelected(ServicesItem item);
    }

    //@Override
    //public void onAttach(Activity activity) {
    //    super.onAttach(activity);
    //    mCallbacks = (Callbacks) activity;
    //}

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Activity activity = getActivity();
        mCallbacks = (Callbacks) activity;
    }

    @Override
    public void onDetach()  {
        super.onDetach();
        mCallbacks = null;
    }

    private class ServiceHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ServicesItem mServicesItem;

        private TextView mNameView;
        private TextView mAddressView;
        private TextView mPhoneView;
        private TextView mDistFromCenterView;
        private TextView mFee;
        private ImageView mPhone;
        private ImageView mEmail;
        private ImageView mWebPage;
        private ImageView mDirections;

        private PackageManager mPackageManager;

        @Override
        public void onClick(View v) {

            mCallbacks.onYouthServiceSelected(mServicesItem);
        }

        public ServiceHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);

            mNameView     = (TextView)itemView.findViewById(R.id.list_item_ys_name_text_view);
            mAddressView  = (TextView)itemView.findViewById(R.id.list_item_ys_address_text_view);
            mPhoneView    = (TextView)itemView.findViewById(R.id.list_item_ys_phone_text_view);
            mFee          = (TextView)itemView.findViewById(R.id.list_item_ys_fee_text_view);
            mPhone        = (ImageView)itemView.findViewById(R.id.list_phone);
            mEmail        = (ImageView)itemView.findViewById(R.id.list_email);
            mWebPage      = (ImageView)itemView.findViewById(R.id.list_http);
            mDirections   = (ImageView)itemView.findViewById(R.id.list_directions);

            mDistFromCenterView = (TextView)itemView.findViewById(R.id.list_item_ys_dist_text_view);
        }

        public void bindServicesItem(ServicesItem item) {

            DecimalFormat df = new DecimalFormat("#.#");
            df.setRoundingMode(RoundingMode.CEILING);

            final String telno    = item.getPhone1();
            final String webpage  = item.getWebLink();
            final String XYCoords = item.getLat() + "," + item.getLon();
            final String email    = item.getEmail();

            mServicesItem = item;

            mNameView.setText(item.getF_Name().equals("NULL") ? "Name: N/A" : item.getF_Name());
            mAddressView.setText(item.getAddress().equals("NULL") ? "Address: N/A" : item.getAddress());
            mPhoneView.setText(item.getPhone1().equals("NULL")? "Phone: N/A" : item.getPhone1());
            mDistFromCenterView.setText(df.format(item.getDistFromCenter()) + " mile(s)");
            mFee.setText(item.getFee().equals("NULL") ? "Fee: N/A" : "Fee: " + item.getFee());

            mEmail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("message/rfc822");

                    i.putExtra(Intent.EXTRA_EMAIL, new String[]{mServicesItem.getEmail()});
                    i.putExtra(Intent.EXTRA_SUBJECT, "Youth Services Inquiry");
                    i.putExtra(Intent.EXTRA_TEXT, "Replace this with your message");

                    try {
                        startActivity(Intent.createChooser(i, "Choose email..."));
                    } catch (android.content.ActivityNotFoundException ex) {
                        Toast.makeText(getActivity(), "Mail client not found: " + ex, Toast.LENGTH_SHORT).show();
                    }
                }
            });


            mPhone.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    String telformat = telno.replaceAll("[^0-9]", "");
                    telformat = "tel:" + telformat;

                    Uri number = Uri.parse(telformat);
                    Intent callIntent = new Intent(Intent.ACTION_DIAL, number);
                    startActivity(callIntent);
                }
            });

            mWebPage.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    Uri link = Uri.parse(webpage);
                    Intent callIntent = new Intent(Intent.ACTION_VIEW, link);
                    startActivity(callIntent);
                }
            });

            mDirections.setOnClickListener(new View.OnClickListener()  {

                public void onClick(View v)  {
                    Uri dest = Uri.parse("google.navigation:q=" + XYCoords);
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, dest);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    if(mapIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                        startActivity(mapIntent);
                    }
                }
            });
        }
    }

    private static class StatHandler extends Handler {
        public void handleMessage(Message msg)  {
            super.handleMessage(msg);
        }
    }

    private class FetchItemsTask extends AsyncTask<Void, Void, List<ServicesItem>>  {

        @Override
        protected List<ServicesItem> doInBackground(Void... params) {
            ////Log.i(TAG, "doInBackground()");
            return new YSFetchr().fetchItems();
        }
        @Override
        protected void onPreExecute() {
            ////Log.i(TAG, "onPreExecute()");

            final Handler handle = new StatHandler();

            progress = new ProgressDialog(getActivity());
            progress.setMax(100);
            progress.setMessage("Downloading data for service facilities...");
            progress.setTitle("Youth Services Program");
            progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progress.show();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        while (progress.getProgress() <= progress
                                .getMax()) {
                            Thread.sleep(200);
                            handle.sendMessage(handle.obtainMessage());
                            if (progress.getProgress() == progress
                                    .getMax()) {
                                progress.dismiss();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }

        @Override
        protected void onPostExecute(List<ServicesItem> items) {

            ////Log.d(TAG, "onPostExecute() lat: " + Double.toString(lat) + " lon:" + Double.toString(lon));
            ////Log.i(TAG, "setItemSelected being set to true");
            setItemSelected = true;
            getLocationServicesClient();
            mItems = items;

            ServicesGenerator.addList(mItems);
            setupAdapter();
            updateUI();
            progress.dismiss();
        }


    }

    private class ServiceAdapter extends RecyclerView.Adapter<ServiceHolder> implements Filterable {
        private List<ServicesItem> mServicesItems;
        private List<ServicesItem> mServicesFilterItems;
        private ServiceFilter mServiceFilter;


        public ServiceAdapter(List<ServicesItem> servicesItems) {
            mServicesItems = servicesItems;
        }

        @Override
        public ServiceHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());

            View view = layoutInflater.inflate(R.layout.cards, parent, false);
            return new ServiceHolder(view);
        }

        @Override
        public void onBindViewHolder(ServiceHolder serviceHolder, int position)  {
            ServicesItem servicesItem = mServicesItems.get(position);
            serviceHolder.bindServicesItem(servicesItem);
        }

        @Override
        public int getItemCount()  {
            return mServicesItems.size();
        }

        public void setServicesItems(List<ServicesItem> services)  {
            mServicesItems = services;
        }

        @Override
        public Filter getFilter() {
            if(mServiceFilter == null)  {
                mServiceFilter = new ServiceFilter();
            }

            return mServiceFilter;
        }

        private class ServiceFilter  extends Filter {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                String searchTerm = constraint.toString();
                FilterResults results = new FilterResults();

                float[] distance = new float[1];
                ArrayList<ServicesItem> filterList = new ArrayList<ServicesItem>();

                double yslat, yslon, meters2miles;

                for (ServicesItem item : mItems) {
                    try {
                        yslat = Double.parseDouble(item.getLat());
                        yslon = Double.parseDouble(item.getLon());

                        Location.distanceBetween(lat, lon, yslat, yslon, distance);
                        meters2miles = distance[0] * 0.00062137;

                        item.setDistFromCenter(meters2miles);

                    } catch (Exception e) {
                        yslat = 40.8876;
                        yslon = -72.3853;
                        meters2miles = 0.999;
                    }

                    if (selectedService.equals("All Services") || item.getCategory().contains(selectedService)) {
                        if(selectedTown.equals("All Towns") || item.getTownship().equals(selectedTown))  {
                            filterList.add(item);
                        }
                    }
                }

                Collections.sort(filterList, new Comparator<ServicesItem>() {
                    @Override
                    public int compare(ServicesItem lhs, ServicesItem rhs) {
                        if (lhs.getDistFromCenter() == rhs.getDistFromCenter())
                            return 0;
                        return lhs.getDistFromCenter() < rhs.getDistFromCenter() ? -1 : 1;
                    }
                });

                results.count = filterList.size();
                results.values = filterList;

                return results;
            }

            @Override
            @SuppressWarnings("unchecked")
            protected void publishResults(CharSequence constraint, FilterResults results) {
                //Log.d(TAG, "publishResults() called, setItemSelected is: " + setItemSelected);

                ServicesGenerator.addList((ArrayList<ServicesItem>) results.values);  //problem with unchecked cast
                setServicesItems((ArrayList<ServicesItem>) results.values);//problem with unchecked cast


                if (mAdapter == null) {
                    mYouthServicesRecyclerView.setAdapter(mAdapter);
                    mAdapter = new ServiceAdapter(mServicesItems);

                } else {

                    mYouthServicesRecyclerView.setAdapter(mAdapter);
                    mAdapter.setServicesItems(mServicesItems);
                    mAdapter.notifyDataSetChanged();
                }

                String subtitle = getResources().getQuantityString(R.plurals.subtitle_plural, results.count, results.count);

                if(results.count == 0 && setItemSelected == true)  {

                    //Log.i(TAG, "setItemSelected is true");
                    Toast.makeText(getActivity(), "There are no results for this query!  Please try another combination", Toast.LENGTH_LONG).show();
                }



//                if (!mSubtitleVisible) {
//                    subtitle = null;
//                }
                AppCompatActivity activity = (AppCompatActivity) getActivity();
                activity.getSupportActionBar().setSubtitle(subtitle);
            }
        }
    }

    public static YouthServicesListFragment newInstance()  {
        return new YouthServicesListFragment();
    }


    @Override
    public void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);

        //Log.i(TAG, "onCreate() called");
        getLocationServicesClient();
        setHasOptionsMenu(true);
        setRetainInstance(true);
        new FetchItemsTask().execute();
    }

    @Override
    public void onStart() {
        super.onStart();

        //Log.d(TAG, "onStart() lat: " + Double.toString(lat) + " lon:" + Double.toString(lon));
    }

    @Override
    public void onStop()  {
        //Log.d(TAG, "onStop()");
        mClient.disconnect();
        super.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume()  {
        super.onResume();

        int errorCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getContext());

        if(errorCode != ConnectionResult.SUCCESS)  {
            Dialog erroDialog = GooglePlayServicesUtil
                    .getErrorDialog(errorCode, getActivity(), REQUEST_ERROR, new DialogInterface.OnCancelListener()  {
                       @Override
                       public void onCancel(DialogInterface dialog)  {

                       }
                    });

            erroDialog.show();
        }
        updateUI();
    }

    public void getLocationServicesClient()  {

        mClient = new GoogleApiClient.Builder(getActivity())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle bundle)  {
                        ////Log.d(TAG, "GoogleApi connected");
                        LocationRequest request = LocationRequest.create();
                        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                        request.setNumUpdates(1);
                        request.setInterval(0);

                        if(ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)  {
                            if (!shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                                showMessageOKCancel("Access to location is needed for this program to run",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION},
                                                        REQUEST_CODE_ASK_FINE_LOCATION);
                                            }
                                        });
                                return;
                            }
                        }
                        LocationServices.FusedLocationApi
                                .requestLocationUpdates(mClient, request, new LocationListener() {
                                    @Override
                                    public void onLocationChanged(Location location) {
                                        lat = location.getLatitude();
                                        lon = location.getLongitude();

                                        List<ServicesItem> temp = new ArrayList<ServicesItem>();

                                        for (ServicesItem item : mItems) {
                                            float[] distance = new float[1];

                                            try {
                                                double yslat = Double.parseDouble(item.getLat());
                                                double yslon = Double.parseDouble(item.getLon());
                                                Location.distanceBetween(lat, lon, yslat, yslon, distance);
                                                double meters2miles = distance[0] * 0.00062137;
                                                ////Log.d(TAG, "LocationChanged() lat: " + Double.toString(lat) + " lon:" + Double.toString(lon) + " distance: " + Float.toString(distance[0]) + " miles: " + Double.toString(meters2miles));
                                                item.setDistFromCenter(meters2miles);
                                            } catch (Exception e) {

                                            }

                                        }

                                        Collections.sort(mItems, new Comparator<ServicesItem>() {
                                            @Override
                                            public int compare(ServicesItem lhs, ServicesItem rhs) {
                                                if (lhs.getDistFromCenter() == rhs.getDistFromCenter())
                                                    return 0;
                                                return lhs.getDistFromCenter() < rhs.getDistFromCenter() ? -1 : 1;
                                            }
                                        });

                                        ServicesGenerator.addList(mItems);
                                        setupAdapter();
                                    }
                                });

                    }

                    @Override
                    public void onConnectionSuspended(int i)  {
                        //Toast.makeText(getActivity(), "on connection suspended", Toast.LENGTH_SHORT).show();
                        ////Log.d(TAG, "Connection Suspended");
                    }
                })
                .build();
        mClient.connect();

    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(getActivity())
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_FINE_LOCATION:
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getActivity(), "Access to Fine Location Resources refused", Toast.LENGTH_LONG).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState)  {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)  {
        super.onCreateOptionsMenu(menu, inflater);
/*
        //inflater.inflate(R.menu.fragment_ys_menu, menu);
        //MenuItem subtitleItem = menu.findItem(R.id.menu_item_show_subtitle);

        //if(mSubtitleVisible) {
//            subtitleItem.setTitle(R.string.hide_subtitle);
//        }else {
//            subtitleItem.setTitle(R.string.show_subtitle);
//        }
*/
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)  {
        //Log.i(TAG, "onOptionsItemSelected() called");
        switch (item.getItemId()) {

            case R.id.menu_item_show_subtitle:
                //mSubtitleVisible = !mSubtitleVisible;
                //getActivity().invalidateOptionsMenu();
                updateSubtitle();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.list_masterdetail, container, false);

        Button showHotlineInfo = (Button)view.findViewById(R.id.hotlineButton);
        Button showCalendarInfo = (Button)view.findViewById(R.id.calendarButton);


        showHotlineInfo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                callPopup();
            }
        });

        showCalendarInfo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                callCalendar();
            }
        });

        Spinner servicesSpinner = (Spinner)view.findViewById(R.id.services_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),R.array.services_array, R.layout.support_simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        servicesSpinner.setAdapter(adapter);

        servicesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                //Log.d(TAG, "services onItemSelectedListener called");

                String item = parent.getItemAtPosition(position).toString();
                selectedService = item;
                mAdapter.getFilter().filter(item, new Filter.FilterListener() {
                    @Override
                    public void onFilterComplete(int count) {
                        //Log.d(TAG, "onFilterComplete called");
                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Spinner townsSpinner = (Spinner)view.findViewById(R.id.towns_spinner);
        adapter = ArrayAdapter.createFromResource(getActivity(),R.array.town_array, R.layout.support_simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        townsSpinner.setAdapter(adapter);

        townsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                //Log.d(TAG, "towns onItemSelectedListener called");

                String item = parent.getItemAtPosition(position).toString();
                selectedTown = item;
                mAdapter.getFilter().filter(item, new Filter.FilterListener() {
                    @Override
                    public void onFilterComplete(int count) {
                        //Log.d(TAG, "towns onFilterComplete called");
                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mYouthServicesRecyclerView = (RecyclerView) view.findViewById(R.id.ys_recycler_view);
        mYouthServicesRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        updateUI();
        return view;
    }

    private void setupAdapter()  {
        if(isAdded()) {
            mYouthServicesRecyclerView.setAdapter(new ServiceAdapter(mItems));
        }
    }



    public void updateUI() {
        ServicesGenerator servicesGenerator = ServicesGenerator.get(getActivity());
        List<ServicesItem> services =  ServicesGenerator.getServices();

        if(mAdapter == null) {
            mAdapter = new ServiceAdapter(services);
            mYouthServicesRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.setServicesItems(services);
            mAdapter.notifyDataSetChanged();
        }

        updateSubtitle();
    }

    private void updateSubtitle() {
        ServicesGenerator servicesGenerator = ServicesGenerator.get(getActivity());
        int servicesCount = ServicesGenerator.getServices().size();

        String subtitle = getResources().getQuantityString(R.plurals.subtitle_plural, servicesCount, servicesCount);

//        if (!mSubtitleVisible) {
//            subtitle = null;
//        }
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.getSupportActionBar().setSubtitle(subtitle);
    }


    private void callPopup() {

        LayoutInflater layoutInflater = (LayoutInflater) getActivity()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View popupView = layoutInflater.inflate(R.layout.popup, null);

        popupWindow= new PopupWindow(popupView,AbsListView.LayoutParams.WRAP_CONTENT, AbsListView.LayoutParams.MATCH_PARENT,true);

        popupWindow .setTouchable(true);
        popupWindow .setFocusable(true);

        popupWindow .showAtLocation(popupView, Gravity.CENTER, 0, 0);
        hotlines = (TextView) popupView.findViewById(R.id.hotlineText);

        Resources res = getResources();

        String[] hotentries = res.getStringArray(R.array.hotlines_array);

        String allentries = "\n\n";

        for(String entry: hotentries) {
            allentries += entry + "\n\n";
        }

        hotlines.setText(allentries);


        popupView.findViewById(R.id.cancelbutton)
                .setOnClickListener(new View.OnClickListener() {
                    public void onClick(View arg0) {

                        popupWindow.dismiss();
                    }
                });
    }

    private void callCalendar() {

        LayoutInflater layoutInflater = (LayoutInflater) getActivity()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View popupView = layoutInflater.inflate(R.layout.calendar, null);

        popupWindow= new PopupWindow(popupView,AbsListView.LayoutParams.WRAP_CONTENT, AbsListView.LayoutParams.MATCH_PARENT,true);

        popupWindow .setTouchable(true);
        popupWindow .setFocusable(true);

        popupWindow .showAtLocation(popupView, Gravity.CENTER, 0, 0);
        hotlines = (TextView) popupView.findViewById(R.id.calendarText);
        hotlines.setClickable(true);



        String allentries =
        "<p>&nbsp;</p><a href=\"http://sagharboronline.com/hamptons-events/\">Sag Harbor Online</a>" + "<br><br>" +
        "<a href=\"http://www.27east.com/hamptons-events-calendar/\">27 East</a>" + "<br><br>" +
        "<a href=\"http://hamptons.macaronikid.com/calendar/\">Macaroni Kid</a>" + "<br><br>" +
        "<a href=\"http://www.hamptons.com/calendar\\\">Hamptons.com</a>" + "<br><br>" +
        "<a href=\"https://mommypoppins.com/events?area%5B%5D=116\">Mommy Poppins</a>" + "<br><br>" +
        "<a href=\"http://www.sagharborkids.org\">Sag Harbor For Kids</a>" + "<br><br>" +
        "<a href=\"http://eastendlocal.com/events/\">East End Local</a>" + "<br><br>" +
        "<a href=\"http://events.danspapers.com/events/\">Dan\'s Papers</a>" + "<br><br>" +
        "<a href=\"http://www.longislandbrowser.com/community/events/\">Long Island Browser</a>" + "<br><br>" +
        "<a href=\"http://patch.com/new-york/westhampton-hamptonbays/calendar\">Patch</a>" + "<br><br>" +
        "<a href=\"http://www.newsday.com/entertainment/long-island-events\">Newsday</a>";

        //Resources res = getResources();

        //String[] hotentries = res.getStringArray(R.array.calendar_array);


        //for(String entry: hotentries) {
            //allentries += entry + "<p>&nbsp;</p>";
        //}

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            ((TextView) hotlines.findViewById(R.id.calendarText)).setMovementMethod(LinkMovementMethod.getInstance());
            ((TextView) hotlines.findViewById(R.id.calendarText)).setText(Html.fromHtml(allentries));
        }
        else {
            ((TextView) hotlines.findViewById(R.id.calendarText)).setMovementMethod(LinkMovementMethod.getInstance());
            ((TextView) hotlines.findViewById(R.id.calendarText)).setText(Html.fromHtml(allentries));
        }

        popupView.findViewById(R.id.cancelbutton)
                .setOnClickListener(new View.OnClickListener() {
                    public void onClick(View arg0) {

                        popupWindow.dismiss();
                    }
                });
    }
}

