package com.clouway.searchengine;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author Miroslav Genov (mgenov@gmail.com)
 */
public class IndexWriter {


  public Set<String> createIndex(String... words) {

    Set<String> index = new HashSet<String>();
    for (String word : words) {

      if (word != null) {
        word = word.toLowerCase();
        index.add(word);

        String[] tokens = word.split("\\s");
        for (String token : tokens) {

          index.addAll(normalizeToken(token));
        }

        tokens = word.split("\\.");

        normalizeTokens(index, tokens);

        tokens = word.split("\\~");

        normalizeTokens(index, tokens);

        tokens = word.split("\\:");

        normalizeTokens(index, tokens);

        tokens = word.split("\\-");

        normalizeTokens(index, tokens);
      }

    }

    return index;
  }

  private void normalizeTokens(Set<String> index, String[] tokens) {
    for (String token : tokens) {
      index.addAll(normalizeToken(token));
    }

  }

  private Set<String> normalizeToken(final String token) {

    Set<String> tokens = new TreeSet<String>();

    for (int i = 0; i < token.length(); i++) {
      String word = token.substring(i, token.length());

      tokens.add(word);
    }

    for (int i = 1; i < token.length(); i++) {
      String word = token.substring(0, token.length() - i);
      tokens.add(word);
    }

    for (int i = 0; i < token.length() / 2; i++) {
      String word = token.substring(i, token.length() - i);

      tokens.add(word);
    }

    return tokens;
  }
}
