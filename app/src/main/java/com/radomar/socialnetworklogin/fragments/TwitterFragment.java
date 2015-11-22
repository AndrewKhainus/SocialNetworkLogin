package com.radomar.socialnetworklogin.fragments;

import android.app.Fragment;
import android.content.Intent;
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
import android.widget.Toast;

import com.radomar.socialnetworklogin.R;
import com.squareup.picasso.Picasso;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.models.User;
import com.twitter.sdk.android.core.services.StatusesService;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;

import io.fabric.sdk.android.Fabric;

/**
 * Created by Radomar on 31.10.2015.
 */
public class TwitterFragment extends Fragment implements View.OnClickListener {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "p0Olyd5iQnyo2ISrqHriVIXn0";
    private static final String TWITTER_SECRET = "fgAMACiH7GnhAlfnZU6Xxm0i3CKT49nPokB5je6CVP6VcuDMzR";

    private Button mTweetButton;
    private Button mNewTweet;
    private EditText mText;
    private TwitterLoginButton loginButton;
    private TextView mUserInfo;
    private ImageView mUserImage;
    private TwitterSession mTwitterSession;
    private long mUserId;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_twitter, container, false);

        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(getActivity(), new Twitter(authConfig));

        if (Twitter.getInstance().getSessionManager().getActiveSession() != null) {
            mTwitterSession = Twitter.getInstance().core.getSessionManager().getActiveSession();
            getUserInfo();
//            mEdShareTwitter.setVisibility(View.VISIBLE);
//            mBtnSendTweet.setVisibility(View.VISIBLE);
        }

        findViews(view);
        initLoginButton();
        setListener();

        return view;
    }

    private void findViews(View view) {
        mTweetButton = (Button) view.findViewById(R.id.btTweet_FT);
        mText = (EditText) view.findViewById(R.id.etMessage_FT);
        loginButton = (TwitterLoginButton) view.findViewById(R.id.tlButton_FT);
        mNewTweet = (Button) view.findViewById(R.id.btNewTweet_FT);
        mUserInfo = (TextView) view.findViewById(R.id.tvUserInfo_FT);
        mUserImage = (ImageView) view.findViewById(R.id.ivUserPhoto_FT);
    }

    private void initLoginButton() {
        loginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                // The TwitterSession is also available through:
                // Twitter.getInstance().core.getSessionManager().getActiveSession()
                mTwitterSession = result.data;
                // TODO: Remove toast and use the TwitterSession's userID
                // with your app's user model
                mUserId = mTwitterSession.getUserId();
                Log.d("sometag", "id = " + mUserId);
                String msg = "@" + mTwitterSession.getUserName() + " logged in! (#" + mUserId + ")";
                Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
            }

            @Override
            public void failure(TwitterException exception) {
                Log.d("sometag", "Login with Twitter failure", exception);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        loginButton.onActivityResult(requestCode, resultCode, data);
    }

    private void setListener() {
        mTweetButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btTweet_FT:
                publishTweet();
                break;
        }

    }

    private void publishTweet() {
        try {
            final StatusesService statusesService = Twitter.getInstance().getApiClient().getStatusesService();
            statusesService.update(mText.getText().toString(), null, null, null, null, null, null, null, null, new Callback<Tweet>() {
                @Override
                public void success(Result<Tweet> tweetResult) {
                    Toast.makeText(getActivity(), "Успешно опубликовали статус",
                            Toast.LENGTH_SHORT).show();
                }

                @Override
                public void failure(TwitterException e) {
                    Toast.makeText(getActivity(), "Ошибка при отправке твита",
                            Toast.LENGTH_SHORT).show();
                }
            });
        } catch (IllegalStateException e) {
            Toast.makeText(getActivity(), "login to server", Toast.LENGTH_SHORT).show();
        }

    }

    private void getUserInfo() {
        TwitterApiClient twitterApiClient = TwitterCore.getInstance().getApiClient(mTwitterSession);
        twitterApiClient.getAccountService().verifyCredentials(null, null, new Callback<User>() {
            @Override
            public void success(Result<User> result) {
                Log.d("sometag", "info : " + result.data.name);
                setText(result.data);
                String url = result.data.profileImageUrl;
                Log.d("sometag", "url = " + url);
                Picasso.with(getActivity())
                        .load(url)
                        .into(mUserImage);
            }
            @Override
            public void failure(TwitterException e) {
                Log.d("sometag", e.toString());
            }
        });
    }



    private void setText(User data) {
        mUserInfo.setText("Name: " + data.name + "\n"
                + "id: " + data.id + "\n"
        + "ScreenName: " + data.screenName + "\n"
        + "TimeZone: " + data.timeZone + "\n"
        + "url: " + data.url);
    }

}
