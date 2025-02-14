package com.pc.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.ConfirmPassword;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.Length;
import com.mobsandgeeks.saripaar.annotation.Password;
import com.mobsandgeeks.saripaar.annotation.Pattern;
import com.pc.R;
import com.pc.model.User;
import com.pc.retrofit.Connector;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class RegisterFragment extends Fragment implements Validator.ValidationListener, TextWatcher {

    @BindView(R.id.register_btn)
    Button registerButton;

    @Email
    @BindView(R.id.email_input)
    EditText email;

    @Length(min = 2, max = 30)
    @Pattern(regex = "[A-Z][a-z]+")
    @BindView(R.id.first_name_input)
    EditText firstName;

    @Length(min = 2, max = 30)
    @Pattern(regex = "[A-Z][a-z]+")
    @BindView(R.id.last_name_input)
    EditText lastName;

    @Length(min = 2, max = 20)
    @Pattern(regex = "^[a-zA-Z0-9]+$")
    @BindView(R.id.username_input)
    EditText username;

    @Password(scheme = Password.Scheme.ALPHA_NUMERIC_MIXED_CASE)
    @BindView(R.id.password_input)
    EditText password;

    @ConfirmPassword
    @BindView(R.id.confirm_password_input)
    EditText confirmPassword;

    @BindView(R.id.progress_layout)
    ConstraintLayout progressLayout;

    private boolean isInputValid;
    private Validator validator;
    private Connector connector;
    boolean usernameValid;
    boolean emailValid;

    public RegisterFragment() {
        // Required empty public constructor
    }

    public static RegisterFragment newInstance() {
        RegisterFragment fragment = new RegisterFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        validator = new Validator(this);
        validator.setValidationListener(this);
        connector = Connector.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        ButterKnife.bind(this, view);
        firstName.addTextChangedListener(this);
        lastName.addTextChangedListener(this);
        email.addTextChangedListener(this);
        username.addTextChangedListener(this);
        password.addTextChangedListener(this);
        confirmPassword.addTextChangedListener(this);

        return view;
    }

    @OnClick(R.id.register_btn)
    public void onRegisterButtonClick(){
        validator.validate();
        if(isInputValid){
            progressLayout.setVisibility(View.VISIBLE);
            isEmailUsed();
        }
    }

    @Override
    public void onValidationSucceeded() {
        isInputValid = true;
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        isInputValid = false;
        for (ValidationError error : errors){
            View view = error.getView();
            if (view == firstName)
                ((EditText) view).setError(getResources().getString(R.string.first_name_error));
            else if (view == lastName)
                ((EditText) view).setError(getResources().getString(R.string.last_name_error));
            else if (view == email)
                ((EditText) view).setError(getResources().getString(R.string.email_error));
            else if (view == username)
                ((EditText) view).setError(getResources().getString(R.string.username_error));
            else if (view == password)
                ((EditText) view).setError(getResources().getString(R.string.password_error));
            else if (view == confirmPassword)
                ((EditText) view).setError(getResources().getString(R.string.password_error2));

            else ((EditText) view).setError(null);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

    @Override
    public void afterTextChanged(Editable editable) {
        validator.validate();
    }

    public void isUsernameUsed() {
        Call<User> usernameCall = connector.serverApi.findUserByUsername(username.getText().toString());
        usernameCall.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    username.setError("Podana nazwa użytkownika jest zajęta");
                    progressLayout.setVisibility(View.INVISIBLE);
                } else {
                    createUser();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                usernameValid = false;
            }
        });
    }

    public void isEmailUsed() {
        Call<User> emailCall = connector.serverApi.findUserByEmail(email.getText().toString());
        emailCall.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    email.setError("Podany adres e-mail jest zajęty");
                    progressLayout.setVisibility(View.INVISIBLE);
                } else {
                    isUsernameUsed();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                emailValid = false;
            }
        });
    }

    public void createUser() {
        User user = new User(firstName.getText().toString(), lastName.getText().toString(), username.getText().toString(), email.getText().toString(), password.getText().toString());
        Call<String> registerCall = connector.serverApi.createUser(user);
        registerCall.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getActivity().getApplicationContext(), "rejestracja zakończona sukcesem", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "rejestracja zakończona niepowodzeniem", Toast.LENGTH_SHORT).show();
                }
                progressLayout.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                progressLayout.setVisibility(View.INVISIBLE);
            }
        });
    }
}