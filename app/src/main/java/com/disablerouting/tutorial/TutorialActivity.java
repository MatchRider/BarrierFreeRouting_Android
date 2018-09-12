package com.disablerouting.tutorial;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.disablerouting.R;
import com.disablerouting.base.BaseActivityImpl;
import com.disablerouting.home.HomeActivity;

public class TutorialActivity extends BaseActivityImpl implements ViewPager.OnPageChangeListener {


    @BindView(R.id.view_pager_tutorial)
    ViewPager mViewPager;

    @BindView(R.id.layoutDots)
    LinearLayout mLinearLayoutDots;

    private int[] mLayouts;
    private ImageView[] mDots;
    private int dotsCount;
    private TutorialPagerAdapter mTutorialPagerAdapter;
    private TutorialPrefManager mTutorialPrefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTutorialPrefManager = new TutorialPrefManager(this);
        if (!mTutorialPrefManager.isFirstTimeLaunch()) {
            launchHomeScreen();
            finish();
        }

        changeStatusBarColor();

        setContentView(R.layout.activity_tutorial);
        ButterKnife.bind(this);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        createTutorialPages();

        mTutorialPagerAdapter = new TutorialPagerAdapter(this, mLayouts);
        mViewPager.setAdapter(mTutorialPagerAdapter);
        mViewPager.setCurrentItem(0);
        mViewPager.addOnPageChangeListener(this);

        addPageViewIndicator();
    }

    /**
     * Tutorial pages
     */
    private void createTutorialPages() {
        mLayouts = new int[]{
                R.layout.tutorial_page1,
                R.layout.tutorial_page2,
                R.layout.tutorial_page3,
                R.layout.tutorial_page4,
                R.layout.tutorial_page5,
                R.layout.tutorial_page6,
                R.layout.tutorial_page7,
                R.layout.tutorial_page8,
                R.layout.tutorial_page9,
                R.layout.tutorial_page10};
    }


    /**
     * Making notification bar transparent
     */
    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    /**
     * Add circle pager indicator
     */
    private void addPageViewIndicator() {
        dotsCount = mTutorialPagerAdapter.getCount();
        mDots = new ImageView[dotsCount];

        for (int i = 0; i < dotsCount; i++) {
            mDots[i] = new ImageView(this);
            mDots[i].setImageDrawable(getResources().getDrawable(R.drawable.nonselecteditem_dot));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(4, 0, 4, 0);
            mLinearLayoutDots.addView(mDots[i], params);
        }

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        for (int i = 0; i < dotsCount; i++) {
            mDots[i].setImageDrawable(getResources().getDrawable(R.drawable.nonselecteditem_dot));
        }
        mDots[position].setImageDrawable(getResources().getDrawable(R.drawable.selecteditem_dot));
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    /**
     * Launch home screen after introduction tutorial section
     */
    private void launchHomeScreen() {
        mTutorialPrefManager.setFirstTimeLaunch(false);
        launchActivity(this,HomeActivity.class);
    }


    @OnClick(R.id.btn_skip)
    public void onClickSkip() {
        launchHomeScreen();
        finish();
    }



}



