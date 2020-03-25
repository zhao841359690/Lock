package com.zhao.lock.ui.fragment;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.zhao.lock.R;
import com.zhao.lock.app.BaseApp;
import com.zhao.lock.base.BaseFragment;
import com.zhao.lock.util.GlideCircleTransform;

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
        Glide.with(BaseApp.getContext()).load(R.mipmap.ic_launcher)
                .apply(new RequestOptions().error(BaseApp.getContext().getDrawable(R.mipmap.ic_launcher)))
                .placeholder(R.mipmap.ic_launcher).centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL)
                .transform(new GlideCircleTransform(6, BaseApp.getContext().getColor(R.color.head)))
                .into(headIv);
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
