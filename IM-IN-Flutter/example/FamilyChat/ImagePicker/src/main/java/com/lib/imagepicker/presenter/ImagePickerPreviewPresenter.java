package com.lib.imagepicker.presenter;

import com.lib.imagepicker.ImagePicker;
import com.lib.imagepicker.bean.ImageBean;
import com.lib.imagepicker.presenter.impl.PreviewPickerPresenterImpl;
import com.lib.imagepicker.view.impl.PreviewPickerViewImpl;

/**
 * Function:已选图片界面Presenter
 */
public class ImagePickerPreviewPresenter implements PreviewPickerPresenterImpl
{
    private PreviewPickerViewImpl mViewImpl;

    public ImagePickerPreviewPresenter(PreviewPickerViewImpl view)
    {
        this.mViewImpl = view;
    }

    @Override
    public void addImage(ImageBean imageBean)
    {
        boolean success = ImagePicker.getInstance().addImage(imageBean);
        if (success)
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
