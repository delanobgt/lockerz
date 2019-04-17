package com.delanobgt.lockerz.activities;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.delanobgt.lockerz.R;
import com.delanobgt.lockerz.fragments.AboutFragment;
import com.delanobgt.lockerz.fragments.LockerListFragment;
import com.delanobgt.lockerz.fragments.RecentActionListFragment;
import com.delanobgt.lockerz.fragments.SettingsFragment;

public class DrawerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private LockerListFragment lockerListFragment;
    private RecentActionListFragment recentActivityListFragment;
    private SettingsFragment settingsFragment;
    private AboutFragment aboutFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        lockerListFragment = new LockerListFragment();
        recentActivityListFragment = new RecentActionListFragment();
        settingsFragment = new SettingsFragment();
        aboutFragment = new AboutFragment();

        setTitle("Lockers");
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_placeholder, lockerListFragment, "MyFragments");
        fragmentTransaction.commit();

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        if (id == R.id.nav_lockers) {
            setTitle("Lockers");
            fragmentTransaction.replace(R.id.fragment_placeholder, lockerListFragment, "MyFragments");
        } else if (id == R.id.nav_recent_actions) {
            setTitle("Recent Actions");
            fragmentTransaction.replace(R.id.fragment_placeholder, recentActivityListFragment, "MyFragments");
        } else if (id == R.id.nav_settings) {
            setTitle("Settings");
            fragmentTransaction.replace(R.id.fragment_placeholder, settingsFragment, "MyFragments");
        } else if (id == R.id.nav_about) {
            setTitle("About");
            fragmentTransaction.replace(R.id.fragment_placeholder, aboutFragment, "MyFragments");
        } else if (id == R.id.nav_exit) {
            moveTaskToBack(true);
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        }
        fragmentTransaction.commit();

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
