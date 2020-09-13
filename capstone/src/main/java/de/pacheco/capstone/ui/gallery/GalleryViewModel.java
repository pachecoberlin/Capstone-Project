package de.pacheco.capstone.ui.gallery;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class GalleryViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public GalleryViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Credits for Pictures, Websites, people that helped me out");
    }

    public LiveData<String> getText() {
        return mText;
    }
}