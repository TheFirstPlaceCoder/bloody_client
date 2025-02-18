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
                String name = line;
                String isFavorite = "false";
                try {
                    name = line.split(":")[0];
                    isFavorite = line.split(":")[1];
                } catch (Exception e) {}

                add(name, Boolean.parseBoolean(isFavorite));
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
                br.write(s.name + ":" + s.isFavorite + "\n");
            }
            br.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void add(Account account) {
        if (!accounts.contains(account)) {
            accounts.add(account);

            AccountUtils.shouldUpdate = true;
        }
    }

    public static void remove(Account account) {
        accounts.remove(account);

        AccountUtils.shouldUpdate = true;
    }

    public static void add(String account, boolean isFavorite) {
        if (accounts.stream().noneMatch(e -> e.name.equals(account))) {
            accounts.add(new Account(account, isFavorite));

            AccountUtils.shouldUpdate = true;
        }
    }

    public static void remove(String account) {
        accounts.removeIf(e -> e.name.equals(account));

        AccountUtils.shouldUpdate = true;
    }
}