package me.williandrade.activity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallEndCause;
import com.sinch.android.rtc.video.VideoCallListener;
import com.sinch.android.rtc.video.VideoController;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incoming_call_screen);

        ImageView answer = (ImageView) findViewById(R.id.acceptCallBtn);
        answer.setOnClickListener(mClickListener);
        ImageView decline = (ImageView) findViewById(R.id.refuseCallBtn);
        decline.setOnClickListener(mClickListener);

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

        } else {
            Log.e(TAG, "Started with invalid callId, aborting");
            finish();
        }
    }

    private void answerClicked() {
        mAudioPlayer.stopRingtone();
        Call call = getSinchServiceInterface().getCall(mCallId);
        if (call != null) {
            call.answer();

            VideoController vc = getSinchServiceInterface().getVideoController();

            if (vc != null) {
                LinearLayout layout = (LinearLayout) findViewById(R.id.imageIncommingCallScreen);
                layout.removeView(vc.getRemoteView());
            }

            Intent intent = new Intent(this, CallScreenActivity.class);
            intent.putExtra(SinchService.CALL_ID, mCallId);
            startActivity(intent);
        } else {
            finish();
        }
    }

    private void declineClicked() {
        mAudioPlayer.stopRingtone();
        Call call = getSinchServiceInterface().getCall(mCallId);
        if (call != null) {
            call.hangup();
        }
        finish();
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
                    Log.d("UserDAO", json);
                    UserDTO user = gson.fromJson(json, UserDTO.class);

                    if (user.getUid().equals(uid)) {
                        TextView name = (TextView) findViewById(R.id.nameIncommingCallScreen);
                        name.setText(user.getDisplayName());

                        TextView function = (TextView) findViewById(R.id.functionCallScreem);
                        function.setText(user.getDoing());

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
            finish();
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
            VideoController vc = getSinchServiceInterface().getVideoController();

            if (vc != null) {
                LinearLayout layout = (LinearLayout) findViewById(R.id.imageIncommingCallScreen);
                layout.addView(vc.getRemoteView());
            } else {
                Log.d("VEJAAAA", "Sem Video");
            }

        }
    }

    private OnClickListener mClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.acceptCallBtn:
                    answerClicked();
                    break;
                case R.id.refuseCallBtn:
                    declineClicked();
                    break;
            }
        }
    };
}
