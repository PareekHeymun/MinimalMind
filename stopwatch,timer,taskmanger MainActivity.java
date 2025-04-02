package com.example.minimalmindtaskmanager;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText taskInput;
    private Button addTaskButton, resetTaskListButton, startPauseButton, resetButton;
    private LinearLayout taskList;
    private TextView productivityScore, timerDisplay;
    private RadioGroup modeSelector;
    private RadioButton stopwatchButton, timerButton;

    private CountDownTimer countUpTimer, countDownTimer;
    private boolean isTimerRunning = false;
    private long elapsedTime = 0, timerDuration = 0, remainingTime = 0;
    private boolean hasUsedTimer = false, hasUsedStopwatch = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UI elements
        taskInput = findViewById(R.id.taskInput);
        addTaskButton = findViewById(R.id.addTaskButton);
        resetTaskListButton = findViewById(R.id.resetTaskListButton);
        startPauseButton = findViewById(R.id.startPauseButton);
        resetButton = findViewById(R.id.resetButton);
        taskList = findViewById(R.id.taskList);
        productivityScore = findViewById(R.id.productivityScore);
        timerDisplay = findViewById(R.id.timer);
        modeSelector = findViewById(R.id.modeSelector);
        stopwatchButton = findViewById(R.id.stopwatchButton);
        timerButton = findViewById(R.id.timerButton);

        updateTimerDisplay(0);

        // Mode selection logic
        modeSelector.setOnCheckedChangeListener((group, checkedId) -> {
            if (isTimerRunning) pauseTimer();

            if (stopwatchButton.isChecked()) {
                elapsedTime = hasUsedStopwatch ? elapsedTime : 0;
            } else {
                remainingTime = hasUsedTimer ? remainingTime : 0;
            }
            updateTimerDisplay(stopwatchButton.isChecked() ? elapsedTime : remainingTime);
            startPauseButton.setText("Start");
        });

        // Button Listeners
        addTaskButton.setOnClickListener(v -> addTask());
        resetTaskListButton.setOnClickListener(v -> resetTasks());

        startPauseButton.setOnClickListener(v -> {
            if (modeSelector.getCheckedRadioButtonId() == -1) {
                showAlert("Selection Required", "Please select a mode: Stopwatch or Timer.");
                return;
            }
            if (isTimerRunning) {
                pauseTimer();
            } else {
                if (timerButton.isChecked()) {
                    if (remainingTime > 0) startTimer(remainingTime);
                    else promptForTimerDuration();
                } else {
                    startStopwatch();
                }
            }
        });

        resetButton.setOnClickListener(v -> {
            if (timerButton.isChecked()) resetTimer();
            else resetStopwatch();
        });
    }

    private void addTask() {
        String taskText = taskInput.getText().toString().trim();
        if (!TextUtils.isEmpty(taskText)) {
            CheckBox checkBox = new CheckBox(this);
            checkBox.setText(taskText);
            checkBox.setTextColor(getResources().getColor(android.R.color.white));
            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> updateProductivityScore());
            taskList.addView(checkBox);
            taskInput.setText("");
            updateProductivityScore();
        }
    }

    private void resetTasks() {
        taskList.removeAllViews();
        updateProductivityScore();
    }

    private void updateProductivityScore() {
        int totalTasks = taskList.getChildCount();
        int completedTasks = 0;
        for (int i = 0; i < totalTasks; i++) {
            if (((CheckBox) taskList.getChildAt(i)).isChecked()) completedTasks++;
        }
        productivityScore.setText("Productivity Score: " + completedTasks + "/" + totalTasks);
    }

    private void startStopwatch() {
        isTimerRunning = true;
        hasUsedStopwatch = true;
        startPauseButton.setText("Stop");

        countUpTimer = new CountDownTimer(Long.MAX_VALUE, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                elapsedTime++;
                updateTimerDisplay(elapsedTime);
            }
            @Override
            public void onFinish() { }
        }.start();
    }

    private void startTimer(long durationInSeconds) {
        timerDuration = durationInSeconds;
        remainingTime = timerDuration;
        isTimerRunning = true;
        hasUsedTimer = true;
        startPauseButton.setText("Stop");

        countDownTimer = new CountDownTimer(remainingTime * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                remainingTime = millisUntilFinished / 1000;
                updateTimerDisplay(remainingTime);
            }
            @Override
            public void onFinish() {
                isTimerRunning = false;
                startPauseButton.setText("Start");
                updateTimerDisplay(0);
                hasUsedTimer = false;
            }
        }.start();
    }

    private void pauseTimer() {
        if (countUpTimer != null) countUpTimer.cancel();
        if (countDownTimer != null) countDownTimer.cancel();
        isTimerRunning = false;
        startPauseButton.setText("Start");
    }

    private void resetTimer() {
        remainingTime = 0;
        pauseTimer();
        updateTimerDisplay(0);
        hasUsedTimer = false;
    }

    private void resetStopwatch() {
        elapsedTime = 0;
        pauseTimer();
        updateTimerDisplay(0);
        hasUsedStopwatch = false;
    }

    private void updateTimerDisplay(long timeInSeconds) {
        int hours = (int) (timeInSeconds / 3600);
        int minutes = (int) ((timeInSeconds % 3600) / 60);
        int seconds = (int) (timeInSeconds % 60);
        timerDisplay.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));
    }

    private void promptForTimerDuration() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Set Timer Duration");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText hoursInput = createInputField("Hours (00)");
        final EditText minutesInput = createInputField("Minutes (00)");
        final EditText secondsInput = createInputField("Seconds (00)");

        layout.addView(hoursInput);
        layout.addView(minutesInput);
        layout.addView(secondsInput);
        builder.setView(layout);

        builder.setPositiveButton("Start", (dialog, which) -> {
            int hours = parseInput(hoursInput);
            int minutes = parseInput(minutesInput);
            int seconds = parseInput(secondsInput);

            long totalDuration = (hours * 3600) + (minutes * 60) + seconds;
            if (totalDuration > 0) {
                startTimer(totalDuration);
            } else {
                showAlert("Invalid Input", "Please enter a duration greater than 0.");
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private EditText createInputField(String hint) {
        EditText input = new EditText(this);
        input.setHint(hint);
        input.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        input.setFilters(new android.text.InputFilter[]{new android.text.InputFilter.LengthFilter(2)});
        return input;
    }

    private int parseInput(EditText input) {
        return TextUtils.isEmpty(input.getText()) ? 0 : Integer.parseInt(input.getText().toString());
    }

    private void showAlert(String title, String message) {
        new AlertDialog.Builder(this).setTitle(title).setMessage(message).setPositiveButton("OK", null).show();
    }
}
