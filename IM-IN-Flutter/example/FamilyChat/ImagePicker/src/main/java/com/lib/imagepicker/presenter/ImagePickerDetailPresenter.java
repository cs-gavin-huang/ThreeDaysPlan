package com.lib.imagepicker.presenter;

import com.lib.imagepicker.ImagePicker;
import com.lib.imagepicker.bean.ImageBean;
import com.lib.imagepicker.bean.ImageFloderBean;
import com.lib.imagepicker.model.ImageScanModel;
import com.lib.imagepicker.presenter.impl.DetailPickerPresenterImpl;
import com.lib.imagepicker.view.impl.DetailPickerViewImpl;

import java.util.List;

/**
 * Function:图片详情界面Presenter
 */
public class ImagePickerDetailPresenter implements DetailPickerPresenterImpl
{
    private DetailPickerViewImpl mViewImpl;

    private ImageScanModel mModel;

    public ImagePickerDetailPresenter(DetailPickerViewImpl view)
    {
        this.mViewImpl = view;
        mModel = ImageScanModel.getInstance();
    }

    @Override
    public List<ImageBean> getImagesByFloder(ImageFloderBean floder)
    {
        return mModel.getImagesByFloder(floder);
    }

    @Override
    public ImageFloderBean getFloderById(String id)
    {
        return mModel.getFloderById(id);
    }

    @Override
    public void addImage(ImageBean imageBean)
    {
        boolean success = ImagePicker.getInstance().addImage(imageBean);
        if (!success)
        {
            mViewImpl.onNumLimited(ImagePicker.getInstance().getOptions().getLimitNum());
        } else
        {
            mViewImpl.onCurImageBeAdded();
            mViewImpl.onSelectedNumChanged(ImagePicker.getInstance().getAllSelectedImages().size(),
                    ImagePicker.getInstance().getOptions().getLimitNum());
        }
    }

    @Override
    public void removeImage(ImageBean imageBean)
    {
        ImagePicker.getInstance().removeImage(imageBean);
        mViewImpl.onCurImageBeRemoved();
        mViewImpl.onSelectedNumChanged(ImagePicker.getInstance().getAllSelectedImages().size(),
                ImagePicker.getInstance().getOptions().getLimitNum());
    }

    @Override
    public boolean hasSelectedData(ImageBean imageBean)
    {
        return ImagePicker.getInstance().getAllSelectedImages().contains(imageBean);
    }

    @Override
    public void selectNumChanged()
    {
        mViewImpl.onSelectedNumChanged(ImagePicker.getInstance().getAllSelectedImages().size(),
                ImagePicker.getInstance().getOptions().getLimitNum());
    }
}
