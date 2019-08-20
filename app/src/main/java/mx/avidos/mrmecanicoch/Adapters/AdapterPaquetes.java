package mx.avidos.mrmecanicoch.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.List;
import mx.avidos.mrmecanicoch.Entidad.Paquete;
import mx.avidos.mrmecanicoch.PaqueteDetail;
import mx.avidos.mrmecanicoch.R;

public class AdapterPaquetes extends RecyclerView.Adapter<AdapterPaquetes.RecyclerPaquetes> implements View.OnClickListener{
    public AdapterPaquetes(List<Paquete> paquetes) {
        this.paquetes = paquetes;
    }

    private List<Paquete> paquetes;
    private View.OnClickListener listener;
    @NonNull
    @Override
    public AdapterPaquetes.RecyclerPaquetes onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_paquetes, viewGroup, false);
        AdapterPaquetes.RecyclerPaquetes recyclerPaquetes = new AdapterPaquetes.RecyclerPaquetes(v);
        v.setOnClickListener(this);
        return recyclerPaquetes;
    }

    @Override
    public void onBindViewHolder(@NonNull final AdapterPaquetes.RecyclerPaquetes recyclerPaquetes, final int i) {
        final Paquete paquete = paquetes.get(i);
        String url = paquete.getUrl();

        if (url != null){
            Glide.with(recyclerPaquetes.context).load(url).into(recyclerPaquetes.ivImagePaquete);
        }

        recyclerPaquetes.tvNombre.setText(paquete.getPaquete());
        recyclerPaquetes.tvDescripcionCorta.setText(paquete.getDescripcion_corta());
        recyclerPaquetes.tvNombre2.setText(paquete.getPaquete()+" incluye:");
        recyclerPaquetes.tvDescripcionLarga.setText(" - "+paquete.getDescripcion_larga().replace("/", "\n \n - "));

        recyclerPaquetes.tvVerMas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(recyclerPaquetes.context, PaqueteDetail.class);
                intent.putExtra("UidAuto", paquete.getUidAutomovil());
                intent.putExtra("UidPaquete", paquete.getUidPaquete());
                intent.putExtra("Nombre", paquete.getPaquete());
                intent.putExtra("DescripcionCorta", paquete.getDescripcion_corta());
                intent.putExtra("DescripcionLarga", paquete.getDescripcion_larga());
                intent.putExtra("Url", paquete.getUrl());
                intent.putExtra("Precio", String.valueOf(paquete.getPrecio()));
                recyclerPaquetes.context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return paquetes.size();
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

    public static class RecyclerPaquetes extends RecyclerView.ViewHolder implements View.OnClickListener {

        DatabaseReference mDatabaseReference;
        FirebaseAuth mAuth;
        Context context;
        TextView tvNombre, tvDescripcionCorta, tvNombre2, tvDescripcionLarga, tvVerMas;
        ImageView ivImagePaquete;
        List<Paquete> asignaciones;

        public RecyclerPaquetes(View itemView, List<Paquete> asignaciones) {
            super(itemView);
            this.asignaciones = asignaciones;
        }

        public RecyclerPaquetes(@NonNull View itemView) {
            super(itemView);
            context = itemView.getContext();
            mDatabaseReference = FirebaseDatabase.getInstance().getReference();
            mAuth = FirebaseAuth.getInstance();
            ivImagePaquete = itemView.findViewById(R.id.ivImagePaquete);
            tvNombre = itemView.findViewById(R.id.tvNombre);
            tvDescripcionCorta = itemView.findViewById(R.id.tvDescripcionCorta);
            tvNombre2 = itemView.findViewById(R.id.tvNombre2);
            tvDescripcionLarga = itemView.findViewById(R.id.tvDescripcionLarga);
            tvVerMas = itemView.findViewById(R.id.tvVerMas);
        }

        @Override
        public void onClick(View view) {

        }
    }
}
