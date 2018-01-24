package com.tinyfight.core.base;

import android.support.annotation.CallSuper;

/**
 * Created by katedshan on 17/8/5.
 */

public abstract class BaseRxPresenter implements BasePresenter {
    @Override
    @CallSuper
    public void subscribe() {

    }

    @Override
    @CallSuper
    public void unsubscribe() {

    }
}
