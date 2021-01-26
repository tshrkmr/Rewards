package edu.tkumar;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class EmployeeViewHolder extends RecyclerView.ViewHolder {

    TextView employeeName;
    TextView employeeTitle;
    TextView employeePoints;
    ImageView employeeImage;
    ImageView listSeparator;

    public EmployeeViewHolder(@NonNull @org.jetbrains.annotations.NotNull View itemView) {
        super(itemView);
        employeeName = itemView.findViewById(R.id.employeeNameTextview);
        employeeTitle = itemView.findViewById(R.id.employeeTitleTextview);
        employeePoints = itemView.findViewById(R.id.employeePointsTextview);
        employeeImage = itemView.findViewById(R.id.employeeImageview);
        listSeparator = itemView.findViewById(R.id.listSeperatorImageview);

    }
}
