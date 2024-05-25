package org.poo.cb;

class Actiune implements ElementPortofoliu {
    private String numeCompanie;
    private int valoare;

    public Actiune(String numeCompanie, int valoare) {
        this.numeCompanie = numeCompanie;
        this.valoare = valoare;
    }

    @Override
    public String toString() {
        return "{\"stockName\":\"" + numeCompanie + "\",\"amount\":" + valoare + "}";
    }
}
