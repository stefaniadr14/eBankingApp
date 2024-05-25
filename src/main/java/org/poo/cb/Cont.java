package org.poo.cb;

class Cont implements ElementPortofoliu {
    private double sold;
    private String valuta;

    public Cont(String valuta) {
        this.sold = 0;
        this.valuta = valuta;
    }

    public String getValuta() {
        return valuta;
    }

    public double getSold() {
        return sold;
    }

    public void adaugaBani(double suma) {
        sold += suma;
    }

    public void retrageBani(double suma) {
        sold -= suma;
    }

    @Override
    public String toString() {
        return "{\"currencyName\":\"" + valuta + "\",\"amount\":\"" + String.format("%.2f", sold) + "\"}";
    }
}
