package com.ivan.puebafirebase;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ivan.puebafirebase.model.Local;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    EditText text_nombre_local;
    EditText text_telefono_local;
    EditText text_direccion_local;
    DatabaseReference bd;
    ListView listView_locales;
    ArrayList<String> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        text_nombre_local = findViewById(R.id.edt_nombre);
        text_telefono_local = findViewById(R.id.edt_telefono);
        text_direccion_local = findViewById(R.id.edt_direccion);
        bd = FirebaseDatabase.getInstance().getReference("locales");//nos colocamos en el nodo donde vamos a realizar operaciones
        listView_locales = findViewById(R.id.lv_lista_locales);
        escuchaBD();
        escuchaLisView();
    }

    private void escuchaLisView() {
        listView_locales.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            int pos;

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Query q = bd.orderByChild("nombre").equalTo(list.get(position));
                pos = position;
                q.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            Local loc =ds.getValue(Local.class);
                            if (list.get(pos).equals(loc.getNombre())) {
                                text_nombre_local.setText(loc.getNombre());
                                text_telefono_local.setText(loc.getTelefono());
                                text_direccion_local.setText(loc.getDireccion());
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        });
    }

    public void guardarBD(String nom, String tlf, String dir) {
        Local local = new Local(nom, tlf, dir);
        String clave = bd.push().getKey();
        bd.child(clave).setValue(local);
    }

    public void escuchaBD() {
        list = new ArrayList<String>();
        bd.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayAdapter<String> ad;

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Local loc = ds.getValue(Local.class);
                    list.add(loc.getNombre());

                }
                ad = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, list);
                listView_locales.setAdapter(ad);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void onClickAceptar(View view) {
        boolean malReyenado = false;
        if (text_nombre_local.getText().toString().isEmpty()) {
            malReyenado = true;
            Toast.makeText(this, "Debe insertar el nombre.", Toast.LENGTH_SHORT).show();

        }
        if (text_telefono_local.getText().toString().isEmpty()) {
            malReyenado = true;
            Toast.makeText(this, "Debe insertar el telefono.", Toast.LENGTH_SHORT).show();

        }
        if (text_direccion_local.getText().toString().isEmpty()) {
            malReyenado = true;
            Toast.makeText(this, "Debe insertar la direccion.", Toast.LENGTH_SHORT).show();

        }
        if (!malReyenado) {
            guardarBD(text_nombre_local.getText().toString(), text_telefono_local.getText().toString(), text_direccion_local.getText().toString());
            Toast.makeText(this, "Insertado con exito", Toast.LENGTH_SHORT).show();
        }
    }
}
