package com.zhao.lock.ui.fragment;

import android.text.Html;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zhao.lock.R;
import com.zhao.lock.base.BaseFragment;

import butterknife.BindView;

public class AboutFragment extends BaseFragment {


    public static AboutFragment newInstance() {
        return new AboutFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_about;
    }

    @Override
    protected void initView() {

    }
}
