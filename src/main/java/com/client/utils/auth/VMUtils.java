package com.client.utils.auth;

import oshi.SystemInfo;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.NetworkIF;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class VMUtils {
    // TODO: Данный класс пиздец какой нагруженный, я сделал в 3 сука раза меньше кода
    // Однако доверимся профессионалам и оставим этот код

    public static final String OSHI_VM_MAC_ADDR_PROPERTIES = "oshi.vmmacaddr.properties";
    public static final Properties vmMacAddressProps = readPropertiesFromFilename(OSHI_VM_MAC_ADDR_PROPERTIES);
    public static final Map<String, String> vmVendor = new HashMap<>();

    static {
        vmVendor.put("bhyve bhyve", "bhyve");
        vmVendor.put("KVMKVMKVM", "KVM");
        vmVendor.put("TCGTCGTCGTCG", "QEMU");
        vmVendor.put("lrpepyh vr", "Parallels");
        vmVendor.put("VMwareVMware", "VMware");
        vmVendor.put("XenVMMXenVMM", "Xen HVM");
        vmVendor.put("ACRNACRNACRN", "Project ACRN");
        vmVendor.put("QNXQVMBSQG", "QNX Hypervisor");
    }

    public static final String[] vmModelArray = new String[]{"Linux KVM", "Linux lguest", "OpenVZ", "Qemu",
            "VMWare", "linux-vserver", "Xen", "FreeBSD Jail", "VirtualBox", "Parallels",
            "Linux Containers", "LXC"};

    public static String identifyVM() {
        SystemInfo si = new SystemInfo();
        HardwareAbstractionLayer hw = si.getHardware();
        // Проверяем CPU Vendor
        String vendor = hw.getProcessor().getProcessorIdentifier().getVendor().trim();
        if (vmVendor.containsKey(vendor)) return vmVendor.get(vendor);
        // Проверяем известные мак адреса ВМок
        List<NetworkIF> nifs = hw.getNetworkIFs();
        for (NetworkIF nif : nifs) {
            String mac = nif.getMacaddr().toUpperCase();
            String oui = mac.length() > 7 ? mac.substring(0, 8) : mac;
            if (vmMacAddressProps.containsKey(oui)) return vmMacAddressProps.getProperty(oui);
        }
        // Проверяем известные модели ВМок
        String model = hw.getComputerSystem().getModel();
        for (String vm : vmModelArray) if (model.contains(vm)) return vm;
        return "";
    }

    public static boolean isOnVM() {
        //TODO: Открепить после исправления совместимости с Линуксом
        String vm = identifyVM();
        return !vm.isEmpty();
    }

    public static Properties readPropertiesFromFilename(String propsFilename) {
        // TODO: спизженно с какого то арабского форума

        Properties archProps = new Properties();
        // Load the configuration file from at least one of multiple possible
        // ClassLoaders, evaluated in order, eliminating duplicates
        for (ClassLoader loader : Stream.of(Thread.currentThread().getContextClassLoader(),
                        ClassLoader.getSystemClassLoader(), VMUtils.class.getClassLoader())
                .collect(Collectors.toCollection(LinkedHashSet::new))) {
            if (readPropertiesFromClassLoader(propsFilename, archProps, loader)) {
                return archProps;
            }
        }
        return archProps;
    }

    private static boolean readPropertiesFromClassLoader(String propsFilename, Properties archProps,
                                                         ClassLoader loader) {
        if (loader == null) {
            return false;
        }

        try {
            List<URL> resources = null;
            try {
                resources = Collections.list(loader.getResources(propsFilename));
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (resources.isEmpty()) {
                return false;
            }
            try (InputStream in = resources.get(0).openStream()) {
                if (in != null) {
                    archProps.load(in);
                }
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}