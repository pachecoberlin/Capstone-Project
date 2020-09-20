package de.pacheco.capstone.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HomeViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public HomeViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Hello my name is Pacheco and this App is a collection of some smaller " +
                "Apps i have created while getting my app creation skills up to date. I love to " +
                "make great Apps with" +
                " a nice User Experience. As of september " +
                "2020 this app is made with state of the art technologies. But you know you never" +
                " stop learning. Checkout the source code if you like: https://github.com/pachecoberlin/Capstone-Project");
    }

    public LiveData<String> getText() {
        return mText;
    }
}