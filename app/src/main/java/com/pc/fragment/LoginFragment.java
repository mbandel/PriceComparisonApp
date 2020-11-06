package com.pc.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.Password;
import com.pc.R;
import com.pc.activity.MainActivity;
import com.pc.model.Credentials;
import com.pc.model.Token;
import com.pc.model.User;
import com.pc.retrofit.Config;
import com.pc.retrofit.Connector;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cz.msebera.android.httpclient.HttpStatus;
import retrofit2.Call;
import retrofit2.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;

import okhttp3.RequestBody;
import retrofit2.Response;

public class LoginFragment extends Fragment implements Validator.ValidationListener, TextWatcher {

    @BindView(R.id.login_btn)
    Button loginButton;
    @Email
    @BindView(R.id.email_input)
    TextInputEditText email;
    @Password(min = 6, scheme = Password.Scheme.ALPHA_NUMERIC_MIXED_CASE)
    @BindView(R.id.password_input)
    TextInputEditText password;
    @BindView(R.id.progress_layout)
    ConstraintLayout progressLayout;

    private boolean isInputValid = false;
    private Validator validator;
    private Connector connector;
    private JSONObject jsonObject = new JSONObject();
    private OkHttpClient httpClient = new OkHttpClient();
    private SharedPreferences sharedPreferences;

    public LoginFragment() {
        // Required empty public constructor
    }

    public static LoginFragment newInstance() {
        LoginFragment fragment = new LoginFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        validator = new Validator(this);
        validator.setValidationListener(this);
        connector = Connector.getInstance();
        sharedPreferences = getActivity().getSharedPreferences("myPreferences", Context.MODE_PRIVATE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        ButterKnife.bind(this, view);
        email.addTextChangedListener(this);
        password.addTextChangedListener(this);
        return view;
    }

    @OnClick(R.id.login_btn)
    public void onLoginButtonClick(){
        loginButton.setEnabled(false);
        validator.validate();
        if (isInputValid) {
            progressLayout.setVisibility(View.VISIBLE);

            Credentials credentials = new Credentials(email.getText().toString(), password.getText().toString());
            Call<Token> authCall = connector.serverApi.authenticate(credentials);
            authCall.enqueue(new Callback<Token>() {
                @Override
                public void onResponse(Call<Token> call, Response<Token> response) {
                    if (response.isSuccessful()) {
                        sharedPreferences.edit().putString("token", "Bearer " + response.body().getToken()).apply();
                        findUserByEmail(sharedPreferences.getString("token", null), email.getText().toString());
                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        startActivity(intent);
                        getActivity().finish();
                        progressLayout.setVisibility(View.INVISIBLE);
                    }
                    else {
                        loginButton.setEnabled(true);
                        Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.incorrect_data), Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<Token> call, Throwable t) {
                    Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.server_error), Toast.LENGTH_LONG).show();
                    progressLayout.setVisibility(View.INVISIBLE);
                }
            });
        }
    }

    public void findUserByEmail(String token, String email){
        Call<User> findUserCall = connector.serverApi.findUserByEmail(token, email);
        findUserCall.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, retrofit2.Response<User> response) {
                if(response.isSuccessful()) {
                    sharedPreferences.edit().putInt("id", response.body().getId()).commit();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
            }
        });
    }

    @Override
    public void onValidationSucceeded() {
        isInputValid = true;
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        isInputValid = false;
        loginButton.setEnabled(true);
        for (ValidationError error : errors){
            View view = error.getView();
            if (view == email){
                ((EditText) view).setError(getResources().getString(R.string.email_error));
            }else if (view == password)
                ((EditText) view).setError(getResources().getString(R.string.password_error));
            else ((EditText) view).setError(null);
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