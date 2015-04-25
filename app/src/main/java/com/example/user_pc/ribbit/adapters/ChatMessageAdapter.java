package com.example.user_pc.ribbit.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.user_pc.ribbit.utils.ParseConstants;
import com.example.user_pc.ribbit.R;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.List;

/**
 * Created by User-PC on 4/16/2015.
 */
public class ChatMessageAdapter extends ArrayAdapter<ParseObject> {


    protected Context mContext;
    protected List<ParseObject> mMessages;

    public ChatMessageAdapter(Context context, List<ParseObject> Messages) {
        super(context, R.layout.chat_message,Messages);
        mContext = context;
        mMessages = Messages;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        if (convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.chat_message,null);
            holder = new ViewHolder();
            holder.recipientTextView = (TextView)convertView.findViewById(R.id.recipientChatMessage);
            holder.senderTextView = (TextView)convertView.findViewById(R.id.senderChatMessage);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder)convertView.getTag();
        }

        ParseObject message = mMessages.get(position);

            if(message.getString(ParseConstants.CHAT_SENDER).equals(ParseUser.getCurrentUser().getObjectId())) {
            holder.senderTextView.setText(message.getString(ParseConstants.CHAT_MESSAGE));
            holder.senderTextView.setVisibility(View.VISIBLE);
            holder.recipientTextView.setVisibility(View.INVISIBLE);
        }else {
                holder.recipientTextView.setText(message.getString(ParseConstants.CHAT_MESSAGE));
                holder.recipientTextView.setVisibility(View.VISIBLE);
                holder.senderTextView.setVisibility(View.INVISIBLE);
            }
        return convertView;
    }

    private static class ViewHolder{
        TextView recipientTextView;
        TextView senderTextView;
    }

    public void refill(List<ParseObject> messages){ // this method makes the scrolling contents reposition after the user
        //come back from another activity.the position fixed on the content that user stop before.
        mMessages.clear();
        mMessages.addAll(messages);
        notifyDataSetChanged();
    }


}
