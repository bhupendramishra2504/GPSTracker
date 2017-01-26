package gps.tracker.com.gpstracker;



import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class Search_activity_v2 extends AppCompatActivity
{

    //private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    AppCompatActivity activity;
    Fragment search_city,search_mobile,search_owner,search_vname,search_vnumber;
    int position=0;
    // Tab titles

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_v2);
        //toolbar = (Toolbar) findViewById(R.id.toolbar);
       // setSupportActionBar(toolbar);
        Global.set_action_bar_details(Search_activity_v2.this,"Search","");
        activity=Search_activity_v2.this;

        search_city=new Search_city();
        search_mobile=new Search_mobile();
        search_owner=new Search_owner();
        search_vname=new Search_vname();
        search_vnumber=new Search_vnumber();

        Global.search_string="NA";

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);



        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);



        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener(){
            @Override
            public void onTabSelected(TabLayout.Tab tab){
                position = tab.getPosition();
                LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(Search_activity_v2.this);
                Intent i = new Intent("TAG_REFRESH");
                lbm.sendBroadcast(i);

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        // Inflate menu to add items to action bar if it is present.
        inflater.inflate(R.menu.search_activity_v2, menu);
        SearchManager manager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        SearchView search = (SearchView) menu.findItem(R.id.search).getActionView();

        search.setSearchableInfo(manager.getSearchableInfo(getComponentName()));

        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                Global.search_string=query;
                Toast.makeText(activity,"search string is"+Global.search_string,Toast.LENGTH_LONG).show();
                LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(Search_activity_v2.this);
                Intent i = new Intent("TAG_REFRESH");
                lbm.sendBroadcast(i);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String query) {

                return true;

            }

        });
        // Associate searchable configuration with the SearchView
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int id = item.getItemId();
         if(id==android.R.id.home) {
            // app icon in action bar clicked; go home
            Intent intent = new Intent(this, Dashboard.class);
            startActivity(intent);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);



    }



    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new Search_owner(), "OWNER");
        adapter.addFragment(new Search_city(), "CITY");
        adapter.addFragment(new Search_mobile(), "MOBILE");
        adapter.addFragment(new Search_vname(), "VEHICLE NAME");
        adapter.addFragment(new Search_vnumber(), "VEHICLE NUMBER");
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }


}
