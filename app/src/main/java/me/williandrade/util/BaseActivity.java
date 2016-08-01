package me.williandrade.util;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.google.firebase.auth.FirebaseUser;

import me.williandrade.R;
import me.williandrade.adapter.MenuAdapter;
import me.williandrade.dto.UserDTO;
import me.williandrade.service.SinchService;

public abstract class BaseActivity extends AppCompatActivity implements ServiceConnection {

    private final String TAG = this.getClass().getSimpleName();

    private SinchService.SinchServiceInterface sinchServiceInterface;

    private Toolbar myToolbar;

    /*
    * SIDE MENU
    * */
    private RecyclerView drawerRecycler;
    private RecyclerView.Adapter drawerAdapter;
    private RecyclerView.LayoutManager drawerLayoutManager;
    private DrawerLayout drawerLayout;

    private ActionBarDrawerToggle mDrawerToggle;
    private final String userTitles[] = {"Index", "Favorites", "Services", "History of Services", "My Schedule", "Settigns", "Sign Out"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "Binding Service, will create if necessary.");
        getApplicationContext().bindService(
                new Intent(this, SinchService.class),
                this,
                BIND_AUTO_CREATE);

    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        Log.d(TAG, "Service Connected, the component name is: " + componentName);

        if (SinchService.class.getName().equals(componentName.getClassName())) {
            sinchServiceInterface = (SinchService.SinchServiceInterface) iBinder;
            Log.d(TAG, "Calling global method onServiceConnected");
            onServiceConnected();
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        Log.d(TAG, "Service Disconnected, the component name is: " + componentName);

        if (SinchService.class.getName().equals(componentName.getClassName())) {
            sinchServiceInterface = null;
            Log.d(TAG, "Calling global method onServiceDisconnected");
            onServiceDisconnected();
        }
    }

    protected void onServiceConnected() {
        // for subclasses
    }

    protected void onServiceDisconnected() {
        // for subclasses
    }


    protected SinchService.SinchServiceInterface getSinchServiceInterface() {
        Log.d(TAG, "Getting sinchServiceInterface");
        return sinchServiceInterface;
    }

    public void makeDrawer(Context context) {

        Log.d(TAG, "Making Drawer");
        drawerRecycler = (RecyclerView) findViewById(R.id.recyclerView);

        Log.d(TAG, "Generating MenuAdapter");

        drawerAdapter = new MenuAdapter(userTitles, sinchServiceInterface.getUser(), context);

        Log.d(TAG, "Setting Adapter");
        drawerRecycler.setAdapter(drawerAdapter);

        drawerLayoutManager = new LinearLayoutManager(this);

        Log.d(TAG, "Add to layout");
        drawerRecycler.setLayoutManager(drawerLayoutManager);

        Log.d(TAG, "Creating Toggle Drawer ");
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, myToolbar, R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }


        };
        Log.d(TAG, "Add Toggle Drawer");
        drawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
    }

    public void makeToolbar() {
        Log.d(TAG, "Making Toolbar");

        myToolbar = (Toolbar) findViewById(R.id.includeToolbar);
        myToolbar.setPadding(0, getStatusBarHeight(), 0, 0);
        this.setSupportActionBar(myToolbar);
    }

    public int getStatusBarHeight() {
        Log.d(TAG, "Getting Bar Height");

        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

}
