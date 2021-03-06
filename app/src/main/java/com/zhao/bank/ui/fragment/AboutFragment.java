package com.zhao.bank.ui.fragment;

import com.zhao.bank.R;
import com.zhao.bank.base.BaseFragment;

public class AboutFragment extends BaseFragment {

    private static AboutFragment aboutFragment = null;

    public static AboutFragment newInstance() {
        if (aboutFragment == null) {
            synchronized (AboutFragment.class) {
                if (aboutFragment == null) {
                    aboutFragment = new AboutFragment();
                }
            }
        }
        return aboutFragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_about;
    }

    @Override
    protected void initView() {

    }
}
