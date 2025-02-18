package com.client.utils.auth;

import javax.swing.*;

public class UpdateWindow extends JFrame {
    // TODO: Дефолтный класс окна уведомления о том, что вышло обновление

    public boolean isOk;

    public UpdateWindow() {
        super("Обновление клиента");
        isOk = false;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel label = new JLabel("Вышло новое обновление клиента! Скачайте его, чтобы продолжить играть!");
        JLabel label1 = new JLabel("New update is out! Please, download it to continue playing!");
        JButton okButton = new JButton("OK");

        okButton.addActionListener(e -> {
            isOk = true;
            Runtime.getRuntime().halt(0);
        });

        JPanel panel = new JPanel();
        panel.add(label);
        panel.add(label1);
        panel.add(okButton);

        add(panel);

        pack();
        setVisible(true);
    }
}