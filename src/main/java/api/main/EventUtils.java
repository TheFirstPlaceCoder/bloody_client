package api.main;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

public class EventUtils {
    private static final EventBus BUS = new EventBus();
    private static final List<Object> SUBSCRIBED = new ArrayList<>();

    public static void init() {
        BUS.registerLambdaFactory("", (lookupInMethod, klass) -> (MethodHandles.Lookup) lookupInMethod.invoke(null, klass, MethodHandles.lookup()));
    }

    public static void post(Object object) {
        BUS.post(object);
    }

    public static void post(Object... objects) {
        for (Object o : objects) {
            BUS.post(o);
        }
    }

    public static void register(Object object) {
        if (SUBSCRIBED.contains(object))
            return;

        BUS.subscribe(object);
        SUBSCRIBED.add(object);
    }

    public static void unregister(Object object) {
        if (!SUBSCRIBED.contains(object))
            return;

        BUS.unsubscribe(object);
        SUBSCRIBED.remove(object);
    }
}
