package com.client.alt;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Accounts {
    public static List<Account> accounts = new ArrayList<>();

    public static List<Account> getAccounts() {
        return accounts;
    }

    public static void load(File file) {
        if (!file.exists()) return;

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));

            String line;

            while ((line = br.readLine()) != null) {
                add(line);
            }

            br.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void save(File file) {
        if (file.exists()) file.delete();

        try {
            file.createNewFile();

            BufferedWriter br = new BufferedWriter(new FileWriter(file));
            for (Account s : getAccounts()) {
                br.write(s.name + "\n");
            }
            br.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void add(Account account) {
        if (!accounts.contains(account))
            accounts.add(account);
    }

    public static void remove(Account account) {
        accounts.remove(account);
    }

    public static void add(String account) {
        if (accounts.stream().noneMatch(e -> e.name.equals(account)))
            accounts.add(new Account(account));
    }

    public static void remove(String account) {
        accounts.removeIf(e -> e.name.equals(account));
    }
}