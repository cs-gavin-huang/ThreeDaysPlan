package com.lwk.familycontact.project.contact.view;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.cengalabs.flatui.views.FlatButton;
import com.cengalabs.flatui.views.FlatTextView;
import com.lib.base.utils.PhoneUtils;
import com.lib.base.utils.StringUtil;
import com.lib.base.widget.CommonActionBar;
import com.lib.imagepicker.ImagePicker;
import com.lib.imagepicker.ImagePickerOptions;
import com.lib.imagepicker.bean.ImageBean;
import com.lib.imagepicker.model.ImagePickerMode;
import com.lwk.familycontact.R;
import com.lwk.familycontact.base.FCBaseActivity;
import com.lwk.familycontact.project.call.view.HxVideoCallActivity;
import com.lwk.familycontact.project.call.view.HxVoiceCallActivity;
import com.lwk.familycontact.project.chat.view.HxChatActivity;
import com.lwk.familycontact.project.common.CommonUtils;
import com.lwk.familycontact.project.common.FCCache;
import com.lwk.familycontact.project.contact.presenter.ContactDetailPresenter;
import com.lwk.familycontact.storage.db.user.UserBean;
import com.lwk.familycontact.utils.event.EventBusHelper;
import com.lwk.familycontact.utils.event.ProfileUpdateEventBean;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

/**
 * 联系人资料详情界面
 */
@RuntimePermissions
public class ContactDetailActivity extends FCBaseActivity implements UserDetailView, UpdateNameAppDialog.onNameAppUpdateListener
{
    private static final String INTENT_KEY = "user_data_key";
    private ContactDetailPresenter mPresenter;
    private UserBean mUserBean;
    private CommonActionBar mActionBar;
    private ImageView mImgHead;
    private FlatTextView mTvName;
    private FlatTextView mTvPhone;
    private TextView mTvNonFriendHint;
    private FlatButton mBtnChat;
    private FlatButton mBtnVoiceCall;
    private FlatButton mBtnVideoCall;

    /**
     * 跳转到该界面的公用方法
     *
     * @param activity 跳转前的activity
     * @param userBean 传递的用户数据对象
     */
    public static void skip(Activity activity, UserBean userBean)
    {
        Intent intent = new Intent(activity, ContactDetailActivity.class);
        intent.putExtra(INTENT_KEY, userBean);
        activity.startActivity(intent);
    }

    @Override
    protected void beforeOnCreate(Bundle savedInstanceState)
    {
        super.beforeOnCreate(savedInstanceState);
        mUserBean = getIntent().getParcelableExtra(INTENT_KEY);
        mPresenter = new ContactDetailPresenter(this);
        EventBusHelper.getInstance().regist(this);
    }

    @Override
    protected int setContentViewId()
    {
        return R.layout.activity_contact_detail;
    }

    @Override
    protected void initUI()
    {
        mActionBar = findView(R.id.cab_contact_detail);
        mActionBar.setLeftLayoutAsBack(this);
        mImgHead = findView(R.id.img_contact_detail_head);
        mTvName = findView(R.id.tv_contact_detail_name);
        mTvPhone = findView(R.id.tv_contact_detail_phone);
        mTvNonFriendHint = findView(R.id.tv_contact_detail_non_friend_hint);
        mBtnChat = findView(R.id.btn_contact_detail_send_msg);
        mBtnVoiceCall = findView(R.id.btn_contact_detail_voice_call);
        mBtnVideoCall = findView(R.id.btn_contact_detail_video_call);

        Drawable drawable = getResources().getDrawable(R.drawable.ic_update);
        int size = getResources().getDimensionPixelSize(R.dimen.dp_25);
        drawable.setBounds(0, 0, size, size);
        mTvName.setCompoundDrawables(null, null, drawable, null);

        addClick(mImgHead);
        addClick(mTvName);
        addClick(mBtnChat);
        addClick(mBtnVoiceCall);
        addClick(mBtnVideoCall);
        addClick(R.id.btn_contact_detail_system_call);
    }

    @Override
    protected void initData()
    {
        super.initData();
        mPresenter.initData(mUserBean);
        mPresenter.checkIfFirstEnter();
    }

    @Override
    public void setDefaultHead()
    {
        if (mImgHead != null)
            mImgHead.setImageResource(R.drawable.default_avatar);
    }

    @Override
    public void setHead(String url)
    {
        if (mImgHead != null)
            CommonUtils.getInstance().getImageDisplayer()
                    .display(this, mImgHead, url, 300, 300, R.drawable.default_avatar, R.drawable.default_avatar);
    }

    @Override
    public void setName(String name)
    {
        if (mActionBar != null)
            mActionBar.setTitleText(name);
        if (mTvName != null)
            mTvName.setText(name);
    }

    @Override
    public void setPhone(String phone)
    {
        if (mTvPhone != null)
            mTvPhone.setText(phone);
    }

    @Override
    public void nonFriend()
    {
        if (mTvNonFriendHint != null)
            mTvNonFriendHint.setVisibility(View.VISIBLE);
        if (mBtnChat != null)
            mBtnChat.setEnabled(false);
        if (mBtnVoiceCall != null)
            mBtnVoiceCall.setEnabled(false);
        if (mBtnVideoCall != null)
            mBtnVideoCall.setEnabled(false);
    }

    @Override
    protected void onClick(int id, View v)
    {
        switch (id)
        {
            case R.id.img_contact_detail_head:
                if (mUserBean == null)
                    return;

                ImagePickerOptions options = new ImagePickerOptions.Builder()
                        .pickMode(ImagePickerMode.SINGLE)
                        .cachePath(FCCache.getInstance().getUserHeadCachePath())
                        .needCrop(true)
                        .showCamera(true)
                        .build();
                ImagePicker.getInstance().pickWithOptions(this, options, new ImagePicker.OnSelectedListener()
                {
                    @Override
                    public void onSelected(List<ImageBean> list)
                    {
                        if (list != null && list.size() > 0)
                            mPresenter.updateUserLocalHead(mUserBean.getPhone(), list.get(0));
                    }
                });
                break;
            case R.id.tv_contact_detail_name:
                //修改的是app内备注名,所以传入的是原始app备注名
                UpdateNameAppDialog dialog = new UpdateNameAppDialog(this, mUserBean.getNameApp());
                dialog.setOnNameAppUpdateListener(this);
                dialog.show();
                break;
            case R.id.btn_contact_detail_system_call:
                ContactDetailActivityPermissionsDispatcher.callSystemPhoneWithCheck(this);
                break;
            case R.id.btn_contact_detail_voice_call:
                HxVoiceCallActivity.start(this, mUserBean.getPhone(), false);
                break;
            case R.id.btn_contact_detail_send_msg:
                HxChatActivity.start(this, mUserBean.getPhone(), mUserBean);
                break;
            case R.id.btn_contact_detail_video_call:
                HxVideoCallActivity.start(this, mUserBean.getPhone(), false);
                break;
        }
    }

    @Override
    public void onNameAppUpdated(String name)
    {
        if (mUserBean != null)
        {
            if (StringUtil.isEquals(name, mUserBean.getNameApp()))
                return;

            mUserBean.setNameApp(name);
            mPresenter.updateUserNameApp(mUserBean);
        }
    }

    @NeedsPermission(Manifest.permission.CALL_PHONE)
    public void callSystemPhone()
    {
        if (mUserBean != null && StringUtil.isNotEmpty(mUserBean.getPhone()))
            PhoneUtils.callPhone(this, mUserBean.getPhone());
    }

    @OnShowRationale(Manifest.permission.CALL_PHONE)
    public void showRationaleForCallPhone(final PermissionRequest request)
    {
        new AlertDialog.Builder(this)
                .setCancelable(false)
                .setTitle(R.string.dialog_permission_title)
                .setMessage(R.string.dialog_permission_call_phone_message)
                .setPositiveButton(R.string.dialog_confirm, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        request.proceed();
                    }
                }).create().show();
    }

    @OnPermissionDenied(Manifest.permission.CALL_PHONE)
    public void onCallPhonePermissionDenied()
    {
        showLongToast(R.string.warning_permission_callphone_denied);
    }

    @OnNeverAskAgain(Manifest.permission.CALL_PHONE)
    public void onCallPhonePermissionNeverAsk()
    {
        new AlertDialog.Builder(this).setCancelable(false)
                .setTitle(R.string.warning_permission_callphone_denied)
                .setMessage(R.string.dialog_permission_call_phone_nerver_ask_message)
                .setNegativeButton(R.string.dialog_imagepicker_permission_nerver_ask_cancel, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        showLongToast(R.string.warning_permission_callphone_denied);
                        dialog.dismiss();
                    }
                })
                .setPositiveButton(R.string.dialog_permission_nerver_ask_confirm, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.dismiss();
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_SETTINGS);
                        startActivity(intent);
                    }
                }).create().show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        ContactDetailActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        EventBusHelper.getInstance().unregist(this);
    }

    @Override
    public void showFirstEnterDialog()
    {
        new AlertDialog.Builder(this)
                .setMessage(R.string.dialog_contact_detail_first_use_hint)
                .setPositiveButton(R.string.dialog_confirm, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.dismiss();
                    }
                }).create().show();
    }

    @Override
    public void updateLocalHeadFail()
    {
        showShortToast(R.string.error_unknow);
    }

    @Override
    public void updateNameFail()
    {
        showShortToast(R.string.error_unknow);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void userProfileUpdated(ProfileUpdateEventBean eventBean)
    {
        String phone = eventBean.getPhone();
        if (StringUtil.isNotEmpty(phone) && mUserBean != null
                && StringUtil.isEquals(mUserBean.getPhone(), phone))
        {
            mUserBean = eventBean.getUserBean();
            mPresenter.initData(mUserBean);
        }
    }
}
