package org.poo.cb;

import java.util.*;
import java.io.*;

public class Main {
    public static void main(String[] args) {
        if (args == null) {
            System.out.println("Running Main");
        } else {
            String exchangeRatesFile = args[0];
            String stockValuesFile = args[1];
            String commandFile = args[2];

            AplicatieEBanking aplicatie = new AplicatieEBanking();
            aplicatie.incarcaRataSchimb(exchangeRatesFile);
            aplicatie.stockPrice(stockValuesFile);
            aplicatie.executaComenzi(commandFile);
        }
    }
}