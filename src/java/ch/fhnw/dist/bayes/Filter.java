package ch.fhnw.dist.bayes;

import java.util.HashMap;

/**
 * The Bayes-Spam-Filter
 *
 * @author Lukas Gysin
 */
public class Filter {
  private final HashMap<String, Integer> ham = new HashMap<>();
  private final HashMap<String, Integer> spam = new HashMap<>();

  /**
   * Adds a word to train the filter
   *
   * @param word The word to be added
   * @param ham True, if the mail is ham
   */
  public void add(String word, boolean ham){
    if (ham){
      addHam(word);
    } else {
      addSpam(word);
    }
  }

  /**
   * Adds a word found in a ham-mail
   *
   * @param word The word found in the ham-mail
   */
  public void addHam(String word){
    ham.put(word, (ham.containsKey(word) ? ham.get(word) + 1 : 1));
  }

  /**
   * Adds a word found in a spam-mail
   *
   * @param word The word found in the spam-mail
   */
  public void addSpam(String word){
    spam.put(word, (spam.containsKey(word) ? spam.get(word) + 1 : 1));
  }
}
