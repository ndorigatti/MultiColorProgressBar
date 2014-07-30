package it.ndorigatti.mcp.app;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;

import com.github.amlcurran.showcaseview.OnShowcaseEventListener;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ActionViewTarget;
import com.github.amlcurran.showcaseview.targets.ViewTarget;

import java.util.Random;

import it.gmariotti.android.example.colorpicker.dashclockpicker.ColorPickerDialogDash;
import it.ndorigatti.android.view.MulticolorProgressBar;

public class MainActivity extends Activity {
    MulticolorProgressBar multicolorProgressBar;
    private ImageButton primaryColorPreview, secondaryColorPreview;
    private int primColor, secondColor;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        multicolorProgressBar = (MulticolorProgressBar) findViewById(R.id.bicolorProgressBar);
        primColor = getResources().getColor(R.color.initial_progress_color);
        secondColor = getResources().getColor(R.color.initial_secondaryprogress_color);
        primaryColorPreview = (ImageButton) findViewById(R.id.primaryProgressColorPreview);
        secondaryColorPreview = (ImageButton) findViewById(R.id.secondaryProgressColorPreview);
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


        //ShowcaseView scv =
        ShowcaseView scv = new ShowcaseView.Builder(this)
                .setTarget(new ViewTarget(secondaryColorPreview))
                .setContentTitle("Change Colors")
                .setContentText("Click on coloured tiles to change the progressbar colors")
                .hideOnTouchOutside().setStyle(R.style.ShowcaseView_MCP).setShowcaseEventListener(new OnShowcaseEventListener() {
                    @Override
                    public void onShowcaseViewHide(ShowcaseView showcaseView) {

                        new ShowcaseView.Builder(MainActivity.this).setTarget(new ViewTarget(randomColors))
                                .setContentTitle("Random Colors").setContentText("Click to generate Random Progress Bar Colors")
                                .setStyle(R.style.ShowcaseView_MCP).hideOnTouchOutside().setShowcaseEventListener(new OnShowcaseEventListener() {
                            @Override
                            public void onShowcaseViewHide(ShowcaseView showcaseView) {
                                new ShowcaseView.Builder(MainActivity.this)
                                        .setTarget(new ActionViewTarget(MainActivity.this, ActionViewTarget.Type.HOME))
                                        .setContentTitle("And a big THANK YOU to Taylor Ling for the app icon!")
                                        .hideOnTouchOutside().setStyle(R.style.ShowcaseView_MCP)
                                        .build();
                                //.setShouldCentreText(true);
                            }

                            @Override
                            public void onShowcaseViewDidHide(ShowcaseView showcaseView) {

                            }

                            @Override
                            public void onShowcaseViewShow(ShowcaseView showcaseView) {

                            }
                        }).build().setShouldCentreText(true);
                    }

                    @Override
                    public void onShowcaseViewDidHide(ShowcaseView showcaseView) {

                    }

                    @Override
                    public void onShowcaseViewShow(ShowcaseView showcaseView) {

                    }
                })
                .singleShot(140586L)
                .build();
        scv.setShouldCentreText(true);


    }
}
