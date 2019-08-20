package mx.avidos.mrmecanicoch;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import mx.avidos.mrmecanicoch.Bases.BaseActivity;
import mx.avidos.mrmecanicoch.Fragments.FragmentCitas;
import mx.avidos.mrmecanicoch.Fragments.FragmentNotificaciones;
import mx.avidos.mrmecanicoch.Fragments.FragmentPaquetes;
import mx.avidos.mrmecanicoch.Fragments.FragmentPerfil;
import mx.avidos.mrmecanicoch.Fragments.FragmentTalleres;

public class MainActivity extends BaseActivity {
    private BottomNavigationView bottomNavigationView;
    private static String CONSTAN_FRAGMENT = "FRAGMENTS";
    private static String CONSTAN_FRAGMENT_MAIN = "MAIN";
    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeNavigationBottom();
        loadFragment(new FragmentPaquetes(), CONSTAN_FRAGMENT_MAIN);
        initializeToolbar(getString(R.string.string_paquetes_disponibles));
        bottomNavigationView.getMenu().getItem(1).setChecked(true);
    }

    private void initializeNavigationBottom (){
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_perfil:
                        initializeToolbar(getString(R.string.string_perfil));
                        loadFragment(new FragmentPerfil(), CONSTAN_FRAGMENT);
                        break;
                    case R.id.navigation_paquetes:
                        initializeToolbar(getString(R.string.string_paquetes_disponibles));
                        loadFragment(new FragmentPaquetes(), CONSTAN_FRAGMENT_MAIN);
                        break;
                    case R.id.navigation_talleres:
                        initializeToolbar(getString(R.string.string_mapa_talleres));
                        loadFragment(new FragmentTalleres(), CONSTAN_FRAGMENT);
                        break;
                    case R.id.navigation_citas:
                        initializeToolbar(getString(R.string.string_citas_programadas));
                        loadFragment(new FragmentCitas(), CONSTAN_FRAGMENT);
                        break;
                    case R.id.navigation_notifications:
                        initializeToolbar(getString(R.string.string_notificaciones));
                        loadFragment(new FragmentNotificaciones(), CONSTAN_FRAGMENT);
                        break;
                }
                return true;
            }
        });
    }

    private void initializeToolbar (String titulo){
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(titulo);
    }

    private boolean loadFragment(Fragment fragment, String backStack) {
        if (fragment != null && !fragment.isStateSaved()) {

            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.container, fragment)
                    .addToBackStack(backStack)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit();

            return true;
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_appbar, menu);

        if (getSupportActionBar().getTitle().equals(getString(R.string.string_perfil))) {
            showOption(R.id.action_edit);
        } else {
            hideOption(R.id.action_edit);
        }

        for(int i = 0; i < menu.size(); i++){
            Drawable drawable = menu.getItem(i).getIcon();
            if(drawable != null) {
                drawable.mutate();
                drawable.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
            }
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_close:
                dialogCloseSesion();
                return true;

            case R.id.action_edit:
                Intent intent = new Intent(getApplicationContext(), EditarPerfil.class);
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void dialogCloseSesion (){
        alertDialog = new AlertDialog.Builder(this)
                .setTitle(getString(R.string.string_cerrar_sesion))
                .setMessage(getString(R.string.string_cerrar_sesion_confirm))
                .setPositiveButton(getString(R.string.string_aceptar), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialog.dismiss();
                        auth.signOut();
                    }
                })
                .setNegativeButton(getString(R.string.string_cancelar), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialog.dismiss();
                    }
                })
                .show();
    }

    public void hideOption(int id) {
        if (menu != null) {
            MenuItem item = menu.findItem(id);
            item.setVisible(false);
        }
    }

    public void showOption(int id) {
        if (menu != null) {
            MenuItem item = menu.findItem(id);
            item.setVisible(true);
        }
    }

    @Override
    public void onBackPressed() {
        /*int count = getSupportFragmentManager().getBackStackEntryCount();
        if (count == 1) {
            super.onBackPressed();
        } else {
            int index = ((getSupportFragmentManager().getBackStackEntryCount()) -1);
            getSupportFragmentManager().popBackStack();
            FragmentManager.BackStackEntry backEntry = getSupportFragmentManager().getBackStackEntryAt(index-1);
            int stackId = backEntry.getId();
            bottomNavigationView.getMenu().getItem(stackId).setChecked(true);
        }*/
        if (getSupportFragmentManager().getBackStackEntryCount() > 1){
            if (getSupportActionBar().getTitle().equals(getString(R.string.string_paquetes_disponibles))){
                super.onBackPressed();
            } else {
                hideOption(R.id.action_edit);
                getSupportFragmentManager().popBackStack(CONSTAN_FRAGMENT, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                bottomNavigationView.getMenu().getItem(1).setChecked(true);
                initializeToolbar(getString(R.string.string_paquetes_disponibles));
            }
        } else if (getSupportActionBar().getTitle().equals(getString(R.string.string_paquetes_disponibles))){
            super.onBackPressed();
        }
    }

}
