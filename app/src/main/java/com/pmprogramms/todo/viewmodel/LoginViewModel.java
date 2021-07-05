package com.pmprogramms.todo.viewmodel;

import android.app.Application;
import android.util.Patterns;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.pmprogramms.todo.API.retrofit.login.JsonHelperLogin;
import com.pmprogramms.todo.helpers.forms.LoginFieldForm;
import com.pmprogramms.todo.helpers.validation.LoginValidationEnum;
import com.pmprogramms.todo.repository.LoginRepository;

import java.util.HashMap;

public class LoginViewModel extends AndroidViewModel {
    private MutableLiveData<LoginValidationEnum> resultOfValidation;
    private final LoginRepository loginRepository = new LoginRepository();

    public LoginViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<LoginValidationEnum> validLoginForm(LoginFieldForm loginFieldForm) {
        if (resultOfValidation == null)
            resultOfValidation = new MutableLiveData<>();

        if (loginFieldForm.getEmail() == null || loginFieldForm.getEmail().equals(""))
            resultOfValidation.postValue(LoginValidationEnum.EMPTY_EMAIL);

        else if (loginFieldForm.getPassword() == null || loginFieldForm.getPassword().equals(""))
            resultOfValidation.postValue(LoginValidationEnum.EMPTY_PASSWORD);

        else if (loginFieldForm.getPassword().length() < 8)
            resultOfValidation.postValue(LoginValidationEnum.SHORT_PASSWORD);

        else if (!Patterns.EMAIL_ADDRESS.matcher(loginFieldForm.getEmail()).matches())
            resultOfValidation.postValue(LoginValidationEnum.WRONG_EMAIL);

        else resultOfValidation.postValue(LoginValidationEnum.OK);

        return resultOfValidation;
    }

    public LiveData<JsonHelperLogin> loginUser(HashMap<String, String> authData) {
        return loginRepository.loginUser(authData);
    }
}
