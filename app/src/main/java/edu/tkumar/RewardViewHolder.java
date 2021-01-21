package edu.tkumar;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

public class RewardViewHolder extends RecyclerView.ViewHolder {

    TextView rewardDate;
    TextView rewardSender;
    TextView rewardPoints;
    TextView rewardNote;

    public RewardViewHolder(@NonNull @NotNull View itemView) {
        super(itemView);
        rewardDate = itemView.findViewById(R.id.rewardDateTextview);
        rewardSender = itemView.findViewById(R.id.rewardSenderTextview);
        rewardPoints = itemView.findViewById(R.id.rewardPointsTextview);
        rewardNote = itemView.findViewById(R.id.rewardNoteTextview);

    }
}
