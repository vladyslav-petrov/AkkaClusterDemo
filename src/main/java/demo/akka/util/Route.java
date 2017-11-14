package demo.akka.util;

/**
 * @author <a href="mailto:vladyslav@dsi.io">Vladyslav Petrov</a>
 */
public class Route {
    private static final String PREFIX = "/user/";

    // actor ref in AKKA format - /user/destination_1
    public static String getActorRef(Class clazz, String param) {
        return PREFIX + getName(clazz, param);
    }

    // actor name - destination_2
    public static String getName(Class clazz, String param) {
        param = "".equals(param) ? "" : ("_" + param);
        return clazz.getSimpleName().toLowerCase() + param;
    }
}
