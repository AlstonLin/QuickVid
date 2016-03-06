package io.alstonlin.quickvid;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.lorentzos.flingswipe.SwipeFlingAdapterView;

import java.util.ArrayList;


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
        fragment.activity = activity;
        return fragment;
    }

    /**
     * Sets up the Fragment.
     * @param savedInstanceState The previous saved state
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            activity = (MainActivity) getArguments().getSerializable(ARG_ACTIVITY);
        }
    }

    /**
     * Once the View has been created, sets up the View.
     * @param inflater The Inflator of the Activity
     * @param container The container that the Fragement is in
     * @param savedInstanceState The previously saved instance of the Activity
     * @return The View once it is set up
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_explore, container, false);
        SwipeFlingAdapterView flingContainer = (SwipeFlingAdapterView) v.findViewById(R.id.fling_view);
        // Sets up Overlay
        final ImageView likeOverlay = new ImageView(activity);
        final ImageView passOverlay = new ImageView(activity);
        likeOverlay.setImageResource(R.drawable.plus_one);
        passOverlay.setImageResource(R.drawable.pass);
        final FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        // Sets up Fling Adapter
        final FlingAdapter adapter = new FlingAdapter(activity, new ArrayList<VideoItem>());
        flingContainer.setAdapter(adapter);
        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {
                adapter.getItems().remove(0);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onLeftCardExit(Object dataObject) {
                DAO.getInstance().passItem((VideoItem) dataObject);
                FrameLayout frame = (FrameLayout) activity.findViewById(R.id.container);
                if (likeOverlay.getParent() != null) frame.removeView(likeOverlay);
                if (passOverlay.getParent() != null) frame.removeView(passOverlay);
            }

            @Override
            public void onRightCardExit(Object dataObject) {
                DAO.getInstance().likeItem((VideoItem) dataObject);
                FrameLayout frame = (FrameLayout) activity.findViewById(R.id.container);
                if (likeOverlay.getParent() != null) frame.removeView(likeOverlay);
                if (passOverlay.getParent() != null) frame.removeView(passOverlay);
            }

            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {
                if (itemsInAdapter == 0){
                    DAO.getInstance().getVideoItems(adapter, adapter.getMinId(), adapter.getMaxId());
                }
            }

            @Override
            public void onScroll(float v) {
                FrameLayout frame = (FrameLayout) activity.findViewById(R.id.container);
                if (v > 0.5){
                    if (passOverlay.getParent() != null) frame.removeView(passOverlay);
                    if (likeOverlay.getParent() == null) frame.addView(likeOverlay, params);
                    float alpha = v / 1.5f;
                    likeOverlay.setAlpha(alpha > 1 ? 1 : alpha);
                } else if (v < -0.5){
                    if (likeOverlay.getParent() != null) frame.removeView(likeOverlay);
                    if (passOverlay.getParent() == null) frame.addView(passOverlay, params);
                    float alpha = v / 1.5f;
                    passOverlay.setAlpha(alpha < -1 ? 1 : -alpha);
                } else{
                    if (likeOverlay.getParent() != null) frame.removeView(likeOverlay);
                    if (passOverlay.getParent() != null) frame.removeView(passOverlay);
                }
            }
        });
        DAO.getInstance().getVideoItems(adapter, null, null);
        return v;
    }

}
