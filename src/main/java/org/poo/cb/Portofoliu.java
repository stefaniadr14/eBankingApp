package org.poo.cb;
import java.util.*;

// Interfața pentru obiectele din portofoliul utilizatorului
interface ElementPortofoliu {
    public String toString();
}

// Clasa Composite pentru portofoliul utilizatorului care conține atât conturi în diverse valute, cât și acțiuni
class Portofoliu implements ElementPortofoliu, Iterable<ElementPortofoliu> {
    private List<ElementPortofoliu> elemente = new ArrayList<>();
    private int nCont = 0;
    private int nActiune = 0;

    public Iterator<ElementPortofoliu> iterator() {
        return new PortofoliuIterator(elemente);
    }

    public void adaugaCont(Cont cont) {
        elemente.add(cont);
        nCont++;
    }

    public void adaugaActiune(Actiune actiune) {
        elemente.add(actiune);
        nActiune++;
    }

    public int getnCont() {
        return nCont;
    }

    public int getnActiune() {
        return nActiune;
    }

    public String toString() {
        return "{ }";
    }
}

class PortofoliuIterator implements Iterator<ElementPortofoliu> {
    private List<ElementPortofoliu> elemente;
    private int pozitie = 0;

    public PortofoliuIterator(List<ElementPortofoliu> elemente) {
        this.elemente = elemente;
    }

    public boolean hasNext() {
        return pozitie < elemente.size();
    }

    public ElementPortofoliu next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        ElementPortofoliu element = elemente.get(pozitie);
        pozitie++;
        return element;
    }
}