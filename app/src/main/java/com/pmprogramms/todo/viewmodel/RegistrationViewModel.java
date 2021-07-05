package com.pmprogramms.todo.viewmodel;

import android.app.Application;
import android.util.Patterns;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.pmprogramms.todo.helpers.forms.RegisterFieldForm;
import com.pmprogramms.todo.helpers.validation.RegisterValidationEnum;
import com.pmprogramms.todo.repository.RegistrationRepository;

import java.util.HashMap;

public class RegistrationViewModel extends AndroidViewModel {
    private MutableLiveData<RegisterValidationEnum> resultOfValidation;
    private final RegistrationRepository registrationRepository = new RegistrationRepository();

    public RegistrationViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<RegisterValidationEnum> validationRegisterForm(RegisterFieldForm registerFieldForm) {
        if (resultOfValidation == null)
            resultOfValidation = new MutableLiveData<>();

        if (registerFieldForm.getEmail() == null || registerFieldForm.getEmail().equals("")) {
            resultOfValidation.postValue(RegisterValidationEnum.EMPTY_EMAIL);
        } else if (!Patterns.EMAIL_ADDRESS.matcher(registerFieldForm.getEmail()).matches()) {
            resultOfValidation.postValue(RegisterValidationEnum.WRONG_EMAIL);
        } else if (registerFieldForm.getPassword() == null || registerFieldForm.getPassword().equals("")) {
            resultOfValidation.postValue(RegisterValidationEnum.EMPTY_PASSWORD);
        } else if (registerFieldForm.getPassword().trim().length() < 8) {
            resultOfValidation.postValue(RegisterValidationEnum.SHORT_PASSWORD);
        } else if (!registerFieldForm.isAcceptedPrivacy()) {
            resultOfValidation.postValue(RegisterValidationEnum.NOT_ACCEPTED_PRIVACY);
        } else if (registerFieldForm.getUsername() == null || registerFieldForm.getUsername().equals("")) {
            resultOfValidation.postValue(RegisterValidationEnum.EMPTY_USERNAME);
        } else if (!registerFieldForm.getPassword().equals(registerFieldForm.getRepeatPassword())) {
            resultOfValidation.postValue(RegisterValidationEnum.PASSWORD_NOT_MATCH);
        } else
            resultOfValidation.postValue(RegisterValidationEnum.OK);

        return resultOfValidation;
    }

    public LiveData<Integer> createUser(HashMap<String, Object> userData) {
        return registrationRepository.createUser(userData);
    }
}
