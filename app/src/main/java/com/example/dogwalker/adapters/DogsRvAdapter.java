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
import com.example.dogwalker.models.Dog;

import java.util.List;

public class DogsRvAdapter extends RecyclerView.Adapter<DogsRvAdapter.DogsRvViewHolder> {

    private List<Dog> dogList;
    private DogClickListener dogClickListener;
    public DogsRvAdapter(List<Dog> dogs,DogClickListener dogClickListener) {
        this.dogList = dogs;
        this.dogClickListener = dogClickListener;
    }

    @NonNull
    @Override
    public DogsRvViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DogsRvViewHolder(
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull DogsRvViewHolder holder, int position) {
        Dog dog = dogList.get(position);
        holder.bind(dog,dogClickListener);
    }

    @Override
    public int getItemCount() {
        return dogList.size();
    }

    static class DogsRvViewHolder extends RecyclerView.ViewHolder {
        ImageView itemImage;
        TextView itemName;

        public DogsRvViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImage = itemView.findViewById(R.id.list_item_iv);
            itemName = itemView.findViewById(R.id.list_item_tv);
        }

        public void bind(Dog dog,DogClickListener dogClickListener) {
            itemName.setText(dog.getName());
            if(!dog.getImageAddress().equals("undefined")) {
                Glide.with(itemView.getContext()).load(dog.getImageAddress()).into(itemImage);
            }else {
                itemImage.setImageResource(R.drawable.dog);
            }
            itemView.setOnClickListener(view -> dogClickListener.onClick(dog));
        }
    }
}
