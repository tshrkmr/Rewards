package edu.tkumar;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Locale;

public class EmployeeAdapter extends RecyclerView.Adapter<EmployeeViewHolder> {

    private final List<Employee> employeeList;
    private final LeaderboardActivity leaderboardActivity;

    public EmployeeAdapter(List<Employee> employeeList, LeaderboardActivity leaderboardActivity) {
        this.employeeList = employeeList;
        this.leaderboardActivity = leaderboardActivity;
    }

    @NonNull
    @NotNull
    @Override
    public EmployeeViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.employee_list_entry, parent, false);
        itemView.setOnClickListener(leaderboardActivity);
        return new EmployeeViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull EmployeeViewHolder holder, int position) {
        Employee employee = employeeList.get(position);
        holder.employeeName.setText(String.format("%s, %s", employee.getLastName(), employee.getFirstName()));
        holder.employeeTitle.setText(String.format("%s, %s", employee.getPosition(), employee.getDepartment()));
        holder.employeePoints.setText(String.format("%d", employee.getPointsAwarded()));

        byte[] imageBytes = Base64.decode(employee.getImageBytes(), Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        holder.employeeImage.setImageBitmap(bitmap);
    }

    @Override
    public int getItemCount() {
        return employeeList.size();
    }
}
