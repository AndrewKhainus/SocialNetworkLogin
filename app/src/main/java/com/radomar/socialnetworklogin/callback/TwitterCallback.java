package com.radomar.socialnetworklogin.callback;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;

/**
 * Created by Андрей on 03.11.2015.
 */
public class TwitterCallback extends Callback<TwitterSession> {

    @Override
    public void success(Result<TwitterSession> result) {

    }

    @Override
    public void failure(TwitterException e) {

    }
}
