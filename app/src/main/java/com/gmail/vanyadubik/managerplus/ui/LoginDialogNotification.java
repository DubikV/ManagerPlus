package com.gmail.vanyadubik.managerplus.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.gmail.vanyadubik.managerplus.R;
import com.gmail.vanyadubik.managerplus.utils.SharedStorage;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import static com.gmail.vanyadubik.managerplus.common.Consts.GOOGLE_ACC_CONNECTED;
import static com.gmail.vanyadubik.managerplus.common.Consts.GOOGLE_ACC_EMAIL;
import static com.gmail.vanyadubik.managerplus.common.Consts.GOOGLE_ACC_ID;
import static com.gmail.vanyadubik.managerplus.common.Consts.GOOGLE_ACC_IMAGE;
import static com.gmail.vanyadubik.managerplus.common.Consts.GOOGLE_ACC_NAME;
import static com.gmail.vanyadubik.managerplus.common.Consts.GOOGLE_ACC_URL;
import static com.gmail.vanyadubik.managerplus.common.Consts.TAGLOG;

public class LoginDialogNotification extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

    public static final int CAPTURE_LOGIN_DIALOG_GOOGLE_REQ = 1001;
    public static final int CAPTURE_LOGIN_DIALOG_ACTIVITY_REQ = 1001;
    public static final String LOGIN_DIALOG_ACTIVITY_RESULT = "login_dialog_activity_result";

    private TextView usernameInput, passwordInput;
    private GoogleApiClient mGoogleApiClient;
    private Person googlePerson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gps_tracking_notification);

//        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestEmail()
//                .requestProfile()
////                .requestScopes(new Scope(Scopes.PLUS_ME))
////                .requestScopes(new Scope(Scopes.PLUS_LOGIN))
////                .requestScopes(new Scope(Scopes.PLUS_ME), new Scope(Scopes.PLUS_LOGIN),new Scope(Scopes.PROFILE))
//                .build();
//        mGoogleApiClient = new GoogleApiClient.Builder(this)
//                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
//                .addConnectionCallbacks(this)
//                .addOnConnectionFailedListener(this)
//               // .addApi(Plus.API)
//                .build();
        GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestScopes(new Scope(Scopes.PLUS_LOGIN))
                .build();

        // Build the GoogleApiClient object
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

    }

    public void onStart() {
        super.onStart();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.login_dialog,null);
        dialogView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), android.R.color.transparent));
        usernameInput = (EditText)dialogView.findViewById(R.id.login);
        passwordInput = (EditText)dialogView.findViewById(R.id.password);
        builder.setView(dialogView);

        builder.setTitle(getResources().getString(R.string.google_acc_name));
        builder.setMessage(getResources().getString(R.string.google_acc_login));
        builder.setPositiveButton(getString(R.string.questions_answer_save), new SaveButton());
        builder.setNegativeButton(getString(R.string.questions_answer_cancel), new DissmisButton());
        builder.setNeutralButton(getString(R.string.questions_get_from_list), new ShowAllAccountsGoogle());

        builder.create().show();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        googlePerson = Plus.PeopleApi
                .getCurrentPerson(mGoogleApiClient);
        if(googlePerson == null){
            Toast.makeText(this, getResources().getString(R.string.google_account_not_found), Toast.LENGTH_LONG).show();
            return;
        }
        SharedStorage.setBoolean(this.getApplicationContext(), GOOGLE_ACC_CONNECTED, true);
        SharedStorage.setString(this.getApplicationContext(), GOOGLE_ACC_ID, (String) googlePerson.getId());
        SharedStorage.setString(this.getApplicationContext(), GOOGLE_ACC_URL, (String) googlePerson.getUrl());
        SharedStorage.setString(this.getApplicationContext(), GOOGLE_ACC_NAME, (String) googlePerson.getDisplayName());
        SharedStorage.setString(this.getApplicationContext(), GOOGLE_ACC_EMAIL, Plus.AccountApi.getAccountName(mGoogleApiClient));
        SharedStorage.setString(getApplicationContext(), GOOGLE_ACC_IMAGE, (String) googlePerson.getImage().getUrl());

        Log.d(TAGLOG, "Current person: name=" + googlePerson.getDisplayName() + ", has birthday = " + (googlePerson.hasBirthday() ? "yes, it is" + googlePerson.getBirthday() : "no"));

        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.clearDefaultAccountAndReconnect();
        }

       setResultActivity(true);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, getResources().getString(R.string.google_account_not_connect), Toast.LENGTH_LONG).show();

    }

    private class SaveButton implements OnClickListener {
        SaveButton() {
        }

        public void onClick(DialogInterface dialog, int id) {
            dialog.cancel();
            setResultActivity(true);
        }
    }

    private class DissmisButton implements OnClickListener {
        DissmisButton() {
        }

        public void onClick(DialogInterface dialog, int id) {
            dialog.cancel();
            setResultActivity(false);
        }
    }

    private class ShowAllAccountsGoogle implements OnClickListener {
        ShowAllAccountsGoogle() {
        }

        public void onClick(DialogInterface dialog, int id) {
            dialog.dismiss();
            Auth.GoogleSignInApi.signOut(mGoogleApiClient);
            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
            startActivityForResult(signInIntent, CAPTURE_LOGIN_DIALOG_GOOGLE_REQ);
        }
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
                    SharedStorage.setBoolean(this.getApplicationContext(), GOOGLE_ACC_CONNECTED, true);
                    SharedStorage.setString(this.getApplicationContext(), GOOGLE_ACC_ID, (String) googleSignInAccount.getId());
                    SharedStorage.setString(this.getApplicationContext(), GOOGLE_ACC_NAME, (String) googleSignInAccount.getDisplayName());
                    SharedStorage.setString(this.getApplicationContext(), GOOGLE_ACC_EMAIL, (String) googleSignInAccount.getEmail());
                    SharedStorage.setString(getApplicationContext(), GOOGLE_ACC_IMAGE, String.valueOf(googleSignInAccount.getPhotoUrl()));
                    setResultActivity(true);
                } else {
                    Toast.makeText(this, getResources().getString(R.string.google_account_not_connect), Toast.LENGTH_LONG).show();
                }
            }else{
                Toast.makeText(this, getResources().getString(R.string.google_account_not_connect), Toast.LENGTH_LONG).show();
            }
        }
    }

    private void setResultActivity(Boolean confirmed){
        Intent intent = new Intent();
        intent.putExtra(LOGIN_DIALOG_ACTIVITY_RESULT, confirmed);
        setResult(RESULT_OK, intent);
        finish();
    }

}