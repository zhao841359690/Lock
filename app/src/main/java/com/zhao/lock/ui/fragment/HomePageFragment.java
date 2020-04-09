package com.zhao.lock.ui.fragment;

import android.support.v7.widget.CardView;
import android.text.Html;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zhao.lock.R;
import com.zhao.lock.base.BaseFragment;
import com.zhao.lock.bean.TodoOrdersBean;
import com.zhao.lock.core.constant.Constants;
import com.zhao.lock.util.SharedPreferencesUtils;

import butterknife.BindView;
import butterknife.OnClick;
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
        RxHttp.get(Constants.BASE_URL + "/app/todoOrders")
                .add("token", SharedPreferencesUtils.getInstance().getToken())
                .asClass(TodoOrdersBean.class)
                .subscribe(todoOrdersBean -> {
                    if (todoOrdersBean.getCode() == 200 && todoOrdersBean.getData() != null && todoOrdersBean.getData().size() > 0) {
                        pendingCv.setVisibility(View.VISIBLE);
                        pendingTv.setText(Html.fromHtml("您有" + todoOrdersBean.getData().size() + "条<font color='#0E5EAB'>[待操作]</font>的订单"));
                    } else {
                        pendingCv.setVisibility(View.GONE);
                    }
                }, throwable -> {
                    pendingCv.setVisibility(View.GONE);
                });

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
}
