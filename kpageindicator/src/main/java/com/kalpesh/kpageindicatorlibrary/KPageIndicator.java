package com.kalpesh.kpageindicatorlibrary;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import java.util.ArrayList;

public class KPageIndicator extends View {

    /*
    Page indicator animation types.
     */
    public static final int ANIMATION_ENLARGE = 2;
    public static final int ANIMATION_LINEAR = 1;
    public static final int ANIMATION_NONE = 0;

    private static final int DEFAULT_ANIMATION_DURATION = 300;

    private static final int DEF_INDICATOR_BG_COLOR = Color.parseColor("#50ffffff");
    private static final int DEF_INDICATOR_FG_COLOR = Color.parseColor("#ffffff");

    /*
    Page indicator styles
     */
    public static final int STYLE_ENLARGED = 1;
    public static final int STYLE_DEFAULT = 0;

    private int currentIndicatorColor = DEF_INDICATOR_FG_COLOR;
    private int defaultIndicatorColor = DEF_INDICATOR_BG_COLOR;

    private int currentPage;
    private int indicatorAnimation = ANIMATION_NONE;
    private int indicatorStyle = STYLE_DEFAULT;

    private ArrayList<Paint> indicators = new ArrayList<>();

    private int mHeight;
    private int mWidth;
    private int newR;
    private int newX;
    private int pageCount;
    private int prevX;

    private Paint selectedIndicator = new Paint();

    private int spacingBetweenIndicators;
    private int verticalIndicatorSpacing;

    public KPageIndicator(Context context) {
        super(context);
    }

    public KPageIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        int defVertSpacing = (int) getResources().getDimension(R.dimen.default_vertical_indicator_spacing);
        int defSpacingBetInd = (int) getResources().getDimension(R.dimen.default_spacing_between_indicators);

        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.KPageIndicator);
        pageCount = typedArray.getInt(R.styleable.KPageIndicator_page_count, 0);
        currentPage = typedArray.getInt(R.styleable.KPageIndicator_current_page, 0);
        currentIndicatorColor = typedArray.getColor(R.styleable.KPageIndicator_current_indicator_color, DEF_INDICATOR_FG_COLOR);
        defaultIndicatorColor = typedArray.getColor(R.styleable.KPageIndicator_default_indicator_color, DEF_INDICATOR_BG_COLOR);
        verticalIndicatorSpacing = (int) typedArray.getDimension(R.styleable.KPageIndicator_vertical_indicator_spacing, (float) defVertSpacing);
        spacingBetweenIndicators = (int) typedArray.getDimension(R.styleable.KPageIndicator_spacing_between_indicators, (float) defSpacingBetInd);
        if (typedArray.hasValue(R.styleable.KPageIndicator_indicator_animation)) {
            indicatorAnimation = typedArray.getInt(R.styleable.KPageIndicator_indicator_animation, ANIMATION_NONE);
        }
        if (typedArray.hasValue(R.styleable.KPageIndicator_indicator_style)) {
            indicatorStyle = typedArray.getInt(R.styleable.KPageIndicator_indicator_style, STYLE_DEFAULT);
        }
        typedArray.recycle();

        init();
    }

    private void init() {
        indicators.clear();
        for (int i = 0; i < pageCount; i++) {
            indicators.add(new Paint());
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width;
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int requiredWidth = (getHeight() * pageCount) + (spacingBetweenIndicators * (pageCount - 1));
        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else if (widthMode == Integer.MIN_VALUE) {
            width = Math.min(requiredWidth, widthSize);
        } else {
            width = requiredWidth;
        }
        int defHeight = (int) getResources().getDimension(R.dimen.default_height);
        if (heightMode == Integer.MIN_VALUE) {
            heightSize = Math.min(defHeight, heightSize);
        } else if (heightMode == MeasureSpec.UNSPECIFIED) {
            heightSize = defHeight;
        }
        if (heightSize == 0) {
            heightSize = defHeight;
        }
        setMeasuredDimension(width, heightSize);
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int r = mHeight / 2;
        int y = mHeight / 2;
        int x = r;
        for (int i = 0; i < indicators.size(); i++) {
            int radius = r - (indicatorStyle == STYLE_DEFAULT ? 0 : verticalIndicatorSpacing);
            if (i == currentPage && newX == 0) {
                selectedIndicator.setColor(currentIndicatorColor);
                canvas.drawCircle((float) x, (float) y, (float) r, selectedIndicator);
                prevX = x;
            }
            if (newX > 0) {
                selectedIndicator.setColor(currentIndicatorColor);
                canvas.drawCircle((float) newX, (float) y, (float) newR, selectedIndicator);
                prevX = newX;
            }
            Paint paint = indicators.get(i);
            paint.setColor(defaultIndicatorColor);
            canvas.drawCircle((float) x, (float) y, (float) radius, paint);
            x += spacingBetweenIndicators + mHeight;
        }
    }

    /**
     * Set total pages to display page indicators.
     *
     * @param pageCount Total number of pages.
     */
    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
        init();
        requestLayout();
    }

    /**
     * Get total number of pages.
     *
     * @return Total number of pages.
     */
    public int getPageCount() {
        return pageCount;
    }

    /**
     * Set current page.
     *
     * @param pageIndex Page index to be selected.
     */
    public void setCurrentPage(int pageIndex) {
        if (pageIndex >= 0 && pageIndex < pageCount) {
            currentPage = pageIndex;
            setCurrentPage(pageIndex, DEFAULT_ANIMATION_DURATION);
        }
    }

    /**
     * Set current page with animation.
     *
     * @param pageIndex         Page index to be selected.
     * @param animationDuration Duration of the animation.
     *                          Value <= 0 will take the default animation duration i.e. 300 ms.
     */
    public void setCurrentPage(int pageIndex, long animationDuration) {
        if (pageIndex >= 0 && pageIndex < pageCount) {
            currentPage = pageIndex;
            updateCurrentPage(animationDuration);
        }
    }

    private void updateCurrentPage(long animationDuration) {
        int finalValue;
        int initialValue;
        int x = mHeight / 2;
        int i = 0;

        while (true) {
            if (i >= indicators.size()) {
                break;
            } else if (i == currentPage) {
                newX = x;
                break;
            } else {
                x += spacingBetweenIndicators + mHeight;
                i++;
            }
        }

        newR = mHeight / 2;
        if (indicatorAnimation == ANIMATION_NONE) {
            invalidate();
            return;
        }
        if (animationDuration == 0) {
            animationDuration = DEFAULT_ANIMATION_DURATION;
        }
        if (indicatorAnimation == ANIMATION_LINEAR) {
            initialValue = prevX;
            finalValue = newX;
        } else {
            initialValue = 0;
            finalValue = newR;
        }

        ValueAnimator animator = ValueAnimator.ofInt(initialValue, finalValue);
        animator.setDuration(animationDuration);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.addUpdateListener(new AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int animationValue = (Integer) valueAnimator.getAnimatedValue();
                if (indicatorAnimation == ANIMATION_LINEAR) {
                    newX = animationValue;
                } else {
                    newR = animationValue;
                }
                invalidate();
            }
        });
        animator.start();
    }

    /**
     * Get current page index.
     *
     * @return Current page index.
     */
    public int getCurrentPage() {
        return currentPage;
    }

    /**
     * Default indicator color. Color for indicators which are not highlighted.
     *
     * @param defaultIndicatorColor Default indicator color.
     */
    public void setDefaultIndicatorColor(int defaultIndicatorColor) {
        this.defaultIndicatorColor = defaultIndicatorColor;
    }

    /**
     * Default indicator color.
     *
     * @return Default indicator color.
     */
    public int getDefaultIndicatorColor() {
        return defaultIndicatorColor;
    }

    /**
     * Color for the selected indicator.
     *
     * @param currentIndicatorColor Selected indicator color.
     */
    public void setCurrentIndicatorColor(int currentIndicatorColor) {
        this.currentIndicatorColor = currentIndicatorColor;
    }

    /**
     * Selected indicator color.
     *
     * @return Selected indicator color.
     */
    public int getCurrentIndicatorColor() {
        return currentIndicatorColor;
    }

    /**
     * Indicator change animation.
     *
     * @param indicatorAnimation Indicator change animation.
     */
    public void setIndicatorAnimation(int indicatorAnimation) {
        this.indicatorAnimation = indicatorAnimation;
    }

    /**
     * Indicator change animation.
     *
     * @return Indicator change animation.
     */
    public int getIndicatorAnimation() {
        return indicatorAnimation;
    }

    /**
     * Indicator style.
     *
     * @param indicatorStyle Indicator style.
     */
    public void setIndicatorStyle(int indicatorStyle) {
        this.indicatorStyle = indicatorStyle;
        requestLayout();
    }

    /**
     * Indicator style.
     *
     * @return Indicator style.
     */
    public int getIndicatorStyle() {
        return indicatorStyle;
    }

}
