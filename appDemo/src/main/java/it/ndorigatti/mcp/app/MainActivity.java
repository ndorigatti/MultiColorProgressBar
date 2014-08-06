/*******************************************************************************
 * Copyright (C) 2014 Nicola Dorigatti
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 ******************************************************************************/

package it.ndorigatti.mcp.app;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

import com.google.samples.apps.iosched.ui.widget.BezelImageView;

import java.util.Random;

import it.gmariotti.android.example.colorpicker.dashclockpicker.ColorPickerDialogDash;
import it.ndorigatti.android.view.MulticolorProgressBar;

public class MainActivity extends Activity {
    private MulticolorProgressBar multicolorProgressBar;
    private BezelImageView primaryColorPreview,secondaryColorPreview;
    private int primColor, secondColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        multicolorProgressBar = (MulticolorProgressBar) findViewById(R.id.bicolorProgressBar);
        primColor = getResources().getColor(R.color.initial_progress_color);
        secondColor = getResources().getColor(R.color.initial_secondaryprogress_color);

        if (null != savedInstanceState) {
            primColor = savedInstanceState.getInt("mcpb_primary_color", primColor);
            secondColor = savedInstanceState.getInt("mcpb_secondary_color", secondColor);
        }

        primaryColorPreview = (BezelImageView) findViewById(R.id.primaryProgressColorPreview);
        secondaryColorPreview = (BezelImageView) findViewById(R.id.secondaryProgressColorPreview);
        primaryColorPreview.setImageDrawable(new ColorDrawable(primColor));
        secondaryColorPreview.setImageDrawable(new ColorDrawable(secondColor));

        SeekBar seekBarPrimary = (SeekBar) findViewById(R.id.seekBarPrimary);
        SeekBar seekBarSecondary = (SeekBar) findViewById(R.id.seekBarSecondary);
        final int[] mColor = colorChoice(this);
        primaryColorPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorPickerDialogDash colordashfragment = ColorPickerDialogDash.newInstance(R.string.color_picker_default_title,
                        mColor, primColor, 5);

                //Implement listener to get color value
                colordashfragment.setOnColorSelectedListener(new ColorPickerDialogDash.OnColorSelectedListener() {

                    @Override
                    public void onColorSelected(int color) {
                        primColor = color;
                        multicolorProgressBar.setProgressColor(primColor);
                        primaryColorPreview.setImageDrawable(new ColorDrawable(primColor));
                    }

                });

                colordashfragment.show(getFragmentManager(), "dash");
            }
        });
        secondaryColorPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorPickerDialogDash colordashfragment = ColorPickerDialogDash.newInstance(R.string.color_picker_default_title,
                        mColor, secondColor, 5);

                //Implement listener to get color value
                colordashfragment.setOnColorSelectedListener(new ColorPickerDialogDash.OnColorSelectedListener() {

                    @Override
                    public void onColorSelected(int color) {
                        secondColor = color;
                        multicolorProgressBar.setSecondaryProgressColor(secondColor);
                        secondaryColorPreview.setImageDrawable(new ColorDrawable(secondColor));
                    }

                });

                colordashfragment.show(getFragmentManager(), "dash");
            }
        });

        seekBarPrimary.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    multicolorProgressBar.setProgress(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        seekBarSecondary.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    multicolorProgressBar.setSecondaryProgress(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        final Button randomColors = (Button) findViewById(R.id.changecolorsbtn);
        randomColors.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Choose two colors that are not the same...
                Random rnd = new Random();
                primColor = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
                while (primColor == Color.WHITE || primColor == Color.TRANSPARENT) {
                    primColor = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
                }
                secondColor = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
                while (secondColor == Color.WHITE || secondColor == Color.TRANSPARENT || secondColor == primColor) {
                    secondColor = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
                }

                multicolorProgressBar.setProgressColor(primColor);
                multicolorProgressBar.setSecondaryProgressColor(secondColor);
                primaryColorPreview.setImageDrawable(new ColorDrawable(primColor));
                secondaryColorPreview.setImageDrawable(new ColorDrawable(secondColor));
            }
        });



    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("mcpb_primary_color", primColor);
        outState.putInt("mcpb_secondary_color", secondColor);
    }

    public int[] colorChoice(Context context) {

        int[] mColorChoices = null;
        String[] color_array = context.getResources().getStringArray(R.array.default_color_choice_values);

        if (color_array != null && color_array.length > 0) {
            mColorChoices = new int[color_array.length];
            for (int i = 0; i < color_array.length; i++) {
                mColorChoices[i] = Color.parseColor(color_array[i]);
            }
        }
        return mColorChoices;
    }
}
