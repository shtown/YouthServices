package gov.townofsouthamptonny.android.youthservices;

import android.support.v4.app.Fragment;

public class YouthServicesActivity extends SingleFragmentActivity {
    public static final String EXTRA_YOUTH_SERVICES_ID = "gov.townofsouthamptonny.android.youthservices.services_id";

    @Override
    protected Fragment createFragment()  {
        return new YouthServicesFragment();
    }
}
