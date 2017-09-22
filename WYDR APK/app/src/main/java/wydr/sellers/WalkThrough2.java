package wydr.sellers;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;


import com.google.android.gms.analytics.Tracker;
import com.squareup.picasso.Picasso;

import wydr.sellers.activities.AppUtil;
import wydr.sellers.activities.Controller;
import wydr.sellers.activities.Home;


public class WalkThrough2 extends Fragment {
ImageView p2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_walk_through2, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Button b = (Button)getActivity().findViewById(R.id.b);
        p2 = (ImageView)getActivity().findViewById(R.id.walkthtough2);

        Picasso.with(getActivity())
                .load(AppUtil.WalkthroughURL+"images/walkthrough/intro_scr_03.png")
                .placeholder(R.drawable.default_product)
                .into(p2);

        b.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent i = new Intent(getActivity(), Home.class);
                startActivity(i);
                getActivity().finish();
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
        Controller application = (Controller) getActivity().getApplication();
        Tracker mTracker = application.getDefaultTracker();
        mTracker.setScreenName("Walkthrough2");
    }
}
