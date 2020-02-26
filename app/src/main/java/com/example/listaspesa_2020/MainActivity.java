package com.example.listaspesa_2020;

import android.os.Bundle;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmConfiguration.Builder.*;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public class MainActivity extends Activity {
    Realm realm;
    LinearLayout pricipal_layout;
    ScrollView scroll_layout;
    TableLayout tabella_layout;
    int i;
    TextView tv;
    // int vet_validi[]=new int [20] ;
    Button b_piu;
    Button b_cancella_tutto;
    int larghezza_testo = 250;
    int altezza_testo = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Lista della spesa");
        // ------------ Creazione Layout generale -------------
        pricipal_layout = new LinearLayout(this);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.FILL_PARENT, AbsListView.LayoutParams.FILL_PARENT);
        pricipal_layout.setLayoutParams(lp);

        pricipal_layout.setOrientation(LinearLayout.VERTICAL);
        scroll_layout = new ScrollView(this);
        tabella_layout = new TableLayout(this);
        // ------------ Creazione bottoni e textview ------------
        tv = new TextView(this);
        b_piu = new Button(this);
        b_cancella_tutto = new Button(this);
        b_piu.setTextSize(altezza_testo);
        b_cancella_tutto.setTextSize((int) (altezza_testo * .5));
        b_piu.setText("Crea nuova lista + ");
        b_cancella_tutto.setText("Elimina tutte le liste");
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE); // the results will be higher than using the activity context object or the getWindowManager() shortcut
        wm.getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;
        int screenHeight = displayMetrics.heightPixels;
        larghezza_testo = (int) (screenWidth * 0.70);
        // ----- Crea una nuova lista e passa il controllo alla Activity2 ----------------
        b_piu.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Crea una RealmConfiguration che salva il  file Realm nella dir dei file della app
                Realm.init(getApplicationContext());
                RealmConfiguration realmConfig = new RealmConfiguration.
                        Builder().
                        deleteRealmIfMigrationNeeded().
                        build();
                //RealmConfiguration realmConfig = new RealmConfiguration.Builder(getApplicationContext()).deleteRealmIfMigrationNeeded().build();
                //RealmConfiguration realmConfig = new RealmConfiguration().Builder(getApplicationContext()).dele
                Realm.setDefaultConfiguration(realmConfig);
                realm = Realm.getDefaultInstance();
                Intent intent1 = new Intent(getApplicationContext(), Activity2.class);
                // allego alla chiamata della 2^ activity un parametro
                GregorianCalendar gc = new GregorianCalendar();
                Date date = gc.getTime();
                intent1.putExtra("parametro", date.toString());
                realm.beginTransaction();
                Lista miaLista = realm.createObject(Lista.class); // Crea un nuovo oggetto Lista
                miaLista.id_lista = date.toString();
                miaLista.data_lista = date;
                realm.commitTransaction();
                //realm.close();
                startActivity(intent1);
                finish();
            }
        });

        //--------- Cancella tutte le liste ----------------
        b_cancella_tutto.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new AlertDialog.Builder(MainActivity.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Cancellazione di tutte le liste")
                        .setMessage("Sicuro?")
                        .setPositiveButton("Sì", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Realm.init(getApplicationContext());
                                RealmConfiguration realmConfig = new RealmConfiguration.
                                        Builder().
                                        deleteRealmIfMigrationNeeded().
                                        build();
                                //RealmConfiguration realmConfig = new RealmConfiguration.Builder(getApplicationContext()).deleteRealmIfMigrationNeeded().build();
                                Realm.setDefaultConfiguration(realmConfig);
                                realm = Realm.getDefaultInstance();
                                realm.beginTransaction();
                                realm.deleteAll();
                                realm.commitTransaction();
                                //realm.close();
                                // tv.setText("");
                                mostra_liste_memorizzate();
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });

        //---------- Rifinisce il layout generale -------------
        scroll_layout.addView(tabella_layout);
        pricipal_layout.addView(b_piu);
        pricipal_layout.addView((b_cancella_tutto));
        pricipal_layout.addView(tv);
        pricipal_layout.addView(scroll_layout);
        setContentView(pricipal_layout);
        mostra_liste_memorizzate();
    }

    //------------ Mostra l'elenco delle liste memorizzate --------------
    void mostra_liste_memorizzate() {
        Realm.init(getApplicationContext());
        RealmConfiguration realmConfig = new RealmConfiguration.
                Builder().
                deleteRealmIfMigrationNeeded().
                build();
        //RealmConfiguration realmConfig = new RealmConfiguration.Builder(getApplicationContext()).deleteRealmIfMigrationNeeded().build();
        Realm.setDefaultConfiguration(realmConfig);
        realm = Realm.getDefaultInstance();
        RealmQuery<Lista> query = realm.where(Lista.class);
        RealmResults<Lista> result1 = query.findAll();
        tabella_layout.removeAllViews();
        if (!result1.isEmpty()) {
            String s = "";
            String ss = "";
            for (int i = 0; i < result1.size(); i++) {
                //s=(i+1)+" - "+result1.get(i).id_lista;  //lista i-esima
                s = DateToStringItalianStyle(result1.get(i).data_lista);  //lista i-esima
                //Toast.makeText(MainActivity.this,result1.get(i).data_lista.toString(), Toast.LENGTH_SHORT).show();
                if (!result1.get(i).lista.isEmpty())
                    ss = result1.get(i).lista.first().getVoce();  //prima voce della lista i-esima
                if (ss.length() > 30)
                    ss = ss.substring(30);
                TableRow tableRow = new TableRow(this);
                tableRow.setGravity(Gravity.CENTER);
                // tableRow.setBackgroundColor(Color.rgb(228,203,116));
                TextView lis = new TextView(this);
                lis.setText(s + "\n" + ss);
                lis.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                lis.setBackgroundColor(Color.rgb(228, 203, 116));
                lis.setWidth(larghezza_testo);
                tableRow.addView(lis);
                lis.setOnClickListener(sulClickMostra(result1.get(i).id_lista));
                Button but = new Button(this);
                tableRow.addView(but);
                but.setText(" - ");
                but.setOnClickListener(sulClickCancella(result1.get(i).id_lista));
                tabella_layout.addView(tableRow);
            }
        }
    } // fine mostra_liste_memorizzate()


    // --------- Va a Activity2 e le passa l'id (=data passata come parametro) di quella lista  ------
    View.OnClickListener sulClickMostra(final String quella_data) {
        return new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent1 = new Intent(getApplicationContext(), Activity2.class);
                intent1.putExtra("parametro", quella_data);
                startActivity(intent1);
                finish();
            }
        };
    }

    // ---------------- Cancella la lista con l'id (=data) passato come parametro  ------
    View.OnClickListener sulClickCancella(final String quella_data) {
        return new View.OnClickListener() {
            public void onClick(View v) {
                // sei sicuro?
                new AlertDialog.Builder(MainActivity.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Cancellazione della lista")
                        .setMessage("Sicuro?")
                        .setPositiveButton("Sì", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Realm.init(getApplicationContext());
                                RealmConfiguration realmConfig = new RealmConfiguration.
                                        Builder().
                                        deleteRealmIfMigrationNeeded().
                                        build();

                                //RealmConfiguration realmConfig = new RealmConfiguration.Builder(getApplicationContext()).deleteRealmIfMigrationNeeded().build();
                                Realm.setDefaultConfiguration(realmConfig);
                                realm = Realm.getDefaultInstance();
                                RealmQuery<Lista> query = realm.where(Lista.class);
                                query.equalTo("id_lista", quella_data);
                                RealmResults<Lista> result1 = query.findAll();
                                realm.beginTransaction();
                                result1.deleteAllFromRealm();
                                realm.commitTransaction();
                                //realm.close();
                                mostra_liste_memorizzate();
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        };
    }

    String DateToStringItalianStyle(Date d) {
        //String settimana[]= {"sab","dom","lun","mar","mer","gio","ven"};
        String ds = "";
        int giorno, mese, anno, mm, hh;
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(d);
        giorno = gc.get(Calendar.DAY_OF_MONTH);
        mese = gc.get(Calendar.MONTH) + 1;
        anno = gc.get(Calendar.YEAR);
        hh = gc.get(Calendar.HOUR_OF_DAY);
        mm = gc.get(Calendar.MINUTE);
        //ds=settimana[gc.get(Calendar.DAY_OF_WEEK)]+"  ";
        if (giorno < 10)
            ds = ds + "0";
        ds = ds + giorno + "/";
        if (mese < 10)
            ds = ds + "0";
        ds = ds + mese + "/";
        ds = ds + anno + " - ";
        if (hh < 10)
            ds = ds + "0";
        ds = ds + hh + ":";
        if (mm < 10)
            ds = mm + "0";
        ds = ds + mm;


        //ds=giorno+"/"+mese+"/"+anno+" - "+hh+":"+mm;
        return ds;
    }


    // ------------- Se premi Esc ... termine app
    @Override
    public void onBackPressed() {
       /*
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Chiusura Lista Spesa")
                .setMessage("Vuoi proprio uscire?")
                .setPositiveButton("Sì", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (Activity2.fa!=null)
                            Activity2.fa.finish();
                        finish();
                        return;
                    }
                })
                .setNegativeButton("No", null)
                .show();
                */
        Toast.makeText(MainActivity.this, "Liste salvate", Toast.LENGTH_SHORT).show();
        finish();
    }

} //fine activity
