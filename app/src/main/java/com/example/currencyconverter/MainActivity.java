package com.example.currencyconverter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    private Spinner spinner1,spinner2;
    private EditText editText;
    private TextView result;
    private Button submit;
    private ProgressBar load,submitload;
    private String input="";
    private String localch1=null;
    private String localch2=null;
    private DatabaseReference RootRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RootRef = FirebaseDatabase.getInstance().getReference();


        spinner1 = findViewById(R.id.spinner1);
        spinner2 = findViewById(R.id.spinner2);
        editText = findViewById(R.id.edittext);
        result = findViewById(R.id.showresult);
        submit = findViewById(R.id.button);
        load = findViewById(R.id.load);
        submitload = findViewById(R.id.submit_load);





        HELLOMETHOD();
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitload.setVisibility(View.VISIBLE);
                String input = editText.getText().toString();
                String s1 = spinner1.getSelectedItem().toString();
                String s2 = spinner2.getSelectedItem().toString();
                String real1 = s1.substring(s1.length() - 3);
                String real2 = s2.substring(s2.length() - 3);
                if (TextUtils.isEmpty(input)) {
                    Toast.makeText(MainActivity.this, "Enter some value....", Toast.LENGTH_SHORT).show();
                } else {
                    FetchData(input, real1, real2);
                }
            }
        });

    }


        private void HELLOMETHOD()
    {
            load.setVisibility(View.VISIBLE);
            RootRef.child("Country").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    final List<String> list = new ArrayList<String>();

                   // upto this ok

                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        String name2 = dataSnapshot.child("code").getValue(String.class);
                        String name1 = dataSnapshot.child("name").getValue(String.class);

                        String finalstr = name1 + " \t " + name2;

                        list.add(finalstr);
                    }


                    ArrayAdapter<String> arrayAdapter1 = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_item,list);
                    arrayAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner1.setAdapter(arrayAdapter1);


                    ArrayAdapter<String> arrayAdapter2 = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_item,list);
                    arrayAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner2.setAdapter(arrayAdapter2);

                    load.setVisibility(View.GONE);


                    localch1 = spinner1.getSelectedItem().toString();
                    localch2 = spinner2.getSelectedItem().toString();

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }



    private void FetchData(String input, String real1, String real2) {


        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="https://free.currconv.com/api/v7/convert?q="+real1+"_"+real2+"&compact=ultra&apiKey=c3f7eb8a14ad2fa9afa4";

// Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                     float igo=Float.parseFloat(input);

                      try {

                          JSONObject jsonObject = new JSONObject(response.toString());
                       float string=Float.parseFloat(jsonObject.getString(real1+"_"+real2));
                        float rs=string*igo;
                        result.setText(String.valueOf(rs));

                        submitload.setVisibility(View.GONE);
                      }catch (JSONException e){
                          e.printStackTrace();
                          submitload.setVisibility(View.GONE);
                      }

                      submitload.setVisibility(View.GONE);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "error", Toast.LENGTH_SHORT).show();
            }
        });

// Add the request to the RequestQueue.
        queue.add(stringRequest);



    }
}