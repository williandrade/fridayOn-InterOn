package me.williandrade.activity;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sinch.android.rtc.SinchError;
import com.sinch.android.rtc.calling.Call;

import me.williandrade.R;
import me.williandrade.fragments.IndexUserFragment;
import me.williandrade.service.SinchService;
import me.williandrade.util.BaseActivity;

public class IndexActivity extends BaseActivity implements SinchService.StartFailedListener {

    private FragmentTransaction fragmentTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_with_lateral);

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            comeBackLogin();
        }

        this.makeToolbar();
        this.makeDrawer(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        MenuItem item = menu.getItem(0);

        View notificationView = getLayoutInflater().inflate(R.layout.notification_view, null);

        TextView counter = (TextView) notificationView.findViewById(R.id.counter);
        counter.setText("10");

        item.setActionView(notificationView);
        return true;
    }


    @Override
    protected void onServiceConnected() {
        fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.content_frame, new IndexUserFragment());
        fragmentTransaction.commit();
    }

    @Override
    public void onDestroy() {
        if (getSinchServiceInterface() != null) {
            getSinchServiceInterface().stopClient();
        }
        super.onDestroy();
    }

    public void stopButtonClicked() {
        if (getSinchServiceInterface() != null) {
            getSinchServiceInterface().stopClient();
        }

        FirebaseAuth instance = FirebaseAuth.getInstance();

        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("logged");
        myRef.child(instance.getCurrentUser().getUid()).removeValue();

        instance.signOut();

        comeBackLogin();
    }

    public void callButtonClicked(String userId) {

        Call call = getSinchServiceInterface().callUserVideo(userId);
        String callId = call.getCallId();

        Intent callScreen = new Intent(this, CallScreenActivity.class);
        callScreen.putExtra(SinchService.CALL_ID, callId);
        startActivity(callScreen);
    }

    private void comeBackLogin() {
        Intent mainActivity = new Intent(this, LoginActivity.class);
        startActivity(mainActivity);
        finish();
        return;
    }


    @Override
    public void onStartFailed(SinchError error) {
        Toast.makeText(this, error.toString(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onStarted() {
        comeBackLogin();
    }


}