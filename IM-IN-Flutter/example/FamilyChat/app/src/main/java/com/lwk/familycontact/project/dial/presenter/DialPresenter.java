package com.lwk.familycontact.project.dial.presenter;

import android.os.AsyncTask;
import android.util.Pair;

import com.lib.base.utils.StringUtil;
import com.lwk.familycontact.project.common.FCCallBack;
import com.lwk.familycontact.project.dial.task.DialSearchTask;
import com.lwk.familycontact.project.dial.view.DialView;
import com.lwk.familycontact.storage.db.user.UserBean;

import java.util.List;

/**
 * Created by LWK
 * TODO 拨号盘片段Presenter
 * 2016/8/19
 */
public class DialPresenter
{
    private DialView mDialView;
    private DialSearchTask mSearchTask;

    public DialPresenter(DialView dialView)
    {
        this.mDialView = dialView;
    }

    public void searchUsers(final String phone)
    {
        if (StringUtil.isEmpty(phone))
        {
            mDialView.resetSearchResult();
            mDialView.closeAddContact();
        } else
        {
            if (mSearchTask != null && mSearchTask.getStatus() != AsyncTask.Status.FINISHED)
            {
                mSearchTask.cancel(true);
                mSearchTask = null;
            }

            mSearchTask = new DialSearchTask(phone, new FCCallBack<Pair<Boolean, List<UserBean>>>()
            {
                @Override
                public void onFail(int status, int errorMsgResId)
                {
                    mDialView.resetSearchResult();
                    mDialView.closeAddContact();
                }

                @Override
                public void onSuccess(Pair<Boolean, List<UserBean>> pair)
                {
                    boolean hasUser = pair.first;
                    if (hasUser)
                        mDialView.closeAddContact();
                    else
                        mDialView.showAddContact(phone);

                    List<UserBean> resultList = pair.second;
                    if (resultList == null || resultList.size() == 0)
                        mDialView.onSearchResultEmpty(phone);
                    else
                        mDialView.onSearchResultSuccess(resultList);
                }
            });

            mSearchTask.execute();
        }
    }
}
