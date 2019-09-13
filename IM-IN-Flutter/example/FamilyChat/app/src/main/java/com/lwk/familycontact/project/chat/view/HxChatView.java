package com.lwk.familycontact.project.chat.view;

import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;

import java.util.List;

/**
 * Created by LWK
 * TODO 聊天界面View实现的接口
 * 2016/9/20
 */
public interface HxChatView
{
    /**
     * 刷新ActionBar
     */
    void onRefreshActionBarTitle(String title);

    /**
     * 获取适配器里第一条消息
     */
    EMMessage getAdapterFirstMsg();

    /**
     * 获取适配器里消息数量
     */
    int getAdapterMsgCount();

    /**
     * 获取会话id
     */
    String getConversationId();

    /**
     * 获取会话类型
     */
    EMConversation.EMConversationType getConversationType();

    /**
     * 加载一页消息成功
     *
     * @param messages    消息list
     * @param isFirstLoad 是否为第一次加载
     */
    void loadOnePageMessagesSuccess(List<EMMessage> messages, boolean isFirstLoad);

    /**
     * 下拉刷新成功
     */
    void onPtrSuccess();

    /**
     * 下拉刷新失败
     */
    void onPtrFail();

    /**
     * 提示没有更多消息记录
     */
    void showNoMoreMessageWarning();

    /**
     * RecyclerView滚到底部
     */
    void scrollToBottom();

    /**
     * 刷新适配器状态
     */
    void refershAdapterStatus();

    /**
     * 添加新消息
     */
    void addNewMessage(EMMessage message, boolean forceScrollToBottom);

    /**
     * 某条消息的状态更改
     */
    void onMessageStatusChanged(EMMessage message);

    /**
     * 移除一条消息
     */
    void removeMessage(EMMessage message, int position);

    /**
     * 跳转到查看大图界面
     *
     * @param firstVisiableMsgId 第一张图的msgid
     */
    void startToImageDetailAct(String firstVisiableMsgId);

    /**
     * 跳转到短视频播放界面
     *
     * @param message 短视频消息
     */
    void startToShortVideoPlay(EMMessage message);

    /**
     * 当语音消息在听筒播放时提示用户
     */
    void showVoicePlayInCall();

    /**
     * 关闭语音消息在听筒播放的提示
     */
    void closeVoicePlayInCall();

    /**
     * 显示处理数据的Dialog
     */
    void showHandlingDialog(int msgResId);

    /**
     * 关闭处理数据的Dialog
     */
    void closeHandlingDialog();

    /**
     * 提示错误
     *
     * @param errorCode   错误码
     * @param errMsgResId 错误描述资源id
     */
    void showError(int errorCode, int errMsgResId);
}
