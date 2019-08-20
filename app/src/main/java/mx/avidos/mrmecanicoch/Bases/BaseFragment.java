package mx.avidos.mrmecanicoch.Bases;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * A simple {@link Fragment} subclass.
 */
public class BaseFragment extends Fragment {

    public View view;
    public LayoutInflater layoutInflater;
    public AlertDialog alertDialog;

    protected FirebaseUser mUser;
    protected FirebaseAuth mAuth;
    protected DatabaseReference mDatabaseReference;
    protected StorageReference mStorageReference;
    protected SharedPreferences.Editor mEditor;

    public String uidPaquete;
    public String nombrePaquete;
    public String desCorta;
    public String desLarga;
    public String urlPaquete;
    public String precioPaquete;

    public String nombre, correoElectronico, telefono, contrasena, placa, marca, modelo, anio, kilometraje, uidAutomovil;

    public BaseFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mStorageReference = FirebaseStorage.getInstance().getReference();

    }

    public String getUid (){
        return mAuth.getCurrentUser().getUid();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        TextView textView = new TextView(getActivity());
        textView.setText("hello_blank_fragment");
        return textView;
    }

    public void focusableOnTouch (final EditText editText){
        editText.setFocusable(false);
        editText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                editText.setFocusableInTouchMode(true);
                return false;
            }
        });
    }

}

