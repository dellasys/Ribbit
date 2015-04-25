package com.example.user_pc.ribbit.ui;

import android.app.AlertDialog;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.internal.widget.AdapterViewCompat;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user_pc.ribbit.adapters.UserAdapter;
import com.example.user_pc.ribbit.utils.FileHelper;
import com.example.user_pc.ribbit.utils.ParseConstants;
import com.example.user_pc.ribbit.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class RecipientsActivity extends ActionBarActivity {



    public static final String TAG = RecipientsActivity.class.getSimpleName();

    protected List<ParseUser> mFriends;
    protected ParseRelation<ParseUser> mFriendsRelation;
    protected ParseUser mCurrentUser;
    protected MenuItem mSendMenuItem;
    protected Uri mMediaUri;
    protected String mFileType;
    @InjectView(R.id.friendsGrid) GridView mGridView;


       // @InjectView(R.id.RecipientsList) ListView mRecipientListView;
       // @InjectView(R.id.empty_friend_list) TextView mEmptyFriendList;
       // @InjectView(R.id.friendListLoadingProgressBar) ProgressBar mfriendListLoadingProgressBar;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.user_grid);

            ButterKnife.inject(this);

            mGridView.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE);
            mGridView.setOnItemClickListener(mOnItemClickListener);

            TextView emptyTextView = (TextView)findViewById(android.R.id.empty);
            mGridView.setEmptyView(emptyTextView);

            mMediaUri = getIntent().getData();
            mFileType = getIntent().getExtras().getString(ParseConstants.KEY_FILE_TYPE);
        }


        @Override
        public void onResume() {
            super.onResume();

            mCurrentUser = ParseUser.getCurrentUser();
            mFriendsRelation = mCurrentUser.getRelation(ParseConstants.KEY_FRIENDS_RELATIONS);

            ParseQuery<ParseUser> query = mFriendsRelation.getQuery();
            query.addAscendingOrder(ParseConstants.KEY_USERNAME);
           // mfriendListLoadingProgressBar.setVisibility(View.VISIBLE);
            query.findInBackground(new FindCallback<ParseUser>() {
                @Override
                public void done(List<ParseUser> friends, ParseException e) {

                    if (e == null) {
                        mFriends = friends;

                        String[] usernames = new String[mFriends.size()];
                        int i = 0;
                        for (ParseUser user : mFriends) {
                            usernames[i] = user.getUsername();
                            i++;
                        }
                        if (mGridView.getAdapter() == null) {
                            UserAdapter adapter = new UserAdapter(RecipientsActivity.this, mFriends);
                            mGridView.setAdapter(adapter);
                        }else{
                            ((UserAdapter)mGridView.getAdapter()).refill(mFriends);
                        }

                    } else {

                        Log.e(TAG, e.getMessage());
                        AlertDialog.Builder builder = new AlertDialog.Builder(RecipientsActivity.this);
                        builder.setMessage(e.getMessage())
                                .setTitle(R.string.error_title)
                                .setPositiveButton(android.R.string.ok, null);
                        AlertDialog dialog = builder.create();
                        dialog.show();
                      //  mfriendListLoadingProgressBar.setVisibility(View.VISIBLE);

                    }
                }
            });
        }


    public int getCheckedItemCount() {
        GridView gridView = mGridView;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            return gridView.getCheckedItemCount();
        }

        SparseBooleanArray checkedItems = gridView.getCheckedItemPositions();
        int count = 0;
        for (int i = 0, size = checkedItems.size(); i < size; ++i) {
            if (checkedItems.valueAt(i)) {
                count++;
            }
        }
        return count;
    }

        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            // Inflate the menu; this adds items to the action bar if it is present.
            getMenuInflater().inflate(R.menu.menu_recipients, menu);
            mSendMenuItem = menu.getItem(0);
            return true;
        }


            @Override
            public boolean onOptionsItemSelected(MenuItem item) {
                // Handle action bar item clicks here. The action bar will
                // automatically handle clicks on the Home/Up button, so long
                // as you specify a parent activity in AndroidManifest.xml.
                switch(item.getItemId()){
                case android.R.id.home:
                //noinspection SimplifiableIfStatement
                NavUtils.navigateUpFromSameTask(this);
                return true;
                case R.id.action_send :
                    ParseObject message = createMessage(); //message will be null if something goes wrong
                    if (message == null){
                        //error
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setMessage(R.string.error_selecting_file)
                                .setTitle(R.string.error_selecting_title)
                                .setPositiveButton(android.R.string.ok, null);
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }else {
                        send(message);
                        finish(); // close up current activity and back to the parent activity.
                    }
                    return true;
                }

                return super.onOptionsItemSelected(item);
            }

    protected ParseObject createMessage(){

        ParseObject message = new ParseObject(ParseConstants.CLASS_MESSAGES);
        message.put(ParseConstants.KEY_SENDER_ID, ParseUser.getCurrentUser().getObjectId());
        message.put(ParseConstants.KEY_SENDER_NAME, ParseUser.getCurrentUser().getUsername());
        message.put(ParseConstants.KEY_RECIPIENT_IDS, getRecipientIds());
        message.put(ParseConstants.KEY_FILE_TYPE, mFileType);

        byte[] fileBytes = FileHelper.getByteArrayFromFile(this, mMediaUri);

        if (fileBytes == null){
            return null;
        }else{
            if (mFileType.equals(ParseConstants.TYPE_IMAGE)){
                fileBytes = FileHelper.reduceImageForUpload(fileBytes);
            }

            String fileName = FileHelper.getFileName(this,mMediaUri,mFileType);
            ParseFile file = new ParseFile(fileName, fileBytes);
            message.put(ParseConstants.KEY_FILE, file);

            return message;
        }
    }

    public ArrayList<String> getRecipientIds(){
        ArrayList<String> recipientIds = new ArrayList<String>();
        for (int i = 0; i < mGridView.getCount();i++){
            if (mGridView.isItemChecked(i)){
                recipientIds.add(mFriends.get(i).getObjectId());
            }

          }       return recipientIds;
    }

    protected void send(ParseObject message){
        message.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null){
                    //success!
                    Toast.makeText(RecipientsActivity.this,R.string.success_message,Toast.LENGTH_LONG).show();
                    sendPushNotifications();
                }else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(RecipientsActivity.this);
                    builder.setMessage(R.string.error_sending_message)
                            .setTitle(R.string.error_selecting_title)
                            .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });
    }

    protected AdapterView.OnItemClickListener mOnItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

            if(getCheckedItemCount() > 0){
                mSendMenuItem.setVisible(true);

            }else{
                mSendMenuItem.setVisible(false);
            }

            ImageView checkImageView = (ImageView)view.findViewById(R.id.checkImageView);

            if (mGridView.isItemChecked(position)){
                //add recipient
                checkImageView.setVisibility(View.VISIBLE);

            }else{
                //remove recipient
                checkImageView.setVisibility(View.INVISIBLE);
            }
        }
    };

    protected void sendPushNotifications(){
        ParseQuery<ParseInstallation> query = ParseInstallation.getQuery();
        query.whereContainedIn(ParseConstants.KEY_USER_ID, getRecipientIds());

        //send push notification
        ParsePush push = new ParsePush();
        push.setQuery(query);
        push.setMessage(getString(R.string.push_message,ParseUser.getCurrentUser().getUsername()));

        push.sendInBackground();
    }

    /*
        @Override
        protected void onListItemClick(ListView l, View v, int position, long id) {
            super.onListItemClick(l, v, position, id);

            mSendMenuItem.setVisible(true);
        }
    */

}
