package com.example.lawre.week7day2part2;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private JobScheduler jobScheduler;
    private Switch deviceIdleSwitch, deviceChargingSwitch;
    private SeekBar seekBar;
    public static final int JOB_ID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        deviceChargingSwitch = findViewById(R.id.chargingSwitch);
        deviceIdleSwitch = findViewById(R.id.idleSwitch);
        seekBar = findViewById(R.id.seekBar);
        final TextView seekBarProgress = findViewById(R.id.tvSeekBarProgress);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(progress > 0)
                {
                    seekBarProgress.setText(progress + "s");
                }
                else
                {
                    seekBarProgress.setText("Not Set");
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }


    public void scheduleJob(View view)
    {
        int seekBarInt = seekBar.getProgress();
        boolean seekBarSet = seekBarInt > 0;
        jobScheduler = (JobScheduler)getSystemService(JOB_SCHEDULER_SERVICE);
        RadioGroup networkOptions = findViewById(R.id.networkOptions);
        int selectedNetworkId = networkOptions.getCheckedRadioButtonId();
        int selectedNetworkOption = JobInfo.NETWORK_TYPE_NONE;
        switch(selectedNetworkId)
        {
            case R.id.noNetwork:
                selectedNetworkOption = JobInfo.NETWORK_TYPE_NONE;
                break;
            case R.id.anyNetwork:
                selectedNetworkOption = JobInfo.NETWORK_TYPE_ANY;
                break;
            case R.id.wifiNetwork:
                selectedNetworkOption = JobInfo.NETWORK_TYPE_UNMETERED;
                break;
        }
        ComponentName serviceName = new ComponentName(getPackageName(),NotificationJobService.class.getName());
        JobInfo.Builder builder = new JobInfo.Builder(JOB_ID, serviceName);
        builder.setRequiredNetworkType(selectedNetworkOption);
        builder.setRequiresDeviceIdle(deviceIdleSwitch.isChecked());
        builder.setRequiresCharging(deviceChargingSwitch.isChecked());
        if(seekBarSet)
        {
            builder.setOverrideDeadline(seekBarInt * 1000);
        }
        boolean constraintSet = (selectedNetworkOption != JobInfo.NETWORK_TYPE_NONE) || deviceIdleSwitch.isChecked() || deviceChargingSwitch.isChecked() || seekBarSet;
        if(constraintSet) {
            JobInfo jobInfo = builder.build();
            jobScheduler.schedule(jobInfo);
            Toast.makeText(this, "Job Scheduled. Job will run when " + "constraints are met.", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(this,"Please set at least one constraint",Toast.LENGTH_SHORT).show();
        }
    }

    public void cancelJobs(View view)
    {
        if(jobScheduler != null)
        {
            jobScheduler.cancelAll();
            jobScheduler = null;
            Toast.makeText(this,"All jobs cancelled.",Toast.LENGTH_SHORT).show();
        }
    }
}
