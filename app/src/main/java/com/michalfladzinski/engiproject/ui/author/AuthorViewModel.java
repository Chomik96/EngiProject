package com.michalfladzinski.engiproject.ui.author;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AuthorViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public AuthorViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Praca została wykonana\nw ramach projektu inżynierskiego.\nMichał Fladziński");
    }

    public LiveData<String> getText() {
        return mText;
    }
}