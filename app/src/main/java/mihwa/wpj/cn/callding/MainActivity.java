package mihwa.wpj.cn.callding;

import android.annotation.SuppressLint;
import android.app.KeyguardManager;
import android.content.Intent;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    private TimePicker tp;
    private TextView tv_btn;
    private Timer timer;
    private View view;
    private RadioGroup rg_day;
    private boolean isWorking;
    private int hour, minuteOfHour, day;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        isWorking = false;
        hour = 8;
        minuteOfHour = 10;
        day = 1;

        tp = findViewById(R.id.tp);
        tp.setDescendantFocusability(TimePicker.FOCUS_BLOCK_DESCENDANTS);  //设置点击事件不弹键盘
        tp.setIs24HourView(true);   //设置时间显示为24小时
        tp.setHour(hour);  //设置当前小时
        tp.setMinute(minuteOfHour); //设置当前分（0-59）
        tp.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {  //获取当前选择的时间
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                Log.i(MainActivity.class.getName(), "onTimeChanged," + hourOfDay + ":" + minute);
                hour = hourOfDay;
                minuteOfHour = minute;
            }
        });

        rg_day = (RadioGroup) findViewById(R.id.rg_day);
        rg_day.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkId) {
                switch (checkId) {
                    case R.id.rb_0:
                        day = 0;
                        break;
                    case R.id.rb_1:
                        day = 1;
                        break;
                    case R.id.rb_2:
                        day = 2;
                        break;
                    case R.id.rb_3:
                        day = 3;
                        break;
                    default:
                        day = 1;
                        break;
                }
            }
        });

        view = findViewById(R.id.view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "点击取消按钮，取消当前任务", Toast.LENGTH_SHORT).show();
            }
        });

        tv_btn = findViewById(R.id.tv_btn);
        tv_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isWorking) {
                    isWorking = false;
                    view.setVisibility(View.GONE);
                    tv_btn.setText("确定");
                    if (timer != null)
                        timer.cancel();
                } else {
                    isWorking = true;
                    view.setVisibility(View.VISIBLE);
                    Log.i(MainActivity.class.getName(), "schedule," + hour + ":" + minuteOfHour);
                    tv_btn.setText("取消");
                    start();
                }
            }
        });
    }

    private void start() {
        Calendar calendarNow = Calendar.getInstance();
        Log.i(MainActivity.class.getName(), "nowTime:" + calendarNow);

        Calendar calendar = new GregorianCalendar();
        calendar.setTime(new Date());
        calendar.add(calendar.DAY_OF_MONTH, day);
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), hour, minuteOfHour);
        Log.i(MainActivity.class.getName(), "executeTime:" + calendar);

        long delay = calendar.getTimeInMillis() - calendarNow.getTimeInMillis();
        Log.i(MainActivity.class.getName(), "delay:" + delay + "|hour:" + delay / (1000 * 60 * 60) + "|minute:" + (delay / (1000 * 60)) % 60);
        Toast.makeText(MainActivity.this, "将在" + delay / (1000 * 60 * 60) + "小时" + (delay / (1000 * 60)) % 60 + "分后执行", Toast.LENGTH_SHORT).show();

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                go();
            }
        }, delay);
    }

    private void go() {
        Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = Calendar.MINUTE;
        Log.i(MainActivity.class.getName(), "time," + hour + ":" + minute);
//        if (hour == 10 && minute >= 0 && minute <= 30) {
        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
        @SuppressLint("InvalidWakeLockTag")
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.FULL_WAKE_LOCK, "bright");
        wl.acquire();
        wl.release();

        KeyguardManager.KeyguardLock mUnLock;
        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        assert keyguardManager != null;
        mUnLock = keyguardManager.newKeyguardLock("unLock");
        mUnLock.disableKeyguard();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.i(MainActivity.class.getName(), "CallDing");
                Intent intent1 = getPackageManager().getLaunchIntentForPackage("com.alibaba.android.rimet");
                intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent1);
            }
        });
    }
//    }
}