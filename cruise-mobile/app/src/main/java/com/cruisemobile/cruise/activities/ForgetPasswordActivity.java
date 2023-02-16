package com.cruisemobile.cruise.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.cruisemobile.cruise.R;
import com.cruisemobile.cruise.services.ServiceUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgetPasswordActivity extends AppCompatActivity {

    private final EditText[] mCodeInputs = new EditText[6];

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        for (int i = 0; i < 6; i++) {
            final int codeIndex = i;
            mCodeInputs[i] = findViewById(getCodeInputId(i));
            mCodeInputs[i].setInputType(InputType.TYPE_CLASS_NUMBER);
            mCodeInputs[i].setFilters(new InputFilter[] {new InputFilter.LengthFilter(1)});
            mCodeInputs[i].setOnKeyListener((v, keyCode, event) -> {
                if (mCodeInputs[codeIndex].getText().length() == 1) {
                    if (codeIndex < 5) {
                        mCodeInputs[codeIndex + 1].requestFocus();
                    } else {
                        // Hide the soft keyboard if the last digit is filled
                        mCodeInputs[codeIndex].onEditorAction(EditorInfo.IME_ACTION_DONE);
                    }
                }
                return false;
            });
        }
        String email = getIntent().getStringExtra("email");
        Button button = findViewById(R.id.changePassBtn);
        button.setOnClickListener(view -> tryChange(email));
    }


    private void tryChange(String email) {
        StringBuilder code = new StringBuilder();
        for(int i = 0; i < 6; i++)
            code.append(mCodeInputs[i].getText().toString());
        Call<Boolean> call = ServiceUtils.userEndpoints.checkCode(email, code.toString());
        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if(Boolean.TRUE.equals(response.body()))
                {
                    Intent intent = new Intent(getApplicationContext(), PasswordChangeActivity.class);
                    intent.putExtra("code", code.toString());
                    intent.putExtra("email", email);
                    startActivity(intent);
                    finish();
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Code is incorrect!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {

            }
        });
    }
    private int getCodeInputId(int index) {
        switch (index) {
            case 0: return R.id.digit1;
            case 1: return R.id.digit2;
            case 2: return R.id.digit3;
            case 3: return R.id.digit4;
            case 4: return R.id.digit5;
            case 5: return R.id.digit6;
            default: throw new IllegalArgumentException("Invalid code input index: " + index);
        }
    }
}
