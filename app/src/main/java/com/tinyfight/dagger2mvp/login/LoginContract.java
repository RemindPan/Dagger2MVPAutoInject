package com.tinyfight.dagger2mvp.login;

import com.tinyfight.core.base.BasePresenter;
import com.tinyfight.core.base.BaseView;

/**
 * Created by tinyfight on 2018/1/24.
 */

public class LoginContract {
    public interface View extends BaseView<Presenter> {
        void showContent();
    }

    public interface Presenter extends BasePresenter {
        void getContent();
    }
}
