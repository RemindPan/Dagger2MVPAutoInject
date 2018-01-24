package com.tinyfight.dagger2mvp.base;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.tinyfight.android.di.BaseAutoInjectActivity;
import com.tinyfight.core.base.BasePresenter;
import com.tinyfight.core.base.BaseView;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by tinyfight on 2018/1/24.
 */

public class BaseBusinessActivity<T extends BasePresenter> extends BaseAutoInjectActivity implements BaseView<T> {
    @Inject
    protected T mPresenter;

    private Unbinder mUnBinder;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if (mPresenter != null) mPresenter.subscribe();
        super.onCreate(savedInstanceState);
        mUnBinder = ButterKnife.bind(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mUnBinder.unbind();
        if (mPresenter != null) mPresenter.unsubscribe();
    }
}
