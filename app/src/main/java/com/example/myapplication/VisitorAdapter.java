package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;
import java.util.function.BiConsumer;

public class VisitorAdapter extends RecyclerView.Adapter<VisitorAdapter.ViewHolder> {
    private List<Visitor> visitors;
    private BiConsumer<Integer, String> onApprovalClick;

    public VisitorAdapter(List<Visitor> visitors, BiConsumer<Integer, String> onApprovalClick) {
        this.visitors = visitors;
        this.onApprovalClick = onApprovalClick;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.visitor_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Visitor visitor = visitors.get(position);
        holder.bind(visitor);
    }

    @Override
    public int getItemCount() {
        return visitors.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView visitorPhoto;
        TextView visitorName, visitorPhone, visitorEmail, visitorIdProof;
        Button approveButton, rejectButton;

        ViewHolder(View itemView) {
            super(itemView);
            visitorPhoto = itemView.findViewById(R.id.visitorPhoto);
            visitorName = itemView.findViewById(R.id.visitorName);
            visitorPhone = itemView.findViewById(R.id.visitorPhone);
            visitorEmail = itemView.findViewById(R.id.visitorEmail);
            visitorIdProof = itemView.findViewById(R.id.visitorIdProof);
            approveButton = itemView.findViewById(R.id.approveButton);
            rejectButton = itemView.findViewById(R.id.rejectButton);
        }

        void bind(Visitor visitor) {
            visitorName.setText(visitor.getName());
            visitorPhone.setText(visitor.getPhone());
            visitorEmail.setText(visitor.getEmail());
            visitorIdProof.setText(visitor.getIdProof());

            if (visitor.getPhotoPath() != null && !visitor.getPhotoPath().isEmpty()) {
                String baseUrl = "https://vulture-on-treefrog.ngrok-free.app/";
                String fullPhotoUrl = baseUrl + visitor.getPhotoPath().replace("\\", "/");
                Glide.with(itemView.getContext())
                        .load(fullPhotoUrl)
                        .into(visitorPhoto);
            }

            approveButton.setOnClickListener(v ->
                    onApprovalClick.accept(visitor.getId(), "approved"));
            rejectButton.setOnClickListener(v ->
                    onApprovalClick.accept(visitor.getId(), "rejected"));
        }
    }
}