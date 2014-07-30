package it.ndorigatti.android.view;
/*
 * Copyright (C) 2006 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.util.Pools;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ProgressBar;

import java.util.ArrayList;

/**
 * <p>
 * Visual indicator of progress in some operation.  Displays a bar to the user
 * representing how far the operation has progressed; the application can
 * change the amount of progress (modifying the length of the bar) as it moves
 * forward.  There is also a secondary progress displayable on a progress bar
 * which is useful for displaying intermediate progress, such as the buffer
 * level during a streaming playback progress bar.
 * </p>
 * <p/>
 * <p>
 * A progress bar can also be made indeterminate. In indeterminate mode, the
 * progress bar shows a cyclic animation without an indication of progress. This mode is used by
 * applications when the length of the task is unknown. The indeterminate progress bar can be either
 * a spinning wheel or a horizontal bar.
 * </p>
 * <p/>
 * <p>The following code example shows how a progress bar can be used from
 * a worker thread to update the user interface to notify the user of progress:
 * </p>
 * <p/>
 * <pre>
 * public class MyActivity extends Activity {
 *     private static final int PROGRESS = 0x1;
 *
 *     private ProgressBar mProgress;
 *     private int mProgressStatus = 0;
 *
 *     private Handler mHandler = new Handler();
 *
 *     protected void onCreate(Bundle icicle) {
 *         super.onCreate(icicle);
 *
 *         setContentView(R.layout.progressbar_activity);
 *
 *         mProgress = (ProgressBar) findViewById(R.id.progress_bar);
 *
 *         // Start lengthy operation in a background thread
 *         new Thread(new Runnable() {
 *             public void run() {
 *                 while (mProgressStatus &lt; 100) {
 *                     mProgressStatus = doWork();
 *
 *                     // Update the progress bar
 *                     mHandler.post(new Runnable() {
 *                         public void run() {
 *                             mProgress.setProgress(mProgressStatus);
 *                         }
 *                     });
 *                 }
 *             }
 *         }).start();
 *     }
 * }</pre>
 *
 * <p>To add a progress bar to a layout file, you can use the {@code &lt;ProgressBar&gt;} element.
 * By default, the progress bar is a spinning wheel (an indeterminate indicator). To change to a
 * horizontal progress bar, apply the {@link android.R.style#Widget_ProgressBar_Horizontal
 * Widget.ProgressBar.Horizontal} style, like so:</p>
 *
 * <pre>
 * &lt;ProgressBar
 *     style="@android:style/Widget.ProgressBar.Horizontal"
 *     ... /&gt;</pre>
 *
 * <p>If you will use the progress bar to show real progress, you must use the horizontal bar. You
 * can then increment the  progress with {@link #incrementProgressBy incrementProgressBy()} or
 * {@link #setProgress setProgress()}. By default, the progress bar is full when it reaches 100. If
 * necessary, you can adjust the maximum value (the value for a full bar) using the {@link
 * R.styleable#BaseProgressBar_max android:max} attribute. Other attributes available are listed
 * below.</p>
 *
 * <p>Another common style to apply to the progress bar is {@link
 * android.R.style#Widget_ProgressBar_Small Widget.ProgressBar.Small}, which shows a smaller
 * version of the spinning wheel&mdash;useful when waiting for content to load.
 * For example, you can insert this kind of progress bar into your default layout for
 * a view that will be populated by some content fetched from the Internet&mdash;the spinning wheel
 * appears immediately and when your application receives the content, it replaces the progress bar
 * with the loaded content. For example:</p>
 *
 * <pre>
 * &lt;LinearLayout
 *     android:orientation="horizontal"
 *     ... &gt;
 *     &lt;ProgressBar
 *         android:layout_width="wrap_content"
 *         android:layout_height="wrap_content"
 *         style="@android:style/Widget.ProgressBar.Small"
 *         android:layout_marginRight="5dp" /&gt;
 *     &lt;TextView
 *         android:layout_width="wrap_content"
 *         android:layout_height="wrap_content"
 *         android:text="@string/loading" /&gt;
 * &lt;/LinearLayout&gt;</pre>
 *
 * <p>Other progress bar styles provided by the system include:</p>
 * <ul>
 * <li>{@link android.R.style#Widget_ProgressBar_Horizontal Widget.ProgressBar.Horizontal}</li>
 * </ul>
 * <p>The "inverse" styles provide an inverse color scheme for the spinner, which may be necessary
 * if your application uses a light colored theme (a white background).</p>
 *
 * <p><strong>XML attributes</b></strong>
 * <p>
 * See {@link R.styleable#BaseProgressBar BaseProgressBar Attributes},
 * {@link View View Attributes}
 * </p>
 *
 * @attr ref R.styleable#BaseProgressBar_max
 * @attr ref R.styleable#BaseProgressBar_maxHeight
 * @attr ref R.styleable#BaseProgressBar_maxWidth
 * @attr ref R.styleable#BaseProgressBar_minHeight
 * @attr ref R.styleable#BaseProgressBar_minWidth
 * @attr ref R.styleable#BaseProgressBar_progress
 * @attr ref R.styleable#BaseProgressBar_progressDrawable
 * @attr ref R.styleable#BaseProgressBar_secondaryProgress
 */
public class MulticolorProgressBar extends View {
    private static final int MAX_LEVEL = 10000;
    private final ArrayList<RefreshData> mRefreshData = new ArrayList<RefreshData>();
    int mMinWidth;
    int mMaxWidth;
    int mMinHeight;
    int mMaxHeight;
    int mProgressColor;
    int mSecondaryProgressColor;

    private int mProgress;
    private int mSecondaryProgress;
    private int mMax;
    private Drawable mProgressDrawable;
    private Drawable mCurrentDrawable;
    private Drawable mStraightDrawable;
    private Drawable mReversedDrawable;
    private boolean mNoInvalidate;
    private RefreshProgressRunnable mRefreshProgressRunnable;
    private long mUiThreadId;
    private boolean mAttached;
    private boolean mRefreshIsPosted;

    /**
     * Create a new progress bar with range 0...100 and initial progress of 0.
     *
     * @param context the application environment
     */
    public MulticolorProgressBar(Context context) {
        this(context, null);
    }

    public MulticolorProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.multicolorProgressBarStyle);
    }

    public MulticolorProgressBar(Context context, AttributeSet attrs, int defStyle) {
        this(context, attrs, defStyle, 0);
    }

    /**
     * @hide
     */
    public MulticolorProgressBar(Context context, AttributeSet attrs, int defStyle, int styleRes) {
        super(context, attrs, defStyle);
        mUiThreadId = Thread.currentThread().getId();
        initProgressBar();

        TypedArray a =
                context.obtainStyledAttributes(attrs, R.styleable.MulticolorProgressBar, defStyle, styleRes);

        mNoInvalidate = true;
        //straight (normal) order
        mStraightDrawable = a.getDrawable(R.styleable.MulticolorProgressBar_mcp_straightProgressDrawable);
        if (mStraightDrawable != null) {
            mStraightDrawable = tileify(mStraightDrawable, false);
            // Calling this method can set mMaxHeight, make sure the corresponding
            // XML attribute for mMaxHeight is read after calling this method
            setProgressDrawable(mStraightDrawable);
        }
        //save for later use
        mReversedDrawable = a.getDrawable(R.styleable.MulticolorProgressBar_mcp_reversedProgressDrawable);
        if (mReversedDrawable != null) {
            mReversedDrawable = tileify(mReversedDrawable, false);
        }

        mMinWidth = a.getDimensionPixelSize(R.styleable.MulticolorProgressBar_mcp_minWidth, mMinWidth);
        mMaxWidth = a.getDimensionPixelSize(R.styleable.MulticolorProgressBar_mcp_maxWidth, mMaxWidth);
        mMinHeight = a.getDimensionPixelSize(R.styleable.MulticolorProgressBar_mcp_minHeight, mMinHeight);
        mMaxHeight = a.getDimensionPixelSize(R.styleable.MulticolorProgressBar_mcp_maxHeight, mMaxHeight);

        // set colors and set drawable color filter
        mProgressColor = a.getColor(R.styleable.MulticolorProgressBar_mcp_progressColor, mProgressColor);
        mSecondaryProgressColor = a.getColor(R.styleable.MulticolorProgressBar_mcp_secondaryProgressColor, mSecondaryProgressColor);
        setDrawableColors(mProgressColor, mSecondaryProgressColor);
        //set max and progress
        setMax(a.getInt(R.styleable.MulticolorProgressBar_mcp_max, mMax));
        setProgress(a.getInt(R.styleable.MulticolorProgressBar_mcp_progress, mProgress));
        setSecondaryProgress(a.getInt(R.styleable.MulticolorProgressBar_mcp_secondaryProgress, mSecondaryProgress));
        mNoInvalidate = false;
        a.recycle();
    }

    /**
     * @param firstColor
     * @param secondColor
     */
    private void setDrawableColors(int firstColor, int secondColor) {
        doUpdateProgressColor(android.R.id.progress, firstColor);
        doUpdateProgressColor(android.R.id.secondaryProgress, secondColor);
    }

    /**
     * @param id
     * @param color
     */
    private void doUpdateProgressColor(int id, int color) {
        LayerDrawable layerDrawable = (LayerDrawable) mStraightDrawable;
        LayerDrawable reverseLayerDrawable = (LayerDrawable) mReversedDrawable;
        Drawable mOverlayProgressDrawable = layerDrawable.findDrawableByLayerId(id);
        mOverlayProgressDrawable.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        mOverlayProgressDrawable = reverseLayerDrawable.findDrawableByLayerId(id);
        mOverlayProgressDrawable.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
    }

    /**
     * Converts a drawable to a tiled version of itself. It will recursively
     * traverse layer and state list drawables.
     */
    private Drawable tileify(Drawable drawable, boolean clip) {

        if (drawable instanceof LayerDrawable) {
            LayerDrawable background = (LayerDrawable) drawable;
            final int N = background.getNumberOfLayers();
            Drawable[] outDrawables = new Drawable[N];

            for (int i = 0; i < N; i++) {
                int id = background.getId(i);
                outDrawables[i] = tileify(background.getDrawable(i),
                        (id == android.R.id.progress || id == android.R.id.secondaryProgress));
            }

            LayerDrawable newBg = new LayerDrawable(outDrawables);
            for (int i = 0; i < N; i++) {
                newBg.setId(i, background.getId(i));
            }
            return newBg;
        }
        return drawable;
    }

    /**
     * <p>
     * Initialize the progress bar's default values:
     * </p>
     * <ul>
     * <li>progress = 0</li>
     * <li>max = 100</li>
     * <li>animation duration = 4000 ms</li>
     * <li>indeterminate = false</li>
     * <li>behavior = repeat</li>
     * </ul>
     */
    private void initProgressBar() {
        mMax = 100;
        mProgress = 0;
        mProgressColor = Color.GREEN;
        mSecondaryProgress = 0;
        mSecondaryProgressColor = Color.BLUE;
        mMinWidth = 24;
        mMaxWidth = 48;
        mMinHeight = 24;
        mMaxHeight = 48;
    }

    /**
     * <p>Get the drawable used to draw the progress bar in
     * progress mode.</p>
     *
     * @return a {@link android.graphics.drawable.Drawable} instance
     *
     * @see #setProgressDrawable(android.graphics.drawable.Drawable)
     */
    public Drawable getProgressDrawable() {
        return mProgressDrawable;
    }

    /**
     * <p>Define the drawable used to draw the progress bar in
     * progress mode.</p>
     *
     * @param d the new drawable
     *
     * @see #getProgressDrawable()
     */
    public void setProgressDrawable(Drawable d) {
        boolean needUpdate;
        if (mProgressDrawable != null && d != mProgressDrawable) {
            mProgressDrawable.setCallback(null);
            needUpdate = true;
        } else {
            needUpdate = false;
        }

        if (d != null) {
            d.setCallback(this);

            // Make sure the ProgressBar is always tall enough
            int drawableHeight = d.getMinimumHeight();
            if (mMaxHeight < drawableHeight) {
                mMaxHeight = drawableHeight;
                requestLayout();
            }
        }
        mProgressDrawable = d;
        mCurrentDrawable = d;
        postInvalidate();

        if (needUpdate) {
            updateDrawableBounds(getWidth(), getHeight());
            updateDrawableState();
            doRefreshProgress(android.R.id.progress, mProgress, false, false);
            doRefreshProgress(android.R.id.secondaryProgress, mSecondaryProgress, false, false);
        }
    }

    private synchronized void doRefreshProgress(int id, int progress, boolean fromUser,
                                                boolean callBackToApp) {
        float scale = mMax > 0 ? (float) progress / (float) mMax : 0;
        final Drawable d = mCurrentDrawable;
        if (d != null) {
            Drawable progressDrawable = null;
            if (d instanceof LayerDrawable) {
                progressDrawable = ((LayerDrawable) d).findDrawableByLayerId(id);
            }

            final int level = (int) (scale * MAX_LEVEL);
            (progressDrawable != null ? progressDrawable : d).setLevel(level);
        } else {
            invalidate();
        }
    }

    private synchronized void refreshProgress(int id, int progress, boolean fromUser) {
        if (mUiThreadId == Thread.currentThread().getId()) {
            doRefreshProgress(id, progress, fromUser, true);
        } else {
            if (mRefreshProgressRunnable == null) {
                mRefreshProgressRunnable = new RefreshProgressRunnable();
            }

            final RefreshData rd = RefreshData.obtain(id, progress, fromUser);
            mRefreshData.add(rd);
            if (mAttached && !mRefreshIsPosted) {
                post(mRefreshProgressRunnable);
                mRefreshIsPosted = true;
            }
        }
    }

    synchronized void setProgress(int progress, boolean fromUser) {
        if (progress < 0) {
            progress = 0;
        }

        if (progress > mMax) {
            progress = mMax;
        }
        //before updating progress, check if we need to switch the colors, by doing a double in memory check:
        boolean isOrderChanged = (isNormalColorOrder() ^ checkIsNormalColorOrder(progress, mSecondaryProgress));

        if (progress != mProgress) {
            mProgress = progress;
            if (isOrderChanged) {
                if (checkIsNormalColorOrder(mProgress, mSecondaryProgress)) {
                    setProgressDrawable(mStraightDrawable);
                } else {
                    // in this case the primary progress (the overlay) is greater than secondary, so we should switch to the inversed coloring and reverse the values
                    setProgressDrawable(mReversedDrawable);
                }
            } else {
                //if the progress order has changed, then change the progress drawable, otherwise refresh progress
                refreshProgress(android.R.id.progress, mProgress, fromUser);
            }
        }
    }

    /**
     * <p>Get the progress bar's current level of progress. Return 0 when the
     * progress bar is in indeterminate mode.</p>
     *
     * @return the current progress, between 0 and {@link #getMax()}
     *
     * @see #setProgress(int)
     * @see #setMax(int)
     * @see #getMax()
     */
    public synchronized int getProgress() {
        return mProgress;
    }

    /**
     * <p>Set the current progress to the specified value. Does not do anything
     * if the progress bar is in indeterminate mode.</p>
     *
     * @param progress the new progress, between 0 and {@link #getMax()}
     *
     * @see #getProgress()
     * @see #incrementProgressBy(int)
     */
    public synchronized void setProgress(int progress) {
        setProgress(progress, false);
    }

    /**
     * <p>Set the current progress color to the specified one.</p>
     *
     * @param progressColor the new color; must be a valid color.
     */
    public void setProgressColor(int progressColor) {
        if (progressColor != mProgressColor) {
            mProgressColor = progressColor;
            doUpdateProgressColor(android.R.id.progress, mProgressColor);
        }
    }

    /**
     * <p>Get the progress bar's current level of secondary progress. Return 0 when the
     * progress bar is in indeterminate mode.</p>
     *
     * @return the current secondary progress, between 0 and {@link #getMax()}
     *
     * @see #setSecondaryProgress(int)
     * @see #setMax(int)
     * @see #getMax()
     */
    public synchronized int getSecondaryProgress() {
        return mSecondaryProgress;
    }

    /**
     * <p>
     * Set the current secondary progress to the specified value. Does not do
     * anything if the progress bar is in indeterminate mode.
     * </p>
     *
     * @param secondaryProgress the new secondary progress, between 0 and {@link #getMax()}
     *
     * @see #getSecondaryProgress()
     * @see #incrementSecondaryProgressBy(int)
     */
    public synchronized void setSecondaryProgress(int secondaryProgress) {
        if (secondaryProgress < 0) {
            secondaryProgress = 0;
        }

        if (secondaryProgress > mMax) {
            secondaryProgress = mMax;
        }
        //before updating progress, check if we need to switch the colors, by doing a double in memory check:
        boolean isOrderChanged = (isNormalColorOrder() ^ checkIsNormalColorOrder(mProgress, secondaryProgress));


        if (secondaryProgress != mSecondaryProgress) {
            mSecondaryProgress = secondaryProgress;
            if (isOrderChanged) {
                if (checkIsNormalColorOrder(mProgress, mSecondaryProgress)) {
                    setProgressDrawable(mStraightDrawable);
                } else {
                    // in this case the primary progress (the overlay) is greater than secondary, so we should switch to the inversed coloring and reverse the values
                    setProgressDrawable(mReversedDrawable);
                }
            } else {
                refreshProgress(android.R.id.secondaryProgress, mSecondaryProgress, false);
            }
        }
    }

    /**
     * <p>Set the current secondary progress color to the specified one.</p>
     *
     * @param secondaryProgressColor
     */
    public void setSecondaryProgressColor(int secondaryProgressColor) {
        if (secondaryProgressColor != mSecondaryProgressColor) {
            mSecondaryProgressColor = secondaryProgressColor;
            doUpdateProgressColor(android.R.id.secondaryProgress, mSecondaryProgressColor);
        }
    }

    /**
     * <p>Return the upper limit of this progress bar's range.</p>
     *
     * @return a positive integer
     *
     * @see #setMax(int)
     * @see #getProgress()
     * @see #getSecondaryProgress()
     */
    public synchronized int getMax() {
        return mMax;
    }

    /**
     * <p>Set the range of the progress bar to 0...<tt>max</tt>.</p>
     *
     * @param max the upper range of this progress bar
     *
     * @see #getMax()
     * @see #setProgress(int)
     * @see #setSecondaryProgress(int)
     */
    public synchronized void setMax(int max) {
        if (max < 0) {
            max = 0;
        }
        if (max != mMax) {
            mMax = max;
            postInvalidate();

            if (mProgress > max) {
                mProgress = max;
            }
            refreshProgress(android.R.id.progress, mProgress, false);
            if (mSecondaryProgress > max) {
                mSecondaryProgress = max;
            }
            refreshProgress(android.R.id.secondaryProgress, mSecondaryProgress, false);
        }
    }

    /**
     * <p>Increase the progress bar's progress by the specified amount.</p>
     *
     * @param diff the amount by which the progress must be increased
     *
     * @see #setProgress(int)
     */
    public synchronized final void incrementProgressBy(int diff) {
        setProgress(mProgress + diff);
    }

    /**
     * <p>Increase the progress bar's secondary progress by the specified amount.</p>
     *
     * @param diff the amount by which the secondary progress must be increased
     *
     * @see #setSecondaryProgress(int)
     */
    public synchronized final void incrementSecondaryProgressBy(int diff) {
        setSecondaryProgress(mSecondaryProgress + diff);
    }

    private void updateDrawableBounds(int w, int h) {
        // onDraw will translate the canvas so we draw starting at 0,0.
        // Subtract out padding for the purposes of the calculations below.
        w -= getPaddingRight() + getPaddingLeft();
        h -= getPaddingTop() + getPaddingBottom();

        int right = w;
        int bottom = h;

        if (mProgressDrawable != null) {
            mProgressDrawable.setBounds(0, 0, right, bottom);
        }
    }

    private void updateDrawableState() {
        int[] state = getDrawableState();

        if (mProgressDrawable != null && mProgressDrawable.isStateful()) {
            mProgressDrawable.setState(state);
        }
    }

    @Override
    public void setVisibility(int v) {
        if (getVisibility() != v) {
            super.setVisibility(v);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        updateDrawableBounds(w, h);
    }

    @Override
    public void postInvalidate() {
        if (!mNoInvalidate) {
            super.postInvalidate();
        }
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Drawable d = mCurrentDrawable;
        if (d != null) {
            // Translate canvas so a indeterminate circular progress bar with padding
            // rotates properly in its animation
            canvas.save();
            canvas.translate(getPaddingLeft(), getPaddingTop());
            long time = getDrawingTime();
            d.draw(canvas);
            canvas.restore();
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (mRefreshData != null) {
            synchronized (this) {
                final int count = mRefreshData.size();
                for (int i = 0; i < count; i++) {
                    final RefreshData rd = mRefreshData.get(i);
                    doRefreshProgress(rd.id, rd.progress, rd.fromUser, true);
                    rd.recycle();
                }
                mRefreshData.clear();
            }
        }
        mAttached = true;
    }

    @Override
    protected void onDetachedFromWindow() {
        if (mRefreshProgressRunnable != null) {
            removeCallbacks(mRefreshProgressRunnable);
        }
        if (mRefreshProgressRunnable != null && mRefreshIsPosted) {
            removeCallbacks(mRefreshProgressRunnable);
        }
        // This should come after stopAnimation(), otherwise an invalidate message remains in the
        // queue, which can prevent the entire view hierarchy from being GC'ed during a rotation
        super.onDetachedFromWindow();
        mAttached = false;
    }

    @Override
    public Parcelable onSaveInstanceState() {
        // Force our ancestor class to save its state
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);

        ss.progress = mProgress;
        ss.secondaryProgress = mSecondaryProgress;
        ss.progressColor = mProgressColor;
        ss.secondaryProgressColor = mSecondaryProgressColor;
        return ss;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());

        setProgress(ss.progress);
        setSecondaryProgress(ss.secondaryProgress);
    }

    @Override
    public void invalidateDrawable(Drawable dr) {
        super.invalidateDrawable(dr);
    }

    @Override
    protected boolean verifyDrawable(Drawable who) {
        return who == mProgressDrawable || super.verifyDrawable(who);
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        updateDrawableState();
    }

    @Override
    public void jumpDrawablesToCurrentState() {
        super.jumpDrawablesToCurrentState();
        if (mProgressDrawable != null) mProgressDrawable.jumpToCurrentState();
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Drawable d = mCurrentDrawable;

        int dw = 0;
        int dh = 0;
        if (d != null) {
            dw = Math.max(mMinWidth, Math.min(mMaxWidth, d.getIntrinsicWidth()));
            dh = Math.max(mMinHeight, Math.min(mMaxHeight, d.getIntrinsicHeight()));
        }
        updateDrawableState();
        dw += getPaddingLeft() + getPaddingRight();
        dh += getPaddingTop() + getPaddingBottom();

        setMeasuredDimension(resolveSizeAndState(dw, widthMeasureSpec, 0),
                resolveSizeAndState(dh, heightMeasureSpec, 0));
    }

    /**
     * @param progress
     * @param secondaryProgress
     *
     * @return
     */
    private int compareNewProgress(int progress, int secondaryProgress) {
        return (progress - secondaryProgress);
    }

    /**
     * Returns whether if colors are shown as normal ordering or if they are drawn reversed.
     * It looks up at stored parameter, does not recalculate anything.
     */
    private boolean isNormalColorOrder() {
        return (compareNewProgress(mProgress, mSecondaryProgress) <= 0) ? true : false;
    }

    //Colored

    /**
     * This function calculates (in memory) the ordering of the two given parameter and returns the result as a boolean.
     *
     * @param progress
     * @param secondaryProgress
     *
     * @return
     */
    private boolean checkIsNormalColorOrder(int progress, int secondaryProgress) {
        return (compareNewProgress(progress, secondaryProgress) <= 0) ? true : false;
    }

    private static class RefreshData {
        private static final int POOL_MAX = 24;
        private static final Pools.SynchronizedPool<RefreshData> sPool =
                new Pools.SynchronizedPool<RefreshData>(POOL_MAX);

        public int id;
        public int progress;
        public boolean fromUser;

        public static RefreshData obtain(int id, int progress, boolean fromUser) {
            RefreshData rd = sPool.acquire();
            if (rd == null) {
                rd = new RefreshData();
            }
            rd.id = id;
            rd.progress = progress;
            rd.fromUser = fromUser;
            return rd;
        }

        public void recycle() {
            sPool.release(this);
        }
    }

    static class SavedState extends BaseSavedState {
        public static final Parcelable.Creator<SavedState> CREATOR
                = new Parcelable.Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
        int progress;
        int secondaryProgress;
        int progressColor;
        int secondaryProgressColor;

        /**
         * Constructor called from {@link ProgressBar#onSaveInstanceState()}
         */
        SavedState(Parcelable superState) {
            super(superState);
        }

        /**
         * Constructor called from {@link #CREATOR}
         */
        private SavedState(Parcel in) {
            super(in);
            progress = in.readInt();
            secondaryProgress = in.readInt();
            progressColor = in.readInt();
            secondaryProgressColor = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(progress);
            out.writeInt(secondaryProgress);
            out.writeInt(progressColor);
            out.writeInt(secondaryProgressColor);
        }
    }

    private class RefreshProgressRunnable implements Runnable {
        public void run() {
            synchronized (MulticolorProgressBar.this) {
                final int count = mRefreshData.size();
                for (int i = 0; i < count; i++) {
                    final RefreshData rd = mRefreshData.get(i);
                    doRefreshProgress(rd.id, rd.progress, rd.fromUser, true);
                    rd.recycle();
                }
                mRefreshData.clear();
                mRefreshIsPosted = false;
            }
        }
    }
}
