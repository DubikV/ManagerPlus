package com.gmail.vanyadubik.managerplus.ui;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.gmail.vanyadubik.managerplus.R;

import static com.gmail.vanyadubik.managerplus.common.Consts.GOOGLE_EMAIL_PARAM;

public class LoginDialogNotification{
    public static final int LOGIN_APP = 1;
    public static final int LOGIN_GOOGLE = 2;

    private Activity mActivity;
    private LoginDialogNotificationListener dialogNotificationListener;
    private int typeDialogLogin;
    private TextView usernameInput, passwordInput;


    public LoginDialogNotification(Activity mActivity, int typeDialogLogin, LoginDialogNotificationListener dialogNotificationListener) {
        this.mActivity = mActivity;
        this.typeDialogLogin = typeDialogLogin;
        this.dialogNotificationListener = dialogNotificationListener;
    }

    public void setTypeDialogLogin(int typeDialogLogin) {
        this.typeDialogLogin = typeDialogLogin;
    }

    public void showNotification() {
        if (typeDialogLogin > 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
            View dialogView = mActivity.getLayoutInflater().inflate(R.layout.login_dialog,null);
            usernameInput = (EditText)dialogView.findViewById(R.id.login);
            passwordInput = (EditText)dialogView.findViewById(R.id.password);
            builder.setView(dialogView);
            if (typeDialogLogin == 2) {

                builder.setTitle(mActivity.getResources().getString(R.string.google_acc_name));
                builder.setMessage(mActivity.getResources().getString(R.string.google_acc_login));
                builder.setPositiveButton(mActivity.getString(R.string.questions_answer_save), new SaveButton());
                builder.setNegativeButton(mActivity.getString(R.string.questions_answer_cancel), new DissmisButton());
                builder.setNeutralButton(mActivity.getString(R.string.questions_get_from_list), new ShowAllAccountsGoogle());

            }else {
//                boolean isGPSEnabled = ((LocationManager) getSystemService(LOCATION_SERVICE)).isProviderEnabled(Provider.PROVIDER_GPS);
//                String str = "%s%s%s";
//                Object[] objArr = new Object[4];
//                objArr[0] = SharedStorage.getInteger(this, GpsTracking.PREF_INTERVAL, 0) == 0 ? getResources().getString(R.string.service_tracking_null_interval) : "";
//                objArr[1] = !isGPSEnabled ? getResources().getString(R.string.gps_is_disabled) : "";
//                objArr[2] = SharedStorage.getInteger(getApplicationContext(), PREF_TYPE_SERVICE, 0) > 2 ? getResources().getString(R.string.service_tracking_null_type) : "";
//                notifyText = String.format(locale, str, objArr);
//                builder.setTitle(R.string.service_tracking_error_message).
//                        setMessage(notifyText).setCancelable(false).
//                        setNegativeButton(R.string.questions_answer_ok, new DissmisButton());
//
//                if (!isGPSEnabled) {
//                    builder.setNeutralButton(R.string.action_settings, new SettingsButton());
//                } else {
//                    builder.setNeutralButton(R.string.action_settings, new SettingsAppButton());
//                }
            }
            builder.create().show();
        }
    }

    private class SaveButton implements OnClickListener {
        SaveButton() {
        }

        public void onClick(DialogInterface dialog, int id) {
            dialogNotificationListener.onLiginDialogResult();
            dialog.cancel();
        }
    }

    private class DissmisButton implements OnClickListener {
        DissmisButton() {
        }

        public void onClick(DialogInterface dialog, int id) {
            dialogNotificationListener.onLiginDialogResult();
            dialog.cancel();
        }
    }

    private class ShowAllAccountsGoogle implements OnClickListener {
        ShowAllAccountsGoogle() {
        }

        public void onClick(DialogInterface dialog, int id) {

                if ( Build.VERSION.SDK_INT >= 23 &&

                        ContextCompat.checkSelfPermission( mActivity, Manifest.permission.GET_ACCOUNTS ) != PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission( mActivity, Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
                    return;

                }

                Account[] accountList = AccountManager.get(mActivity).getAccountsByType(GOOGLE_EMAIL_PARAM);

                if(accountList.length == 0){
                    Toast.makeText(mActivity, mActivity.getResources().getString(R.string.google_acc_device_not_found), Toast.LENGTH_SHORT).show();
                    return;
                }


                AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);

                View dialogView = mActivity.getLayoutInflater().inflate(R.layout.dialog_list,null);

                final ListView listView = (ListView)dialogView.findViewById(R.id.list_acc);
                final AccountListAdapter adapter = new AccountListAdapter(accountList);
                listView.setAdapter(adapter);
                builder.setView(dialogView);
                builder.setTitle(mActivity.getResources().getString(R.string.google_acc_name));
                builder.setMessage(mActivity.getResources().getString(R.string.google_acc_device_get));

                builder.setPositiveButton(mActivity.getString(R.string.questions_answer_save), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String value1=usernameInput.getText().toString();
                        String value2=passwordInput.getText().toString();
                        if(value1.equals(null)&&value2.equals(null));
                        dialogNotificationListener.onLiginDialogResult();
                    }
                });

                builder.setNegativeButton(mActivity.getString(R.string.questions_answer_cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialogNotificationListener.onLiginDialogResult();
                        dialog.dismiss();

                    }
                });

                AlertDialog alert = builder.create();
                alert.show();
        }
    }


    public class AccountListAdapter extends BaseAdapter{

        private Account[] accountList;
        private LayoutInflater layoutInflater;

        public AccountListAdapter(Account[] accountList) {
            layoutInflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.accountList = accountList;
        }


        @Override
        public int getCount() {
            return accountList.length;
        }

        @Override
        public Object getItem(int position) {
            return accountList[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public
        @NonNull
        View getView(final int position, View convertView, @NonNull ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                view = layoutInflater.inflate(android.R.layout.simple_list_item_1, parent, false);
            }

            final Account account = getAccount(position);

            TextView infoNameTextView = (TextView) view.findViewById(android.R.id.text1);
            infoNameTextView.setText(account.name);
            infoNameTextView.setTextSize(mActivity.getResources().getDimension(R.dimen.text_size_medium));

            return view;
        }

        private Account getAccount(int position) {
            return (Account) getItem(position);
        }

    }



}