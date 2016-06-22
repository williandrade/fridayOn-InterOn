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
import android.view.View;

import java.net.URI;

import me.williandrade.R;
import me.williandrade.adapter.MenuAdapter;
import me.williandrade.service.SinchService;

public abstract class BaseActivity extends AppCompatActivity implements ServiceConnection {

    private SinchService.SinchServiceInterface mSinchServiceInterface;

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
        getApplicationContext().bindService(
                new Intent(this, SinchService.class),
                this,
                BIND_AUTO_CREATE);
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        if (SinchService.class.getName().equals(componentName.getClassName())) {
            mSinchServiceInterface = (SinchService.SinchServiceInterface) iBinder;
            onServiceConnected();
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        if (SinchService.class.getName().equals(componentName.getClassName())) {
            mSinchServiceInterface = null;
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
        return mSinchServiceInterface;
    }

    public void makeDrawer(Context context) {

        drawerRecycler = (RecyclerView) findViewById(R.id.recyclerView);

        String name = "Ashiley Jhonson";
        int credits = 40;
        URI img = null;

        drawerAdapter = new MenuAdapter(userTitles, name, credits, img, context);

        drawerRecycler.setAdapter(drawerAdapter);

        drawerLayoutManager = new LinearLayoutManager(this);

        drawerRecycler.setLayoutManager(drawerLayoutManager);


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
        drawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

    }

    public void makeToolbar() {
        myToolbar = (Toolbar) findViewById(R.id.includeToolbar);
        myToolbar.setPadding(0, getStatusBarHeight(), 0, 0);
        this.setSupportActionBar(myToolbar);
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

}
