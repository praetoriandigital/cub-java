package com.ivelum;

import java.util.List;
import java.util.ListIterator;

public class StringUtils {
  public static String join(String join, List<String> strings) {
    StringBuilder result = new StringBuilder();
    ListIterator<?> it = ((List<?>) strings).listIterator();
    boolean first = true;
    if (strings.isEmpty()) {
      return result.toString();
    } else {
      while (it.hasNext()) {
        if (first) {
          first = false;
        } else {
          result.append(join);
        }
        result.append((String) it.next());
      }
    }

    return result.toString();
  }
}
