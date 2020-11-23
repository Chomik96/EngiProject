package com.michalfladzinski.engiproject.ui.author;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.michalfladzinski.engiproject.R;

public class AuthorFragment extends Fragment {

    private AuthorViewModel authorViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        authorViewModel =
                ViewModelProviders.of(this).get(AuthorViewModel.class);
        View root = inflater.inflate(R.layout.fragment_author, container, false);
        final TextView textView = root.findViewById(R.id.text_tools);
        authorViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}