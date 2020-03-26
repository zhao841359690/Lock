package com.zhao.lock.ui.activity;


import android.Manifest;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qw.soul.permission.SoulPermission;
import com.qw.soul.permission.bean.Permission;
import com.qw.soul.permission.bean.Permissions;
import com.qw.soul.permission.callbcak.CheckRequestPermissionsListener;
import com.zhao.lock.R;
import com.zhao.lock.base.BaseActivity;
import com.zhao.lock.core.constant.Constants;
import com.zhao.lock.ui.fragment.AboutFragment;
import com.zhao.lock.ui.fragment.BleScanFragment;
import com.zhao.lock.ui.fragment.HomePageFragment;
import com.zhao.lock.ui.fragment.MineFragment;
import com.zhao.lock.ui.fragment.MyTicketFragment;

import butterknife.BindView;
import butterknife.OnClick;

public class MainActivity extends BaseActivity implements HomePageFragment.OnHomePageFragmentClickListener, MineFragment.OnMineFragmentClickListener, BleScanFragment.OnBleScanFragmentClickListener {

    @BindView(R.id.toolbar)
    LinearLayout toolBar;
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
    @BindView(R.id.new_ticket_ly)
    LinearLayout newTicketLy;
    @BindView(R.id.new_ticket_iv)
    ImageView newTicketIv;
    @BindView(R.id.new_ticket_tv)
    TextView newTicketTv;
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
    private String myTicketTitle = "我的工单";

    private BleScanFragment bleScanFragment;

    private int mLastFgIndex = -1;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        getPermission();
        showFragment(Constants.TYPE_HOME_PAGE);
    }

    @OnClick({R.id.home_page_ly, R.id.new_ticket_ly, R.id.mine_ly})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.home_page_ly:
                showFragment(Constants.TYPE_HOME_PAGE);
                break;
            case R.id.new_ticket_ly:
                Intent intent = new Intent(this, NewTicketActivity.class);
                startActivityForResult(intent, Constants.NEW_TICKET);
                break;
            case R.id.mine_ly:
                showFragment(Constants.TYPE_MINE);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.NEW_TICKET && resultCode == Constants.NEW_TICKET) {
            myTicketTitle = "我的工单";
            showFragment(Constants.TYPE_MY_TICKET);
        } else if (requestCode == Constants.NO_TICKET && resultCode == Constants.NO_TICKET) {
            Intent intent = new Intent(this, NewTicketActivity.class);
            startActivityForResult(intent, Constants.NEW_TICKET);
        } else if (requestCode == Constants.SCAN_CODE && resultCode == Constants.SCAN_CODE) {
            if (data != null) {
                String result = data.getStringExtra("result");

                if ("1".equals(result)) {
                    myTicketTitle = "MPDTC-XXXXXX的工单";
                    showFragment(Constants.TYPE_MY_TICKET);
                } else {
                    Intent intent = new Intent(this, NoTicketActivity.class);
                    startActivityForResult(intent, Constants.NO_TICKET);
                }
            }
        }
    }

    @Override
    public void onScanCodeClick() {
        Intent intent = new Intent(this, ScanCodeActivity.class);
        startActivityForResult(intent, Constants.SCAN_CODE);
    }

    @Override
    public void onBleScanClick() {
        showFragment(Constants.TYPE_BLE_SCAN);
    }

    @Override
    public void onPendingClick() {
        myTicketTitle = "我的工单";
        showFragment(Constants.TYPE_MY_TICKET);
    }

    @Override
    public void onMyTicketClick() {
        myTicketTitle = "我的工单";
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

    @Override
    public void onAccessClick(int position) {
        if (position == 0) {
            Intent intent = new Intent(this, NoTicketActivity.class);
            startActivityForResult(intent, Constants.NO_TICKET);
        } else {
            myTicketTitle = "MPDTC-XXXXXX的工单";
            showFragment(Constants.TYPE_MY_TICKET);
        }
    }

    private void showFragment(int index) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        hideFragment(transaction);
        mLastFgIndex = index;
        switch (index) {
            case Constants.TYPE_HOME_PAGE:
                toolBar.setVisibility(View.VISIBLE);
                titleTv.setVisibility(View.VISIBLE);
                titleTv.setText("首页");
                homePageIv.setImageResource(R.drawable.home_pitch_icon);
                if (homePageFragment == null) {
                    homePageFragment = HomePageFragment.newInstance();
                    transaction.add(R.id.fragment_group, homePageFragment);
                }
                homePageFragment.setOnHomePageFragmentClickListener(this);
                transaction.show(homePageFragment);
                break;
            case Constants.TYPE_MINE:
                toolBar.setVisibility(View.GONE);
                mineIv.setImageResource(R.drawable.me_pitch_icon);
                if (mineFragment == null) {
                    mineFragment = MineFragment.newInstance();
                    transaction.add(R.id.fragment_group, mineFragment);
                }
                mineFragment.setOnMineFragmentClickListener(this);
                transaction.show(mineFragment);
                break;
            case Constants.TYPE_ABOUT:
                toolBar.setVisibility(View.VISIBLE);
                titleTv.setText("关于软件");
                if (aboutFragment == null) {
                    aboutFragment = AboutFragment.newInstance();
                    transaction.add(R.id.fragment_group, aboutFragment);
                }
                transaction.show(aboutFragment);
                break;
            case Constants.TYPE_BLE_SCAN:
                toolBar.setVisibility(View.VISIBLE);
                titleTv.setText("BLE扫描-结果");
                if (bleScanFragment == null) {
                    bleScanFragment = BleScanFragment.newInstance();
                    transaction.add(R.id.fragment_group, bleScanFragment);
                }
                bleScanFragment.setOnBleScanFragmentClickListener(this);
                transaction.show(bleScanFragment);
                break;
            case Constants.TYPE_MY_TICKET:
                toolBar.setVisibility(View.VISIBLE);
                titleTv.setText(myTicketTitle);
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
        homePageIv.setImageResource(R.drawable.home_normal_icon);
        mineIv.setImageResource(R.drawable.me_normal_icon);

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
            case Constants.TYPE_BLE_SCAN:
                if (bleScanFragment != null) {
                    transaction.hide(bleScanFragment);
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

    private void getPermission() {
        SoulPermission.getInstance().checkAndRequestPermissions(Permissions.build(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.CAMERA)
                , new CheckRequestPermissionsListener() {
                    @Override
                    public void onAllPermissionOk(Permission[] allPermissions) {

                    }

                    @Override
                    public void onPermissionDenied(Permission[] refusedPermissions) {

                    }
                });
    }
}
