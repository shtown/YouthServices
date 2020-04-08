package gov.townofsouthamptonny.android.youthservices;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;
import java.util.UUID;

/**
 * Created by JDaly on 2/4/2016.
 */
public class YouthServicesPagerActivity extends AppCompatActivity implements YouthServicesFragment.Callbacks{

    private static final String EXTRA_SERVICES_ID = "gov.townofsouthampton.android.youthservices.services_id";
    private ViewPager mViewPager;
    private List<ServicesItem>  mServices;


    public static Intent newIntent(Context packageContext,  UUID servicesId ) {
        Intent intent = new Intent(packageContext, YouthServicesPagerActivity.class);
        intent.putExtra(EXTRA_SERVICES_ID, servicesId);
        return intent;
    }

    @Override
    public void onYouthServiceUpdated(ServicesItem item)  {

    }
    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ys_pager);

        UUID servicesID = (UUID) getIntent().getSerializableExtra(EXTRA_SERVICES_ID);

        mViewPager = (ViewPager)findViewById(R.id.activity_ys_pager_view_pager);

        mServices = ServicesGenerator.get(this).getServices();
        FragmentManager fragmentManager = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {
            @Override
            public Fragment getItem(int position) {
                ServicesItem service = mServices.get(position);
                return YouthServicesFragment.newInstance(service.getUUID());
            }

            @Override
            public int getCount() {
                return mServices.size();
            }
        });

        for (int i = 0; i < mServices.size(); i++) {
            if (mServices.get(i).getUUID().equals(servicesID)) {
                UUID temp = mServices.get(i).getUUID();
                mViewPager.setCurrentItem(i);
                break;
            }
        }
    }
}

