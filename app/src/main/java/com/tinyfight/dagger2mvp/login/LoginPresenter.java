package com.tinyfight.dagger2mvp.login;

import com.tinyfight.dagger2mvp.base.BaseBusinessPresenter;

/**
 * Created by tinyfight on 2018/1/24.
 */

public class LoginPresenter extends BaseBusinessPresenter<LoginContract.View> implements LoginContract.Presenter {
    public LoginPresenter(LoginContract.View view) {
        super(view);
    }

    @Override
    public void getContent() {
        mView.showContent();
    }
}
