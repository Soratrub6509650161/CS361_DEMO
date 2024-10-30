package com.example.cs361v2;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Random;

public class HomeFragment extends Fragment {

    CalendarView calendarView;
    Calendar calendar;
    LinearLayout historyList; // Used to display transaction history
    TextView noTransactionsText; // TextView for "No Transactions" message

    private final String[] incomeCategories = {"Salary", "Other Income", "Transfer In", "Interest"};
    private final String[] outcomeCategories = {"Food & Beverages", "Bills & Utilities", "Shopping",
            "Household Items","Family","Travel","Health & Fitness","Education","Entertainment"
            ,"Giving & Donations","Insurance","Other Expenses"};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        calendarView = view.findViewById(R.id.calendarView);
        calendar = Calendar.getInstance();

        // Show the selected date
        getDate();

        // Set LinearLayout for transaction history
        historyList = view.findViewById(R.id.history_list); // Ensure this exists in XML

        // Set TextView for "No Transactions" message
        noTransactionsText = view.findViewById(R.id.no_transactions_message);
        noTransactionsText.setVisibility(View.VISIBLE); // Show message by default

        // Set date selection in CalendarView
        calendarView.setOnDateChangeListener((calendarView, year, month, day) -> showAddTransactionDialog(year, month + 1, day));

        // Set click listener for add transaction button
        ImageButton addTransactionButton = view.findViewById(R.id.addTransactionButton);
        addTransactionButton.setOnClickListener(v -> {
            // Use the selected date from CalendarView
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            showAddTransactionDialog(year, month + 1, day); // Add 1 to month
        });

        return view; // Return view
    }

    public void getDate() {
        long date = calendarView.getDate();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yy", Locale.getDefault());
        calendar.setTimeInMillis(date);
        String selected_date = simpleDateFormat.format(calendar.getTime());
        Toast.makeText(getActivity(), selected_date, Toast.LENGTH_SHORT).show();
    }

    // Function to show dialog for adding transaction
    @SuppressLint("SetTextI18n")
    private void showAddTransactionDialog(int year, int month, int day) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Add Transaction");

        // Create Layout for Dialog
        LinearLayout layout = new LinearLayout(getActivity());
        layout.setOrientation(LinearLayout.VERTICAL);

        // Income Checkbox
        CheckBox incomeCheckbox = new CheckBox(getActivity());
        incomeCheckbox.setText("Income");
        layout.addView(incomeCheckbox);

        // Outcome Checkbox
        CheckBox outcomeCheckbox = new CheckBox(getActivity());
        outcomeCheckbox.setText("Outcome");
        layout.addView(outcomeCheckbox);

        // Spinner for categories
        final Spinner categorySpinner = new Spinner(getActivity());
        layout.addView(categorySpinner);

        // Update the category spinner based on the selected checkboxes
        incomeCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                outcomeCheckbox.setChecked(false); // Uncheck outcome if income is selected
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, incomeCategories);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                categorySpinner.setAdapter(adapter);
            }
        });

        outcomeCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                incomeCheckbox.setChecked(false); // Uncheck income if outcome is selected
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, outcomeCategories);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                categorySpinner.setAdapter(adapter);
            }
        });

        // Add EditText for amount input
        final EditText amountInput = new EditText(getActivity());
        amountInput.setHint("Enter Amount");
        layout.addView(amountInput);

        builder.setView(layout);

        // Dialog buttons
        builder.setPositiveButton("Add", (dialog, which) -> {
            String billType = "";
            if (incomeCheckbox.isChecked()) {
                billType = "Income";
            } else if (outcomeCheckbox.isChecked()) {
                billType = "Outcome";
            }

            String amount = amountInput.getText().toString();
            String category = categorySpinner.getSelectedItem() != null ? categorySpinner.getSelectedItem().toString() : ""; // Get selected category

            if (!amount.isEmpty() && !billType.isEmpty()) {
                addTransaction(year, month, day, billType, amount, category); // Pass category to the method
            } else {
                Toast.makeText(getActivity(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    @SuppressLint("SetTextI18n")
    private void addTransaction(int year, int month, int day, String billType, String amount, String category) {
        // Create a LinearLayout to hold the transaction details
        LinearLayout transactionContainer = new LinearLayout(getActivity());
        transactionContainer.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        transactionContainer.setOrientation(LinearLayout.VERTICAL);
        transactionContainer.setPadding(16, 16, 16, 16);

        // Generate random color for the border
        int[] colors = {0xFFE57373, 0xFF81C784, 0xFF64B5F6, 0xFFFFB74D, 0xFFBA68C8};
        int randomColor = colors[new Random().nextInt(colors.length)];

        // Set the background with border
        transactionContainer.setBackground(createBorderDrawable(randomColor)); // Create border drawable

        // Create a TextView to display the transaction details
        TextView transactionView = new TextView(getActivity());

        // Format date and time
        String date = day + "/" + month + "/" + year;
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String time = timeFormat.format(Calendar.getInstance().getTime());

        // Set the transaction text with date, time, type, category, and amount
        transactionView.setText(date + " " + time + ": " + billType + " - " + category + " - " + amount + " Baht");

        // Add the transaction view to the container
        transactionContainer.addView(transactionView);

        // Set margin between transaction items
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) transactionContainer.getLayoutParams();
        params.setMargins(0, 16, 0, 0); // Set top margin to 16dp
        transactionContainer.setLayoutParams(params);

        // Add the container to the history list
        historyList.addView(transactionContainer);

        // Hide the "no transactions" message if there are transactions
        noTransactionsText.setVisibility(View.GONE);
    }

    // Method to create a border drawable
    private GradientDrawable createBorderDrawable(int color) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(Color.WHITE); // Background color
        drawable.setStroke(4, color); // Border width and color
        return drawable;
    }
}
