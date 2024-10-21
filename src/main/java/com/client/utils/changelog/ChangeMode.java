package com.client.utils.changelog;

import java.awt.*;

public enum ChangeMode {
    ADDED("[+] ", Color.GREEN, 1),
    REWRITTEN("[/] ", Color.YELLOW, 2),
    DELETED("[-] ", Color.RED, 3),
    CORRECTED("[~] ", Color.CYAN, 4);

    final String prefix;
    final Color color;
    final int id;

    ChangeMode(String prefix, Color color, int id) {
        this.prefix = prefix;
        this.color = color;
        this.id = id;
    }
}
