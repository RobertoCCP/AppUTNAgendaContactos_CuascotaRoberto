package com.example.apputncontactos;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Map;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    EditText txtNombres, txtTelefono, txtEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        txtNombres = findViewById(R.id.txtNombres);
        txtTelefono = findViewById(R.id.txtTelefono);
        txtEmail = findViewById(R.id.txtEmail);
    }

    public void cmdGuardar_onClick(View v) {
        String nombres = txtNombres.getText().toString().trim();
        String telefono = txtTelefono.getText().toString().trim();
        String email = txtEmail.getText().toString().trim();

        if (nombres.isEmpty() || telefono.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Por favor, completa todos los campos.", Toast.LENGTH_LONG).show();
        } else if (!isValidEmail(email)) {
            Toast.makeText(this, "Ingresa un correo electrónico válido.", Toast.LENGTH_LONG).show();
        } else if (telefono.length() != 10) {
            Toast.makeText(this, "El número de teléfono debe tener 10 dígitos.", Toast.LENGTH_LONG).show();
        } else {
            SharedPreferences preferences = getSharedPreferences("datos", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();

            String idContacto = UUID.randomUUID().toString();

            editor.putString("nombres_" + idContacto, nombres);
            editor.putString("telefono_" + idContacto, telefono);
            editor.putString("email_" + idContacto, email);
            editor.commit();

            Toast.makeText(this, "Datos Guardados Correctamente!!", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public void cmdBuscar_onClick(View v) {
        String nombreABuscar = txtNombres.getText().toString().trim();

        // Verificar si el campo de nombre está vacío
        if (nombreABuscar.isEmpty()) {
            Toast.makeText(this, "Por favor, ingresa un nombre para buscar los datos.", Toast.LENGTH_LONG).show();
            return; // Salir del método si no se ha ingresado un nombre
        }

        SharedPreferences preferences = getSharedPreferences("datos", Context.MODE_PRIVATE);
        Map<String, ?> allEntries = preferences.getAll();

        boolean contactoEncontrado = false;
        String nombreEncontrado = "";
        String telefonoEncontrado = "";
        String emailEncontrado = "";

        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            if (entry.getKey().startsWith("nombres_")) {
                String idContacto = entry.getKey().substring("nombres_".length());

                String nombreGuardado = preferences.getString("nombres_" + idContacto, "");

                // Comprobar si el nombre guardado contiene la cadena buscada
                if (nombreGuardado.toLowerCase().contains(nombreABuscar.toLowerCase())) {
                    nombreEncontrado = nombreGuardado;
                    telefonoEncontrado = preferences.getString("telefono_" + idContacto, "");
                    emailEncontrado = preferences.getString("email_" + idContacto, "");
                    contactoEncontrado = true;
                    break;
                }
            }
        }

        if (contactoEncontrado) {
            txtNombres.setText(nombreEncontrado);
            txtTelefono.setText(telefonoEncontrado);
            txtEmail.setText(emailEncontrado);
            Toast.makeText(this, "Contacto encontrado y cargado en campos", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "No se encontraron datos para el nombre ingresado.", Toast.LENGTH_LONG).show();
        }
    }

    public  void cmdLimpiar_onClick(View v){
        txtNombres.setText("");
        txtTelefono.setText("");
        txtEmail.setText("");
    }
}