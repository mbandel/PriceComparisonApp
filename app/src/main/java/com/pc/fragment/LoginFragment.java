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
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

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
        progressLayout.setVisibility(View.VISIBLE);

        String requestUrl = Config.URL + getResources().getString(R.string.api_auth);
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        try {
            jsonObject
                    .put("email", Objects.requireNonNull(email.getText()).toString())
                    .put("password", Objects.requireNonNull(password.getText()).toString());
        } catch (JSONException ignored) {}

        RequestBody body = RequestBody.create(jsonObject.toString(), JSON);
        Request loginRequest = new Request.Builder()
                .url(requestUrl)
                .post(body)
                .build();

        httpClient.newCall(loginRequest).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                getActivity().runOnUiThread(() -> {
                    System.out.println("blad" + e.toString());
                    Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.server_error), Toast.LENGTH_LONG).show();
                    progressLayout.setVisibility(View.INVISIBLE);
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response)
                    throws IOException  {
                if(response.code() == HttpStatus.SC_OK) {
                    try {
                        jsonObject = new JSONObject(
                                Objects.requireNonNull(response.body()).string());
                       String token = jsonObject.getString("token");
                       sharedPreferences.edit().putString("token", "Bearer " + token).commit();
                       getActivity().runOnUiThread(() -> {
                        });

                       Intent intent = new Intent(getActivity(), MainActivity.class);
                       startActivity(intent);
                        progressLayout.setVisibility(View.INVISIBLE);
                    } catch (JSONException ignored) {}
                } else {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getActivity().getApplicationContext(), "Niepoprawny email lub haslo", Toast.LENGTH_LONG).show();
                        progressLayout.setVisibility(View.INVISIBLE);
                    });
                }
            }
        });

        //Intent intent = new Intent(getActivity(), MainActivity.class);
        //startActivity(intent);
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