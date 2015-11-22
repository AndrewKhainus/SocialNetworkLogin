package com.radomar.socialnetworklogin.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.PlusShare;
import com.google.android.gms.plus.model.people.Person;
import com.radomar.socialnetworklogin.R;
import com.squareup.picasso.Picasso;

/**
 * Created by Radomar on 02.11.2015.
 */
public class GooglePlusFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks,
                                                            GoogleApiClient.OnConnectionFailedListener,
                                                            View.OnClickListener {

    private final static String TAG = "sometag";
    private final static int RC_SIGN_IN = 42;

    private GoogleApiClient mGoogleApiClient;
    /* Is there a ConnectionResult resolution in progress? */
    private boolean mIsResolving = false;

    /* Should we automatically resolve ConnectionResults when possible? */
    private boolean mShouldResolve = false;

    private ImageView mUserImage;
    private TextView mUserInfo;
    private Button mShare;
    private EditText mText;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Build GoogleApiClient with access to basic profile
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(new Scope(Scopes.PROFILE))
                .addScope(new Scope(Scopes.EMAIL))
                .build();

        View view = inflater.inflate(R.layout.fragment_google_plus, container, false);

        view.findViewById(R.id.sign_in_button).setOnClickListener(this);

        mUserImage = (ImageView) view.findViewById(R.id.ivUserPhoto_FGP);
        mUserInfo = (TextView) view.findViewById(R.id.tvUserInfo_FGP);
        mShare = (Button) view.findViewById(R.id.btShare_FGP);
        mText = (EditText) view.findViewById(R.id.etText_FGP);

        mShare.setOnClickListener(this);

        return view;
    }

    @Override
    public void onConnected(Bundle bundle) {
        // onConnected indicates that an account was selected on the device, that the selected
        // account has granted any requested permissions to our app and that we were able to
        // establish a service connection to Google Play services.
        Log.d(TAG, "onConnected:" + bundle);
        mShouldResolve = false;

        Person currentPerson = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
        Log.d("sometag", "" + (currentPerson == null));
        //NullPointerException: Attempt to invoke interface method 'com.google.android.gms.plus.model.people.Person$Image com.google.android.gms.plus.model.people.Person.getImage()' on a null object reference
        String personPhoto = currentPerson.getImage().getUrl();
//        mStatus.setText("Signed In to My App");
        setText(currentPerson);

        Picasso.with(getActivity())
                .load(personPhoto)
                .into(mUserImage);

    }

    private void setText(Person person) {
        mUserInfo.setText("Nick name: " + person.getDisplayName() + "\n \n"
                + "Email address: " + Plus.AccountApi.getAccountName(mGoogleApiClient) + "\n \n"
                + "Url Google Plus Profile: " + person.getUrl() + "\n \n"
                + "Birthday: " + person.getBirthday());
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                onSignInClicked();
                break;
            case R.id.btShare_FGP:
                shareText();
                break;
        }
    }

    private void shareText() {
        Intent shareIntent = new PlusShare.Builder(getActivity())
                .setType("text/plain")
                .setText(mText.getText())
                .getIntent();
        startActivityForResult(shareIntent, 0);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
// Could not connect to Google Play Services.  The user needs to select an account,
        // grant permissions or resolve an error in order to sign in. Refer to the javadoc for
        // ConnectionResult to see possible error codes.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);

        if (!mIsResolving && mShouldResolve) {
            if (connectionResult.hasResolution()) {
                try {
                    connectionResult.startResolutionForResult(getActivity(), RC_SIGN_IN);
                    mIsResolving = true;
                } catch (IntentSender.SendIntentException e) {
                    Log.e(TAG, "Could not resolve ConnectionResult.", e);
                    mIsResolving = false;
                    mGoogleApiClient.connect();
                }
            } else {
                // Could not resolve the connection result, show the user an
                // error dialog.
//                showErrorDialog(connectionResult);
                Log.e(TAG, "Error dialog");
            }
        } else {
            // Show the signed-out UI
//            showSignedOutUI();
            Log.e(TAG, "some problem");
        }
    }

    private void onSignInClicked() {
        // User clicked the sign-in button, so begin the sign-in process and automatically
        // attempt to resolve any errors that occur.
        mShouldResolve = true;
        mGoogleApiClient.connect();

        // Show a message to the user that we are signing in.
//        mStatus.setText(R.string.signing_in);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult:" + requestCode + ":" + resultCode + ":" + data);

        if (requestCode == RC_SIGN_IN) {
            // If the error resolution was not successful we should not resolve further.
            if (resultCode != Activity.RESULT_OK) {
                mShouldResolve = false;
            }

            mIsResolving = false;
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }
}
