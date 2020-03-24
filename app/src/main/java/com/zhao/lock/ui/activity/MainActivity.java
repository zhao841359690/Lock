package com.zhao.lock.ui.activity;


import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zhao.lock.R;
import com.zhao.lock.base.BaseActivity;
import com.zhao.lock.core.constant.Constants;
import com.zhao.lock.ui.fragment.AboutFragment;
import com.zhao.lock.ui.fragment.HomePageFragment;
import com.zhao.lock.ui.fragment.MineFragment;
import com.zhao.lock.ui.fragment.MyTicketFragment;

import butterknife.BindView;
import butterknife.OnClick;

public class MainActivity extends BaseActivity implements HomePageFragment.OnHomePageFragmentClickListener, MineFragment.OnMineFragmentClickListener {

    @BindView(R.id.title_tv)
    TextView titleTv;
    @BindView(R.id.fragment_group)
    FrameLayout fragmentGroup;
    @BindView(R.id.home_page_ly)
    LinearLayout homePageLy;
    @BindView(R.id.home_page_iv)
    ImageView homePageIv;
    @BindView(R.id.home_page_tv)
    TextView homePageTv;
    @BindView(R.id.mine_ly)
    LinearLayout mineLy;
    @BindView(R.id.mine_iv)
    ImageView mineIv;
    @BindView(R.id.mine_tv)
    TextView mineTv;

    private HomePageFragment homePageFragment;
    private MineFragment mineFragment;
    private AboutFragment aboutFragment;
    private MyTicketFragment myTicketFragment;

    private int mLastFgIndex = -1;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        showFragment(Constants.TYPE_HOME_PAGE);
    }

    @OnClick({R.id.home_page_ly, R.id.mine_ly})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.home_page_ly:
                showFragment(Constants.TYPE_HOME_PAGE);
                break;
            case R.id.mine_ly:
                showFragment(Constants.TYPE_MINE);
                break;
        }
    }

    @Override
    public void onScanCodeClick() {

    }

    @Override
    public void onBleScanClick() {

    }

    @Override
    public void onPendingClick() {
        showFragment(Constants.TYPE_MY_TICKET);
    }

    @Override
    public void onMyTicketClick() {
        showFragment(Constants.TYPE_MY_TICKET);
    }

    @Override
    public void onAboutClick() {
        showFragment(Constants.TYPE_ABOUT);
    }

    @Override
    public void onSignOutClick() {
        finish();
    }

    private void showFragment(int index) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        hideFragment(transaction);
        mLastFgIndex = index;
        switch (index) {
            case Constants.TYPE_HOME_PAGE:
                titleTv.setText("首页");
                if (homePageFragment == null) {
                    homePageFragment = HomePageFragment.newInstance();
                    transaction.add(R.id.fragment_group, homePageFragment);
                }
                homePageFragment.setOnHomePageFragmentClickListener(this);
                transaction.show(homePageFragment);
                break;
            case Constants.TYPE_MINE:
                if (mineFragment == null) {
                    mineFragment = MineFragment.newInstance();
                    transaction.add(R.id.fragment_group, mineFragment);
                }
                mineFragment.setOnMineFragmentClickListener(this);
                transaction.show(mineFragment);
                break;
            case Constants.TYPE_ABOUT:
                titleTv.setText("关于软件");
                if (aboutFragment == null) {
                    aboutFragment = AboutFragment.newInstance();
                    transaction.add(R.id.fragment_group, aboutFragment);
                }
                transaction.show(aboutFragment);
                break;
            case Constants.TYPE_MY_TICKET:
                titleTv.setText("我的工单");
                if (myTicketFragment == null) {
                    myTicketFragment = MyTicketFragment.newInstance();
                    transaction.add(R.id.fragment_group, myTicketFragment);
                }
                transaction.show(myTicketFragment);
                break;
            default:
                break;
        }
        transaction.commit();
    }

    private void hideFragment(FragmentTransaction transaction) {
        switch (mLastFgIndex) {
            case Constants.TYPE_HOME_PAGE:
                if (homePageFragment != null) {
                    transaction.hide(homePageFragment);
                }
                break;
            case Constants.TYPE_MINE:
                if (mineFragment != null) {
                    transaction.hide(mineFragment);
                }
                break;
            case Constants.TYPE_ABOUT:
                if (aboutFragment != null) {
                    transaction.hide(aboutFragment);
                }
                break;
            case Constants.TYPE_MY_TICKET:
                if (myTicketFragment != null) {
                    transaction.hide(myTicketFragment);
                }
                break;
            default:
                break;
        }
    }
}
