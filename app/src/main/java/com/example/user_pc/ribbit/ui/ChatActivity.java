package com.example.user_pc.ribbit.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.user_pc.ribbit.utils.ParseConstants;
import com.example.user_pc.ribbit.R;
import com.example.user_pc.ribbit.adapters.ChatMessageAdapter;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class ChatActivity extends ActionBarActivity {

    protected ParseRelation<ParseUser> mChatFriendsRelation;
    protected List<ParseObject> mChatMessages;
    protected ParseUser mCurrentUser;
    protected String mRecipientUserObjId;


    @InjectView(R.id.chatFriendUserName) TextView mChatFriendUserName;
    @InjectView(R.id.currentUserName) TextView mCurrentUserName;
    @InjectView(R.id.chatTextField) EditText mChatText;
    @InjectView(R.id.chatSendButton) Button mChatSendButton;
    @InjectView(R.id.MessageList) ListView mMessageListView;
    @InjectView(R.id.EmptyChat) TextView mEmptyChat;


    protected List<ParseUser> mUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.inject(this);

        Intent intent = getIntent();
        final String currentUser = intent.getStringExtra("currentUsername");
        mRecipientUserObjId = intent.getStringExtra("recipientId");

        mChatFriendUserName.setText(currentUser);


        ParseUser chatCurrentUser = new ParseUser().getCurrentUser();
        String chatUser = chatCurrentUser.getUsername();
        mCurrentUserName.setText(chatUser);

        mChatSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String ChatTextField = mChatText.getText().toString();

                if (ChatTextField == null) {

                    mChatText.setText("");

                } else {

                    ParseObject chatMessage = new ParseObject("chatMessage");

                    String chatMessagesString = mChatText.getText().toString();

                    chatMessage.put("Messages", chatMessagesString);

                    chatMessage.put("recipientId", mRecipientUserObjId);


                    chatMessage.put("senderId", ParseUser.getCurrentUser().getObjectId());

                    chatMessage.saveInBackground();
                    mChatText.setText("");
                }

            }
        });





    }

    @Override
    protected void onResume() {
        super.onResume();

        mCurrentUser = ParseUser.getCurrentUser();

       // mFriendsRelation = mCurrentUser.getRelation(ParseConstants.KEY_FRIENDS_RELATIONS);

        String test = getRecipientId();


        final ParseQuery<ParseObject> query1 = ParseQuery.getQuery(ParseConstants.CLASS_CHAT_MESSAGES);
        //query1.orderByAscending(ParseConstants.CHAT_CREATED_AT);
        query1.whereEqualTo(ParseConstants.CHAT_SENDER, test);
        query1.whereEqualTo(ParseConstants.CHAT_RECIPIENT, ParseUser.getCurrentUser().getObjectId());

        final ParseQuery<ParseObject> query2 = ParseQuery.getQuery(ParseConstants.CLASS_CHAT_MESSAGES);
        query2.whereEqualTo(ParseConstants.CHAT_SENDER, ParseUser.getCurrentUser().getObjectId());
        query2.whereEqualTo(ParseConstants.CHAT_RECIPIENT, test);

        final List<ParseQuery<ParseObject>> queries = new ArrayList<>();
        queries.add(query1);
        queries.add(query2);

        ParseQuery<ParseObject> query = ParseQuery.or(queries);
        query.setLimit(1000); //set the numbers of record to be displayed.
        query.orderByAscending(ParseConstants.CHAT_CREATED_AT);

       // mFriendsProgressBar.setVisibility(View.VISIBLE);
        //query.whereContainedIn(ParseUser.getCurrentUser(),RecipientsActivity.getRecipientIds());
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> Messages, ParseException e) {
                if (e == null) {
                    //Success
                    mChatMessages = Messages;
                    String[] messageList = new String[mChatMessages.size()];
                    int i = 0;
                    for (ParseObject MessagesUser : mChatMessages) {
                       // if (query.whereEqualTo(ParseConstants.CHAT_RECIPIENT,)) {
                            messageList[i] = MessagesUser.getString(ParseConstants.CHAT_MESSAGE);
                            i++;
                       // }
                    }
                    //String[] test123 = {"Hi","Hi","My name is Ys","Nice to meet you"};
                    if (mMessageListView.getAdapter() == null) {
                        ChatMessageAdapter adapter = new ChatMessageAdapter(mMessageListView.getContext(),
                                mChatMessages);
                        mMessageListView.setAdapter(adapter);
                        //adapter.notifyDataSetChanged();
                        //mMessageListView.setSelection(items.size()-1);
                        //mMessageListView.deferNotifyDataSetChanged();
                        mMessageListView.setEmptyView(mEmptyChat);
                    }else{
                        //refill the adapter
                        ((ChatMessageAdapter)mMessageListView.getAdapter()).refill(mChatMessages);
                    }
                    // mFriendsProgressBar.setVisibility(View.INVISIBLE);

                    // addFriendCheckmarks();

                } else {
                    //Log.e(TAG, e.getMessage());
                    AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
                    builder.setMessage(e.getMessage())
                            .setTitle(R.string.error_title)
                            .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    mMessageListView.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chat, menu);
        return true;
    }

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
    }

    public String getRecipientId(){
        return mRecipientUserObjId;
    }

    /*
    protected void sendPushNotifications(){
        ParseQuery<ParseInstallation> query = ParseInstallation.getQuery();
        query.whereContainedIn(ParseConstants.KEY_USER_ID, getRecipientId());

        //send push notification
        ParsePush push = new ParsePush();
        push.setQuery(query);
        push.setMessage(getString(R.string.push_message,ParseUser.getCurrentUser().getUsername()));

        push.sendInBackground();
    }
    */
}
