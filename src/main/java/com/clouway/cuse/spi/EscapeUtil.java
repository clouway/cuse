package com.clouway.cuse.spi;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Mihail Lesikov (mlesikov@gmail.com)
 */
public class EscapeUtil {

  public static String escape(String value) {
    Map<String, String> escapeSymbolsMap = new HashMap<String, String>() {{
      put(":", "\\:");
      put(",", "\\,");
      put("+", "\\+");
      put("-", "\\\\-");
      put("=", "\\=");
      put("<", "\\<");
      put(">", "\\>");
    }};
    for (String symbol : escapeSymbolsMap.keySet()) {
      value = value.replace(symbol, escapeSymbolsMap.get(symbol));
    }
    return value;
  }
}
