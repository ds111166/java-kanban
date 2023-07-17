package server.handler;

import com.sun.net.httpserver.HttpExchange;

import java.util.HashMap;
import java.util.Map;

public class HandlerUtilities {

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

    protected static Integer getParametrId(HttpExchange exchange) {
        try {
            final String requestQuery = exchange.getRequestURI().getQuery();

            final Map<String, String> parametrs = queryToMap(requestQuery);
            if(parametrs == null){
                return null;
            }
            final String strId = parametrs.get("id");
            return Integer.parseInt(strId);
        } catch (NumberFormatException ex) {
            return null;
        }
    }
}
