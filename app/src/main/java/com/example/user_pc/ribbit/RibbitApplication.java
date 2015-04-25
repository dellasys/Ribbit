package com.example.user_pc.ribbit;

import android.app.Application;
import android.util.Log;

import com.example.user_pc.ribbit.ui.MainActivity;
import com.example.user_pc.ribbit.utils.ParseConstants;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseUser;
import com.parse.PushService;
import com.parse.SaveCallback;

/**
 * Created by User-PC on 4/8/2015.
 */
public class RibbitApplication extends Application {

    @Override
    public void onCreate(){
        // Enable Local Datastore.
        super.onCreate();
        Parse.enableLocalDatastore(this);

        //Application ID and Client ID to access our backend
        Parse.initialize(this, "crJmCNQ02wYzA3zOOyy4p5mOYseSlMzk894GM6Az", "3llM3zOJsHR2y1LeHL24ZlHaswut1eXnubjVBBZY");
        ParsePush.subscribeInBackground("", new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.d("com.parse.push", "successfully subscribed to the broadcast channel.");
                } else {
                    Log.e("com.parse.push", "failed to subscribe for push", e);
                }
            }
        });
       // PushService.subscribe(this, "ChannelName", MainActivity.class, R.drawable.ic_stat_treehouse_256_black);
        ParseInstallation.getCurrentInstallation().saveInBackground();

    }

    public static void updateParseInstallation(ParseUser user){
        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        installation.put(ParseConstants.KEY_USER_ID,user.getObjectId());
        installation.saveInBackground();
    }
}
