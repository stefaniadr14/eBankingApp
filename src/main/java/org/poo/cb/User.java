package org.poo.cb;
import java.util.*;

import static org.poo.cb.AplicatieEBanking.users;

public class User {
    private static User instance;

    private String email;
    private String nume;
    private String prenume;
    private String adresa;
    private Portofoliu portofoliu;
    private List<User> prieteni;

    public User(String email, String nume, String prenume, String adresa) {
        this.email = email;
        this.nume = nume;
        this.prenume = prenume;
        this.adresa = adresa;
        this.portofoliu = new Portofoliu();
        this.prieteni = new ArrayList<>();
    }

    // Metoda statică pentru a obține instanța Singleton a utilizatorului
    public static User getInstance(String email, String nume, String prenume, String adresa) {
        if (!users.containsKey(email)) {
            users.put(email, new User(email, nume, prenume, adresa));
        } else {
            System.out.println("User with " + email + " already exists");
        }
        return users.get(email);
    }

    // Metode de acces pentru atributele utilizatorului
    public String getEmail() {
        return email;
    }

    public String getNume() {
        return nume;
    }

    public String getPrenume() {
        return prenume;
    }

    public String getAdresa() {
        return adresa;
    }

    public Portofoliu getPortofoliu() {
        return portofoliu;
    }

    public List<User> getPrieteni() {
        return prieteni;
    }

    public void adaugaPrieten(User prieten) {

        if (this.prieteni.contains(prieten)) {
            System.out.println("User with " + prieten.getEmail() + " is already a friend");
            return;
        }

        this.prieteni.add(prieten);
    }

    public void adaugaCont(String valuta) {
        Cont contNou = new Cont(valuta);
        portofoliu.adaugaCont(contNou); // Adăugăm contul în portofoliul utilizatorului
    }

}
