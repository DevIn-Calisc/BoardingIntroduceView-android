package com.calisc.boardingintroduceview.Fragment;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.media.Image;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.calisc.boardingintroduceview.Models.BoardModel;
import com.calisc.boardingintroduceview.R;
import com.calisc.boardingintroduceview.listeners.AnimatorEndListener;
import com.calisc.boardingintroduceview.listeners.PaperBoardingOnChangeListener;
import com.calisc.boardingintroduceview.listeners.PaperBoardingOnLeftOutListener;
import com.calisc.boardingintroduceview.listeners.PaperBoardingOnRightOutListener;
import com.calisc.boardingintroduceview.utils.ConfigureConstant;

import java.util.ArrayList;

import androidx.constraintlayout.widget.ConstraintLayout;

public class PaperBoardingEngine implements ConfigureConstant {
    private float dpToPixel;

    private RelativeLayout rootLayout;
    private FrameLayout contentTextContainer;
    private FrameLayout contentIconContainer;
    private FrameLayout backgroundContainer;
    private LinearLayout paperIconsContainer;

    private RelativeLayout contentRootLayout;
    private LinearLayout contentCenteredContainer;

    private Context mAppContext;

    private ArrayList<BoardModel> mElements = new ArrayList<>();
    private int mActiveElementIndex = 0;

    private int mPaperElementActiveSize;
    private int mPaperElementNormalSize;
    private int mPaperElementLeftMargin;
    private int mPaperElementRightMargin;


    private PaperBoardingOnChangeListener mOnChangeListener;
    private PaperBoardingOnLeftOutListener mOnLeftOutListener;
    private PaperBoardingOnRightOutListener mOnRightOutListener;

    public PaperBoardingEngine(View rootLayout, ArrayList<BoardModel> elements, Context context) {
        if (elements == null || elements.isEmpty()) {
            throw new IllegalArgumentException("No content elements");
        }

        this.mElements = elements;
        this.mAppContext = context.getApplicationContext();

        this.rootLayout = (RelativeLayout)rootLayout;

        contentTextContainer = rootLayout.findViewById(R.id.contentTextContainer);
        contentIconContainer = rootLayout.findViewById(R.id.contentIconContainer);
        backgroundContainer = rootLayout.findViewById(R.id.backgroundBoard);
        paperIconsContainer = rootLayout.findViewById(R.id.paperIconsContainer);

        contentRootLayout = rootLayout.findViewById(R.id.main_view);
        contentCenteredContainer = contentRootLayout.findViewById(R.id.inside_main_view);

        this.dpToPixel = this.mAppContext.getResources().getDisplayMetrics().density;





    }


    /* Animator set */
    protected AnimatorSet createBGAnimatorSet(final int color) {
        final View bgColorView = new ImageView(mAppContext);
        bgColorView.setLayoutParams(new RelativeLayout.LayoutParams(rootLayout.getWidth(), rootLayout.getHeight()));
        bgColorView.setBackgroundColor(color);

        backgroundContainer.addView(bgColorView);

        int[] pos = calculateCurrentCenterCoordinatesOfPaperElement(mActiveElementIndex);

        float finalRadius = rootLayout.getWidth() > rootLayout.getHeight() ? rootLayout.getWidth() : rootLayout.getHeight();

        AnimatorSet bgAnimSet = new AnimatorSet();
        Animator fadeIn = ObjectAnimator.ofFloat(bgColorView, "alpha", 0, 1);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Animator circularReveal = ViewAnimationUtils.createCircularReveal(bgColorView, pos[0], pos[1], 0, finalRadius);
            circularReveal.setInterpolator(new AccelerateInterpolator());
            bgAnimSet.playTogether(circularReveal, fadeIn);
        } else {
            bgAnimSet.playTogether(fadeIn);
        }

        bgAnimSet.setDuration(ANIM_BACKGROUND_TIME);
        bgAnimSet.addListener(new AnimatorEndListener() {
            @Override
            public void onAnimationEnd(Animator animator) {
                rootLayout.setBackgroundColor(color);
                bgColorView.setVisibility(View.GONE);
                backgroundContainer.removeView(bgColorView);
            }
        });

        return bgAnimSet;
    }

    protected AnimatorSet createContentTextShowAnimation(final View currentContentText, View newContentText) {
        int positionDeltaPx = dpToPixels(CONTENT_TEXT_POS_DELTA_Y_DP);
        AnimatorSet animations = new AnimatorSet();
        Animator currentContentMoveUp = ObjectAnimator.ofFloat(currentContentText, "y", 0, -positionDeltaPx);
        currentContentMoveUp.setDuration(ANIM_CONTENT_TEXT_HIDE_TIME);
        currentContentMoveUp.addListener(new AnimatorEndListener() {
            @Override
            public void onAnimationEnd(Animator animator) {
                contentTextContainer.removeView(currentContentText);
            }
        });

        Animator currentContentFadeOut = ObjectAnimator.ofFloat(currentContentText, "alpha", 1, 0);
        currentContentFadeOut.setDuration(ANIM_CONTENT_TEXT_HIDE_TIME);

        animations.playTogether(currentContentMoveUp, currentContentFadeOut);

        Animator newContentMoveUp = ObjectAnimator.ofFloat(newContentText, "y", positionDeltaPx, 0);
        newContentMoveUp.setDuration(ANIM_CONTENT_TEXT_SHOW_TIME);

        Animator newContentFadeIn = ObjectAnimator.ofFloat(newContentText, "alpha", 0, 1);
        newContentFadeIn.setDuration(ANIM_CONTENT_TEXT_SHOW_TIME);

        animations.playTogether(newContentMoveUp, newContentFadeIn);

        animations.setInterpolator(new DecelerateInterpolator());

        return animations;
    }

    protected AnimatorSet createContentIconShowAnimation(final View currentContentIcon, View newContentIcon) {
        int positionDeltaPx = dpToPixels(CONTENT_ICON_POS_DELTA_Y_DP);
        AnimatorSet animations = new AnimatorSet();
        Animator currentContentMoveUp = ObjectAnimator.ofFloat(currentContentIcon, "y", 0, -positionDeltaPx);
        currentContentMoveUp.setDuration(ANIM_CONTENT_ICON_HIDE_TIME);

        currentContentMoveUp.addListener(new AnimatorEndListener() {
            @Override
            public void onAnimationEnd(Animator animator) {
                contentIconContainer.removeView(currentContentIcon);
            }
        });

        Animator currentContentFadeOut = ObjectAnimator.ofFloat(currentContentIcon, "alpha", 1,0);
        currentContentFadeOut.setDuration(ANIM_CONTENT_ICON_HIDE_TIME);

        animations.playTogether(currentContentMoveUp, currentContentFadeOut);

        Animator newContentMoveUp = ObjectAnimator.ofFloat(newContentIcon, "y", positionDeltaPx, 0);
        newContentMoveUp.setDuration(ANIM_CONTENT_ICON_SHOW_TIME);

        Animator newContentFadeIn = ObjectAnimator.ofFloat(newContentIcon, "alpha", 0, 1);
        newContentFadeIn.setDuration(ANIM_CONTENT_ICON_SHOW_TIME);

        animations.playTogether(newContentMoveUp, newContentFadeIn);

        animations.setInterpolator(new DecelerateInterpolator());

        return animations;
    }

    protected Animator createContenCenterVerticalAnimation(View newContentText, View newContentIcon) {
        newContentText.measure(View.MeasureSpec.makeMeasureSpec(contentCenteredContainer.getWidth(), View.MeasureSpec.AT_MOST), -2);
        int measuredContentTextHeight = newContentText.getMeasuredHeight();
        newContentIcon.measure(-2, -2);
        int measuredContentIconHeight = newContentIcon.getMeasuredHeight();


        int newHeightOfContent = measuredContentIconHeight + measuredContentTextHeight + ((ViewGroup.MarginLayoutParams) contentTextContainer.getLayoutParams()).topMargin;
        Animator centerContentAnimation = ObjectAnimator.ofFloat(contentCenteredContainer, "y", contentCenteredContainer.getY(), (rootLayout.getHeight() - newHeightOfContent) / 2);
        centerContentAnimation.setDuration(ANIM_CONTENT_CENTERING_TIME);
        centerContentAnimation.setInterpolator(new DecelerateInterpolator());

        return centerContentAnimation;
    }

    protected int[] calculateCurrentCenterCoordinatesOfPaperElement(int activeIndex) {
        int y = (int)(paperIconsContainer.getY() + paperIconsContainer.getHeight() / 2);
        if (activeIndex >= paperIconsContainer.getChildCount()) {
            return new int[]{rootLayout.getWidth() / 2, y};
        }

        View pagerElem = contentIconContainer.getChildAt(activeIndex);
        int x = (int)(paperIconsContainer.getX() + pagerElem.getX() + pagerElem.getWidth() / 2);

        return new int[]{x, y};
    }

    protected int calculateNewPagerPosition(int newActiveElement) {
        newActiveElement ++;
        if (newActiveElement <= 0)
            newActiveElement = 1;
        int pagerActiveElemCenterPosX = mPaperElementActiveSize / 2 + newActiveElement * mPaperElementLeftMargin + (newActiveElement - 1) * (mPaperElementNormalSize + mPaperElementRightMargin);
        return rootLayout.getWidth() / 2 - pagerActiveElemCenterPosX;
    }

    protected void initializeStartingState() {
        // create botttom bar icons for all elements with big first icon
        for (int i = 0; i < mElements.size(); i++) {
            BoardModel item = mElements.get(i);
            ViewGroup bottomBarIconElement = createPagerIconElement(item.getBottomBarIconRes(), i==0);
            paperIconsContainer.addView(bottomBarIconElement);
        }

        // initialize first element on screen
        BoardModel activeElement = getActiveElement();

        ViewGroup initialContentText = createContentTextView(activeElement);
        contentTextContainer.addView(initialContentText);

        ImageView initContentIcon = createContentIconView(activeElement);
        contentIconContainer.addView(initContentIcon);

        rootLayout.setBackgroundColor(activeElement.getBgColor());
    }

    protected AnimatorSet createPageIconAnimation(int oldIndex, int newIndex) {
        AnimatorSet animations = new AnimatorSet();
        animations.setDuration(ANIM_PAPER_ICON_TIME);

        // scale down hwole old element
        final ViewGroup oldActiveItem = (ViewGroup) paperIconsContainer.getChildAt(oldIndex);
        final LinearLayout.LayoutParams oldActiveItemParams = (LinearLayout.LayoutParams) oldActiveItem.getLayoutParams();
        ValueAnimator oldItemScaleDown  = ValueAnimator.ofInt(mPaperElementActiveSize, mPaperElementNormalSize);
        oldItemScaleDown.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                oldActiveItemParams.height = (Integer) valueAnimator.getAnimatedValue();
                oldActiveItemParams.width = (Integer) valueAnimator.getAnimatedValue();
                oldActiveItem.requestLayout();
            }
        });

        // fade out old new element icon
        View oldActiveIcon = oldActiveItem.getChildAt(1);
        Animator oldActiveIconFadeOut = ObjectAnimator.ofFloat(oldActiveIcon, "alpha",  1, 0);

        // fade in old element shape
        ImageView oldActiveShape = (ImageView) oldActiveItem.getChildAt(0);
        oldActiveShape.setImageResource(oldIndex - newIndex > 0 ? R.drawable.paper_circle_icon : R.drawable.paper_round_icon);
        Animator oldActiveShapeFadeIn = ObjectAnimator.ofFloat(oldActiveShape, "alpha", 0, PAPER_ICON_SHAPE_ALPHA);

        animations.playTogether(oldItemScaleDown, oldActiveIconFadeOut, oldActiveShapeFadeIn);


        // scale up whole new element
        final ViewGroup newActiveItem = (ViewGroup) paperIconsContainer.getChildAt(newIndex);
        final LinearLayout.LayoutParams newActiveItemParams = (LinearLayout.LayoutParams) newActiveItem.getLayoutParams();
        ValueAnimator newItemScaleUp = ValueAnimator.ofInt(mPaperElementNormalSize, mPaperElementActiveSize);
        newItemScaleUp.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                newActiveItemParams.height = (Integer) valueAnimator.getAnimatedValue();
                newActiveItemParams.width = (Integer) valueAnimator.getAnimatedValue();
                newActiveItem.requestLayout();
            }
        });

        // fade in new element icon
        View newActionIcon = newActiveItem.getChildAt(1);
        Animator newActiveIconFadeIn = ObjectAnimator.ofFloat(newActionIcon, "alpha", 0, 1);

        // fade out new element shape
        ImageView newActiveShape = (ImageView) newActiveItem.getChildAt(0);
        Animator newActiveShapeFadeOut = ObjectAnimator.ofFloat(newActiveShape, "alpha", PAPER_ICON_SHAPE_ALPHA, 0);

        // add
        animations.playTogether(newItemScaleUp, newActiveShapeFadeOut, newActiveIconFadeIn);

        animations.setInterpolator(new DecelerateInterpolator());

        return animations;
    }


//    protected  ImageView createContentIconView(BoardModel item) {
//        ImageView contentIcon = new ImageView(mAppContext);
//
//
//        return contentIcon;
//    }
    protected void toggleContent(boolean prev) {
        int oldElementIndex = mActiveElementIndex;
        BoardModel item = prev ? toggleToPreviousElement() : toggleToNextElement();
        if (item == null) {
            if (prev && mOnLeftOutListener != null)
                mOnLeftOutListener.onLeftOut();
            if (!prev && mOnRightOutListener != null)
                mOnRightOutListener.onRightOut();
            return;
        }

        int newPagerPosX = calculateNewPagerPosition(mActiveElementIndex);

        // animation bg
        AnimatorSet bgAnimation = createBGAnimatorSet(item.getBgColor());

        // animate paper position
        Animator paperMoveAnimation = ObjectAnimator.ofFloat(paperIconsContainer, "x", contentIconContainer.getX(), newPagerPosX);
        paperMoveAnimation.setDuration(ANIM_PAPER_BAR_MOVE_TIME);

        // animate paper icons
        AnimatorSet paperIconAnimation = createPageIconAnimation(oldElementIndex, mActiveElementIndex);

        // animate content text
        ViewGroup newContentText = createContentTextView(item);
        contentTextContainer.addView(newContentText);
        AnimatorSet contentTextShowAnimation = createContentTextShowAnimation(contentTextContainer.getChildAt(contentTextContainer.getChildCount() - 2), newContentText);

        // animate content icon
        ImageView newContentIcon = createContentIconView(item);
        contentTextContainer.addView(newContentIcon);
        AnimatorSet contentIconShowAnimation = createContentIconShowAnimation(contentIconContainer.getChildAt(contentIconContainer.getChildCount() - 2), newContentIcon);

        // animate centering of all content
        Animator centerContentAnimation = createContentCenteringVerticalAnimation(newContentText, newContentIcon);

        centerContentAnimation.start();
        bgAnimation.start();
        paperMoveAnimation.start();
        paperIconAnimation.start();
        contentIconShowAnimation.start();
        contentTextShowAnimation.start();

        if (mOnChangeListener != null)
            mOnChangeListener.onPageChanged(oldElementIndex, mActiveElementIndex);
    }

    protected Animator createContentCenteringVerticalAnimation(View newContentText, View newContentIcon) {
        newContentText.measure(View.MeasureSpec.makeMeasureSpec(contentCenteredContainer.getWidth(), View.MeasureSpec.AT_MOST), -2);
        int measuredContentTextHeight = newContentText.getMeasuredHeight();
        newContentIcon.measure(-2,-2);
        int measuredContentIconHeight = newContentIcon.getMeasuredHeight();

        int newHeightOfContent = measuredContentIconHeight + measuredContentTextHeight + ((ViewGroup.MarginLayoutParams) contentTextContainer.getLayoutParams()).topMargin;
        Animator centerContentAnimation = ObjectAnimator.ofFloat(contentCenteredContainer, "y",contentCenteredContainer.getY(),(contentRootLayout.getHeight() - newHeightOfContent) / 2);
        centerContentAnimation.setDuration(ANIM_CONTENT_CENTERING_TIME);
        centerContentAnimation.setInterpolator(new DecelerateInterpolator());

        return centerContentAnimation;
    }

    protected AnimatorSet createPaperIconAnimation(int oldIndex, int newIndex) {
        AnimatorSet animations = new AnimatorSet();
        animations.setDuration(ANIM_PAPER_ICON_TIME);

        // Scale down whole old element
        final ViewGroup oldActiveItem = (ViewGroup) paperIconsContainer.getChildAt(oldIndex);
        final LinearLayout.LayoutParams oldParams = (LinearLayout.LayoutParams) oldActiveItem.getLayoutParams();
        ValueAnimator oldItemScaleDown = ValueAnimator.ofInt(mPaperElementActiveSize, mPaperElementNormalSize);
        oldItemScaleDown.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                oldParams.height = (Integer)(valueAnimator.getAnimatedValue());
                oldParams.width = (Integer)(valueAnimator.getAnimatedValue());
                oldActiveItem.requestLayout();
            }
        });

        // fade out old new element icon
        View oldActiveIcon = oldActiveItem.getChildAt(1);
        Animator oldActiveIconFadeOut = ObjectAnimator.ofFloat(oldActiveIcon, "alpha", 1, 0);

        // fade in old element shape
        ImageView oldActiveShape = (ImageView) oldActiveItem.getChildAt(0);
        oldActiveShape.setImageResource(oldIndex - newIndex > 0 ? R.drawable.paper_circle_icon : R.drawable.paper_round_icon);

        Animator oldActiveShpeFadeIn = ObjectAnimator.ofFloat(oldActiveShape, "alpha", 0, PAPER_ICON_SHAPE_ALPHA);

        //
        animations.playTogether(oldItemScaleDown, oldActiveIconFadeOut, oldActiveShpeFadeIn);

        // New element
        final ViewGroup newActiveItem = (ViewGroup) paperIconsContainer.getChildAt(newIndex);
        final LinearLayout.LayoutParams newParams = (LinearLayout.LayoutParams) newActiveItem.getLayoutParams();
        ValueAnimator newItemScaleUp = ValueAnimator.ofInt(mPaperElementNormalSize, mPaperElementActiveSize);
        newItemScaleUp.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                newParams.height = (Integer) valueAnimator.getAnimatedValue();
                newParams.width = (Integer) valueAnimator.getAnimatedValue();
                newActiveItem.requestLayout();
            }
        });

        // fade in new element icon
        View newActiveIcon = newActiveItem.getChildAt(1);
        Animator newActiveIconFadeIn = ObjectAnimator.ofFloat(newActiveIcon, "alpha", 0, 1);

        ImageView newAciveShape = (ImageView) newActiveItem.getChildAt(0);
        Animator newActiveShapeFadeOut = ObjectAnimator.ofFloat(newAciveShape, "alpha", PAPER_ICON_SHAPE_ALPHA, 0);

        //
        animations.playTogether(newItemScaleUp, newActiveShapeFadeOut, newActiveIconFadeIn);

        animations.setInterpolator(new DecelerateInterpolator());


        return animations;
    }

    protected ViewGroup createPagerIconElement(int iconDrawableRes, boolean isActive) {
        FrameLayout bottomBarElement = (FrameLayout) LayoutInflater.from(mAppContext).inflate(R.layout.board_pager_layout, paperIconsContainer, false);
        ImageView elementShape = (ImageView) bottomBarElement.getChildAt(0);
        ImageView elementIcon = (ImageView) bottomBarElement.getChildAt(1);
        elementIcon.setImageResource(iconDrawableRes);

        if (isActive) {
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) bottomBarElement.getLayoutParams();
            params.width = paperIconsContainer.getLayoutParams().height;
            params.height = paperIconsContainer.getLayoutParams().height;
            elementShape.setAlpha(0f);
            elementShape.setAlpha(1f);
        } else {
            elementShape.setAlpha(PAPER_ICON_SHAPE_ALPHA);
            elementIcon.setAlpha(0f);
        }

        return bottomBarElement;
    }


    protected ViewGroup createContentTextView(BoardModel item) {
        ViewGroup contentTextView = (ViewGroup) LayoutInflater.from(mAppContext).inflate(R.layout.text_content_layout, contentTextContainer, false);
        TextView contentTitle = (TextView) contentTextView.getChildAt(0);
        contentTitle.setText(item.getTitleText());

        TextView contentText = (TextView) contentTextView.getChildAt(1);
        contentText.setText(item.getDescriptionText());
        return contentTextView;
    }

    protected ImageView createContentIconView(BoardModel item) {
        ImageView contentIcon = new ImageView(mAppContext);
        contentIcon.setImageResource(item.getContentIconRes());
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;

        return contentIcon;
    }
    private int dpToPixels(int dpValue) {
        return (int)(dpValue * dpToPixel * 0.5f);
    }


    public int getmActiveElementIndex() {
        return mActiveElementIndex;
    }
    protected BoardModel getActiveElement() {
        return mElements.size() > mActiveElementIndex ? mElements.get(mActiveElementIndex) : null;
    }

    protected BoardModel toggleToPreviousElement() {
        if (mActiveElementIndex - 1 >= 0) {
            mActiveElementIndex --;
            return mElements.size() > mActiveElementIndex ? mElements.get(mActiveElementIndex) : null;
        }
        return null;
    }

    protected BoardModel toggleToNextElement() {
        if (mActiveElementIndex + 1 < mElements.size()) {
            mActiveElementIndex ++;
            return mElements.size() > mActiveElementIndex ? mElements.get(mActiveElementIndex) : null;
        }
        return null;
    }

    public void setOnChangeListener(PaperBoardingOnChangeListener onChangeListener) {
        this.mOnChangeListener = onChangeListener;
    }

    public void setOnRightOutListener(PaperBoardingOnRightOutListener onRightOutListener) {
        this.mOnRightOutListener = onRightOutListener;
    }

    public void setOnLeftOutListener(PaperBoardingOnLeftOutListener onLeftOutListener) {
        this.mOnLeftOutListener = onLeftOutListener;
    }

}
