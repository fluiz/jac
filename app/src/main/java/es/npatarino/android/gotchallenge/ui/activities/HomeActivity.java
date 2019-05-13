package es.npatarino.android.gotchallenge.ui.activities;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;

import es.npatarino.android.gotchallenge.R;
import es.npatarino.android.gotchallenge.adapters.SectionsPagerAdapter;

public class HomeActivity extends AppCompatActivity {

    private static String TAG = "HomeActivity";

    SectionsPagerAdapter sectionsPageAdapter;
    ViewPager viewPager;
    Toolbar toolbar;
    TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setSectionsPageAdapter(new SectionsPagerAdapter(getSupportFragmentManager()));

        setViewPager((ViewPager) findViewById(R.id.container));
        getViewPager().setAdapter(getSectionsPageAdapter());

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(getViewPager());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public SectionsPagerAdapter getSectionsPageAdapter() {
        return sectionsPageAdapter;
    }

    public void setSectionsPageAdapter(SectionsPagerAdapter sectionsPageAdapter) {
        this.sectionsPageAdapter = sectionsPageAdapter;
    }

    public ViewPager getViewPager() {
        return viewPager;
    }

    public void setViewPager(ViewPager viewPager) {
        this.viewPager = viewPager;
    }
}
