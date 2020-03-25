package com.zhao.lock.ui.fragment;

import android.text.Html;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zhao.lock.R;
import com.zhao.lock.base.BaseFragment;

import butterknife.BindView;
import butterknife.OnClick;

public class HomePageFragment extends BaseFragment {

    @BindView(R.id.scan_code_ly)
    LinearLayout scanCodeLy;
    @BindView(R.id.ble_scan_ly)
    LinearLayout bleScanLy;
    @BindView(R.id.pending_ly)
    LinearLayout pendingLy;
    @BindView(R.id.pending_tv)
    TextView pendingTv;
    @BindView(R.id.ticket_number_tv)
    TextView ticketNumberTv;

    private OnHomePageFragmentClickListener onHomePageFragmentClickListener;

    public void setOnHomePageFragmentClickListener(OnHomePageFragmentClickListener onHomePageFragmentClickListener) {
        this.onHomePageFragmentClickListener = onHomePageFragmentClickListener;
    }

    public interface OnHomePageFragmentClickListener {
        void onScanCodeClick();

        void onBleScanClick();

        void onPendingClick();
    }


    public static HomePageFragment newInstance() {
        return new HomePageFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_home_page;
    }

    @Override
    protected void initView() {
        pendingTv.setText(Html.fromHtml("您有一条<font color='#0E5EAB'>[待操作]</font>的订单"));
    }

    @OnClick({R.id.scan_code_ly, R.id.ble_scan_ly, R.id.pending_ly})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.scan_code_ly:
                onHomePageFragmentClickListener.onScanCodeClick();
                break;
            case R.id.ble_scan_ly:
                onHomePageFragmentClickListener.onBleScanClick();
                break;
            case R.id.pending_ly:
                onHomePageFragmentClickListener.onPendingClick();
                break;
            default:
                break;
        }
    }
}
