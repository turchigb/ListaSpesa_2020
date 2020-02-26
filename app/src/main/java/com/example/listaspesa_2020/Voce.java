package com.example.listaspesa_2020;


/**
 * Created by papa on 21/08/2016.
 */
import io.realm.RealmObject;

/**
 * Created by papa on 08/08/2016.
 */
public class Voce extends RealmObject {

    private String          voce;
    private boolean         flaggata=false;
    private boolean         cancellata=false;

    // Standard getters & setters
    public String getVoce() { return voce; }
    public void   setVoce(String voce) { this.voce = voce; }
    public boolean    getFlaggata() { return flaggata; }
    public void   setFlaggata(boolean flaggata) { this.flaggata = flaggata; }
    public boolean    getCancellata() { return cancellata; }
    public void   setCancellata(boolean cancellata) { this.cancellata = cancellata; }
}
