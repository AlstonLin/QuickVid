package io.alstonlin.quickvid;

import android.os.Bundle;
import android.support.v4.app.Fragment;


public class ExploreFragment extends Fragment {

    private static final String ARG_ACTIVITY = "activity";
    private MainActivity activity;

    /**
     * Use this Factory method to create the Fragment instead of the constructor.
     * @param activity The Activity this Fragment will be attached to
     * @return The new Fragment instance
     */
    public static ExploreFragment newInstance(MainActivity activity) {
        final ExploreFragment fragment = new ExploreFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_ACTIVITY, activity);
        fragment.setArguments(args);
        fragment.activity = activity;
        return fragment;
    }
}
