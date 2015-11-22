package com.radomar.socialnetworklogin;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.radomar.socialnetworklogin.fragments.GooglePlusFragment;
import com.radomar.socialnetworklogin.fragments.TwitterFragment;

public class MainActivity extends Activity implements View.OnClickListener {

    Button mTwitter;
    Button mGooglePlus;

    private TwitterFragment mTwitterFragment;
    private GooglePlusFragment mGooglePlusFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTwitter = (Button)findViewById(R.id.btTwitter_AM);
        mGooglePlus = (Button)findViewById(R.id.btGooglePlus_AM);

        mTwitter.setOnClickListener(this);
        mGooglePlus.setOnClickListener(this);

    }


    private void enableTwitterFragment() {
        mTwitterFragment = new TwitterFragment();

        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();

        fragmentTransaction.replace(R.id.rlForFragment, mTwitterFragment);
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.commit();
    }

    private void enableGooglePlusFragment() {
        mGooglePlusFragment = new GooglePlusFragment();

        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();

        fragmentTransaction.replace(R.id.rlForFragment, mGooglePlusFragment);
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (mGooglePlusFragment != null) {
            mGooglePlusFragment.onActivityResult(requestCode, resultCode, data);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btTwitter_AM:
                enableTwitterFragment();
                break;
            case R.id.btGooglePlus_AM:
                enableGooglePlusFragment();
                break;
        }
    }
}
