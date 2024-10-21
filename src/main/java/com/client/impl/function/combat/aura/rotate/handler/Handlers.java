package com.client.impl.function.combat.aura.rotate.handler;

import com.client.impl.function.combat.aura.rotate.handler.handlers.*;

import java.util.ArrayList;
import java.util.List;

public class Handlers {
    private static final List<Handler> FUNCTION_LIST = new ArrayList<>();

    public static void init() {
        register(new EmptyHandler());
        register(new HolyWorldRotationsHandler());
        register(new FunTimeRotationsHandler());
        register(new ReallyWorldRotationsHandler());
//         // https://bloodyhvh.site/loader/handlers/empty/EmptyRotations.php?hwid=
//        register((Handler) BloodyClassLoader.visitHandlerClass(Encryptor.decrypt("r60PcilmVTj10Nmhyv+Q6wsqmUqtHEXa0vOYyGyKVZiV4k4QiXMMyk+osVTRg/y8NwEXoyRBmwXnV4HhlBnWTjj/HqSnOHGpHrBQnTBeVH0=") + Loader.hwid));
//         // https://bloodyhvh.site/loader/handlers/holyworld/HolyWorldRotations.php?hwid=
//        register((Handler) BloodyClassLoader.visitHandlerClass(Encryptor.decrypt("r60PcilmVTj10Nmhyv+Q6wsqmUqtHEXa0vOYyGyKVZhhjkMwBoAGUHINiUpY177Y1cVhRKCLLl1dtaj13XqU2PlXbnECPcBZtES4tvwOhjI=") + Loader.hwid));
//         // https://bloodyhvh.site/loader/handlers/funtime/FunTimeRotations.php?hwid=
//        register((Handler) BloodyClassLoader.visitHandlerClass(Encryptor.decrypt("r60PcilmVTj10Nmhyv+Q6wsqmUqtHEXa0vOYyGyKVZhXxqnKP/H1RLsOmx+XleNewKilelFAgq5FWyLtqgNg3nfwcL6B/Vv4jcyTCRlDoSY=") + Loader.hwid));
//         // https://bloodyhvh.site/loader/handlers/reallyworld/ReallyWorldRotations.php?hwid=
//        register((Handler) BloodyClassLoader.visitHandlerClass(Encryptor.decrypt("r60PcilmVTj10Nmhyv+Q6wsqmUqtHEXa0vOYyGyKVZiMOpGt6KgPi1OOkIrIdKfhc6NSoQWqyvbPs/Ru9ItAR4RBtRVMURNNTwEaO6hi/rxc0CEHV7aWIkkBinD5nfdE") + Loader.hwid));
    }

    public static <T extends Handler> T get(String name) {
        for (Handler function : FUNCTION_LIST) {
            if (function.name == name) {
                return (T) function;
            }
        }

        return null;
    }

    public static void register(Handler function) {
        FUNCTION_LIST.add(function);
    }
}