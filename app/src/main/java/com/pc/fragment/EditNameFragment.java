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
import com.mobsandgeeks.saripaar.annotation.ConfirmPassword;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.Length;
import com.mobsandgeeks.saripaar.annotation.Password;
import com.mobsandgeeks.saripaar.annotation.Pattern;
import com.pc.R;
import com.pc.util.NavigationEditProfile;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.pc.R.id.email_input;
import static com.pc.R.id.password_input;


public class EditNameFragment extends Fragment implements Validator.ValidationListener, TextWatcher {
    @Length(min = 2, max = 30)
    @Pattern(regex = "[A-Z][a-z]+")
    @BindView(R.id.first_name_input)
    EditText firstName;

    @Length(min = 2, max = 30)
    @Pattern(regex = "[A-Z][a-z]+")
    @BindView(R.id.last_name_input)
    EditText lastName;

    @BindView(R.id.confirm_btn)
    Button confirmButton;

    private boolean isInputValid = false;
    private Validator validator;
    private NavigationEditProfile navigation;

    public EditNameFragment() {

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
        View view = inflater.inflate(R.layout.fragment_edit_name, container, false);
        ButterKnife.bind(this, view);
        firstName.addTextChangedListener(this);
        lastName.addTextChangedListener(this);
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
            navigation.editName(firstName.getText().toString(), lastName.getText().toString());
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
            if (view == firstName)
                ((EditText) view).setError(getResources().getString(R.string.first_name_error));
            else if (view == lastName)
                ((EditText) view).setError(getResources().getString(R.string.last_name_error));
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