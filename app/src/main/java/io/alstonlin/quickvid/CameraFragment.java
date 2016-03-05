package io.alstonlin.quickvid;

import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The Fragment for taking a picture.
 */
public class CameraFragment extends Fragment implements SurfaceHolder.Callback {

    public static final String VIDEO_FILE_NAME = "temp.mp4";
    private static final String ARG_ACTIVITY = "activity";
    private static final int MAX_DURATION = 30000;

    private static Camera camera;
    private SurfaceHolder holder;
    private MediaRecorder recorder;
    private MainActivity activity;
    private boolean recording = false;

    /**
     * Factory method; use this instead of constructor to instantiate the Fragment.
     * @return A new instance of the Fragment
     */
    public static CameraFragment newInstance(MainActivity activity) {
        CameraFragment fragment = new CameraFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_ACTIVITY, activity);
        fragment.setArguments(args);
        fragment.activity = activity;
        return fragment;
    }

    /**
     * Gets the Camera Object for the phone.
     * @return The Camera of the phone, if available
     */
    public static Camera getCameraInstance(){
        if (camera == null) {
            try {
                camera = Camera.open(); // attempt to get a Camera instance
                camera.setDisplayOrientation(90); //Portait Mode
            } catch (Exception e) {
                Logger.getLogger(MainActivity.class.getName()).log(Level.SEVERE, e.getMessage());
            }
        }
        return camera;
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
        // Inflates the view
        View v = inflater.inflate(R.layout.fragment_camera, container, false);
        SurfaceView cameraView = (SurfaceView) v.findViewById(R.id.surface_camera);
        holder = cameraView.getHolder();
        holder.addCallback(this);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        // Sets up the recorder
        recorder = new MediaRecorder();
        // Sets up the FAB
        FloatingActionButton captureButton = (FloatingActionButton) v.findViewById(R.id.capture);
        captureButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    initRecorder();
                    recorder.start();
                    recording = true;
                    return true;
                } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                    recorder.stop();
                    recording = false;
                    DAO.uploadVideo(activity);
                    return true;
                }
                return false;
            }
        });
        return v;
    }

    /**
     * Instantiates the MediaRecorder.
     */
    private void initRecorder() {
        Camera camera = getCameraInstance();
        camera.unlock();
        recorder.setCamera(camera);
        recorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
        recorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        recorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_480P));
        recorder.setMaxDuration(MAX_DURATION);
        recorder.setOutputFile(activity.getFilesDir() + VIDEO_FILE_NAME);
        prepareRecorder();
    }

    /**
     * Safely prepares the recorder to record video.
     */
    private void prepareRecorder() {
        recorder.setPreviewDisplay(holder.getSurface());
        try {
            recorder.prepare();
        } catch (IllegalStateException | IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    /**
     * Setups the Camera once the SurfaceView has been created.
     * @param holder The SurfaceHolder of the View
     */
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Camera camera = getCameraInstance();
        if (camera != null) {
            Camera.Parameters params = camera.getParameters();
            camera.setParameters(params);
            try {
                camera.setPreviewDisplay(holder);
            } catch (IOException e) {
                e.printStackTrace();
            }
            camera.startPreview();
        }
    }

    /**
     * Releases the Camera once the SurfaceView has been destroyed
     * @param holder The SurfaceHolder of the View
     */
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (recording) {
            recorder.stop();
        }
        recorder.release();
    }
}
