package me.mamun.mplayer;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.audiofx.BassBoost;
import android.media.audiofx.Equalizer;
import android.media.audiofx.Virtualizer;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import java.util.ArrayList;

import me.mamun.Utils.AppsHelper;
import me.mamun.Utils.RoundKnobButton;
import me.mamun.Utils.RoundKnobButton.RoundKnobButtonListener;
import me.mamun.Utils.Singleton;

public class EquilizerActivity extends Activity implements OnSeekBarChangeListener {

    String TAG = "Equilizer";
    private SeekBar mSeekBarVolume;
    private AudioManager audioManager;
    private int maxVolume;
    private int curVolume;
    private TextView tvBassValue, tvVirtualValue;
    private ImageButton backToHome;

    //Wheel button
    Singleton m_Inst = Singleton.getInstance();
    RelativeLayout wheelBtn1Spc1, wheelBtn1Spc2;

    //Equilizer
    Equalizer mEquilizer;
    MediaPlayer mediaPlayer;
    TextView tvTotalBands;
    static final int MAX_SLIDERS = 5;
    SeekBar seekBars[] = new SeekBar[MAX_SLIDERS];
    short numberFrequencyBands;
    BassBoost bassBoost = null;
    Virtualizer mVirtualizer = null;
    Spinner spEqPresets;
    ArrayList<String> equilizerpresetNames = new ArrayList<String>();

    short min_level = 0;
    short max_level = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_equilizer);
        mediaPlayer = AppsHelper.mPlayer;
        initializeMainVolume();
        initializeEquilizer();
        initializeBassAndVirtual();
        populateBandListInSpinner();
        backToHome = (ImageButton) findViewById(R.id.equize_btn_back);
        backToHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initializeMainVolume() {
        mSeekBarVolume = (SeekBar) findViewById(R.id.vol_bar);
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        curVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        mSeekBarVolume.setMax(maxVolume);
        mSeekBarVolume.setProgress(curVolume);
        mSeekBarVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar arg0) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar arg0) {

            }

            @Override
            public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, arg1, 0);
            }
        });
    }

    private void initializeEquilizer() {
        seekBars[0] = (SeekBar) findViewById(R.id.bar1);
        seekBars[1] = (SeekBar) findViewById(R.id.bar2);
        seekBars[2] = (SeekBar) findViewById(R.id.bar3);
        seekBars[3] = (SeekBar) findViewById(R.id.bar4);
        seekBars[4] = (SeekBar) findViewById(R.id.bar5);
       // mediaPlayer = MediaPlayer.create(this, R.raw.sample);
       // mediaPlayer.start();
        mEquilizer = new Equalizer(0, mediaPlayer.getAudioSessionId());
        mEquilizer.setEnabled(true);
        numberFrequencyBands = mEquilizer.getNumberOfBands();
//        tvTotalBands.setText(" " + numberFrequencyBands);
        min_level = mEquilizer.getBandLevelRange()[0];
        max_level = mEquilizer.getBandLevelRange()[1];

        // Toast.makeText(MainActivity.this, "Total bands : " + numberFrequencyBands, Toast.LENGTH_SHORT).show();
        if (mEquilizer != null) {
            for (short i = 0; i < numberFrequencyBands; i++) {
                //   Log.d("Bands", ">>> " + mEquilizer.getPresetName(i));
                equilizerpresetNames.add(mEquilizer.getPresetName(i));
                // Log.d("Max", ">>> " + max_level);
                // Log.d("Min", ">>> " + min_level);
                seekBars[i].setMax(max_level - min_level);
                seekBars[i].setProgress(mEquilizer.getBandLevel(i) - min_level);
                //Log.d("Band level", ">>> " + mEquilizer.getBandLevel(i));
                seekBars[i].setOnSeekBarChangeListener(this);
            }
        }

    }

    private void initializeBassAndVirtual() {
        wheelBtn1Spc1 = (RelativeLayout) findViewById(R.id.equize_rv_bass);
        wheelBtn1Spc2 = (RelativeLayout) findViewById(R.id.equize_rv_virtual);
        tvBassValue = (TextView) findViewById(R.id.tv_bass_value);
        tvVirtualValue = (TextView) findViewById(R.id.tv_virtualizer_value);

        m_Inst.InitGUIFrame(this);
        bassBoost = new BassBoost(0, mediaPlayer.getAudioSessionId());
        mVirtualizer = new Virtualizer(0, mediaPlayer.getAudioSessionId());
        RoundKnobButton rv = new RoundKnobButton(this, R.drawable.stator, R.drawable.rotoron, R.drawable.rotoroff,
                m_Inst.Scale(250), m_Inst.Scale(250));

        wheelBtn1Spc1.addView(rv);
        if (bassBoost != null) {
            rv.setRotorPercentage((short) bassBoost.getRoundedStrength());
        } else {
            rv.setRotorPercentage((short) 0);
        }
        rv.SetListener(new RoundKnobButtonListener() {
            public void onRotate(final int percentage) {
                tvBassValue.post(new Runnable() {
                    public void run() {
                        tvBassValue.setText("" + percentage);
                    }
                });
                try {
                    if (bassBoost != null && bassBoost.getStrengthSupported()) {
                        bassBoost.setEnabled(true);
                        bassBoost.setStrength((short) percentage);
                        Log.d("Bassboost", ">>> " + percentage);
                    }
                } catch (IllegalArgumentException e) {
                    Log.e(TAG, "Bassboost effect not supported");
                } catch (IllegalStateException e) {
                    Log.e(TAG, "Bassboost cannot get strength supported");
                } catch (UnsupportedOperationException e) {
                    Log.e(TAG, "Bassboost library not loaded");
                } catch (RuntimeException e) {
                    Log.e(TAG, "Bassboost effect not found");
                }
                // mBassBoostInstances.remove(audioSession);
                //  Toast.makeText(MainActivity.this, "Button 1 pos : " + percentage, Toast.LENGTH_SHORT).show();
            }
        });

        //   Another button
        RoundKnobButton rv2 = new RoundKnobButton(this, R.drawable.stator, R.drawable.rotoron, R.drawable.rotoroff,
                m_Inst.Scale(250), m_Inst.Scale(250));
        //  RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        //  lp.addRule(RelativeLayout.CENTER_IN_PARENT);
        wheelBtn1Spc2.addView(rv2);
        rv2.setRotorPercentage(10);
        rv2.SetListener(new RoundKnobButtonListener() {
         /*   public void onStateChange(boolean newstate) {
                //   Toast.makeText(MainActivity.this, "New state:" + newstate, Toast.LENGTH_SHORT).show();
            }*/

            public void onRotate(final int percentage) {
                tvVirtualValue.post(new Runnable() {
                    public void run() {
                        tvVirtualValue.setText("" + percentage);
                    }
                });

                try {
                    if (mVirtualizer != null && mVirtualizer.getStrengthSupported()) {
                        mVirtualizer.setEnabled(true);
                        mVirtualizer.setStrength((short) percentage);
                        Log.d("Virtualizer", ">>> " + percentage);
                    }
                } catch (IllegalArgumentException e) {
                    Log.e(TAG, "Virtualizer effect not supported");
                } catch (IllegalStateException e) {
                    Log.e(TAG, "Virtualizer cannot get strength supported");
                } catch (UnsupportedOperationException e) {
                    Log.e(TAG, "Virtualizer library not loaded");
                } catch (RuntimeException e) {
                    Log.e(TAG, "Virtualizer effect not found");
                }

                //   Toast.makeText(MainActivity.this, "Button 2 pos : " + percentage, Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void populateBandListInSpinner() {
        spEqPresets = (Spinner) findViewById(R.id.equize_sp_effect);
        ArrayAdapter<String> eqPresetAdapter = new ArrayAdapter<String>(this, R.layout.equilizer_preset_list, equilizerpresetNames);
        spEqPresets.setAdapter(eqPresetAdapter);
        spEqPresets.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mEquilizer.usePreset((short) position);
                short numFreqBand = mEquilizer.getNumberOfBands();
                min_level = mEquilizer.getBandLevelRange()[0];
                max_level = mEquilizer.getBandLevelRange()[1];
                for (short i = 0; i < numFreqBand; i++) {

                    int pos = (max_level - min_level);
                    seekBars[i].setMax(pos);
                    seekBars[i].setProgress(mEquilizer.getBandLevel(i) - min_level);
                    // Log.d("gggg", ""+ (mEquilizer.getBandLevel(i) - min_level));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        // int new_level = min_level + (max_level - min_level) * progress / 100;

        for (int i = 0; i < numberFrequencyBands; i++) {
            if (seekBars[i] == seekBar) {
                mEquilizer.setBandLevel((short) i, (short) (progress + min_level));
                break;
            }
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
