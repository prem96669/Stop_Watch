package com.example.stop_watch;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    //Properties
    private Handler timerHandler;
    private ArrayAdapter<String> itemsAdapter;
    private TextView textTimer;
    private Button btnStartPause, btnLapreset;

    // user to keep track of time
    private long millisecondTime, startTime, pausedTime, updateTime = 0;

    //used to display time
    private  int seconds, minutes, milliSeconds;

    //used to handle the state of the stopwatch
    private boolean stopWatchStarted, stopWatchPaused =  false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Used in this function only, So it don't need to be global and List view is used to display the lap times
        ListView lvLaps;

        //Timehandler is bound to a theread
        // used to schedule our run during particular actions
        timerHandler = new Handler();
        // sets the  layout for each item of the list view
        itemsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        textTimer =  findViewById(R.id.txt_time);
        btnLapreset = findViewById(R.id.btn_lap_reset);
        btnStartPause = findViewById(R.id.btn_start_pause);
        lvLaps = findViewById(R.id.lv_laps);
        // Binds data from adapter to List view
        lvLaps.setAdapter(itemsAdapter);
        btnStartPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!stopWatchStarted || stopWatchPaused){
                    stopWatchStarted = true;
                    stopWatchPaused = false;

                    startTime = SystemClock.uptimeMillis();

                    timerHandler.postDelayed(timerRunnable,0);

                    btnStartPause.setText(R.string.lblPause);
                    btnLapreset.setText(R.string.lblbtnlap);
                } else {
                    pausedTime += millisecondTime;
                    stopWatchPaused = true;
                    timerHandler.removeCallbacks(timerRunnable);
                    btnStartPause.setText(R.string.lblStart);
                    btnLapreset.setText(R.string.ldlReset);
                }
            }
        });
        btnLapreset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (stopWatchStarted && !stopWatchPaused){
                    String lapTime = minutes + ":"
                            + String.format("%02d", seconds) + ":"
                            + String.format("%03d", milliSeconds);
                    itemsAdapter.add(lapTime);
                } else if (stopWatchStarted){
                    stopWatchStarted = false;
                    stopWatchPaused = false;

                    timerHandler.removeCallbacks(timerRunnable);

                    //reset all values
                    millisecondTime = 0;
                    startTime = 0;
                    pausedTime = 0;
                    updateTime = 0;
                    seconds = 0;
                    minutes = 0;
                    milliSeconds = 0;

                    //switching label strings
                    textTimer.setText(R.string.ldlTimer);
                    btnLapreset.setText(R.string.lblbtnlap);

                    //wipe resources
                    itemsAdapter.clear();
                } else {
                    Toast.makeText(getApplicationContext(), "Timer hasn't started yet!", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }
    public  Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            millisecondTime = SystemClock.uptimeMillis() - startTime;

            // Values used to keep track of where the stopwatch time left off
            updateTime =  pausedTime + millisecondTime;
            milliSeconds = (int) (updateTime % 1000);
            seconds = (int) (updateTime / 1000);

            //Converting values to display in particular format
            minutes = seconds / 60;
            seconds = seconds % 60;
            String updatedTime = minutes + ":"
                    + String.format("%02d",seconds) + ":"
                    + String.format("%03d", milliSeconds);

            textTimer.setText(updatedTime);

            // enqueues the Runnable to be called by the message queue after the specified amount of time elapses.
            timerHandler.postDelayed(this, 0);

        }
    };
}
