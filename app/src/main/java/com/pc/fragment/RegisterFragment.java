package com.pc.fragment;

import android.os.Bundle;
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


public class RegisterFragment extends Fragment implements Validator.ValidationListener {

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

    @Length(min = 2, max = 30)
    @Pattern(regex = "[a-z]+")
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @OnClick(R.id.register_btn)
    public void onRegisterButtonClick(){
        validator.validate();
        if(isInputValid){
            progressLayout.setVisibility(View.VISIBLE);
            connector = Connector.getInstance();
            User user = new User(firstName.getText().toString(), lastName.getText().toString(), username.getText().toString(), email.getText().toString(), password.getText().toString());
            Call<String> registerCall = connector.serverApi.createUser(user);
            registerCall.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    if (response.isSuccessful()){
                        System.out.println(response.body());
                        System.out.println("response code " + response.code());
                        getActivity().runOnUiThread(() -> {
                            Toast.makeText(getActivity().getApplicationContext(), "rejestracja zakończona sukcesem", Toast.LENGTH_SHORT).show();
                        });
                    }else{
                        System.out.println(response.body());
                        System.out.println("response code " + response.code());
                        getActivity().runOnUiThread(() -> {
                            Toast.makeText(getActivity().getApplicationContext(), "rejestracja zakończona niepowodzeniem", Toast.LENGTH_SHORT).show();
                        });
                    }
                }
                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                    });
                }
            });
            progressLayout.setVisibility(View.INVISIBLE);
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
}