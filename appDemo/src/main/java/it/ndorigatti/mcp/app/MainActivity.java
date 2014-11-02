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

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.samples.apps.iosched.ui.widget.BezelImageView;

import java.util.Random;

import it.gmariotti.android.example.colorpicker.dashclockpicker.ColorPickerDialogDash;
import it.ndorigatti.android.view.MulticolorProgressBar;

public class MainActivity extends ActionBarActivity {
    private MulticolorProgressBar multicolorProgressBar;
    private BezelImageView primaryColorPreview, secondaryColorPreview;
    private int primColor, secondColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setCollapsible(true);
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
        primaryColorPreview.setCropToPadding(false);

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_about) {
            //Open about dialog
            FragmentManager fm = getFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            Fragment prev = fm.findFragmentByTag("about_dialog");
            if (prev != null) {
                ft.remove(prev);
            }
            ft.addToBackStack(null);

            new AboutDialog().show(ft, "about_dialog");
            return true;
        }
        return super.onOptionsItemSelected(item);
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

    /**
     * About Dialog
     */
    public static class AboutDialog extends DialogFragment {

        public AboutDialog() {
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Get app version
            PackageManager pm = getActivity().getPackageManager();
            String packageName = getActivity().getPackageName();
            String versionName;
            try {
                PackageInfo info = pm.getPackageInfo(packageName, 0);
                versionName = info.versionName;
            } catch (PackageManager.NameNotFoundException e) {
                versionName = "-";
            }

            LayoutInflater layoutInflater = getActivity().getLayoutInflater();
            View rootView = layoutInflater.inflate(R.layout.dialog_about_layout, null);
            TextView nameAndVersionView = (TextView) rootView.findViewById(
                    R.id.app_info);
            nameAndVersionView.setText(Html.fromHtml(
                    getString(R.string.title_about, versionName)));

            TextView aboutBodyView = (TextView) rootView.findViewById(R.id.about_body);
            aboutBodyView.setText(Html.fromHtml(getString(R.string.about_body)));
            aboutBodyView.setMovementMethod(new LinkMovementMethod());

            return new AlertDialog.Builder(getActivity())
                    //.setTitle(R.string.title_about)
                    .setView(rootView)
                    .setPositiveButton(android.R.string.ok,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    dialog.dismiss();
                                }
                            }
                    )
                    .create();
        }
    }
}
