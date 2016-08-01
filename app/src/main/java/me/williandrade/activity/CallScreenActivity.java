package me.williandrade.activity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.sinch.android.rtc.AudioController;
import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallEndCause;
import com.sinch.android.rtc.calling.CallState;
import com.sinch.android.rtc.video.VideoCallListener;
import com.sinch.android.rtc.video.VideoController;

import android.content.Context;
import android.graphics.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.media.AudioManager;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import me.williandrade.R;
import me.williandrade.dto.UserDTO;
import me.williandrade.service.SinchService;
import me.williandrade.util.AudioPlayer;
import me.williandrade.util.BaseActivity;

public class CallScreenActivity extends BaseActivity {

    static final String TAG = CallScreenActivity.class.getSimpleName();

    private AudioPlayer mAudioPlayer;

    private String mCallId;
    private boolean mVideoViewsAdded = false;


    private RelativeLayout callScreenBackView;

    private LinearLayout callTalkingView;
    private TextView callTalkingName;
    private RelativeLayout callLocalView;
    private ImageView callTalkingVoiceBtn;
    private ImageView callTalkingCallEndBtn;
    private ImageView callTalkingVideoBtn;

    private LinearLayout callView;
    private TextView callName;
    private TextView callFunction;
    private ImageView callCancelBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_screen);

        callScreenBackView = (RelativeLayout) findViewById(R.id.callScreenBackView);
        callTalkingView = (LinearLayout) findViewById(R.id.callTalkingView);
        callTalkingName = (TextView) findViewById(R.id.callTalkingName);
        callLocalView = (RelativeLayout) findViewById(R.id.callLocalView);
        callTalkingVoiceBtn = (ImageView) findViewById(R.id.callTalkingVoiceBtn);
        callTalkingCallEndBtn = (ImageView) findViewById(R.id.callTalkingCallEndBtn);
        callTalkingVideoBtn = (ImageView) findViewById(R.id.callTalkingVideoBtn);
        callView = (LinearLayout) findViewById(R.id.callView);
        callName = (TextView) findViewById(R.id.callName);
        callFunction = (TextView) findViewById(R.id.callFunction);
        callCancelBtn = (ImageView) findViewById(R.id.callCancelBtn);

        mAudioPlayer = new AudioPlayer(this);

        mCallId = getIntent().getStringExtra(SinchService.CALL_ID);
    }

    @Override
    public void onServiceConnected() {
        Call call = getSinchServiceInterface().getCall(mCallId);
        if (call != null) {
            call.addCallListener(new SinchCallListener());

            getUserByUid(mCallId);

            callCancelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    endCall();
                }
            });

            callTalkingCallEndBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    endCall();
                }
            });

            try {
                getSinchServiceInterface().getVideoController().setCaptureDevicePosition(getFrontFacingCameraId());
            } catch (Exception e) {
                Log.d("Index Activity", "Error to get Camera");
            }

        } else {
            Log.e(TAG, "Started with invalid callId, aborting.");
            finish();
        }
    }

    private void addVideoViews() {
        if (mVideoViewsAdded || getSinchServiceInterface() == null) {
            return; //early
        }

        final VideoController videoController = getSinchServiceInterface().getVideoController();
        if (videoController != null) {

            try {
                videoController.setCaptureDevicePosition(getFrontFacingCameraId());
            } catch (Exception e) {
                Log.d("Index Activity", "Error to get Camera");
            }

            callLocalView.addView(videoController.getLocalView());
            callLocalView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    videoController.toggleCaptureDevicePosition();
                }
            });

            callScreenBackView.addView(videoController.getRemoteView());
            mVideoViewsAdded = true;
        }
    }

    private void removeVideoViews() {
        if (getSinchServiceInterface() == null) {
            return; // early
        }

        VideoController vc = getSinchServiceInterface().getVideoController();
        if (vc != null) {
            callScreenBackView.removeView(vc.getRemoteView());

            callLocalView.removeView(vc.getLocalView());
            mVideoViewsAdded = false;
        }
    }

    private void endCall() {
        mAudioPlayer.stopRingtone();
        Call call = getSinchServiceInterface().getCall(mCallId);
        if (call != null) {
            removeVideoViews();
            call.hangup();
        }
        finish();
    }

    private Integer getFrontFacingCameraId() throws CameraAccessException {
        CameraManager cManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        for (final String cameraId : cManager.getCameraIdList()) {
            CameraCharacteristics characteristics = cManager.getCameraCharacteristics(cameraId);
            int cOrientation = characteristics.get(CameraCharacteristics.LENS_FACING);
            if (cOrientation == CameraCharacteristics.LENS_FACING_FRONT)
                return Integer.parseInt(cameraId);
        }
        return null;
    }

    public void getUserByUid(final String uid) {

        final Gson gson = new Gson();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("logged");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String json = ds.getValue(String.class);
                    UserDTO user = gson.fromJson(json, UserDTO.class);

                    if (user.getUid().equals(uid)) {
                        callName.setText(user.getDisplayName());
                        callTalkingName.setText(user.getDisplayName());
                        callFunction.setText(user.getDoing());
                    }


                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("UserDAO", databaseError.getMessage());
            }
        });

    }

    private class SinchCallListener implements VideoCallListener {

        @Override
        public void onCallEnded(Call call) {
            CallEndCause cause = call.getDetails().getEndCause();
            Log.d(TAG, "Call ended, cause: " + cause.toString());
            setVolumeControlStream(AudioManager.USE_DEFAULT_STREAM_TYPE);

            endCall();
        }

        @Override
        public void onCallEstablished(Call call) {
            mAudioPlayer.stopProgressTone();
            setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
            AudioController audioController = getSinchServiceInterface().getAudioController();
            audioController.enableSpeaker();

            callTalkingView.setAlpha(1);
            callView.setAlpha(0);
        }

        @Override
        public void onCallProgressing(Call call) {
            mAudioPlayer.playProgressTone();
        }

        @Override
        public void onShouldSendPushNotification(Call call, List<PushPair> pushPairs) {
        }

        @Override
        public void onVideoTrackAdded(Call call) {
            Log.d(TAG, "Video track added");
            addVideoViews();
        }
    }

}
