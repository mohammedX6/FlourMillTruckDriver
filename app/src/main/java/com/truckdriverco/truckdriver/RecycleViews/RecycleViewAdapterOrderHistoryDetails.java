package com.truckdriverco.truckdriver.RecycleViews;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.truckdriverco.truckdriver.Model.OrderProducts;
import com.truckdriverco.truckdriver.R;

import java.util.List;

public class RecycleViewAdapterOrderHistoryDetails extends RecyclerView.Adapter<RecycleViewAdapterOrderHistoryDetails.MyViewHolder> {


    private Context myContext;
    private List<OrderProducts> orderProducts;

    public RecycleViewAdapterOrderHistoryDetails(Context myContext, List<OrderProducts> orderProducts) {
        this.myContext = myContext;
        this.orderProducts = orderProducts;
    }

    @NonNull
    @Override
    public RecycleViewAdapterOrderHistoryDetails.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater MyInflater = LayoutInflater.from(myContext);
        view = MyInflater.inflate(R.layout.cart_item_layout2, parent, false);
        return new RecycleViewAdapterOrderHistoryDetails.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecycleViewAdapterOrderHistoryDetails.MyViewHolder holder, int position) {
        holder.TextName.setText("Badge Name: " + orderProducts.get(position).getBadgeName());
        holder.usage.setText("Total Tons: " + orderProducts.get(position).getTons());
        holder.price.setText("Price: " + orderProducts.get(position).getPrice());
        String imageUrl = orderProducts.get(position).getUrl();
        Picasso.get().load(imageUrl).into(holder.Image);
    }

    @Override
    public int getItemCount() {
        return orderProducts.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView TextName;
        TextView usage;
        TextView price;
        ImageView Image;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            TextName = itemView.findViewById(R.id.cart_prtitle);
            usage = itemView.findViewById(R.id.cart_usage);
            price = itemView.findViewById(R.id.cart_prprice);
            Image = itemView.findViewById(R.id.image_cartlist);
        }
    }
}
