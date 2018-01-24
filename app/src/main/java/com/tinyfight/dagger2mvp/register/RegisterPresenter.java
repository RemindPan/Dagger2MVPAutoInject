package com.tinyfight.dagger2mvp.register;

import com.tinyfight.core.base.BaseRxPresenter;
import com.tinyfight.dagger2mvp.base.BaseBusinessPresenter;

/**
 * Created by tinyfight on 2018/1/24.
 */

public class RegisterPresenter extends BaseBusinessPresenter<RegisterContract.View> implements RegisterContract.Presenter {
    public RegisterPresenter(RegisterContract.View view) {
        super(view);
    }

    @Override
    public void getContent() {

    }
}
