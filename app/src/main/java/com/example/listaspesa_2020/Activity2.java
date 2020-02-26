package com.example.listaspesa_2020;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public class Activity2 extends AppCompatActivity
{
    final int N_MAX_ELEMENTI=60;
    Realm realm;
    String parametro;
    public static Activity fa;
    LinearLayout pricipal_layout;
    ScrollView scroll_layout;
    TableLayout tabella_layout;
    int i;
    public boolean frecce=false;
    EditText[] vet_voci = new EditText[N_MAX_ELEMENTI];
    Button[] vet_bottoni = new Button[N_MAX_ELEMENTI];
    Button[] vet_bottoni_fs = new Button[N_MAX_ELEMENTI]; //bottoni frecciasu
    Button[] vet_bottoni_fg = new Button[N_MAX_ELEMENTI]; //bottoni frecciagiu'

   // ImageButton[] vet_bottoni_fs = new ImageButton[N_MAX_ELEMENTI]; //bottoni frecciasu
   // Button[] vet_bottoni_fg = new Button[N_MAX_ELEMENTI]; //bottoni frecciagiu'

    int screenWidth;
    CheckBox[] vet_spunta= new CheckBox[N_MAX_ELEMENTI];
    int vet_validi[]=new int [N_MAX_ELEMENTI] ;
    Button b_piu;
    int larghezza_testo=250;
    int altezza_testo=30;

    public boolean onCreateOptionsMenu (Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.mio_menu_voce1)   // copia nella Cliboard tutte le voci della lista
        {
            salva_voci_in_realm();
            Toast.makeText(Activity2.this, "Lista copiata", Toast.LENGTH_SHORT).show();
            //  ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
                android.text.ClipboardManager clipboard = (android.text.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                clipboard.setText(accoda_in_stringa_voci_memorizzate(parametro));
            } else {
                android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", accoda_in_stringa_voci_memorizzate(parametro));
                clipboard.setPrimaryClip(clip);
            }
            return true;
        }
        if (id == R.id.mio_menu_voce2) {
            Toast.makeText(Activity2.this, "Esce dal menu", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fa = this;
        setContentView(R.layout.activity_main);
        setTitle("Lista della spesa");
        i=0;

        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE); // the results will be higher than using the activity context object or the getWindowManager() shortcut
        wm.getDefaultDisplay().getMetrics(displayMetrics);
        screenWidth = displayMetrics.widthPixels;
        int screenHeight = displayMetrics.heightPixels;

        larghezza_testo=(int) (screenWidth * 0.65);

        /*
        if (savedInstanceState != null) {
            text1.setText(savedInstanceState.getString("text1", ""));
            // do this for each of your text views
        }
*/

        parametro = getIntent().getExtras().getString("parametro");
        pricipal_layout = new LinearLayout(this);
        pricipal_layout.setOrientation(LinearLayout.VERTICAL);
        scroll_layout = new ScrollView(this);
        tabella_layout = new TableLayout(this);
        b_piu = new Button(this);
        b_piu.setTextSize(altezza_testo);
        b_piu.setText(" + ");

        b_piu.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v)
            {
                //Toast.makeText(Activity2.this, "AGGIUNTA",Toast.LENGTH_SHORT).show();
                aggiungiVoce();
            }
        });
/*
        b_torna=new Button(this);
        b_torna.setTextSize(altezza_testo);
        b_torna.setText("Indietro da "+parametro);
        b_torna.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                Toast.makeText(Activity2.this, "INDIETRO", Toast.LENGTH_SHORT).show();
                salva_voci_in_realm();
                Intent intent2 = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent2);
            }
        });
*/
        scroll_layout.addView(tabella_layout);
        pricipal_layout.addView(b_piu);
        //pricipal_layout.addView(b_torna);
        pricipal_layout.addView(scroll_layout);

        setContentView(pricipal_layout);
        //se la lista è nel DB, allora mostrala a video e poni i=lung. lista, se no ritorna e i=0
        mostra_voci_memorizzate(parametro);
        hideKeyboard();

    }  // ************************ fine on Create

    void aggiungiVoce()
    {
        //****************************************************************************
        if (i>=N_MAX_ELEMENTI)
        {
            Toast.makeText(Activity2.this, "Lunghezza massima lista raggiunta",Toast.LENGTH_LONG).show();
            return;
        }

        //-------------------------------------------------
        // da settare con voce di menu
        if (frecce)
            larghezza_testo=(int) (screenWidth * 0.25);
        TableRow tableRow = new TableRow(this);
        tableRow.setGravity(Gravity.CENTER);
        vet_validi[i]=1;
        vet_spunta[i]=new CheckBox(this);
        tableRow.addView(vet_spunta[i]);
        vet_spunta[i].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                aggiorna_aspetto_voci();
            }
        });
        vet_voci[i] = new EditText(getApplicationContext());

        vet_voci[i].setTextColor(Color.parseColor("#000000"));
        vet_voci[i].setHeight(altezza_testo);
        vet_voci[i].setWidth(larghezza_testo);
        vet_voci[i].setSingleLine(false);
        vet_voci[i].setBackgroundColor(Color.WHITE);
        vet_voci[i].setTextColor(Color.BLACK);

// da abilitare se telefono è nuovo
        vet_voci[i].requestFocus();
        InputMethodManager inputMethodManager=(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        if( ! inputMethodManager.isAcceptingText())
            inputMethodManager.toggleSoftInputFromWindow(pricipal_layout.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);

        tableRow.addView(vet_voci[i]);
/*
            vet_voci[i].setOnLongClickListener(
                    new View.OnLongClickListener() {
                        public boolean onLongClick(View view) {
                            //view.setBackgroundColor(Color.GREEN);
                            //Toast.makeText(MainActivity.this, "-------",
                            // Toast.LENGTH_SHORT).show();

                            return false;
                        }
                    }
            );
           // REM vet_voci_contenuto[i]=vet_voci[i].getText().toString();
*/
        vet_bottoni[i] = new Button(getApplicationContext());
        vet_bottoni[i].setText("-");
        vet_bottoni[i].setTextColor(Color.parseColor("#000033"));
        vet_bottoni[i].setTextSize(altezza_testo-(altezza_testo/5));
        vet_bottoni[i].setBackgroundColor(Color.LTGRAY);
        tableRow.addView(vet_bottoni[i]);
            vet_bottoni_fs[i] = new Button(getApplicationContext());
            vet_bottoni_fs[i].setText("\u2191");
            vet_bottoni_fs[i].setTextColor(Color.parseColor("#330000"));
            vet_bottoni_fs[i].setTextSize(altezza_testo - (altezza_testo / 4));
            vet_bottoni_fs[i].setBackgroundColor(Color.LTGRAY);
            tableRow.addView(vet_bottoni_fs[i]);
            vet_bottoni_fs[i].setWidth(20);
            //non si vede e non occupa spazio

            vet_bottoni_fg[i] = new Button(getApplicationContext());
            vet_bottoni_fg[i].setText("\u2193");
            vet_bottoni_fg[i].setTextColor(Color.parseColor("#330033"));
            vet_bottoni_fg[i].setTextSize(altezza_testo - (altezza_testo / 4));
            vet_bottoni_fg[i].setBackgroundColor(Color.LTGRAY);
            tableRow.addView(vet_bottoni_fg[i]);
            vet_bottoni_fg[i].setWidth(20);
        //-------------------------------------------------
        // da settare con voce di menu
        if(!frecce) {
            vet_bottoni_fs[i].setVisibility(View.GONE);
            vet_bottoni_fg[i].setVisibility(View.GONE);
            larghezza_testo = (int) (screenWidth * 0.25);
        }

        View riga = (View) vet_voci[i].getParent();
        vet_bottoni[i].setOnClickListener(sulClickCancella(vet_spunta[i],vet_voci[i],vet_bottoni[i],riga, i));
        vet_bottoni_fs[i].setOnClickListener(sulClickPortaSu(vet_spunta[i],vet_voci[i],vet_bottoni[i],riga, i));
        vet_bottoni_fg[i].setOnClickListener(sulClickPortaGiu(vet_spunta[i],vet_voci[i],vet_bottoni[i],riga, i));

        i++;
        tabella_layout.addView(tableRow);
        //tabella_layout.layout(20,20,20,20);
        //------------------- dare il focus alla nuova voce ---------------------------------
    }

    View.OnClickListener sulClickCancella(final CheckBox ckb, final TextView tv, final Button button, final View riga, final int k )
    {
        return new View.OnClickListener() {
            public void onClick(View v) {
                // String s=button.getText().toString();
                // String ss=tv.getText().toString();
                // Toast.makeText(MainActivity.this, s+" "+ss+"  ",Toast.LENGTH_SHORT).show();
                tabella_layout.removeView(riga);
                vet_validi[k]=-1;
                aggiorna_aspetto_voci();
            }
        };
    }
    View.OnClickListener sulClickPortaSu(final CheckBox ckb, final TextView tv, final Button button, final View riga, final int k )
    {
        return new View.OnClickListener() {
            public void onClick(View v) {
                //porta su la riga con la voce di menu cliccata
                /*******************************************/
                if (k>0)
                    scambia_riga(k,k-1);
                aggiorna_aspetto_voci();
            }
        };
    }


    View.OnClickListener sulClickPortaGiu(final CheckBox ckb, final TextView tv, final Button button, final View riga, final int k )
    {
        return new View.OnClickListener() {
            public void onClick(View v) {
                //porta giù la riga con la voce di menu cliccata
                /*******************************************/
                if (k<vet_voci.length-1)
                    scambia_riga(k,k+1);
                aggiorna_aspetto_voci();
            }
        };
    }

    void aggiorna_aspetto_voci() //  aggiorna l'aspetto  della voce
    {
        for (int z=0;z<i;z++){
            if (vet_validi[z]==1)
            {
                if (vet_spunta[z].isChecked()){
                    vet_voci[z].setText(vet_voci[z].getText().toString());
                    vet_voci[z].setPaintFlags(vet_voci[z].getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    vet_bottoni[z].setBackgroundColor(Color.GREEN);
                    //vet_spunta_contenuto[z]='S';
                }
                else{
                    vet_bottoni[z].setBackgroundColor(Color.LTGRAY);
                    //vet_spunta_contenuto[z]='N';
                    vet_voci[z].setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
                    vet_voci[z].setTypeface(null, Typeface.NORMAL);
                    vet_voci[z].setPaintFlags(257);
                    vet_voci[z].setText(vet_voci[z].getText().toString());

                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        salva_voci_in_realm();
        Intent intent2 = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(intent2);
        finish();
        //moveTaskToBack(true);
    }

    //salva tutto quando esci o ruoti lo schermo o quando si spegne lo schermo o altro
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        salva_voci_in_realm();
        outState.putString("parametro",parametro);
    }

    // ripristina lo stato
    protected void onRestoreInstanceState(Bundle savedState) {
        super.onRestoreInstanceState(savedState);
        //parametro=savedState.getString("parametro");
        parametro = getIntent().getExtras().getString("parametro");
        mostra_voci_memorizzate(parametro);
        hideKeyboard();
    }


    //------------ Mostra l'elenco delle voci memorizzate in quella lista prelevandole dal DB --------------
    void mostra_voci_memorizzate(String parametro)
    {
        //se tastiera virtuale attiva, falla scomparire
        Realm.init(getApplicationContext());
        RealmConfiguration realmConfig = new RealmConfiguration.
                Builder().
                deleteRealmIfMigrationNeeded().
                build();
        //RealmConfiguration realmConfig = new RealmConfiguration.Builder(getApplicationContext()).deleteRealmIfMigrationNeeded().build();
        Realm.setDefaultConfiguration(realmConfig);
        realm = Realm.getDefaultInstance();
        RealmQuery<Lista> query = realm.where(Lista.class);
        query.equalTo("id_lista", parametro);
        RealmResults<Lista> result1 = query.findAll();
        if (result1==null)
        {
            Toast.makeText(Activity2.this,"NUOVA lista", Toast.LENGTH_LONG).show();
            return;
        }
        i=result1.first().lista.size();
        tabella_layout.removeAllViews();
        for (int k=0;k<i;k++){
            if (result1.first().lista.get(k).getCancellata())
                vet_validi[k]=-1;
            else
            {
                vet_validi[k]=1;
                TableRow tableRow=new TableRow(this);
                tableRow.setGravity(Gravity.CENTER);
                vet_spunta[k]=new CheckBox(this);
                tableRow.addView(vet_spunta[k]);
                vet_spunta[k].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        aggiorna_aspetto_voci();
                    }
                });
                vet_voci[k] = new EditText(getApplicationContext());
                vet_voci[k].setTextColor(Color.parseColor("#000000"));
                vet_voci[k].setHeight(altezza_testo);
                vet_voci[k].setWidth(larghezza_testo);
                vet_voci[k].setSingleLine(false);
                vet_voci[k].setText(result1.first().lista.get(k).getVoce());
                vet_voci[k].setBackgroundColor(Color.WHITE);
                vet_voci[k].setTextColor(Color.BLACK);
                // Toast.makeText(MainActivity.this, s_voci[k], Toast.LENGTH_SHORT).show();
                tableRow.addView(vet_voci[k]);
                vet_bottoni[k] = new Button(getApplicationContext());
                vet_bottoni[k].setText("-");
                vet_bottoni[k].setTextColor(Color.parseColor("#000033"));
                vet_bottoni[k].setTextSize(25);
                vet_bottoni[k].setBackgroundColor(Color.LTGRAY);
                tableRow.addView(vet_bottoni[k]);
                View riga = (View) vet_voci[k].getParent();
                vet_bottoni[k].setOnClickListener(sulClickCancella(vet_spunta[k],vet_voci[k],vet_bottoni[k],riga, k));
                if (result1.first().lista.get(k).getFlaggata()){
                    vet_spunta[k].setChecked(true);
                    vet_bottoni[k].setBackgroundColor(Color.GREEN);
                }
                else
                {
                    vet_spunta[k].setChecked(false);
                    vet_bottoni[k].setBackgroundColor(Color.LTGRAY);
                }

                if(!frecce) {
                    vet_bottoni_fs[i].setVisibility(View.GONE);
                    vet_bottoni_fg[i].setVisibility(View.GONE);
                    larghezza_testo = (int) (screenWidth * 0.25);
                }


                tabella_layout.addView(tableRow);
            }// fine else
        }  //fine for
        aggiorna_aspetto_voci();
        hideKeyboard();
    } // fine mostra_voci_memorizzate()


    // -------------- Salva le voci di quella lista nel DB
    void salva_voci_in_realm()
    {
        cancella_voci_di_quella_lista(parametro);
        Realm.init(getApplicationContext());
        RealmConfiguration realmConfig = new RealmConfiguration.
                Builder().
                deleteRealmIfMigrationNeeded().
                build();
        //RealmConfiguration realmConfig = new RealmConfiguration.Builder(getApplicationContext()).deleteRealmIfMigrationNeeded().build();
        Realm.setDefaultConfiguration(realmConfig);
        realm = Realm.getDefaultInstance();
        RealmQuery<Lista> query = realm.where(Lista.class);
        query.equalTo("id_lista", parametro);
        RealmResults<Lista> result1 = query.findAll();

        for (int j=0;j<i;j++){
            //aggiungi un elem alla lista result1
            if(vet_validi[j]==1)
            {
                Voce vo=new Voce();
                vo.setVoce(vet_voci[j].getText().toString());
                vo.setFlaggata(vet_spunta[j].isChecked());
                if (vet_validi[j]==-1)
                    vo.setCancellata(true);
                else
                    vo.setCancellata(false);
                realm.beginTransaction();
                result1.first().lista.add(vo);
                // Toast.makeText(Activity2.this,vo.getVoce()+" "+j,Toast.LENGTH_SHORT).show();
                realm.commitTransaction();
                // realm.close();
                // nuovo_i++;
            }
        }
    }

    void cancella_voci_di_quella_lista(String parametro)
    {
        Realm.init(getApplicationContext());
        RealmConfiguration realmConfig = new RealmConfiguration.
                Builder().
                deleteRealmIfMigrationNeeded().
                build();
        //RealmConfiguration realmConfig = new RealmConfiguration.Builder(getApplicationContext()).deleteRealmIfMigrationNeeded().build();
        Realm.setDefaultConfiguration(realmConfig);
        realm = Realm.getDefaultInstance();
        RealmQuery<Lista> query = realm.where(Lista.class);
        query.equalTo("id_lista", parametro);
        RealmResults<Lista> result1 = query.findAll();
        if (result1==null)
            return;
        realm.beginTransaction();
        result1.first().lista.deleteAllFromRealm();
        realm.commitTransaction();
        //realm.close();
    }


    void scambia_riga(int uno, int due)
    {
        String voce;
        boolean spunta;
        int  valid;
        //salvo le informazioni relative a quella riga nelle var temporanee
        voce=vet_voci[uno].getText().toString();
        spunta=vet_spunta[uno].isChecked();
        valid=vet_validi[uno];

        //assegno alla riga uno la riga due
        vet_voci[uno].setText(vet_voci[due].getText());
        vet_spunta[uno].setChecked(vet_spunta[due].isChecked());
        vet_validi[uno]=vet_validi[due];

        //assegno alla riga due i valori salvati di riga uno
        vet_voci[due].setText(voce);
        vet_spunta[due].setChecked(spunta);
        vet_validi[due]=valid;
    }

    //------------ accoda in una stringa le voci memorizzate in quella lista prelevandole dal DB --------------
    String accoda_in_stringa_voci_memorizzate(String parametro)
    {
        String s="";
        Realm.init(getApplicationContext());
        RealmConfiguration realmConfig = new RealmConfiguration.
                Builder().
                deleteRealmIfMigrationNeeded().
                build();
        //RealmConfiguration realmConfig = new RealmConfiguration.Builder(getApplicationContext()).deleteRealmIfMigrationNeeded().build();
        Realm.setDefaultConfiguration(realmConfig);
        realm = Realm.getDefaultInstance();
        RealmQuery<Lista> query = realm.where(Lista.class);
        query.equalTo("id_lista", parametro);
        RealmResults<Lista> result1 = query.findAll();
        if (result1==null)
        {
            Toast.makeText(Activity2.this,"Lista vuota", Toast.LENGTH_LONG).show();
            return s;
        }
        i=result1.first().lista.size();
        for (int k=0;k<i;k++)
        {
            if (!result1.first().lista.get(k).getCancellata())
            {
                s=s+"\n \u2022 "+(result1.first().lista.get(k).getVoce());
            }
        }
        return s;
    } // fine accoda_in_stringa_voci_memorizzate

    private void hideKeyboard()
    {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        Log.d("----------------->","Passato in hideKeyboard");
    }


}// fine classe della activity
