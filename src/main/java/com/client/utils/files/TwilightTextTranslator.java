package com.client.utils.files;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TwilightTextTranslator {

    static Map<String, String> net = new TreeMap<>();
    public static Map<String, String> clas = new TreeMap<>();
    static Map<String, String> field = new TreeMap<>();
    static Map<String, String> method = new TreeMap<>();
    static Map<String, String> comp = new TreeMap<>();

    static Pattern pnet2 = Pattern.compile("net.minecraft.class_[0-9]+\\.class_[0-9]+");
    static Pattern pnet = Pattern.compile("net.minecraft.class_[0-9]+");
    static Pattern pclas = Pattern.compile("class_[0-9]+");
    static Pattern pmethod = Pattern.compile("method_[0-9]+");
    static Pattern pfield = Pattern.compile("field_[0-9]+");
    static Pattern pcomp = Pattern.compile("comp_[0-9]+");

    static {
        for (String v : new BufferedReader(new InputStreamReader(TwilightTextTranslator.class.getClassLoader().getResourceAsStream("mappings-base.tiny"))).lines().toList()) {

            if (v.startsWith("c\t")) {

                v = v.replace("$", ".");
                String[] s = v.split("\t");
                if (!s[1].contains("_")) continue;
                net.put(s[1].replace("/", "."), s[2].replace("/", "."));
                String key = s[1].replace(".", "/").replaceAll(".*/", "");
                String value = s[2].replaceAll(".*/", "");
                clas.put(key, value);

            } else {
                boolean isMethod = v.startsWith("\tm\t");
                boolean isField = v.startsWith("\tf\t");

                if (isMethod || isField) {
                    String[] s = v.split("\t");
                    if (!s[3].contains("_") || s[3].equals(s[4])) continue;

                    if (s[3].startsWith("comp_")) comp.put(s[3], s[4]);
                    else if (isMethod) method.put(s[3], s[4]);
                    else field.put(s[3], s[4]);
                }
            }
        }

    }


    public static String translate(String data) {
        Matcher m;
        for (Pattern pattern : List.of(pnet2, pnet)) {
            m = pattern.matcher(data);
            if (m.find()) data = m.replaceAll(result -> (Object) net.get(result.group()) instanceof String text ? text : result.group());
        }
        m = pclas.matcher(data);
        if (m.find()) data = m.replaceAll(result -> {
            String group = result.group();
            return (Object) clas.get(group) instanceof String text ? text : group;
        });
        m = pmethod.matcher(data);
        if (m.find()) data = m.replaceAll(result -> {
            String group = result.group();
            return (Object) method.get(group) instanceof String text ? text : group;
        });
        m = pfield.matcher(data);
        if (m.find()) data = m.replaceAll(result -> {
            String group = result.group();
            return (Object) field.get(group) instanceof String text ? text : group;
        });
        m = pcomp.matcher(data);
        if (m.find()) data = m.replaceAll(result -> {
            String group = result.group();
            return (Object) comp.get(group) instanceof String text ? text : group;
        });
        return data;


    }
}