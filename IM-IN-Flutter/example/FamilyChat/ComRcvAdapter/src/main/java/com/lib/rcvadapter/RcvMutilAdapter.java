package com.lib.rcvadapter;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import com.lib.rcvadapter.animation.RcvAlphaInAnim;
import com.lib.rcvadapter.animation.RcvBaseAnimation;
import com.lib.rcvadapter.holder.RcvHolder;
import com.lib.rcvadapter.view.RcvBaseItemView;
import com.lib.rcvadapter.view.RcvBaseLoadMoreView;
import com.lib.rcvadapter.view.RcvLoadMoreView;

import java.util.ArrayList;
import java.util.List;

/**
 * Function:RecycleView通用多布局适配器
 */
public abstract class RcvMutilAdapter<T> extends RecyclerView.Adapter<RcvHolder>
{
    //HeadView的ViewType基础标识
    protected static final int VIEW_TYPE_HEAD = 1000000;
    //FootView的ViewType基础标识
    protected static final int VIEW_TYPE_FOOT = 2000000;
    //LoadMoreLayout的ViewType标识
    protected static final int VIEW_TYPE_LOADMORE = Integer.MAX_VALUE - 2;
    //EmptyView的ViewType标识
    protected static final int VIEW_TYPE_EMPTY = Integer.MAX_VALUE - 1;
    //上下文
    protected Context mContext;
    //数据源
    protected List<T> mDataList = new ArrayList<>();
    //子布局管理器
    protected RcvItemViewManager mItemViewManager;
    //存放头部布局的容器
    protected SparseArray<View> mHeadViews;
    //存放底部布局的容器
    protected SparseArray<View> mFootViews;
    //底部加载更多的布局
    protected RcvBaseLoadMoreView mLoadMoreLayout;
    //行布局点击监听
    protected onItemClickListener mOnItemClickListener;
    //行布局长按监听
    protected onItemLongClickListener mOnItemLongClickListener;
    //空数据占位View
    protected View mEmptyView;
    //空数据占位View的布局id
    protected int mEmptyViewId;
    //子item展示动画
    protected RcvBaseAnimation mAnimation;
    //上次子item展示动画最后的位置
    protected int mAnimLastPosition = -1;

    public RcvMutilAdapter(Context context, List<T> datas)
    {
        this.mContext = context;
        if (datas != null && datas.size() > 0)
            this.mDataList.addAll(datas);
        mItemViewManager = new RcvItemViewManager();
    }

    /**
     * 获取当前所有数据
     */
    public List<T> getDatas()
    {
        return mDataList;
    }

    /**
     * 添加子布局类型
     */
    public RcvMutilAdapter addItemView(RcvBaseItemView<T> itemView)
    {
        mItemViewManager.addItemView(itemView);
        return this;
    }

    /**
     * 添加子布局类型
     */
    public RcvMutilAdapter addItemView(int viewType, RcvBaseItemView<T> itemView)
    {
        mItemViewManager.addItemView(viewType, itemView);
        return this;
    }

    /**
     * 添加HeadView
     * [注意！调用该方法前请确保RecyclerView已经调用了setLayoutManager()]
     */
    public void addHeadView(View... headViews)
    {
        if (mHeadViews == null)
            mHeadViews = new SparseArray<>();
        for (View headView : headViews)
        {
            mHeadViews.put(VIEW_TYPE_HEAD + getHeadCounts(), headView);
        }
        notifyDataSetChanged();
    }

    /**
     * 删除指定位置的HeadView
     *
     * @param index 索引位置
     */
    public void removeHeadViewAt(int index)
    {
        if (mHeadViews != null)
        {
            mHeadViews.removeAt(index);
            notifyDataSetChanged();
        }
    }

    /**
     * 清空HeadView
     */
    public void clearHeadViews()
    {
        if (mHeadViews != null && mHeadViews.size() > 0)
        {
            mHeadViews.clear();
            notifyDataSetChanged();
        }
    }

    /**
     * 添加FootView
     * [注意！调用该方法前请确保RecyclerView已经调用了setLayoutManager()]
     */
    public void addFootView(View... footViews)
    {
        if (mFootViews == null)
            mFootViews = new SparseArray<>();
        for (View footView : footViews)
        {
            mFootViews.put(VIEW_TYPE_FOOT + getFootCounts(), footView);
        }
        notifyDataSetChanged();
    }


    /**
     * 删除指定位置的FootView
     *
     * @param index 索引位置
     */
    public void removeFootViewAt(int index)
    {
        if (mFootViews != null)
        {
            mFootViews.removeAt(index);
            notifyDataSetChanged();
        }
    }

    /**
     * 清空FootView
     */
    public void clearFootViews()
    {
        if (mFootViews != null && mFootViews.size() > 0)
        {
            mFootViews.clear();
            notifyDataSetChanged();
        }
    }


    /**
     * 是否开启加载更多功能【使用默认布局】
     */
    public void openLoadMore()
    {
        openLoadMore(new RcvLoadMoreView(mContext));
    }

    /**
     * 是否开启加载更多功能
     *
     * @param layout 自定义加载更多布局
     */
    public void openLoadMore(RcvBaseLoadMoreView layout)
    {
        this.mLoadMoreLayout = layout;
        notifyDataSetChanged();
    }

    /**
     * 是否开启加载更多功能
     */
    public boolean isLoadMoreEnable()
    {
        return mLoadMoreLayout != null ? true : false;
    }

    /**
     * 设置空数据占位VIew
     */
    public void setEmptyView(View emptyView)
    {
        this.mEmptyView = emptyView;
    }

    /**
     * 设置空数据占位View的id
     */
    public void setEmptyView(int layoutId)
    {
        this.mEmptyViewId = layoutId;
    }

    //子item展示动画是否开启
    protected boolean isItemShowingAnimEnable()
    {
        return mAnimation != null ? true : false;
    }

    /**
     * 开启子item展示动画
     * [默认为AlphaIn动画]
     */
    public void openItemShowingAnim()
    {
        openItemShowingAnim(new RcvAlphaInAnim());
    }

    /**
     * 开启子item展示动画
     * [注意！当有HeadView或LoadMore的情况时，自定义动画可能会有问题!!!]
     *
     * @param animation 自定义动画
     */
    public void openItemShowingAnim(RcvBaseAnimation animation)
    {
        this.mAnimation = animation;
    }

    @Override
    public RcvHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        if (mHeadViews != null && mHeadViews.get(viewType) != null)
        {
            return RcvHolder.get(mContext, mHeadViews.get(viewType));
        } else if (mFootViews != null && mFootViews.get(viewType) != null)
        {
            return RcvHolder.get(mContext, mFootViews.get(viewType));
        } else if (viewType == VIEW_TYPE_LOADMORE && isLoadMoreEnable())
        {
            return RcvHolder.get(mContext, (View) mLoadMoreLayout);
        } else if (viewType == VIEW_TYPE_EMPTY)
        {
            if (mEmptyView != null)
                return RcvHolder.get(mContext, mEmptyView);
            else
                return RcvHolder.get(mContext, parent, mEmptyViewId);
        } else
        {
            int layoutId = mItemViewManager.getItemViewLayoutId(viewType);
            RcvHolder holder = RcvHolder.get(mContext, parent, layoutId);
            setListener(parent, holder, viewType);
            return holder;
        }
    }

    @Override
    public void onBindViewHolder(RcvHolder holder, int position)
    {
        if (isInHeadViewPos(position) || isInFootViewPos(position) || isInEmptyStatus())
        {
            return;
        } else if (isInLoadMorePos(position))
        {
            mLoadMoreLayout.handleLoadMoreRequest();
            return;
        } else
        {
            //设置数据
            setData(holder, mDataList.get(position - getHeadCounts()), holder.getLayoutPosition());
            //设置动画
            setItemShowingAnim(holder);
        }
    }

    //设置子item展示动画
    protected void setItemShowingAnim(RcvHolder holder)
    {
        if (isItemShowingAnimEnable() && holder.getLayoutPosition() > mAnimLastPosition)
        {
            mAnimLastPosition = holder.getLayoutPosition();
            mAnimation.startAnim(holder.itemView);
        }
    }

    @Override
    public int getItemViewType(int position)
    {
        if (isInHeadViewPos(position))
            return mHeadViews.keyAt(position);
        else if (isInFootViewPos(position))
            return mFootViews.keyAt(position - getDataSize() - getHeadCounts());
        else if (isInLoadMorePos(position))
            return VIEW_TYPE_LOADMORE;
        else if (isInEmptyStatus())
            return VIEW_TYPE_EMPTY;
        else if (useItemViewManager())
            return mItemViewManager.getItemViewType(mDataList.get(position - getHeadCounts()), position - getHeadCounts());
        else
            return super.getItemViewType(position);
    }

    /**
     * 是否启用子布局管理器
     * 【会根据其内子布局个数自动判断】
     */
    protected boolean useItemViewManager()
    {
        return mItemViewManager.getItemViewCount() > 0;
    }

    /**
     * 获取数据数量
     */
    protected int getDataSize()
    {
        return mDataList.size();
    }

    /**
     * 获取HeadView的数量
     */
    protected int getHeadCounts()
    {
        return mHeadViews != null ? mHeadViews.size() : 0;
    }

    /**
     * 获取FootView的数量
     */
    protected int getFootCounts()
    {
        return mFootViews != null ? mFootViews.size() : 0;
    }

    /**
     * 获取加载更多的数量
     */
    protected int getLoadMoreCounts()
    {
        return isLoadMoreEnable() ? 1 : 0;
    }

    /**
     * 某个位置是否处于HeadView的位置内
     */
    protected boolean isInHeadViewPos(int p)
    {
        return p < getHeadCounts();
    }

    /**
     * 某个位置是否处于FootView的位置内
     */
    protected boolean isInFootViewPos(int p)
    {
        return p >= getDataSize() + getHeadCounts() &&
                p < getDataSize() + getHeadCounts() + getFootCounts();
    }

    /**
     * 某个位置是否处于LoadMore的位置内
     */
    protected boolean isInLoadMorePos(int p)
    {
        return isLoadMoreEnable() &&
                p == getDataSize() + getHeadCounts() + getFootCounts();
    }

    /**
     * 判断当前是否符合空数据状态
     */
    protected boolean isInEmptyStatus()
    {
        return (mEmptyView != null || mEmptyViewId != 0) &&
                (getDataSize() + getHeadCounts() + getFootCounts() + getLoadMoreCounts()) == 0;
    }

    /**
     * 设置行布局点击监听【单击和长按】
     */
    protected void setListener(final ViewGroup parent, final RcvHolder viewHolder, int viewType)
    {
        viewHolder.getConvertView().setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (mOnItemClickListener != null)
                {
                    int position = viewHolder.getLayoutPosition();
                    mOnItemClickListener.onItemClick(v, viewHolder, mDataList.get(position - getHeadCounts()), position);
                }
            }
        });

        viewHolder.getConvertView().setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View v)
            {
                if (mOnItemLongClickListener != null)
                {
                    int position = viewHolder.getLayoutPosition();
                    mOnItemLongClickListener.onItemLongClick(v, viewHolder, mDataList.get(position - getHeadCounts()), position);
                }
                return false;
            }
        });
    }

    public void setData(RcvHolder holder, T t, int position)
    {
        mItemViewManager.setData(holder, t, position);
    }

    @Override
    public int getItemCount()
    {
        if (isInEmptyStatus())
            return 1;
        else
            return getDataSize()
                    + getHeadCounts()
                    + getFootCounts()
                    + getLoadMoreCounts();
    }

    /**
     * 添加HeadView或FootView或LoadMore或EmptyView
     * 兼容GridLayoutMananger的方法
     */
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView)
    {
        super.onAttachedToRecyclerView(recyclerView);
        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
        if (manager instanceof GridLayoutManager)
        {
            final GridLayoutManager gridManager = ((GridLayoutManager) manager);
            gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup()
            {
                @Override
                public int getSpanSize(int position)
                {
                    if (isInHeadViewPos(position)
                            || isInFootViewPos(position)
                            || isInLoadMorePos(position)
                            || isInEmptyStatus())
                        return gridManager.getSpanCount();
                    else
                        return 1;
                }
            });
            gridManager.setSpanCount(gridManager.getSpanCount());
        }
    }

    /**
     * 添加HeadView或FootView或LoadMore或EmptyView后
     * 兼容StaggeredGridLayoutManager的方法
     */
    @Override
    public void onViewAttachedToWindow(RcvHolder holder)
    {
        super.onViewAttachedToWindow(holder);
        if (isInHeadViewPos(holder.getLayoutPosition())
                || isInFootViewPos(holder.getLayoutPosition())
                || isInLoadMorePos(holder.getLayoutPosition())
                || isInEmptyStatus())
        {
            ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
            if (lp != null && lp instanceof StaggeredGridLayoutManager.LayoutParams)
            {
                StaggeredGridLayoutManager.LayoutParams p = (StaggeredGridLayoutManager.LayoutParams) lp;
                p.setFullSpan(true);
            }
        }
    }

    /**
     * 刷新数据的方法
     */
    public void refreshDatas(List<T> data)
    {
        mDataList.clear();
        if (data != null && data.size() > 0)
            mDataList.addAll(data);
        notifyDataSetChanged();
    }

    /**
     * 增加一条数据
     *
     * @return 成功则返回添加的位置，失败返回-1
     */
    public int addData(T t)
    {
        return addData(getHeadCounts() + getDataSize(), t);
    }

    /**
     * 添加一条数据
     *
     * @param position 添加的位置，需要考虑HeadView的数量
     * @param t        数据
     * @return 成功则返回添加的位置，失败返回-1
     */
    public int addData(int position, T t)
    {
        if (t != null)
        {
            mDataList.add(position - getHeadCounts(), t);
            notifyItemInserted(position);
            return position;
        }
        return -1;
    }

    /**
     * 添加若干条数据
     *
     * @param data 数据list
     */
    public void addDatas(List<T> data)
    {
        if (data != null && data.size() > 0)
        {
            int posStart = getHeadCounts() + getDataSize();
            mDataList.addAll(data);
            notifyItemRangeInserted(posStart, data.size());
        }
    }

    /**
     * 删除一条数据
     *
     * @param position 位置，需要考虑HeadView的数量
     * @return 成功返回位置，失败返回-1
     */
    public int deleteData(int position)
    {
        if (mDataList != null)
        {
            mDataList.remove(position - getHeadCounts());
            notifyItemRemoved(position);
            return position;
        }
        return -1;
    }

    /**
     * 删除一条数据，但不主动调用notifyDataSetChanged()
     *
     * @param t 数据
     * @return 成功返回位置，失败返回-1
     */
    public int deleteData(T t)
    {
        if (mDataList != null)
        {
            int p = mDataList.indexOf(t);
            if (mDataList.remove(t))
            {
                int position = p + getHeadCounts();
                notifyItemRemoved(position);
                return position;
            }
        }
        return -1;
    }

    /**
     * 删除若干条数据
     *
     * @param data 数据
     */
    public void deleteDatas(List<T> data)
    {
        if (data != null && data.size() > 0 && mDataList != null)
        {
            mDataList.removeAll(data);
            notifyDataSetChanged();
        }
    }

    /**********************
     * 点击事件
     ****************************************************/
    public interface onItemClickListener<T>
    {
        void onItemClick(View view, RcvHolder holder, T t, int position);
    }

    public interface onItemLongClickListener<T>
    {
        void onItemLongClick(View view, RcvHolder holder, T t, int position);
    }

    public void setOnItemClickListener(onItemClickListener l)
    {
        this.mOnItemClickListener = l;
    }

    public void setOnItemLongClickListener(onItemLongClickListener l)
    {
        this.mOnItemLongClickListener = l;
    }

    /*****************
     * 加载更多监听
     *************************************/
    public interface onLoadMoreListener
    {
        void onLoadMoreRequest();
    }

    /**
     * 设置加载更多的监听
     */
    public void setOnLoadMoreListener(onLoadMoreListener l)
    {
        if (isLoadMoreEnable())
            mLoadMoreLayout.setOnLoadMoreListener(l);
        else
            throw new IllegalArgumentException("RcvMutilAdapter: Must openLoadMore()");
    }

    /**
     * 通知加载更多成功
     */
    public void notifyLoadMoreSuccess(final List<T> newDataList, final boolean hasMoreData)
    {
        if (isLoadMoreEnable())
        {
            mLoadMoreLayout.handleLoadSuccess();
            mLoadMoreLayout.postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    addDatas(newDataList);
                    if (hasMoreData)
                        mLoadMoreLayout.handleLoadInit();
                    else
                        mLoadMoreLayout.handleNoMoreData();
                }
            }, 500);
        } else
            throw new IllegalArgumentException("RcvMutilAdapter: Must openLoadMore()");
    }

    /**
     * 通知加载更多失败
     */
    public void notifyLoadMoreFail()
    {
        if (isLoadMoreEnable())
            mLoadMoreLayout.handleLoadFail();
        else
            throw new IllegalArgumentException("RcvMutilAdapter: Must openLoadMore()");
    }

    /**
     * 通知加载更多没有更多数据
     */
    public void notifyLoadMoreHasNoMoreData()
    {
        if (isLoadMoreEnable())
            mLoadMoreLayout.handleNoMoreData();
        else
            throw new IllegalArgumentException("RcvMutilAdapter: Must openLoadMore()");
    }
}
