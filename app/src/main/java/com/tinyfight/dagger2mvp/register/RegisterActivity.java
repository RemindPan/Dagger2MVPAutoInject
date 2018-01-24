package com.tinyfight.dagger2mvp.register;

import com.tinyfight.annotation.AutoInject;
import com.tinyfight.dagger2mvp.base.BaseBusinessActivity;

/**
 * Created by tinyfight on 2018/1/24.
 */

@AutoInject(presenter = RegisterPresenter.class,contract = RegisterContract.class)
public class RegisterActivity extends BaseBusinessActivity<RegisterContract.Presenter> implements RegisterContract.View {
    @Override
    public void showContent() {

    }
}
