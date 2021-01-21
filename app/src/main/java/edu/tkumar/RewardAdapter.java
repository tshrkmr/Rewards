package edu.tkumar;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class RewardAdapter extends RecyclerView.Adapter<RewardViewHolder> {

    private final List<Reward> rewardList;
    private final ProfileActivity profileActivity;

    public RewardAdapter(List<Reward> rewardList, ProfileActivity profileActivity) {
        this.rewardList = rewardList;
        this.profileActivity = profileActivity;
    }

    @NonNull
    @NotNull
    @Override
    public RewardViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.reward_list_entry, parent, false);
        itemView.setOnClickListener(profileActivity);
        return new RewardViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RewardViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
