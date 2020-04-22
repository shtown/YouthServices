package gov.townofsouthamptonny.android.youthservices;

import android.content.Intent;
import androidx.fragment.app.Fragment;


/**
 * Created by JDaly on 2/25/2016.
 */
public class YouthServicesListActivity extends SingleFragmentActivity implements YouthServicesListFragment.Callbacks, YouthServicesFragment.Callbacks {

    @Override
    protected Fragment createFragment() {
        return new YouthServicesListFragment();
    }

    @Override
    protected int getLayoutResId()  {
        return R.layout.activity_masterdetail;
    }

    @Override
    public void onYouthServiceSelected(ServicesItem service) {

        if(findViewById(R.id.detail_fragment_container) == null)  {
            Intent intent = YouthServicesPagerActivity.newIntent(this, service.getUUID());
            startActivity(intent);
        }  else {
            Fragment newDetail = YouthServicesFragment.newInstance(service.getUUID());

            getSupportFragmentManager().beginTransaction()

                    .replace(R.id.detail_fragment_container, newDetail)
                    .commit();
        }


        //MapView mMapView = (MapView) findViewById(R.id.default_map);
        //mMapView.removeAllViewsInLayout();

    }

    @Override
    public void onYouthServiceUpdated(ServicesItem item)  {
        YouthServicesListFragment listFragment = (YouthServicesListFragment)getSupportFragmentManager()
                .findFragmentById((R.id.fragment_container));
        listFragment.updateUI();
    }
}
