package com.gmail.vanyadubik.managerplus.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gmail.vanyadubik.managerplus.R;
import com.gmail.vanyadubik.managerplus.adapter.tabadapter.TabFragmentVisit;
import com.gmail.vanyadubik.managerplus.adapter.tabadapter.TabFragmentWaybill;
import com.gmail.vanyadubik.managerplus.app.ManagerPlusAplication;
import com.gmail.vanyadubik.managerplus.fragment.ClientListFragment;
import com.gmail.vanyadubik.managerplus.gps.service.GpsTrackingNotification;
import com.gmail.vanyadubik.managerplus.repository.DataRepository;
import com.gmail.vanyadubik.managerplus.service.gps.SyncIntentTrackService;
import com.gmail.vanyadubik.managerplus.ui.CircleImageView;
import com.gmail.vanyadubik.managerplus.utils.ActivityUtils;
import com.gmail.vanyadubik.managerplus.utils.SharedStorage;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.squareup.picasso.Picasso;

import javax.inject.Inject;

import static com.gmail.vanyadubik.managerplus.common.Consts.GOOGLE_ACC_CONNECTED;
import static com.gmail.vanyadubik.managerplus.common.Consts.GOOGLE_ACC_CONNECTED_VISITS;
import static com.gmail.vanyadubik.managerplus.common.Consts.GOOGLE_ACC_EMAIL;
import static com.gmail.vanyadubik.managerplus.common.Consts.GOOGLE_ACC_ID;
import static com.gmail.vanyadubik.managerplus.common.Consts.GOOGLE_ACC_IMAGE;
import static com.gmail.vanyadubik.managerplus.common.Consts.GOOGLE_ACC_NAME;
import static com.gmail.vanyadubik.managerplus.common.Consts.TAGLOG;
import static com.gmail.vanyadubik.managerplus.service.gps.SyncIntentTrackService.MIN_COUNT;

public class StartActivity extends AppCompatActivity{
    @Inject
    DataRepository dataRepository;
    @Inject
    ActivityUtils activityUtils;

    private static final int CAPTURE_LOGIN_DIALOG_GOOGLE_REQ = 1001;

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private String mActivityTitle;
    private CircleImageView googleAccIcon;
    private TextView googleAccName, googleAccEmail;
    private FragmentManager mFragmentManager;
    private FragmentTransaction mFragmentTransaction;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ((ManagerPlusAplication) getApplication()).getComponent().inject(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        initNavigationView();

        initTabs();

        initGoogleAccount();
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        }else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_sync) {
                Intent intent = new Intent(this, SyncIntentTrackService.class);
                intent.putExtra(MIN_COUNT, 5000);
                startService(intent);
            return true;
        }

        if (id == R.id.action_startServiceLocation) {
            startActivity(new Intent(this, GpsTrackingNotification.class).putExtra("fromServiceGpsTrackingNotify", true));
            return true;
        }


        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void initTabs() {

        mFragmentManager = getSupportFragmentManager();
        mFragmentTransaction = mFragmentManager.beginTransaction();
        mFragmentTransaction.replace(R.id.containerView, new TabFragmentWaybill()).commit();

    }

    private void initNavigationView() {

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        mActivityTitle = getTitle().toString();

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, 0, 0) {


            @Override
            public boolean onOptionsItemSelected(MenuItem item) {
                return super.onOptionsItemSelected(item);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
                getSupportActionBar().setTitle(mActivityTitle);
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank

                 /* hide keyboard */
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle(R.string.action_side_panel);
                invalidateOptionsMenu();
                getSupportActionBar().setSubtitle("");
            }
        };

        mDrawerToggle.setDrawerIndicatorEnabled(true);

        mDrawerLayout.setDrawerListener(mDrawerToggle);

        mDrawerToggle.syncState();

        //Initializing NavigationView
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                mDrawerLayout.closeDrawers();

                item.setChecked(true);

                FragmentTransaction xfragmentTransaction = mFragmentManager.beginTransaction();
                switch (item.getItemId()) {
                    case R.id.nav_waybill:
                        xfragmentTransaction.replace(R.id.containerView, new TabFragmentWaybill()).commit();
                        getSupportActionBar().setSubtitle(R.string.work_plase_name);
                        break;
                    case R.id.nav_visit:
                        xfragmentTransaction.replace(R.id.containerView, new TabFragmentVisit()).commit();
                        getSupportActionBar().setSubtitle(R.string.visit_name);
                        break;
                    case R.id.nav_clients:
                        xfragmentTransaction.replace(R.id.containerView, new ClientListFragment()).commit();
                        getSupportActionBar().setSubtitle(R.string.clients);
                        break;
                    case R.id.nav_settings:
                        startActivity(new Intent(getBaseContext(), SettingsActivity.class));
                        getSupportActionBar().setSubtitle("");
                        break;
                    case R.id.nav_exit:
                        logout();
                        break;
                    default:
                        return false;
                }
                return true;
            }


        });

        googleAccIcon = (CircleImageView)navigationView.getHeaderView(0).findViewById(R.id.google_acc_icon);
        googleAccName = (TextView)navigationView.getHeaderView(0).findViewById(R.id.google_acc_name);
        googleAccEmail = (TextView)navigationView.getHeaderView(0).findViewById(R.id.google_acc_mail);

        initDataNavogationBar();

        ImageView googleAccEdit = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.google_acc_edit);
        googleAccEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.closeDrawers();
                Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, CAPTURE_LOGIN_DIALOG_GOOGLE_REQ);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_LOGIN_DIALOG_GOOGLE_REQ) {
            if(resultCode == RESULT_OK && data != null &&
                    data.getExtras() != null) {
                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                Log.d(TAGLOG, "handleSignInResult:" + result.isSuccess());
                if (result.isSuccess()) {
                    GoogleSignInAccount googleSignInAccount = result.getSignInAccount();
                    SharedStorage.setBoolean(getApplicationContext(), GOOGLE_ACC_CONNECTED, true);
                    SharedStorage.setString(getApplicationContext(), GOOGLE_ACC_ID, (String) googleSignInAccount.getId());
                    SharedStorage.setString(getApplicationContext(), GOOGLE_ACC_NAME, (String) googleSignInAccount.getDisplayName());
                    SharedStorage.setString(getApplicationContext(), GOOGLE_ACC_EMAIL, (String) googleSignInAccount.getEmail());
                    SharedStorage.setString(getApplicationContext(), GOOGLE_ACC_IMAGE, String.valueOf(googleSignInAccount.getPhotoUrl()));

                    activityUtils.showQuestion(StartActivity.this, getString(R.string.synchronization), getString(R.string.synchronize_visits_google_acc), new ActivityUtils.QuestionAnswer() {
                        @Override
                        public void onPositiveAnsver() {
                            SharedStorage.setBoolean(getApplicationContext(), GOOGLE_ACC_CONNECTED_VISITS, true);
                        }

                        @Override
                        public void onNegativeAnsver() {
                            SharedStorage.setBoolean(getApplicationContext(), GOOGLE_ACC_CONNECTED_VISITS, false);
                        }
                        @Override
                        public void onNeutralAnsver() {}
                    });
                } else {
                    Toast.makeText(this, getResources().getString(R.string.google_account_not_connect), Toast.LENGTH_LONG).show();
                    SharedStorage.setBoolean(getApplicationContext(), GOOGLE_ACC_CONNECTED, false);
                    SharedStorage.setString(getApplicationContext(), GOOGLE_ACC_ID, "");
                    SharedStorage.setString(getApplicationContext(), GOOGLE_ACC_NAME, "");
                    SharedStorage.setString(getApplicationContext(), GOOGLE_ACC_EMAIL, "");
                    SharedStorage.setString(getApplicationContext(), GOOGLE_ACC_IMAGE, "");
                    SharedStorage.setBoolean(getApplicationContext(), GOOGLE_ACC_CONNECTED_VISITS, false);
                }
            }else if(resultCode != RESULT_CANCELED){
                Toast.makeText(this, getResources().getString(R.string.google_account_not_connect), Toast.LENGTH_LONG).show();
                SharedStorage.setBoolean(getApplicationContext(), GOOGLE_ACC_CONNECTED, false);
                SharedStorage.setString(getApplicationContext(), GOOGLE_ACC_ID, "");
                SharedStorage.setString(getApplicationContext(), GOOGLE_ACC_NAME, "");
                SharedStorage.setString(getApplicationContext(), GOOGLE_ACC_EMAIL, "");
                SharedStorage.setString(getApplicationContext(), GOOGLE_ACC_IMAGE, "");
                SharedStorage.setBoolean(getApplicationContext(), GOOGLE_ACC_CONNECTED_VISITS, false);
            }
            initDataNavogationBar();
        }

    }

    private void logout() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.item_exit));
        builder.setMessage(getString(R.string.questions_exit_clear));

        builder.setPositiveButton(getString(R.string.questions_answer_yes), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

//                cm.clearData();
//
//                File cooperativeDir = new File(getExternalStoragePublicDirectory(DIRECTORY_DCIM).getPath()
//                        + File.separator + ROOT_DIR);
//                deleteRecursive(cooperativeDir);
//
//                if (CLEAR_DATABASE_IN_LOGOUT) {
//                    dataRepository.clearDataBase();
//                }
//
//                dialog.dismiss();
//                Intent intent = new Intent(getBaseContext(), LoginActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(intent);
            }
        });

        builder.setNegativeButton(getString(R.string.questions_answer_no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();

        // TODO (start stub): to set size text in AlertDialog
        TextView textView = (TextView) alert.findViewById(android.R.id.message);
        textView.setTextSize(getResources().getDimension(R.dimen.alert_text_size));
        Button button1 = (Button) alert.findViewById(android.R.id.button1);
        button1.setTextSize(getResources().getDimension(R.dimen.alert_text_size));
        Button button2 = (Button) alert.findViewById(android.R.id.button2);
        button2.setTextSize(getResources().getDimension(R.dimen.alert_text_size));
        // TODO: (end stub) ------------------
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    private void initGoogleAccount(){

        GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestScopes(new Scope(Scopes.PLUS_LOGIN))
                .build();

        // Build the GoogleApiClient object
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Log.d(TAGLOG, "Could not connect to Google Play Services");
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

     private void initDataNavogationBar(){

         if(SharedStorage.getBoolean(getApplicationContext(), GOOGLE_ACC_CONNECTED, false)) {
             String imageUri = (String) SharedStorage.getString(getApplicationContext(), GOOGLE_ACC_IMAGE, "");
             if (googleAccIcon != null &&
                     imageUri != null &&
                     !imageUri.isEmpty()) {
                 Picasso.with(getApplicationContext())
                         .load(imageUri)
                         .placeholder(this.getResources().getDrawable(R.drawable.ic_user))
                         .fit()
                         .into(googleAccIcon);
             }
             if(googleAccName!=null) {
                 googleAccName.setText((String) SharedStorage.getString(getApplicationContext(), GOOGLE_ACC_NAME, ""));
             }
             if(googleAccName!=null) {
                 googleAccEmail.setText((String) SharedStorage.getString(getApplicationContext(), GOOGLE_ACC_EMAIL, ""));
             }
         }
     }
}
