package com.example.dogwalker.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.dogwalker.R;
import com.example.dogwalker.models.User;

import java.util.List;

public class UsersRvAdapter extends RecyclerView.Adapter<UsersRvAdapter.UsersRvViewHolder> {


    private List<User> users;
    private UserClickListener userClickListener;
    public UsersRvAdapter(List<User> users,UserClickListener userClickListener) {
        this.users = users;
        this.userClickListener = userClickListener;
    }

    @NonNull
    @Override
    public UsersRvViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new UsersRvViewHolder(
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull UsersRvViewHolder holder, int position) {
        User user = users.get(position);
        holder.bind(user,userClickListener);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    static class UsersRvViewHolder extends RecyclerView.ViewHolder {
        ImageView itemImage;
        TextView itemName;

        public UsersRvViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImage = itemView.findViewById(R.id.list_item_iv);
            itemName = itemView.findViewById(R.id.list_item_tv);
        }

        public void bind(User user,UserClickListener userClickListener) {
            itemName.setText(user.getFullName());
            if(!user.getImageAddress().equals("undefined")) {
                Glide.with(itemView.getContext()).load(user.getImageAddress()).into(itemImage);
            }else {
                itemImage.setImageResource(R.drawable.downowneruser);
            }
            itemView.setOnClickListener(view -> userClickListener.onClick(user));
        }
    }
}
