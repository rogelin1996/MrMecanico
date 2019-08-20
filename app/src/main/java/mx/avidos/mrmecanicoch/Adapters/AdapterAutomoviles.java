package mx.avidos.mrmecanicoch.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import mx.avidos.mrmecanicoch.Entidad.Automovil;
import mx.avidos.mrmecanicoch.Entidad.Paquete;
import mx.avidos.mrmecanicoch.PaqueteDetail;
import mx.avidos.mrmecanicoch.R;

public class AdapterAutomoviles extends RecyclerView.Adapter<AdapterAutomoviles.Recycler> implements View.OnClickListener{
    public AdapterAutomoviles(List<Automovil> automovils) {
        this.automovils = automovils;
    }

    private List<Automovil> automovils;
    private View.OnClickListener listener;
    @NonNull
    @Override
    public AdapterAutomoviles.Recycler onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_automovil, viewGroup, false);
        AdapterAutomoviles.Recycler recyclerPaquetes = new AdapterAutomoviles.Recycler(v);
        v.setOnClickListener(this);
        return recyclerPaquetes;
    }

    @Override
    public void onBindViewHolder(@NonNull final AdapterAutomoviles.Recycler recycler, final int i) {
        final Automovil automovil = automovils.get(i);
        recycler.etMarca.setText(automovil.getMarca());
        recycler.etModelo.setText(automovil.getModelo());
        recycler.etPlaca.setText(automovil.getPlaca());

        if (automovils.size() > 1){
            recycler.ibDeleteCar.setVisibility(View.VISIBLE);
        } else {
            recycler.ibDeleteCar.setVisibility(View.GONE);
        }

        recycler.ibDeleteCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recycler.alertDialog = new AlertDialog.Builder(recycler.context)
                        .setMessage(recycler.context.getString(R.string.string_eliminar_vehiculo))
                        .setPositiveButton(recycler.context.getString(R.string.string_eliminar), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                recycler.mDatabaseReference.child("usuarios").child(recycler.mAuth.getCurrentUser().getUid()).child("automoviles").child(automovil.getUidAutomovil()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        recycler.alertDialog.dismiss();
                                        if (task.isSuccessful()){
                                            Toast.makeText(recycler.context, recycler.context.getString(R.string.string_vehiculo_eliminado_exito), Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(recycler.context, recycler.context.getString(R.string.string_vehiculo_eliminado_fail), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        })
                        .setNegativeButton(recycler.context.getString(R.string.string_cancelar), null)
                        .show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return automovils.size();
    }

    public void setOnClickListaner(View.OnClickListener listener){
        this.listener = listener;
    }

    @Override
    public void onClick(View v) {
        if(listener != null){
            listener.onClick(v);
        }
    }

    public static class Recycler extends RecyclerView.ViewHolder implements View.OnClickListener {

        DatabaseReference mDatabaseReference;
        FirebaseAuth mAuth;
        Context context;
        EditText etMarca, etModelo, etPlaca;
        ImageButton ibDeleteCar;
        List<Paquete> asignaciones;
        AlertDialog alertDialog;

        public Recycler(View itemView, List<Paquete> asignaciones) {
            super(itemView);
            this.asignaciones = asignaciones;
        }

        public Recycler(@NonNull View itemView) {
            super(itemView);
            context = itemView.getContext();
            mDatabaseReference = FirebaseDatabase.getInstance().getReference();
            mAuth = FirebaseAuth.getInstance();
            etMarca = itemView.findViewById(R.id.etMarca);
            etModelo = itemView.findViewById(R.id.etModelo);
            etPlaca = itemView.findViewById(R.id.etPlaca);
            ibDeleteCar = itemView.findViewById(R.id.ibDeleteCar);
        }

        @Override
        public void onClick(View view) {

        }
    }
}
