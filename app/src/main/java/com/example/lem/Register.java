package com.example.lem;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.Serializable;

public class Register extends AppCompatActivity {
    RequestQueue fileRequeteWS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        fileRequeteWS = Volley.newRequestQueue(this);
    }

    public void createAccount(View v) {
        EditText userPseudo = findViewById(R.id.inputPseudo);
        String userPseudoToString = userPseudo.getText().toString();

        EditText userFirstName = findViewById(R.id.inputFirstName);
        String userFirstNameToString = userFirstName.getText().toString();

        EditText userLastName = findViewById(R.id.inputLastName);
        String userLastNameToString = userLastName.getText().toString();

        EditText userAge = findViewById(R.id.inputAge);
        String userAgeToString = userAge.getText().toString();

        EditText userPhone = findViewById(R.id.inputPhone);
        String userPhoneToString = userPhone.getText().toString();

        EditText userEmail = findViewById(R.id.inputRegisterEmail);
        String userEmailToString = userEmail.getText().toString();

        EditText userRegisterPassword = findViewById(R.id.inputRegisterPassword);
        String userRegisterPasswordToString = userRegisterPassword.getText().toString();

        EditText userConfirmPassword = findViewById(R.id.inputConfirmPassword);
        String userConfirmPasswordToString = userConfirmPassword.getText().toString();

        String url = "http://10.0.2.2/~timeo.cadouot/jumati/public/webservice/create_user?pseudo=" + userPseudoToString + "&firstname=" + userFirstNameToString + "&lastname=" + userLastNameToString + "&age=" + userAgeToString+ "&phone=" + userPhoneToString + "&email=" + userEmailToString + "&password=" + userRegisterPasswordToString + "&confirmpass=" + userConfirmPasswordToString;
        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                url,
                this::parceResultRequest,
                this::getError
        );
        fileRequeteWS.add(stringRequest);

    }

    public void parceResultRequest(String reponse) {
        try {
            JSONArray obj = new JSONArray(reponse);
            //si user create
            if (obj.getJSONObject(0).getString("usercreate").equals("1")) {
                    //Log.d("indi", String.valueOf(obj.getJSONObject(1).getString("user_id")));
                    Intent main = new Intent(this, MainActivity.class);
                    startActivity(main);
            } else {
                if (obj.getJSONObject(0).getString("error").equals("1")) {
                    //mail
                    Toast.makeText(this, "Email used", Toast.LENGTH_LONG).show();
                } else if (obj.getJSONObject(0).getString("error").equals("2")) {
                    //password
                    Toast.makeText(this, "Password incorrect", Toast.LENGTH_LONG).show();
                } else if (obj.getJSONObject(0).getString("error").equals("3")) {
                    //phone
                    Toast.makeText(this, "Phone number incorrect", Toast.LENGTH_LONG).show();
                }   else if (obj.getJSONObject(0).getString("error").equals("4")) {
                    //phone
                    Toast.makeText(this, "Email incorrect", Toast.LENGTH_LONG).show();
                }
            }
        } catch (JSONException e) {
            getError(e);
        }
    }

    public void getError(Throwable t) {
        Toast.makeText(this,"Problème de communication", Toast.LENGTH_LONG).show();
        Log.e("CONNECT", "Problème communication serveur", t);
    }
}