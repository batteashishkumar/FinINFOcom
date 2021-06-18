package com.ashish.fininfocom;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Vector;

public class MainActivity extends AppCompatActivity {
    TextInputLayout outlinedTextField1,outlinedTextField2;
    TextInputEditText tvemail,tvPhone;
    Button addButton;
    RecyclerView rv;
    SampleDoArray sampleDoArray;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    SampleDoAdapter sampleDoAdapter=new SampleDoAdapter();
    public final String NOSQL="NOSQL";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        outlinedTextField1=findViewById(R.id.outlinedTextField1);
        outlinedTextField2=findViewById(R.id.outlinedTextField2);
        tvemail=findViewById(R.id.tvemail);
        tvPhone=findViewById(R.id.tvPhone);
        addButton=findViewById(R.id.addButton);

        pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        editor = pref.edit();

        loadAllsampleDos();
        rv=findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(sampleDoAdapter);
    }

    private void loadAllsampleDos() {
        try {
            sampleDoArray= new Gson().fromJson(pref.getString("NOSQL", null), SampleDoArray.class);
            if (sampleDoArray==null)
                sampleDoArray=new SampleDoArray();
        } catch (Exception e) {
            e.printStackTrace();
            sampleDoArray=new SampleDoArray();
        }
    }

    public void add(View v){
        if (!isValidEmail(tvemail.getText().toString().trim())){
            outlinedTextField1.setError("provide proper email");
        }else if(!isValidNumber(tvPhone.getText().toString().trim())){
            outlinedTextField1.setError(null);
            outlinedTextField2.setError("provide proper number which contain 10 digit");
        }
        else if (alreadyExists(new SampleDo(tvemail.getText().toString().trim(),tvPhone.getText().toString().trim()))){
            Toast.makeText(getApplicationContext(), "Person Already Exists", Toast.LENGTH_LONG).show();
        }
        else {
            outlinedTextField1.setError(null);
            outlinedTextField2.setError(null);
            sampleDoArray.persons.add(new SampleDo(tvemail.getText().toString().trim(),tvPhone.getText().toString().trim()));
            editor.putString("NOSQL",new Gson().toJson(sampleDoArray));
            editor.commit();
            sampleDoAdapter.notifyDataSetChanged();
        }


    }

    private boolean alreadyExists(SampleDo sampleDo) {
        for (SampleDo sampleDo1:sampleDoArray.persons){
            if (sampleDo1.email.equalsIgnoreCase(sampleDo.email))
                return true;
        }
        return false;
    }

    private boolean isValidNumber(String toString) {
        return toString.length()==10?true:false;
    }

    public boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    public class SampleDoAdapter extends RecyclerView.Adapter<SampleDoAdapter.ViewHolder> {

        @NonNull
        @org.jetbrains.annotations.NotNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull @org.jetbrains.annotations.NotNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View listItem= layoutInflater.inflate(R.layout.cell, parent, false);
            ViewHolder viewHolder = new ViewHolder(listItem);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull @org.jetbrains.annotations.NotNull MainActivity.SampleDoAdapter.ViewHolder holder, int position) {
        holder.textView.setText(sampleDoArray.persons.get(position).email);
        holder.textView2.setText(sampleDoArray.persons.get(position).number);
        }

        @Override
        public int getItemCount() {
            return sampleDoArray.persons.size();
        }

        public  class ViewHolder extends RecyclerView.ViewHolder {
            public TextView textView,textView2;
            public ViewHolder(View itemView) {
                super(itemView);
                this.textView = (TextView) itemView.findViewById(R.id.textView);
                this.textView2 = (TextView) itemView.findViewById(R.id.textView2);
            }
        }
    }
    private void writeToFile(String data, Context context) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("json.txt", Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
           e.printStackTrace();
        }
    }
}