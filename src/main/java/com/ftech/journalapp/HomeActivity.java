package com.ftech.journalapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ftech.journalapp.adapters.RecyclerAdapter;
import com.ftech.journalapp.animation.MyBounceInterpolator;
import com.ftech.journalapp.data.ListData;
import com.ftech.journalapp.mDataBase.DBAdapter;
import com.ftech.journalapp.utils.AutoScrollViewPager;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, AdapterView.OnItemSelectedListener {
    public static final String SUBJECT = "SaveLock- ";
    private static final String TAG = "HomeActivity";
    private AutoScrollViewPager viewPager;
    private MyViewPagerAdapter myViewPagerAdapter;
    private LinearLayout dotsLayout;
    private TextView[] dots;
    private int[] layouts;
    private TabLayout mTabLayout;
    SwitchCompat reminderSwitch;
    public TextView userEmail;
    private RelativeLayout navHeader;
    //database items
    static DBAdapter sDb;
    //for recycler view
    private List<ListData> mListData = new ArrayList<>();
    private RecyclerView recyclerView;
    RecyclerAdapter mAdapter;
    //progress bar
    private ProgressBar mProgressBar;
    //data status label
    TextView mNoDataFound;
    //add button
    ImageView mAddBtn;
    //firebase authentication and its listener
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private Toolbar toolbar = null;
    //checks when logout button is clicked
    boolean isLogout;
    //check when back button is pressed twice
    private boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //overridePendingTransition(R.anim.fadein,R.anim.fadeout);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        //
        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() == null){
                    Intent mIntent = new Intent(HomeActivity.this,LoginActivity.class);
                    startActivity(mIntent);
                    //finish();
                    onBackPressed();
                }
            }
        };
        SharedPreferences pref = getSharedPreferences("LOGIN_SESSION",MODE_PRIVATE);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);
        navHeader = (RelativeLayout) headerView.findViewById(R.id.navHeader);
        navHeader.setOnClickListener(this);
        userEmail = (TextView)headerView.findViewById(R.id.userEmail);
        userEmail.setText(pref.getString("email",null));
        //Toast.makeText(this, "pref value = "+pref.getString("email1",null), Toast.LENGTH_LONG).show();
        mNoDataFound = (TextView)findViewById(R.id.noDataFound);
        mProgressBar = (ProgressBar)findViewById(R.id.progressBar);
        //add button
        mAddBtn = (ImageView)findViewById(R.id.addBtn);
        mAddBtn.setOnClickListener(this);
        //Initialize database
        sDb = new DBAdapter(this);
        //my preference
        SharedPreferences mPref = getSharedPreferences("JOURNAL_NOTE_ADDED",MODE_PRIVATE);
        SharedPreferences.Editor editor = mPref.edit();
        if(mPref.getBoolean("note_added",false)) {
            //remove the tag after loading new note
            editor.remove("note_added");
            editor.apply();
        }
        if(mPref.getBoolean("thereIsDataInDatabase",false)) {
            //load items from SQLite database in another thread
            mNoDataFound.setVisibility(View.GONE);
            new LoadingThread().execute("");
        }else{
            mNoDataFound.setVisibility(View.VISIBLE);
        }
        autoAnimateWithBounce(mAddBtn);
        initControls();
    }
    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }
    @Override
    protected void onResume() {
        super.onResume();
        //my preference
        SharedPreferences mPref = getSharedPreferences("JOURNAL_NOTE_ADDED",MODE_PRIVATE);
        SharedPreferences.Editor editor = mPref.edit();
        //can only perform this when new note is added
        if(mPref.getBoolean("note_added",false)) {
            mNoDataFound.setVisibility(View.GONE);
            //load items from SQLite database in another thread
            new LoadingThread().execute("");
            //remove the tag after loading new note
        }
    }

    private void runAnimation(RecyclerView recyclerView, int type) {
        Context context = recyclerView.getContext();
        LayoutAnimationController controller = null;
        if(type == 0) controller = AnimationUtils.loadLayoutAnimation(context,R.anim.layout_anim_down);
        else if(type == 1) controller = AnimationUtils.loadLayoutAnimation(context,R.anim.layout_anim_up);
        else if(type == 2) controller = AnimationUtils.loadLayoutAnimation(context,R.anim.layout_anim_from_right);
        else if(type == 3) controller = AnimationUtils.loadLayoutAnimation(context,R.anim.layout_anim_from_left);

        mAdapter = new RecyclerAdapter(this,mListData);
        recyclerView.setAdapter(mAdapter);
        //
        recyclerView.setLayoutAnimation(controller);
        recyclerView.getAdapter().notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
    }

    public void addListItems(){
        /*mListData.add(new ListData("Software Submit","My experience there wasn't fun at all","S","27-06-18"));
        mListData.add(new ListData("Summer Visit","Such a wonderful experience. Can't wait for the next summer","S","1-06-18"));
        mListData.add(new ListData("Nacoss National Meeting","A lot were discussed which focuses on bringing development to Nacoss","N","5-05-18"));
        mListData.add(new ListData("Nacoss ASPoly","My friday meetings....","P","27-06-18"));
        mListData.add(new ListData("Password","ca't just my password","P","30-06-18"));*/
        //mListData.add(new ListData("Face","Apply","F","22-06-18"));
        mListData.clear();
        sDb.openDB();
        Cursor c=sDb.retrieve();
        ListData listData =null;
        //boolean cToFirst = c.moveToFirst();
        while (c.moveToNext()) {
            int id = c.getInt(0);
            String name = c.getString(1);
            String desc = c.getString(2);
            String letter = c.getString(3);
            String date = c.getString(4);
            String color = c.getString(5);

            listData = new ListData();
            listData.setId(id);
            listData.setmTitle(name);
            listData.setmDesc(desc);
            listData.setmLetter(letter);
            listData.setmDate(date);
            listData.setmLetterColor(color);
            mListData.add(listData);
        }
        sDb.closeDB();
        if(mListData.size() > 0){
            mNoDataFound.setVisibility(View.GONE);
        }else{
            mNoDataFound.setVisibility(View.VISIBLE);
        }
    }
    /**
     * RETRIEVE OR gets the medication sData from database
     */
    public void getDatabaseItems()
    {
        //boolean toFirst = c.moveToFirst();
        //user_name = cursor.getString(cursor.getColumnIndex(Information.NAME));
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                mListData.clear();
                sDb.openDB();
                Cursor c=sDb.retrieve();
                ListData listData =null;
                //boolean cToFirst = c.moveToFirst();
                while (c.moveToNext()) {
                    int id = c.getInt(0);
                    String name = c.getString(1);
                    String desc = c.getString(2);
                    String letter = c.getString(3);
                    String date = c.getString(4);

                    listData = new ListData();
                    listData.setId(id);
                    listData.setmTitle(name);
                    listData.setmDesc(desc);
                    listData.setmLetter(letter);
                    listData.setmDate(date);
                    mListData.add(listData);
                }
                sDb.closeDB();
                if(mListData.size() > 0){
                    mNoDataFound.setVisibility(View.GONE);
                }else{
                    mNoDataFound.setVisibility(View.VISIBLE);
                }
                recyclerView.setAdapter(mAdapter);
            }
        });

    }
    private void initControls() {
        viewPager = (AutoScrollViewPager) findViewById(R.id.topViewPager);
        dotsLayout = (LinearLayout) findViewById(R.id.layoutDots);
        initViewPagerControls();

    }
    private void initViewPagerControls() {

        // layouts of all welcome sliders
        // add few more layouts if you want
        layouts = new int[]{
                R.layout.head_page1,
                R.layout.head_page2,
                R.layout.head_page3};

        // adding bottom dots
        addBottomDots(0);

        // making notification bar transparent
        changeStatusBarColor();

        myViewPagerAdapter = new MyViewPagerAdapter();
        viewPager.setAdapter(myViewPagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);
        Animation anim = AnimationUtils.loadAnimation(this,R.anim.fadein_with_zoom_in);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                viewPager.startAutoScroll();
                viewPager.setInterval(3000);
                viewPager.setCycle(true);
                viewPager.setStopScrollWhenTouch(true);
                viewPager.setAutoScrollDurationFactor(4);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        viewPager.startAnimation(anim);
        //viewPager.setPageTransformer(true, new ZoomOutTranformer());
    }
    private void addBottomDots(int currentPage) {
        dots = new TextView[layouts.length];

        int[] colorsActive = getResources().getIntArray(R.array.array_auto_pager_active);
        int[] colorsInactive = getResources().getIntArray(R.array.array_auto_pager_inactive);

        dotsLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(colorsInactive[currentPage]);
            dotsLayout.addView(dots[i]);
        }

        if (dots.length > 0)
            dots[currentPage].setTextColor(colorsActive[currentPage]);
    }//  viewpager change listener
    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            addBottomDots(position);

            // changing the next button text 'NEXT' / 'GOT IT'
            if (position == layouts.length - 1) {
                // last page. make button text to GOT IT
                /*btnNext.setText(getString(R.string.start));
                btnSkip.setVisibility(View.GONE);*/
            } else {
                // still pages are left
                /*btnNext.setText(getString(R.string.next));
                btnSkip.setVisibility(View.VISIBLE);*/
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

    /**
     * performs a bounce animation to any view passed as parameter
     * @param view the view to be animated
     */
    public void doBounceAnimation(View view){
        //Button button = (Button)findViewById(R.id.button);
        final Animation myAnim = AnimationUtils.loadAnimation(this, R.anim.buble_scale);

        // Use bounce interpolator with amplitude 0.2 and frequency 20
        MyBounceInterpolator interpolator = new MyBounceInterpolator(0.2, 20);
        myAnim.setInterpolator(interpolator);

        view.startAnimation(myAnim);
    }

    /**
     * performs an automatic bounce animation to any view passed as parameter
     * @param view the view to be animated automatically
     */
    public void autoAnimateWithBounce(final View view){
        int currentPage = 0;
        Timer timer;
        final long DELAY_MS = 1000;//delay in milliseconds before task is to be executed
        final long PERIOD_MS = 4000; // time in milliseconds between successive task executions.

            final Handler handler = new Handler();
            final Runnable Update = new Runnable() {
                public void run() {
                    doBounceAnimation(view);
                }
            };

            timer = new Timer(); // This will create a new Thread
            timer .schedule(new TimerTask() { // task to be scheduled

                @Override
                public void run() {
                    handler.post(Update);
                }
            }, DELAY_MS, PERIOD_MS);
    }
    //run task on a separate thread
    private class LoadingThread extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            addListItems();
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
            mProgressBar.setVisibility(View.GONE);
            //initiate recycler view function
            recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            recyclerView.setLayoutManager(mLayoutManager);
            //if you want items in ur recycler view to display in a grid form
            //recyclerView.setLayoutManager(new GridLayoutManager(this,4));
            //recyclerView.setItemAnimator(new DefaultItemAnimator());
            runAnimation(recyclerView,1);
            //my preference
            SharedPreferences mPref = getSharedPreferences("JOURNAL_NOTE_ADDED",MODE_PRIVATE);
            SharedPreferences.Editor editor = mPref.edit();
            //can only perform this when new note is added
            if(mPref.getBoolean("note_added",false)) {
                //remove the tag after loading new note
                editor.remove("note_added");
                editor.apply();
            }

        }

        @Override
        protected void onPreExecute() {
            mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }
    /**
     * Making notification bar transparent
     */
    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    /**
     * View pager adapter
     */
    public class MyViewPagerAdapter extends PagerAdapter {
        private LayoutInflater layoutInflater;

        public MyViewPagerAdapter() {
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View view = layoutInflater.inflate(layouts[position], container, false);
            container.addView(view);

            return view;
        }

        @Override
        public int getCount() {
            return layouts.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }
            if(isLogout){
                super.onBackPressed();
                overridePendingTransition(R.anim.fadein_a_bit,R.anim.slide_out_from_right);
            }
            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce=false;
                }
            }, 2000);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        /*if (id == R.id.action_settings) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_share) {

        } else if (id == R.id.logout) {
            SharedPreferences mPref = getSharedPreferences("LOGIN_SESSION",MODE_PRIVATE);
            SharedPreferences.Editor mEditor = mPref.edit();
            mEditor.clear();
            mEditor.apply();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    /*Intent mIntent = new Intent(HomeActvity.this,LoginActivity.class);
                    startActivity(mIntent);*/
                    mAuth.signOut();
                    isLogout = true;
                }
            },300);
        }else if (id == R.id.nav_exit) {
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View v) {
        if(v == mAddBtn) {
            startActivity(new Intent(HomeActivity.this, JournalNoteActivity.class));
        }
    }
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }
    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        //lockType.setSelection(0);
        //((TextView)parent.getChildAt(0)).setTextColor(0x808080);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // If the broadcast receiver is not null then unregister it.
        // This action is better placed in activity onDestroy() method.
        /*if(this.receiver != null) {
            unregisterReceiver(this.receiver);
        }*/
    }
}
