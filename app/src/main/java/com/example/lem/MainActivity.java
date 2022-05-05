package com.example.lem;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
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

public class MainActivity extends AppCompatActivity {
    RequestQueue fileRequeteWS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fileRequeteWS = Volley.newRequestQueue(this);
    }

    public void connect(View v) {
        EditText userPassword = findViewById(R.id.inputPassword);
        String userPasswordToString = userPassword.getText().toString();

        EditText userEmail = findViewById(R.id.inputEmail);
        String userEmaiToString = userEmail.getText().toString();

        String url = "http://10.0.2.2/~timeo.cadouot/Jumati/public/webservice/get_data_user_with_email?email=" + userEmaiToString + "&mdp=" + userPasswordToString ;
        //String url = "http://10.0.2.2/Jumati/public/webservice/get_data_user_with_email?email=" + userEmaiToString + "&mdp=" + userPasswordToString;
        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                url,
                this::parceResultRequest,
                this::getError
        );
        fileRequeteWS.add(stringRequest);

    }

    public void register(View v) {
        Intent register = new Intent(this, Register.class);
        startActivity(register);
    }

    public void parceResultRequest(String reponse) {
        try {
            EditText userPassword = findViewById(R.id.inputPassword);
            String userPasswordToString = userPassword.getText().toString();

            EditText userEmail = findViewById(R.id.inputEmail);
            String userEmaiToString = userEmail.getText().toString();

            JSONArray obj = new JSONArray(reponse);
            //si user existe
            if (obj.getJSONObject(0).getString("user").equals("1")) {
                //si error
                if(obj.getJSONObject(0).getString("error").equals("1")) {
                    Toast.makeText(this,"Password incorrect", Toast.LENGTH_LONG).show();
                } else {
                    //mdp correct
                    UserClass user = new UserClass(String.valueOf(obj.getJSONObject(1).getString("user_id")),
                            String.valueOf(obj.getJSONObject(1).getString("user_email")),
                            String.valueOf(obj.getJSONObject(1).getString("user_activity_id_join")),
                            String.valueOf(obj.getJSONObject(1).getString("user_identifiant_add_friends")),
                            String.valueOf(obj.getJSONObject(1).getString("user_pseudo")),
                            String.valueOf(obj.getJSONObject(1).getString("user_activity_id_create")),
                            String.valueOf(obj.getJSONObject(1).getString("user_statut")));
                    Intent home = new Intent(this, Home.class);
                    home.putExtra("user", (Serializable) user);
                    startActivity(home);
                }
            } else {
                Toast.makeText(this,"Email incorrect", Toast.LENGTH_LONG).show();
                Log.d("user data", String.valueOf(obj.getJSONObject(0)));
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