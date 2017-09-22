package wydr.sellers.activities;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by surya on 13/10/15.
 */
public class TabsForwardAdapter extends FragmentPagerAdapter

{

    public TabsForwardAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int index) {

        switch (index) {
            case 0:
                // Top  fragment activity
                return new ForwardRecent();
            case 1:
                //  fragment activity
                return new ForwardAll();

        }

        return null;
    }

    @Override
    public int getCount() {
        // get item count - equal to number of tabs
        return 2;
    }

}
