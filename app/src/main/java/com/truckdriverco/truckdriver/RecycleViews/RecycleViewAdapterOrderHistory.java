package com.truckdriverco.truckdriver.RecycleViews;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.truckdriverco.truckdriver.Model.Order;
import com.truckdriverco.truckdriver.R;

import java.util.List;

public class RecycleViewAdapterOrderHistory extends RecyclerView.Adapter<RecycleViewAdapterOrderHistory.MyViewHolder> {
    private Context myContext;
    private List<Order> orders;


    public RecycleViewAdapterOrderHistory(Context myContext, List<Order> orders) {
        this.myContext = myContext;
        this.orders = orders;
    }

    @NonNull
    @Override
    public RecycleViewAdapterOrderHistory.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater MyInflater = LayoutInflater.from(myContext);
        view = MyInflater.inflate(R.layout.cart_item_layout_orderhistory, parent, false);
        return new RecycleViewAdapterOrderHistory.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecycleViewAdapterOrderHistory.MyViewHolder holder, int position) {
        holder.BillAmount.setText("Bill Amount: " + orders.get(position).getTotalPayment() + "$");
        holder.ItemCount.setText("Total Tons: " + (int) orders.get(position).getTotalTons());
        holder.OrderDate.setText("Order Date: " + orders.get(position).getOrder_Date());
        holder.OrderStatues.setText("Order statues: " + OrderStatues(orders.get(position).getOrderStatues()));
        holder.Destination.setText("Order Destination: " + orders.get(position).getDestination());
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    String OrderStatues(int statues) {
        String orderStatues = "";
        if (statues == 1) {
            orderStatues = "Ready to Delivere";
        } else if (statues == 2) {
            orderStatues = "In Progress";
        } else {
            orderStatues = "Deliverd";
        }
        return orderStatues;

    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView BillAmount;
        TextView ItemCount;
        TextView OrderDate;
        TextView OrderStatues;
        TextView Destination;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            BillAmount = itemView.findViewById(R.id.billamount);
            ItemCount = itemView.findViewById(R.id.itemcount);
            OrderDate = itemView.findViewById(R.id.orderdate);
            OrderStatues = itemView.findViewById(R.id.orderstatues);
            Destination = itemView.findViewById(R.id.deliveryaddress);
        }
    }
}
