package it.unipd.dei.esp1617.h2o;

import java.io.Serializable;
import java.util.Calendar;

/**
 * Created by boemd on 10/05/2017.
 */

class NotificationTemplate implements Serializable{ //Serializable perché gli oggetti di questa classe andranno scritti su file tramite stream
    private int id;
    private Calendar when;
    private int numberOfGlasses;

    /**
     * ogni oggetto di questa classe contiene al suo interno le informazioni necessarie alla creazione di una notifica
     * @param id
     * @param when
     * @param numberOfGlasses
     */
    NotificationTemplate(int id, Calendar when, int numberOfGlasses){
        this.id=id;
        this.when=when;
        this.numberOfGlasses=numberOfGlasses;
    }

    int getId() {
        return id;
    }

    Calendar getWhen() {
        return when;
    }

    int getNumberOfGlasses() {
        return numberOfGlasses;
    }
}
