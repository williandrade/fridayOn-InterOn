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
import com.sinch.android.rtc.video.VideoCallListener;
import com.sinch.android.rtc.video.VideoController;

import android.content.Context;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import me.williandrade.R;
import me.williandrade.dto.UserDTO;
import me.williandrade.service.SinchService;
import me.williandrade.util.AudioPlayer;
import me.williandrade.util.BaseActivity;

public class IncomingCallScreenActivity extends BaseActivity {

    static final String TAG = IncomingCallScreenActivity.class.getSimpleName();
    private String mCallId;
    private AudioPlayer mAudioPlayer;

    private boolean mVideoViewsAdded = false;

    private RelativeLayout incomingCallBackView;

    private LinearLayout incomingTalkingView;
    private TextView incomingTalkingName;
    private RelativeLayout incomingCallLocalView;
    private ImageView incomingCallTalkingVoiceBtn;
    private ImageView incomingTalkingCallEndBtn;
    private ImageView incomingCallTalkingVideoBtn;

    private LinearLayout incomingView;
    private TextView incomingName;
    private TextView incomingFunction;
    private ImageView incomingRefuseCall;
    private ImageView incomingAcceptCall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incoming_call_screen);

        incomingCallBackView = (RelativeLayout) findViewById(R.id.incomingCallBackView);
        incomingTalkingView = (LinearLayout) findViewById(R.id.incomingTalkingView);
        incomingTalkingName = (TextView) findViewById(R.id.incomingTalkingName);
        incomingCallLocalView = (RelativeLayout) findViewById(R.id.incomingCallLocalView);
        incomingCallTalkingVoiceBtn = (ImageView) findViewById(R.id.incomingCallTalkingVoiceBtn);
        incomingTalkingCallEndBtn = (ImageView) findViewById(R.id.incomingTalkingCallEndBtn);
        incomingCallTalkingVideoBtn = (ImageView) findViewById(R.id.incomingCallTalkingVideoBtn);
        incomingView = (LinearLayout) findViewById(R.id.incomingView);
        incomingName = (TextView) findViewById(R.id.incomingName);
        incomingFunction = (TextView) findViewById(R.id.incomingFunction);
        incomingRefuseCall = (ImageView) findViewById(R.id.incomingRefuseCall);
        incomingAcceptCall = (ImageView) findViewById(R.id.incomingAcceptCall);

        mAudioPlayer = new AudioPlayer(this);
        mAudioPlayer.playRingtone();

        mCallId = getIntent().getStringExtra(SinchService.CALL_ID);
    }

    @Override
    protected void onServiceConnected() {
        Call call = getSinchServiceInterface().getCall(mCallId);
        if (call != null) {

            call.addCallListener(new SinchCallListener());

            getUserByUid(mCallId);


            incomingAcceptCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    answerClicked();
                }
            });

            incomingRefuseCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    endCall();
                }
            });

            incomingTalkingCallEndBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    endCall();
                }
            });

            addVideoViews();

        } else {
            Log.e(TAG, "Started with invalid callId, aborting");
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

            incomingCallLocalView.addView(videoController.getLocalView());
            incomingCallLocalView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    videoController.toggleCaptureDevicePosition();
                }
            });

            incomingCallBackView.addView(videoController.getRemoteView());
            mVideoViewsAdded = true;
        }
    }

    private void removeVideoViews() {
        if (getSinchServiceInterface() == null) {
            return; // early
        }

        VideoController vc = getSinchServiceInterface().getVideoController();
        if (vc != null) {
            incomingCallBackView.removeView(vc.getRemoteView());

            incomingCallLocalView.removeView(vc.getLocalView());
            mVideoViewsAdded = false;
        }
    }

    private void answerClicked() {
        mAudioPlayer.stopRingtone();
        Call call = getSinchServiceInterface().getCall(mCallId);
        if (call != null) {
            call.answer();

            setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
            AudioController audioController = getSinchServiceInterface().getAudioController();
            audioController.enableSpeaker();

            incomingView.setAlpha(0);
            incomingTalkingView.setAlpha(1);


        } else {
            finish();
        }
    }

    private void endCall() {
        mAudioPlayer.stopRingtone();
        setVolumeControlStream(AudioManager.USE_DEFAULT_STREAM_TYPE);
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
                        incomingName.setText(user.getDisplayName());
                        incomingTalkingName.setText(user.getDisplayName());
                        incomingFunction.setText(user.getDoing());
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
            mAudioPlayer.stopRingtone();

            endCall();
        }

        @Override
        public void onCallEstablished(Call call) {
            Log.d(TAG, "Call established");
        }

        @Override
        public void onCallProgressing(Call call) {
            Log.d(TAG, "Call progressing");
        }

        @Override
        public void onShouldSendPushNotification(Call call, List<PushPair> pushPairs) {
            // Send a push through your push provider here, e.g. GCM
        }

        @Override
        public void onVideoTrackAdded(Call call) {
            Log.d(TAG, "Video track added");

        }
    }
}
