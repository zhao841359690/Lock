package com.zhao.bank.ui.fragment;

import android.support.v7.widget.CardView;
import android.text.Html;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zhao.bank.R;
import com.zhao.bank.base.BaseFragment;
import com.zhao.bank.bean.TodoOrdersBean;
import com.zhao.bank.util.SharedPreferencesUtils;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import rxhttp.wrapper.param.RxHttp;

public class HomePageFragment extends BaseFragment {
    @BindView(R.id.scan_code_ly)
    LinearLayout scanCodeLy;
    @BindView(R.id.ble_scan_ly)
    LinearLayout bleScanLy;
    @BindView(R.id.pending_cv)
    CardView pendingCv;
    @BindView(R.id.pending_tv)
    TextView pendingTv;
    @BindView(R.id.ticket_number_tv)
    TextView ticketNumberTv;

    private static HomePageFragment homePageFragment = null;

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
        if (homePageFragment == null) {
            synchronized (HomePageFragment.class) {
                if (homePageFragment == null) {
                    homePageFragment = new HomePageFragment();
                }
            }
        }
        return homePageFragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_home_page;
    }

    @Override
    protected void initView() {
        initTodoOrdersData();
    }

    @OnClick({R.id.scan_code_ly, R.id.ble_scan_ly, R.id.pending_cv})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.scan_code_ly:
                onHomePageFragmentClickListener.onScanCodeClick();
                break;
            case R.id.ble_scan_ly:
                onHomePageFragmentClickListener.onBleScanClick();
                break;
            case R.id.pending_cv:
                onHomePageFragmentClickListener.onPendingClick();
                break;
            default:
                break;
        }
    }

    private void initTodoOrdersData() {
        RxHttp.get("/app/todoOrders")
                .add("token", SharedPreferencesUtils.getInstance().getToken())
                .asClass(TodoOrdersBean.class)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(todoOrdersBean -> {
                    if (todoOrdersBean.getCode() == 200 && todoOrdersBean.getData() != null && todoOrdersBean.getData().size() > 0) {
                        pendingCv.setVisibility(View.VISIBLE);
                        pendingTv.setText(Html.fromHtml("您有" + todoOrdersBean.getData().size() + "条<font color='#0E5EAB'>[待操作]</font>的订单"));
                        ticketNumberTv.setText("工单编号:" + todoOrdersBean.getData().get(0).getWorkId());
                    } else {
                        pendingCv.setVisibility(View.GONE);
                    }
                }, throwable -> {
                    pendingCv.setVisibility(View.GONE);
                });
    }
}
