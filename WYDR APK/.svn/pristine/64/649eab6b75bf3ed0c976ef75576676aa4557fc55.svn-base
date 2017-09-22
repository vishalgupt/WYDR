package wydr.sellers;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


import com.google.android.gms.analytics.Tracker;
import com.squareup.picasso.Picasso;

import wydr.sellers.activities.AppUtil;
import wydr.sellers.activities.Controller;


public class walk_through3 extends Fragment {
    ImageView p3;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_walk_through3, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        p3 = (ImageView)getActivity().findViewById(R.id.walkthtough3);
        Picasso.with(getActivity())
                .load(AppUtil.WalkthroughURL+"images/walkthrough/intro_scr_02.png")
                .placeholder(R.drawable.default_product)
                .into(p3);
    }


    @Override
    public void onResume() {
        super.onResume();
        Controller application = (Controller) getActivity().getApplication();
        Tracker mTracker = application.getDefaultTracker();
        mTracker.setScreenName("Walkthrough3");
    }

}
