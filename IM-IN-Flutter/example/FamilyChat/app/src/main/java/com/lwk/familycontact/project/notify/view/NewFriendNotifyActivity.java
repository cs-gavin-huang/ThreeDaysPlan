package com.lwk.familycontact.project.notify.view;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lib.base.widget.CommonActionBar;
import com.lwk.familycontact.R;
import com.lwk.familycontact.base.FCBaseActivity;
import com.lwk.familycontact.project.notify.adapter.NewFriendNotifyAdapter;
import com.lwk.familycontact.project.notify.presenter.NewFriendPresenter;
import com.lwk.familycontact.storage.db.invite.InviteBean;
import com.lwk.familycontact.utils.event.ComNotifyConfig;
import com.lwk.familycontact.utils.event.ComNotifyEventBean;
import com.lwk.familycontact.utils.event.EventBusHelper;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

/**
 * 新的好友通知详情界面
 */
public class NewFriendNotifyActivity extends FCBaseActivity implements NewFriendView
{
    private NewFriendPresenter mPresenter;
    private RecyclerView mRecyclerView;
    private NewFriendNotifyAdapter mAdapter;
    private ProgressDialog mDialog;

    @Override
    protected int setContentViewId()
    {
        mPresenter = new NewFriendPresenter(this, mMainHandler);
        EventBusHelper.getInstance().regist(this);
        return R.layout.activity_new_friend_notify;
    }

    @Override
    protected void initUI()
    {
        CommonActionBar actionBar = findView(R.id.cab_new_friend_notify);
        actionBar.setTitleText(R.string.tv_new_friend_notify_title);
        actionBar.setLeftLayoutAsBack(this);
        actionBar.setRightTvText(R.string.tv_new_friend_notify_clear);
        actionBar.setRightLayoutClickListener(this);

        mRecyclerView = findView(R.id.rcv_new_friend_notify);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mAdapter = new NewFriendNotifyAdapter(this, null, mPresenter);
        View emptyView = getLayoutInflater().inflate(R.layout.layout_empty_view
                , (ViewGroup) findViewById(android.R.id.content)
                , false);
        TextView tvEmpty = (TextView) emptyView.findViewById(R.id.tv_empty_view);
        tvEmpty.setText(R.string.tv_warning_friend_notify_empty);
        mAdapter.setEmptyView(emptyView);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void initData()
    {
        super.initData();
        mPresenter.refreshAllNotify();
    }

    @Override
    protected void onClick(int id, View v)
    {
        switch (id)
        {
            case R.id.fl_common_actionbar_right:
                clearAllNotify();
                break;
        }
    }

    @Override
    public void onRefreshAllNotifySuccess(List<InviteBean> list)
    {
        mAdapter.refreshDatas(list);
    }

    @Override
    public void showHandlingDialog()
    {
        mMainHandler.post(new Runnable()
        {
            @Override
            public void run()
            {
                mDialog = new ProgressDialog(NewFriendNotifyActivity.this);
                mDialog.setCancelable(false);
                mDialog.show();
            }
        });
    }

    @Override
    public void closeHandingDialog()
    {
        if (mDialog != null)
        {
            mMainHandler.post(new Runnable()
            {
                @Override
                public void run()
                {
                    mDialog.dismiss();
                    mDialog = null;
                }
            });
        }
    }

    @Override
    public void showHandlingError(int status, int errResId)
    {
        showShortToast(errResId);
    }

    @Override
    public void onNotifyStatusChanged()
    {
        mMainHandler.post(new Runnable()
        {
            @Override
            public void run()
            {
                if (mAdapter != null)
                    mAdapter.notifyDataSetChanged();
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNotifyEventReceived(ComNotifyEventBean eventBean)
    {
        switch (eventBean.getFlag())
        {
            case ComNotifyConfig.REFRESH_USER_INVITE:
                mPresenter.refreshAllNotify();
                break;
        }
    }

    //清空通知，二次确认
    private void clearAllNotify()
    {
        new AlertDialog.Builder(this)
                .setCancelable(true)
                .setTitle(R.string.dialog_new_friend_notify_clear_title)
                .setMessage(R.string.dialog_new_friend_notify_clear_message)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton(R.string.confrim, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.dismiss();
                        mPresenter.clearAllNotify();
                    }
                }).create().show();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        EventBusHelper.getInstance().unregist(this);
        mPresenter.markAllNotifyAsRead();
    }
}
