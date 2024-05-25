package org.poo.cb;

public interface ComisionStrategy {
    double calculeazaComision(double suma, double sold);
}

class FaraComision implements ComisionStrategy {
    @Override
    public double calculeazaComision(double suma, double sold) {
        return 0; // Fara comision
    }
}
class Comision implements ComisionStrategy {
    @Override
    public double calculeazaComision(double suma, double sold) {
        if (suma > 0.5 * sold) { // Verificați dacă suma este mai mare de 50% din limita contului
            return suma * 0.01; // Comision de 1% pentru sume mai mari de 50%
        } else {
            return 0;
        }
    }
}
