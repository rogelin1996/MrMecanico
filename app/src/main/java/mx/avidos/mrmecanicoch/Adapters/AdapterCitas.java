package mx.avidos.mrmecanicoch.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import mx.avidos.mrmecanicoch.CitaDetail;
import mx.avidos.mrmecanicoch.Entidad.Automovil;
import mx.avidos.mrmecanicoch.Entidad.Cita;
import mx.avidos.mrmecanicoch.Entidad.Paquete;
import mx.avidos.mrmecanicoch.R;

public class AdapterCitas extends RecyclerView.Adapter<AdapterCitas.Recycler> implements View.OnClickListener{
    public AdapterCitas(List<Cita> citas) {
        this.citas = citas;
    }

    private List<Cita> citas;
    private View.OnClickListener listener;
    @NonNull
    @Override
    public AdapterCitas.Recycler onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_citas, viewGroup, false);
        AdapterCitas.Recycler recyclerPaquetes = new AdapterCitas.Recycler(v);
        v.setOnClickListener(this);
        return recyclerPaquetes;
    }

    @Override
    public void onBindViewHolder(@NonNull final AdapterCitas.Recycler recycler, final int i) {
        final Cita cita = citas.get(i);
        recycler.tvFecha.setText(recycler.timestampInMillisToStringParse(cita.getFecha()));
        recycler.tvHora.setText(recycler.timestampInMillisToStringParseTime(cita.getFecha()));

        recycler.mDatabaseReference.child("paquetes").child(cita.getUidPaquete()).child("paquete").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                    recycler.tvNombreServicio.setText(dataSnapshot.getValue(String.class));
                else
                    recycler.tvNombreServicio.setText(recycler.context.getString(R.string.string_paquete_eliminado));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        recycler.mDatabaseReference.child("talleres").child(cita.getTalleruid()).child("nombre").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                    recycler.tvNombreTaller.setText(dataSnapshot.getValue(String.class));
                else
                    recycler.tvNombreTaller.setText(recycler.context.getString(R.string.string_taller_eliminado));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        recycler.mDatabaseReference.child("usuarios").child(cita.getUidUsuario()).child("automoviles").child(cita.getAutomovil()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                    recycler.tvAuto.setText(dataSnapshot.child("modelo").getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        recycler.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(recycler.context, CitaDetail.class);
                intent.putExtra("UidCita", cita.getUidCita());
                intent.putExtra("UidPaquete", cita.getUidPaquete());
                intent.putExtra("UidTaller", cita.getTalleruid());
                intent.putExtra("FechaHora", cita.getFecha());
                intent.putExtra("UidAutomovil", cita.getAutomovil());
                recycler.context.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return citas.size();
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
        TextView tvNombreServicio, tvNombreTaller, tvAuto, tvFecha, tvHora;
        List<Paquete> asignaciones;

        public Recycler(View itemView, List<Paquete> asignaciones) {
            super(itemView);
            this.asignaciones = asignaciones;
        }

        public Recycler(@NonNull View itemView) {
            super(itemView);
            context = itemView.getContext();
            mDatabaseReference = FirebaseDatabase.getInstance().getReference();
            mAuth = FirebaseAuth.getInstance();
            tvNombreServicio = itemView.findViewById(R.id.tvNombreServicio);
            tvNombreTaller = itemView.findViewById(R.id.tvNombreTaller);
            tvAuto = itemView.findViewById(R.id.tvAuto);
            tvFecha = itemView.findViewById(R.id.tvFecha);
            tvHora = itemView.findViewById(R.id.tvHora);
        }

        public static String timestampInMillisToStringParse(long timestampInMillis) {
            final Date date = new Date(timestampInMillis);
            try {
                final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
                return simpleDateFormat.format(date);
            } catch (Exception e) {
                return "Error en la fecha";
            }
        }

        public String timestampInMillisToStringParseTime(long timestampInMillis) {
            final Date date = new Date(timestampInMillis);
            try {
                final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("h:mm a", Locale.getDefault());
                return simpleDateFormat.format(date);
            } catch (Exception e) {
                return "Error en la fecha";
            }
        }

        @Override
        public void onClick(View view) {

        }
    }
}