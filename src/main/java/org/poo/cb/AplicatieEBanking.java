package org.poo.cb;

import javax.sound.sampled.Port;
import java.io.*;
import java.util.*;

public class AplicatieEBanking {
    public static HashMap<String, User> users;
    private static Map<String, Cont> conturi;
    private static Map<String, Map<String, Double>> rates;
    private static Map<String, Double[]> stocks;
    private ComisionStrategy strategieComision = new Comision();

    public AplicatieEBanking() {
        this.users = new HashMap<>();
        this.conturi = new HashMap<>();
        this.rates = new HashMap<>();
        this.stocks = new HashMap<>();
    }

    public static void createUser(String email, String firstName, String lastName, String address) {
        if (users.containsKey(email)) {
            System.out.println("User with " + email + " already exists");
        } else {
            User utilizator = User.getInstance(email, firstName, lastName, address);
            users.put(email, utilizator);
        }
    }

    public void incarcaRataSchimb(String fisier) {
        try (BufferedReader br = new BufferedReader(new FileReader("src/main/resources/" + fisier))) {
            String line;
            String[] first = new String[7];
            boolean isFirstLine = true;
            while ((line = br.readLine()) != null) {
                if (isFirstLine) {
                    first = line.split(",");
                    isFirstLine = false;
                    continue; // Skip first line
                }
                String[] parts = line.split(",");
                String currency = parts[0];
                Map<String, Double> exchangeRates = new HashMap<>();
                for (int i = 1; i < parts.length; i++) {
                    exchangeRates.put(first[i], Double.parseDouble(parts[i]));
                }
                rates.put(currency, exchangeRates);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stockPrice(String fisier) {
        try (BufferedReader br = new BufferedReader(new FileReader("src/main/resources/" + fisier))) {
            String line;
            boolean isFirstLine = true;

            while ((line = br.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue; // Skip the header line
                }

                String[] parts = line.split(",");
                String ticker = parts[0];
                Double[] prices = new Double[10];

                // Get the prices for the last 5 days
                for (int i = 0; i < 10; i++) {
                    prices[i] = Double.parseDouble(parts[parts.length - 1 - i]);
                }

                stocks.put(ticker, prices);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void listareUtilizator(HashMap<String, User> users, String email) {
        User user = users.get(email);
        if (user != null) {
            System.out.print("{");
            System.out.print("\"email\":\"" + user.getEmail() + "\",");
            System.out.print("\"firstname\":\"" + user.getNume() + "\",");
            System.out.print("\"lastname\":\"" + user.getPrenume() + "\",");
            System.out.print("\"address\":\"" + user.getAdresa() + "\",");
            System.out.print("\"friends\":[");
            for (User friend: user.getPrieteni()) {
                System.out.print("\"" + friend.getEmail() + "\"");
            }
            System.out.println("]}");
        } else {
            System.out.println("User with " + email + " doesn't exist");
        }
    }

    public void executaComenzi(String commandFile) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("src/main/resources/" + commandFile));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] comanda = line.split(" ");
                switch (comanda[0]) {
                    case "CREATE":
                        executaComandaCreate(comanda);
                        break;
                    case "ADD":
                        executaComandaAdd(comanda);
                        break;
                    case "EXCHANGE":
                        executaComandaExchange(comanda);
                        break;
                    case "TRANSFER":
                        executaComandaTransfer(comanda);
                        break;
                    case "BUY":
                        executaComandaBuyStocks(comanda);
                        break;
                    case "LIST":
                        executaComandaList(comanda);
                        break;
                    case "RECOMMEND":
                        recommendStocks(stocks);
                        break;
                    default:
                        System.out.println("Invalid command");
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        users.clear();
    }

    private void executaComandaCreate(String[] comanda) {

        String email = comanda[2];
        String firstName = comanda[3];
        String lastName = comanda[4];
        String address = String.join(" ", Arrays.copyOfRange(comanda, 5, comanda.length));

        createUser(email, firstName, lastName, address);
    }

    private void executaComandaAdd(String[] comanda) {

        switch (comanda[1]) {
            case "FRIEND":
                executaComandaAddFriend(comanda);
                break;
            case "ACCOUNT":
                executaComandaAddAccount(comanda);
                break;
            case "MONEY":
                executaComandaAddMoney(comanda);
                break;
            default:
                System.out.println("Invalid command");
        }
    }

    private void executaComandaAddFriend(String[] comanda) {

        String userEmail = comanda[2];
        String friendEmail = comanda[3];

        if (!users.containsKey(userEmail)) {
            System.out.println("User with " + userEmail + " doesn't exist");
            return;
        }

        if (!users.containsKey(friendEmail)) {
            System.out.println("User with " + friendEmail + " doesn't exist");
            return;
        }

        User user = users.get(userEmail);
        User prieten = users.get(friendEmail);

        user.adaugaPrieten(prieten);
        prieten.adaugaPrieten(user);
    }

    private void executaComandaAddAccount(String[] comanda) {

        String email = comanda[2];
        String valuta = comanda[3];

        User user = users.get(email);
        if (user == null) {
            System.out.println("User with " + email + " doesn’t exist");
            return;
        }

        user.adaugaCont(valuta);
    }

    private void executaComandaAddMoney(String[] comanda) {

        String email = comanda[2];
        String currency = comanda[3];
        double amount = Double.parseDouble(comanda[4]);

        if (!users.containsKey(email)) {
            System.out.println("User with " + email + " doesn’t exist");
            return;
        }

        User utilizator = users.get(email);
        Portofoliu portofoliu = utilizator.getPortofoliu();
        Cont cont = null;


        Iterator<ElementPortofoliu> iterator = portofoliu.iterator();
        while (iterator.hasNext()) {
            ElementPortofoliu element = iterator.next();
            if (element instanceof Cont) {
                Cont contCurent = (Cont) element;
                if (contCurent.getValuta().equalsIgnoreCase(currency)) {
                    cont = contCurent;
                    break;
                }
            }
        }

        if (cont == null) {
            System.out.println("Account with " + currency + " currency does not exist for user");
            return;
        }

        cont.adaugaBani(amount);
    }

    private void executaComandaExchange(String[] comanda) {

        String email = comanda[2];
        String sourceCurrency = comanda[3];
        String destinationCurrency = comanda[4];
        double amount = Double.parseDouble(comanda[5]);

        if (!users.containsKey(email)) {
            System.out.println("User with " + email + " doesn’t exist");
            return;
        }

        User utilizator = users.get(email);
        Portofoliu portofoliu = utilizator.getPortofoliu();
        Cont contSursa = null;
        Cont contDestinatie = null;

        // Cautam conturile sursa si destinatie in portofoliul utilizatorului
        Iterator<ElementPortofoliu> iterator = portofoliu.iterator();
        while (iterator.hasNext()) {
            ElementPortofoliu element = iterator.next();
            if (element instanceof Cont) {
                Cont cont = (Cont) element;
                if (cont.getValuta().equalsIgnoreCase(sourceCurrency)) {
                    contSursa = cont;
                } else if (cont.getValuta().equalsIgnoreCase(destinationCurrency)) {
                    contDestinatie = cont;
                }
            }
        }

        // Calculam suma finala care va fi schimbata
        double sumaFinala = amount;

        // Verificam daca sursa si destinatia sunt aceleasi valute
        if (!sourceCurrency.equalsIgnoreCase(destinationCurrency)) {
            // Calculam suma finala pe care utilizatorul doreste sa o schimbe din valuta sursa
            double rataSchimb = rates.get(destinationCurrency).get(sourceCurrency);
            sumaFinala *= rataSchimb;
        }

        double comision = strategieComision.calculeazaComision(sumaFinala, contSursa.getSold());
        contSursa.retrageBani(comision);

        // Verificam daca exista suficienta suma in contul sursa
        if (contSursa.getSold() < sumaFinala) {
            System.out.println("Insufficient amount in account " + sourceCurrency + " for exchange");
            return;
        }

        // Actualizam soldurile conturilor
        contSursa.retrageBani(sumaFinala);
        contDestinatie.adaugaBani(amount);
    }

    private void executaComandaTransfer(String[] comanda) {

        String email = comanda[2];
        String friendEmail = comanda[3];
        String currency = comanda[4];
        double amount = Double.parseDouble(comanda[5]);

        if (!users.containsKey(email) || !users.containsKey(friendEmail)) {
            return;
        }

        User utilizator = users.get(email);
        User prieten = users.get(friendEmail);

        if (!utilizator.getPrieteni().contains(prieten)) {
            System.out.println("You are not allowed to transfer money to " + prieten.getEmail());
            return;
        }

        Portofoliu portofoliuUtilizator = utilizator.getPortofoliu();
        Portofoliu portofoliuPrieten = prieten.getPortofoliu();

        Cont contUtilizator = null;
        Cont contPrieten = null;

        // Căutăm conturile în portofoliile utilizatorilor
        Iterator<ElementPortofoliu> iterator = portofoliuUtilizator.iterator();
        while (iterator.hasNext()) {
            ElementPortofoliu element = iterator.next();
            if (element instanceof Cont && ((Cont) element).getValuta().equalsIgnoreCase(currency)) {
                contUtilizator = (Cont) element;
                break;
            }
        }

        Iterator<ElementPortofoliu> iterator1 = portofoliuPrieten.iterator();
        while (iterator1.hasNext()) {
            ElementPortofoliu element = iterator1.next();
            if (element instanceof Cont && ((Cont) element).getValuta().equalsIgnoreCase(currency)) {
                contPrieten = (Cont) element;
                break;
            }
        }

        if (contUtilizator.getSold() < amount) {
            System.out.println("Insufficient amount in account " + currency + " for transfer");
            return;
        }

        // Efectuăm transferul
        contUtilizator.retrageBani(amount);
        contPrieten.adaugaBani(amount);
    }


    private void executaComandaBuyStocks(String[] comanda) {
        // Implementare pentru comanda BUY STOCKS
        String email = comanda[2];
        String company = comanda[3];
        int numberOfStocks = Integer.parseInt(comanda[4]);

        if (!users.containsKey(email)) {
            System.out.println("User with " + email + " doesn’t exist");
            return;
        }

        User utilizator = users.get(email);
        Portofoliu portofoliu = utilizator.getPortofoliu();

        // Căutăm contul utilizatorului în portofoliu
        Cont cont = null;
        Iterator<ElementPortofoliu> iterator = portofoliu.iterator();
        while (iterator.hasNext()) {
            ElementPortofoliu element = iterator.next();
            if (element instanceof Cont && ((Cont) element).getValuta().equalsIgnoreCase("USD")) {
                cont = (Cont) element;
                break;
            }
        }

        if (cont == null) {
            System.out.println("No USD account found in user's portfolio");
            return;
        }

        Double[] prices = stocks.get(company);

        // Obțineți ultimul preț din lista de prețuri
        double lastPrice = prices[0];
        double totalPrice = numberOfStocks * lastPrice;
        if (cont.getSold() < totalPrice) {
            System.out.println("Insufficient amount in account for buying stock");
            return;
        }

        cont.retrageBani(totalPrice);

        // Actualizăm portofoliul utilizatorului cu acțiunile achiziționate
        Actiune actiune = new Actiune(company, numberOfStocks);
        portofoliu.adaugaActiune(actiune);

    }

    private void executaComandaList(String[] comanda) {
        switch (comanda[1]) {
            case "USER":
                executaComandaListUser(comanda);
                break;
            case "PORTFOLIO":
                executaComandaListPortfolio(comanda);
                break;
            default:
                System.out.println("Invalid command");
        }
    }

    private void executaComandaListUser(String[] comanda) {

        String email = comanda[2];
        listareUtilizator(users, email);
    }

    private void executaComandaListPortfolio(String[] comanda) {

        String email = comanda[2];
        if (!users.containsKey(email)) {
            System.out.println("User with " + email + " doesn’t exist");
            return;
        }

        User utilizator = users.get(email);
        Portofoliu portofoliu = utilizator.getPortofoliu();
        int i = 0;
        System.out.print("{\"stocks\":[");
        Iterator<ElementPortofoliu> iterator = portofoliu.iterator();
        while (iterator.hasNext()) {
            ElementPortofoliu element = iterator.next();
            if (element instanceof Actiune) {
                System.out.print(element.toString());
                i++;
                if (i < portofoliu.getnActiune()) {
                    System.out.print(",");
                }
            }
        }

        System.out.print("],\"accounts\":[");
        i = 0;
        Iterator<ElementPortofoliu> iterator1 = portofoliu.iterator();
        while (iterator1.hasNext()) {
            ElementPortofoliu element = iterator1.next();
            if (element instanceof Cont) {
                System.out.print(element.toString());
                i++;
                if (i < portofoliu.getnCont()) {
                    System.out.print(",");
                }
            }
        }

        System.out.println("]}");
    }

    public static void recommendStocks(Map<String, Double[]> stockPrices) {
        List<String> recommendedStocks = new ArrayList<>();

        for (Map.Entry<String, Double[]> entry : stockPrices.entrySet()) {
            String company = entry.getKey();
            Double[] prices = entry.getValue();

            // Calculați SMA pentru termenul scurt (ultimele 5 zile)
            double shortTermSMA = 0;
            int i = 0;
            for (double price : prices) {
                shortTermSMA += price;
                i++;
                if (i == 5) {
                    break;
                }
            }
            shortTermSMA /= 5;

            // Calculați SMA pentru termenul lung (ultimele 10 zile)
            double longTermSMA = 0;
            for (double price : prices) {
                longTermSMA += price;
            }
            longTermSMA /= 10;

            if (shortTermSMA > longTermSMA) {
                recommendedStocks.add(company);
            }
        }

        System.out.print("{\"stocksToBuy\": [");
        for (int i = 0; i < recommendedStocks.size(); i++) {
            String stock = recommendedStocks.get(i);
            System.out.print("\"" + stock + "\"");
            if (i < recommendedStocks.size() - 1) {
                System.out.print(",");
            }
        }
        System.out.println("]}");
    }
}
