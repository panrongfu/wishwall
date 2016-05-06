package net.wishwall.rong.fragment;

import android.support.v4.app.Fragment;

import com.sea_monster.exception.BaseException;
import com.sea_monster.network.AbstractHttpRequest;
import com.sea_monster.network.ApiCallback;

/**
 * @author panRongFu on 2016/4/1.
 * @Description
 * @email pan@ipushan.com
 */
public abstract class BaseFragment extends Fragment implements ApiCallback {
    public abstract void onCallApiSuccess(AbstractHttpRequest request, Object obj);

    public abstract void onCallApiFailure(AbstractHttpRequest request, BaseException e);


    @Override
    public void onComplete(final AbstractHttpRequest abstractHttpRequest, final Object o) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                onCallApiSuccess(abstractHttpRequest,o);
            }
        });
    }

    @Override
    public void onFailure(final AbstractHttpRequest abstractHttpRequest, final BaseException e) {
        if(getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    onCallApiFailure(abstractHttpRequest, e);
                }
            });
        }
    }
}
