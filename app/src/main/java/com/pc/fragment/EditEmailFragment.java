package com.pc.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.textfield.TextInputEditText;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.pc.R;
import com.pc.util.NavigationEditProfile;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.pc.R.id.email_input;


public class EditEmailFragment extends Fragment implements Validator.ValidationListener, TextWatcher {

    @Email
    @BindView(email_input)
    TextInputEditText email;

    @BindView(R.id.confirm_btn)
    Button confirmButton;

    private boolean isInputValid = false;
    private Validator validator;
    private NavigationEditProfile navigation;

    public EditEmailFragment() {

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof NavigationEditProfile) {
            navigation = (NavigationEditProfile) context;
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_email, container, false);
        ButterKnife.bind(this, view);
        email.addTextChangedListener(this);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        validator = new Validator(this);
        validator.setValidationListener(this);
    }

    @OnClick(R.id.confirm_btn)
    public void onConfirmButtonClick() {
        validator.validate();
        if (isInputValid) {
            navigation.editEmail(email.getText().toString());
        }
    }

    @Override
    public void onValidationSucceeded() {
        isInputValid = true;
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        isInputValid = false;
        confirmButton.setEnabled(true);
        for (ValidationError error : errors) {
            View view = error.getView();
            if (view == email) {
                ((EditText) view).setError(getResources().getString(R.string.email_error));
            }
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }

    @Override
    public void afterTextChanged(Editable editable) {
        validator.validate();
    }
}