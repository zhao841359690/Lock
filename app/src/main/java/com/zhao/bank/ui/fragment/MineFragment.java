package com.zhao.bank.ui.fragment;

import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zhao.bank.R;
import com.zhao.bank.base.BaseFragment;
import com.zhao.bank.util.SharedPreferencesUtils;

import butterknife.BindView;
import butterknife.OnClick;

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
    private static MineFragment mineFragment = null;

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
        if (mineFragment == null) {
            synchronized (MineFragment.class) {
                if (mineFragment == null) {
                    mineFragment = new MineFragment();
                }
            }
        }
        return mineFragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_mine;
    }

    @Override
    protected void initView() {
        nameTv.setText(SharedPreferencesUtils.getInstance().getUserName());
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
