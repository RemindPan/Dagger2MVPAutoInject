package com.tinyfight.dagger2mvp.base;

import com.tinyfight.core.base.BaseRxPresenter;
import com.tinyfight.core.base.BaseView;

/**
 * Created by tinyfight on 2018/1/24.
 */

public class BaseBusinessPresenter<T extends BaseView> extends BaseRxPresenter {
    protected T mView;

    public BaseBusinessPresenter(T view) {
        mView = view;
    }
}
