package server.handler;
import java.util.HashMap;
import java.util.Map;

public class HandlerUtilities {
    /**
     *  Разбирает строку вида "parametr1=value1&parametr2=value2&parametr3=value3..."
     *  в Map<String, String>, где ключ: имя парамера, значение: значение параметра
     *  HttpExchange httpExchange
     *  String query = httpExchange.getRequestURI().getQuery()
     */
    public static Map<String, String> queryToMap(String query) {
        if (query == null) {
            return null;
        }
        Map<String, String> result = new HashMap<>();
        for (String param : query.split("&")) {
            String[] entry = param.split("=");
            if (entry.length > 1) {
                result.put(entry[0], entry[1]);
            } else {
                result.put(entry[0], "");
            }
        }
        return result;
    }
}
