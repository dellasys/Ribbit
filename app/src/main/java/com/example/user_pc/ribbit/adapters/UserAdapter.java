package com.example.user_pc.ribbit.adapters;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.user_pc.ribbit.R;
import com.example.user_pc.ribbit.utils.MD5Util;
import com.example.user_pc.ribbit.utils.ParseConstants;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.List;

/**
 * Created by User-PC on 4/13/2015.
 */
public class UserAdapter extends ArrayAdapter<ParseUser>{

    protected Context mContext;
    protected List<ParseUser> mUsers;

    public UserAdapter(Context context, List<ParseUser> users) {
       super(context, R.layout.message_item,users);
        mContext = context;
        mUsers = users;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        if (convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.user_item,null);
            holder = new ViewHolder();
            holder.userImageView = (ImageView)convertView.findViewById(R.id.userImageView);
            holder.nameLabel = (TextView)convertView.findViewById(R.id.nameLabel);
            holder.checkImageView = (ImageView)convertView.findViewById(R.id.checkImageView);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder)convertView.getTag();
        }

        ParseUser user = mUsers.get(position);
        String email = user.getEmail().toLowerCase();


        if (email.equals("")){
            holder.userImageView.setImageResource(R.drawable.avatar_empty);
        }else{
            String hash = MD5Util.md5Hex(email);
            String gravatarUrl = "http://www.gravatar.com/avatar/HASH"
                    + hash + "?s=204&d=404";
            Picasso.with(mContext).load(gravatarUrl)
                    .placeholder(R.drawable.avatar_empty)
                    .into(holder.userImageView);

        }

//        if (user.getString(ParseConstants.KEY_FILE_TYPE).equals(ParseConstants.TYPE_IMAGE)) {
//            holder.userImageView.setImageResource(R.drawable.ic_picture);
//        }else{
//            holder.userImageView.setImageResource(R.drawable.ic_video);
//        }
        holder.nameLabel.setText(user.getUsername());

        GridView gridView = (GridView)parent;
        if (gridView.isItemChecked(position)){
            holder.checkImageView.setVisibility(View.VISIBLE);
        }else{
            holder.checkImageView.setVisibility(View.INVISIBLE);
        }

        return convertView;
    }

    private static class ViewHolder{
        ImageView userImageView;
        ImageView checkImageView;
        TextView nameLabel;
    }

    public void refill(List<ParseUser> users){ // this method makes the scrolling contents reposition after the user
        //come back from another activity.the position fixed on the content that user stop before.
        mUsers.clear();
        mUsers.addAll(users);
        notifyDataSetChanged();
    }
}
