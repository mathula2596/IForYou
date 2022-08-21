package com.ifu.iforyou;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    ArrayList<Timetable> dataHolder;
    private RecyclerViewClickListener listener;
    private FragmentTransaction fragmentTransaction;
    private EditTimetableFragment editTimetableFragment;
    private Bundle bundle;

    public RecyclerViewAdapter(ArrayList<Timetable> dataHolder) {
        this.dataHolder = dataHolder;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.view_list_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.date.setText(dataHolder.get(position).getDate());
        holder.startTime.setText(dataHolder.get(position).getStartTime());
        holder.endTime.setText(dataHolder.get(position).getEndTime());
        holder.lecturerId.setText(dataHolder.get(position).getLecturerId());
        holder.type.setText(dataHolder.get(position).getType());
        holder.location.setText(dataHolder.get(position).getLocation());

        bundle = new Bundle();


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bundle.putString("id", dataHolder.get(position).getId());
                editTimetableFragment = new EditTimetableFragment();
                editTimetableFragment.setArguments(bundle);
                AppCompatActivity activity = (AppCompatActivity)view.getContext();
                fragmentTransaction = activity.getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout,editTimetableFragment).addToBackStack("tag");
                fragmentTransaction.commit();
            }
        });
    }


    @Override
    public int getItemCount() {
        return dataHolder.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements  View.OnClickListener
    {
        TextView date, startTime, endTime, lecturerId, type, location, id;
        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);
            date=itemView.findViewById(R.id.date);
            startTime=itemView.findViewById(R.id.startTime);
            endTime=itemView.findViewById(R.id.endTime);
            lecturerId=itemView.findViewById(R.id.lecturerId);
            type=itemView.findViewById(R.id.type);
            location=itemView.findViewById(R.id.location);
        }

        @Override
        public void onClick(View view) {
            listener.onCLick(view,getAdapterPosition());
        }
    }


    public interface RecyclerViewClickListener{
        void onCLick(View v, int position);
    }

}
