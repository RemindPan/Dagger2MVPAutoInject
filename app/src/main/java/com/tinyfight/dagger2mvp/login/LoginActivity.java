package com.tinyfight.dagger2mvp.login;

import com.tinyfight.annotation.AutoInject;
import com.tinyfight.dagger2mvp.base.BaseBusinessActivity;

/**
 * Created by tinyfight on 2018/1/24.
 */

@AutoInject(presenter = LoginPresenter.class,contract = LoginContract.class)
public class LoginActivity extends BaseBusinessActivity<LoginContract.Presenter> implements LoginContract.View{
    @Override
    public void showContent() {

    }
}
