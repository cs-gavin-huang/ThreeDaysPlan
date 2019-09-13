package com.lwk.familycontact.project.contact.presenter;

import android.content.Context;
import android.os.AsyncTask;

import com.lwk.familycontact.project.common.FCCallBack;
import com.lwk.familycontact.project.contact.model.ContactModel;
import com.lwk.familycontact.project.contact.task.RefreshContactDataTask;
import com.lwk.familycontact.project.contact.view.ContactView;
import com.lwk.familycontact.storage.db.user.UserBean;
import com.lwk.familycontact.utils.other.ThreadManager;

import java.util.List;
import java.util.concurrent.Executors;

/**
 * Created by LWK
 * TODO 通讯录界面Presenter
 * 2016/8/9
 */
public class ContactPresenter
{
    private ContactView mContactView;
    private ContactModel mModel;
    private RefreshContactDataTask mRefreshContactDataTask;

    public ContactPresenter(ContactView contactView)
    {
        this.mContactView = contactView;
        mModel = new ContactModel();
    }

    /**
     * 初始化数据
     */
    public void initData()
    {
        if (mModel.needAutoRefresh())
            mContactView.autoRefresh();
        else
            refreshContactDataInDb(false);
    }

    /**
     * 刷新所有通讯录数据[环信好友+本机通讯录]
     *
     * @param context 上下文环境
     */
    public void refreshAllContactData(Context context)
    {
        if (mRefreshContactDataTask != null && mRefreshContactDataTask.getStatus() != AsyncTask.Status.FINISHED)
        {
            mRefreshContactDataTask.cancel(true);
            mRefreshContactDataTask = null;
        }

        mRefreshContactDataTask = new RefreshContactDataTask(context, mModel, new FCCallBack<List<UserBean>>()
        {
            @Override
            public void onFail(int status, int errorMsgResId)
            {
                mContactView.refreshAllUsersFail(errorMsgResId);
                mContactView.refreshContactNum();
            }

            @Override
            public void onSuccess(List<UserBean> resultList)
            {
                mModel.syncAutoRefreshTime();
                mContactView.refreshAllUsersSuccess(true, resultList);
                mContactView.refreshContactNum();
            }
        });
        mRefreshContactDataTask.executeOnExecutor(Executors.newCachedThreadPool());
    }

    /**
     * 刷新数据库中好友数据
     * [不获取系统通讯录中好友]
     */
    public void refreshContactDataInDb(final boolean isPtrRefresh)
    {
        ThreadManager.getInstance().addNewRunnable(new Runnable()
        {
            @Override
            public void run()
            {
                mContactView.refreshAllUsersSuccess(isPtrRefresh, mModel.getContactDataInDb());
                mContactView.refreshContactNum();
            }
        });
    }

    /**
     * 刷新好友通知
     */
    public void refreshFriendNotify()
    {
        int unreadNum = mModel.getUnreadFriendNotifyNum();
        if (unreadNum == 0)
            mContactView.onAllFriendNotifyRead();
        else
            mContactView.onFriendNotifyUnread(unreadNum);
    }
}
