package com.zhao.bank.ui.activity;


import android.Manifest;
import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.qw.soul.permission.SoulPermission;
import com.qw.soul.permission.bean.Permission;
import com.qw.soul.permission.bean.Permissions;
import com.qw.soul.permission.callbcak.CheckRequestPermissionsListener;
import com.zhao.bank.R;
import com.zhao.bank.base.BaseActivity;
import com.zhao.bank.core.constant.Constants;
import com.zhao.bank.ui.fragment.AboutFragment;
import com.zhao.bank.ui.fragment.HomePageFragment;
import com.zhao.bank.ui.fragment.MineFragment;


import butterknife.BindView;
import butterknife.OnClick;

public class MainActivity extends BaseActivity implements HomePageFragment.OnHomePageFragmentClickListener, MineFragment.OnMineFragmentClickListener {
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
                startActivity(intent);
                break;
            case R.id.mine_ly:
                showFragment(Constants.TYPE_MINE);
                break;
        }
    }

    //扫码
    @Override
    public void onScanCodeClick() {
        Toast.makeText(this, "暂无此功能", Toast.LENGTH_SHORT).show();
//        Intent intent = new Intent(this, ScanCodeActivity.class);
//        startActivity(intent);
    }

    //Ble扫描
    @Override
    public void onBleScanClick() {
        Intent intent = new Intent(this, BleScanActivity.class);
        startActivity(intent);
    }

    //待操作
    @Override
    public void onPendingClick() {
        Intent intent = new Intent(this, OrdersActivity.class);
        startActivity(intent);
    }

    //我的工单
    @Override
    public void onMyTicketClick() {
        Intent intent = new Intent(this, OrdersActivity.class);
        startActivity(intent);
    }

    //关于软件
    @Override
    public void onAboutClick() {
        showFragment(Constants.TYPE_ABOUT);
    }

    //退出登录
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
