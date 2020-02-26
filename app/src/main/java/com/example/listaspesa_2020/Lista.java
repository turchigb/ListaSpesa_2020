package com.example.listaspesa_2020;



import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by papa on 19/08/2016.
 */
public class Lista extends RealmObject {
    String id_lista;  // id_lista e data_lista riportano entrambe la data di creazione della lista
    Date data_lista;
    RealmList < Voce > lista; //associazione 1:N tra Lista e Voci che la compongono
}