package com.gmail.vanyadubik.managerplus.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.gmail.vanyadubik.managerplus.R;
import com.gmail.vanyadubik.managerplus.adapter.tabadapter.TabFragmentVisit;
import com.gmail.vanyadubik.managerplus.adapter.tabadapter.TabFragmentWaybill;
import com.gmail.vanyadubik.managerplus.app.ManagerPlusAplication;
import com.gmail.vanyadubik.managerplus.fragment.ClientListFragment;
import com.gmail.vanyadubik.managerplus.gps.service.GpsTrackingNotification;
import com.gmail.vanyadubik.managerplus.repository.DataRepository;
import com.gmail.vanyadubik.managerplus.service.gps.SyncIntentTrackService;
import com.gmail.vanyadubik.managerplus.ui.LoginDialogNotification;
import com.gmail.vanyadubik.managerplus.ui.LoginDialogNotificationListener;

import javax.inject.Inject;

import static com.gmail.vanyadubik.managerplus.service.gps.SyncIntentTrackService.MIN_COUNT;
import static com.gmail.vanyadubik.managerplus.ui.LoginDialogNotification.LOGIN_GOOGLE;

public class StartActivity extends AppCompatActivity{
    @Inject
    DataRepository dataRepository;

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private String mActivityTitle;
    private FragmentManager mFragmentManager;
    private FragmentTransaction mFragmentTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ((ManagerPlusAplication) getApplication()).getComponent().inject(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        initNavigationView();

        initTabs();
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

        ImageView googleAccEdit = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.google_acc_edit);
        googleAccEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.closeDrawers();
                LoginDialogNotification loginDialogNotification = new LoginDialogNotification(StartActivity.this, LOGIN_GOOGLE, new LoginDialogNotificationListener() {
                    @Override
                    public void onLiginDialogResult() {

                    }
                });
                loginDialogNotification.showNotification();
            }
        });
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

//    private void logInGoogleAcc(){
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//
//        View dialogView = this.getLayoutInflater().inflate(R.layout.login_dialog,null);
//
//        final EditText usernameInput=(EditText)dialogView.findViewById(R.id.login);
//        final EditText passwordInput=(EditText)dialogView.findViewById(R.id.password);
//        builder.setView(dialogView);
//        builder.setTitle(getResources().getString(R.string.google_acc_name));
//        builder.setMessage(getResources().getString(R.string.google_acc_login));
//
//        builder.setPositiveButton(getString(R.string.questions_answer_save), new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int which) {
//                String value1=usernameInput.getText().toString();
//                String value2=passwordInput.getText().toString();
//                if(value1.equals(null)&&value2.equals(null));
//            }
//        });
//
//        builder.setNegativeButton(getString(R.string.questions_answer_cancel), new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//
//                dialog.dismiss();
//            }
//        });
//
//        builder.setNeutralButton(getString(R.string.questions_get_from_list), new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//
//                dialog.dismiss();
//
//               // showListGoogleAcc();
//
//            }
//        });
//
//        AlertDialog alert = builder.create();
//        alert.show();
//
//        // TODO (start stub): to set size text in AlertDialog
//        TextView textView = (TextView) alert.findViewById(android.R.id.message);
//        textView.setTextSize(getResources().getDimension(R.dimen.alert_text_size));
//        Button button1 = (Button) alert.findViewById(android.R.id.button1);
//        button1.setTextSize(getResources().getDimension(R.dimen.alert_text_size_medium));
//        Button button2 = (Button) alert.findViewById(android.R.id.button2);
//        button2.setTextSize(getResources().getDimension(R.dimen.alert_text_size_medium));
//        Button button3 = (Button) alert.findViewById(android.R.id.button3);
//        button3.setTextSize(getResources().getDimension(R.dimen.alert_text_size_medium));
//        // TODO: (end stub) ------------------
//    }

//    private void showListGoogleAcc(){
//
//        if ( Build.VERSION.SDK_INT >= 23 &&
//
//                ContextCompat.checkSelfPermission( getApplicationContext(), Manifest.permission.GET_ACCOUNTS ) != PackageManager.PERMISSION_GRANTED &&
//                ContextCompat.checkSelfPermission( getApplicationContext(), Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
//            return;
//
//        }
//
//        Account[] accountList = AccountManager.get(getApplicationContext()).getAccountsByType(GOOGLE_EMAIL_PARAM);
//
//        if(accountList.length == 0){
//            Toast.makeText(this, getResources().getString(R.string.google_acc_device_not_found), Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//
//        View dialogView = this.getLayoutInflater().inflate(R.layout.login_dialog,null);
//
//        final EditText usernameInput=(EditText)dialogView.findViewById(R.id.login);
//        final EditText passwordInput=(EditText)dialogView.findViewById(R.id.password);
//        builder.setView(dialogView);
//        builder.setTitle(getResources().getString(R.string.google_acc_name));
//        builder.setMessage(getResources().getString(R.string.google_acc_device_get));
//
//        builder.setPositiveButton(getString(R.string.questions_answer_save), new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int which) {
//                String value1=usernameInput.getText().toString();
//                String value2=passwordInput.getText().toString();
//                if(value1.equals(null)&&value2.equals(null));
//            }
//        });
//
//        builder.setNegativeButton(getString(R.string.questions_answer_cancel), new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//
//                dialog.dismiss();
//            }
//        });
//
//        AlertDialog alert = builder.create();
//        alert.show();
//
//        // TODO (start stub): to set size text in AlertDialog
//        TextView textView = (TextView) alert.findViewById(android.R.id.message);
//        textView.setTextSize(getResources().getDimension(R.dimen.alert_text_size));
//        Button button1 = (Button) alert.findViewById(android.R.id.button1);
//        button1.setTextSize(getResources().getDimension(R.dimen.alert_text_size_medium));
//        Button button2 = (Button) alert.findViewById(android.R.id.button2);
//        button2.setTextSize(getResources().getDimension(R.dimen.alert_text_size_medium));
//        Button button3 = (Button) alert.findViewById(android.R.id.button3);
//        button3.setTextSize(getResources().getDimension(R.dimen.alert_text_size_medium));
//        // TODO: (end stub) ------------------
//    }

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
}
