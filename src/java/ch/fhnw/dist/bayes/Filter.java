package ch.fhnw.dist.bayes;

import java.util.HashMap;

/**
 * The Bayes-Spam-Filter
 *
 * @author Lukas Gysin
 */
public class Filter {
  private int nHam = 0;
  private int nSpam = 0;
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
    String tmpWord = preprocessWord(word); // Preprocessing of the word
    ham.put(tmpWord, (ham.containsKey(tmpWord) ? ham.get(tmpWord) + 1 : 1));
    nHam++;
  }

  /**
   * Adds a word found in a spam-mail
   *
   * @param word The word found in the spam-mail
   */
  public void addSpam(String word){
    String tmpWord = preprocessWord(word); // Preprocessing of the word
    spam.put(tmpWord, (spam.containsKey(tmpWord) ? spam.get(tmpWord) + 1 : 1));
    nSpam++;
  }

  /**
   * Calculates the probability that a mail is spam, given the word appears in it.
   *
   * @param word The word witch appears in the mail
   * @return The probability as a double (0 ≤ spamProbability ≤ 1.0)
   */
  public double spamProbability(String word){
    return spamFrequency(word) / (spamFrequency(word) + hamFrequency(word));
  }

  /**
   * Calculates the frequency on witch rate the word appears in ham mails
   *
   * @param word The word
   * @return The frequency the word appears in ham mails
   */
  private double hamFrequency(String word){
    return (double)ham.getOrDefault(preprocessWord(word), 1) / nHam;
  }

  /**
   * Stripes whitespaces, converting to lower case
   *
   * @param word The word to be preprocessed
   * @return The preprocessed word
   */
  private String preprocessWord(String word){
    return word.strip().toLowerCase();
  }

  /**
   * Calculates the frequency on witch rate the word appears in spam mails
   *
   * @param word The word
   * @return The frequency the word appears in spam mails
   */
  private double spamFrequency(String word){
    return (double)spam.getOrDefault(preprocessWord(word), 1) / nSpam;
  }
}
