package com.zhao.lock.ui.fragment;

import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zhao.lock.R;
import com.zhao.lock.base.BaseFragment;
import com.zhao.lock.bean.UserInfoBean;
import com.zhao.lock.core.constant.Constants;
import com.zhao.lock.util.SharedPreferencesUtils;

import butterknife.BindView;
import butterknife.OnClick;
import rxhttp.wrapper.param.RxHttp;

public class MineFragment extends BaseFragment {

    @BindView(R.id.head_iv)
    ImageView headIv;
    @BindView(R.id.name_tv)
    TextView nameTv;
    @BindView(R.id.my_ticket_tv)
    TextView myTicketTv;
    @BindView(R.id.about_rl)
    RelativeLayout aboutRl;
    @BindView(R.id.sign_out_tv)
    TextView signOutTv;

    private OnMineFragmentClickListener onMineFragmentClickListener;

    public void setOnMineFragmentClickListener(OnMineFragmentClickListener onMineFragmentClickListener) {
        this.onMineFragmentClickListener = onMineFragmentClickListener;
    }

    public interface OnMineFragmentClickListener {
        void onMyTicketClick();

        void onAboutClick();

        void onSignOutClick();
    }

    public static MineFragment newInstance() {
        return new MineFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_mine;
    }

    @Override
    protected void initView() {
        RxHttp.get(Constants.BASE_URL + "/app/userInfo")
                .add("token", SharedPreferencesUtils.getInstance().getToken())
                .asClass(UserInfoBean.class)
                .subscribe(userInfoBean -> {
                    if (userInfoBean.getCode() == 200) {
                        nameTv.setText(userInfoBean.getData().getUsername());
                    }
                }, throwable -> {
                });
    }

    @OnClick({R.id.my_ticket_tv, R.id.about_rl, R.id.sign_out_tv})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.my_ticket_tv:
                onMineFragmentClickListener.onMyTicketClick();
                break;
            case R.id.about_rl:
                onMineFragmentClickListener.onAboutClick();
                break;
            case R.id.sign_out_tv:
                onMineFragmentClickListener.onSignOutClick();
                break;
            default:
                break;
        }
    }
}
