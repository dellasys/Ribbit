package com.example.user_pc.ribbit.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
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

/**
 * Created by User-PC on 4/9/2015.
 */
public class FriendsFragment extends Fragment {

    public static final String TAG = FriendsFragment.class.getSimpleName();

    protected List<ParseUser> mFriends;
    protected ParseRelation<ParseUser> mFriendsRelation;
    protected ParseUser mCurrentUser;
    protected ParseRelation<ParseUser> mChatFriendsRelation;
    protected GridView mGridView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.user_grid, container, false); //This line is like setContentView method

        mGridView = (GridView)rootView.findViewById(R.id.friendsGrid);

        TextView emptyTextView = (TextView)rootView.findViewById(android.R.id.empty);
        mGridView.setEmptyView(emptyTextView);

        mGridView.setOnItemClickListener((AdapterView.OnItemClickListener) mOnItemClickListener);

        return rootView;


    }


    @Override
    public void onResume() {
        super.onResume();

        mCurrentUser = ParseUser.getCurrentUser();
        mFriendsRelation = mCurrentUser.getRelation(ParseConstants.KEY_FRIENDS_RELATIONS);

        ParseQuery<ParseUser> query = mFriendsRelation.getQuery();
        query.addAscendingOrder(ParseConstants.KEY_USERNAME);
         query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> friends, ParseException e) {

                if(e == null) {
                    mFriends = friends;

                    String[] usernames = new String[mFriends.size()];
                    int i = 0;
                    for (ParseUser user : mFriends) {
                        usernames[i] = user.getUsername();
                        i++;
                    }
                    if (mGridView.getAdapter() == null) {
                        UserAdapter adapter = new UserAdapter(getActivity(), mFriends);
                        mGridView.setAdapter(adapter);
                    }else{
                        ((UserAdapter)mGridView.getAdapter()).refill(mFriends);
                    }
                }else{

                    Log.e(TAG, e.getMessage());
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage(e.getMessage())
                            .setTitle(R.string.error_title)
                            .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();

                }
            }
        });
    }

    protected AdapterView.OnItemClickListener mOnItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            ParseUser mUser = mFriends.get(position);
            String currentUser = mUser.getUsername();

            mCurrentUser = ParseUser.getCurrentUser();

//        mChatFriendsRelation.add(mUser);
            //  mChatFriendsRelation = mCurrentUser.getRelation(ParseConstants.KEY_FRIENDS_RELATIONS);
            String mRecipientId = mUser.getObjectId();


            Intent intent = new Intent(getActivity(), ChatActivity.class);
            intent.putExtra("currentUsername",currentUser);
            intent.putExtra("recipientId",mRecipientId);

            startActivity(intent);
        }
    };




    /*

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        ParseUser mUser = mFriends.get(position);
        String currentUser = mUser.getUsername();

        mCurrentUser = ParseUser.getCurrentUser();

//        mChatFriendsRelation.add(mUser);
      //  mChatFriendsRelation = mCurrentUser.getRelation(ParseConstants.KEY_FRIENDS_RELATIONS);
        String mRecipientId = mUser.getObjectId();


        Intent intent = new Intent(getActivity(), ChatActivity.class);
        intent.putExtra("currentUsername",currentUser);
        intent.putExtra("recipientId",mRecipientId);

        startActivity(intent);
    }
    */
}