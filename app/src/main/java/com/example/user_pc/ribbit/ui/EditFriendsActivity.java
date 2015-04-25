package com.example.user_pc.ribbit.ui;

import android.app.AlertDialog;
import android.media.Image;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.internal.widget.AdapterViewCompat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.user_pc.ribbit.adapters.UserAdapter;
import com.example.user_pc.ribbit.utils.ParseConstants;
import com.example.user_pc.ribbit.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class EditFriendsActivity extends ActionBarActivity {

    public  static final String TAG = EditFriendsActivity.class.getSimpleName();

   // @InjectView(R.id.friendsListProgressBar) ProgressBar mFriendsProgressBar;
   // @InjectView(R.id.edit_friends_list) ListView mEditFriendsList;
    protected List<ParseUser> mUsers;
    protected ParseRelation<ParseUser> mFriendsRelation;
    protected ParseUser mCurrentUser;
    @InjectView(R.id.friendsGrid) GridView mGridView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_grid);
        ButterKnife.inject(this);

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                if (mGridView.isItemChecked(position)){
                    //add friend
                    mFriendsRelation.add(mUsers.get(position));

                }else{
                    //remove friend
                    mFriendsRelation.remove(mUsers.get(position));
                }
                mCurrentUser.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                       if (e == null){


                       }else{
                           AlertDialog.Builder builder = new AlertDialog.Builder(EditFriendsActivity.this);
                           builder.setMessage(e.getMessage())
                                   .setTitle("Error occurred")
                                   .setPositiveButton(android.R.string.ok, null);
                           AlertDialog dialog = builder.create();
                           dialog.show();
                       }
                    }
                });
            }
        });

        mGridView.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE);
        mGridView.setOnItemClickListener((AdapterView.OnItemClickListener) mOnItemClickListener);

        TextView emptyTextView = (TextView)findViewById(android.R.id.empty);
        mGridView.setEmptyView(emptyTextView);
    }

    @Override
    protected void onResume() {
        super.onResume();

        mCurrentUser = ParseUser.getCurrentUser();
        mFriendsRelation = mCurrentUser.getRelation(ParseConstants.KEY_FRIENDS_RELATIONS);

        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.orderByAscending(ParseConstants.KEY_USERNAME);
        query.setLimit(1000); //set the numbers of record to be displayed.

        //mFriendsProgressBar.setVisibility(View.VISIBLE);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> parseUsers, ParseException e) {
                if (e == null){
                    //Success
                    mUsers = parseUsers;
                    String[] usernames = new String[mUsers.size()];
                    int i = 0;
                    for(ParseUser user : mUsers){
                        usernames[i] = user.getUsername();
                        i++;
                    }
                    if (mGridView.getAdapter() == null) {
                        UserAdapter adapter = new UserAdapter(EditFriendsActivity.this, mUsers);
                        mGridView.setAdapter(adapter);
                    }else{
                        ((UserAdapter)mGridView.getAdapter()).refill(mUsers);
                    }

                    //mFriendsProgressBar.setVisibility(View.INVISIBLE);

                    addFriendCheckmarks();

                }else{
                    Log.e(TAG, e.getMessage());
                    AlertDialog.Builder builder = new AlertDialog.Builder(EditFriendsActivity.this);
                    builder.setMessage(e.getMessage())
                            .setTitle(R.string.error_title)
                            .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    //mFriendsProgressBar.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    private void addFriendCheckmarks() {

        mFriendsRelation.getQuery().findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> friends, ParseException e) {
                   if(e == null){
                       //List returned - look for a match
                       for (int i = 0; i < mUsers.size(); i++){
                           ParseUser user = mUsers.get(i);

                           for (ParseUser friend : friends){
                               if(friend.getObjectId().equals(user.getObjectId())){
                                   mGridView.setItemChecked(i, true);
                               }
                           }
                       }
                   }else{
                        Log.e(TAG,e.getMessage());
                   }
            }
        });
    }

    protected AdapterView.OnItemClickListener mOnItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            ImageView checkImageView = (ImageView)view.findViewById(R.id.checkImageView);

            if (mGridView.isItemChecked(position)){
                //add friend
                mFriendsRelation.add(mUsers.get(position));
                checkImageView.setVisibility(View.VISIBLE);

            }else{
                //remove friend
                mFriendsRelation.remove(mUsers.get(position));
                checkImageView.setVisibility(View.INVISIBLE);
            }
            mCurrentUser.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e != null) {
                        Log.e(TAG, e.getMessage());
                    }
                }
            });
        }
    };
/*
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/
/*
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        if (getListView().isItemChecked(position)){
            //add friend
            mFriendsRelation.add(mUsers.get(position));

        }else{
            //remove friend
            mFriendsRelation.remove(mUsers.get(position));
        }
        mCurrentUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                Log.e(TAG,e.getMessage());
            }
        });

    } */
}
