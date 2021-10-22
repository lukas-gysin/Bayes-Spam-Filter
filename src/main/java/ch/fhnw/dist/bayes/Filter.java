package ch.fhnw.dist.bayes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.zip.ZipFile;

/**
 * The Bayes-Spam-Filter
 *
 * @author Lukas Gysin
 */
public class Filter {
  private final int alpha;
  private int nHam = 0;
  private int nSpam = 0;
  private final HashMap<String, Integer> ham = new HashMap<>();
  private final HashMap<String, Integer> spam = new HashMap<>();

  // Constructors
  public Filter(){
    alpha = 1;
  }

  public Filter(int alpha){
    this.alpha = alpha;
  }

  // Public Methods
  /**
   * Adds a word to train the filter
   *
   * @param word The word to be added
   * @param spam True, if the mail is spam
   */
  public void add(String word, boolean spam){
    if (!spam){
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
   * Calculates the probability that a mail is ham, given the word appears in it.
   *
   * @param word The word witch appears in the mail
   * @return The probability as a double (0 ≤ spamProbability ≤ 1.0)
   */
  public double hamProbability(String word){
    return hamFrequency(word) / (spamFrequency(word) + hamFrequency(word));
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

  public void train(String ham, String spam){
    readZip(ham, false, this::readMail);
    readZip(spam, true, this::readMail);
  }

  // Package-private Methods
  /**
   * Calculates the frequency on witch rate the word appears in ham mails
   *
   * @param word The word
   * @return The frequency the word appears in ham mails
   */
  double hamFrequency(String word){
    return (double)ham.getOrDefault(preprocessWord(word), alpha) / nHam;
  }

  /**
   * Calculates the frequency on witch rate the word appears in spam mails
   *
   * @param word The word
   * @return The frequency the word appears in spam mails
   */
  double spamFrequency(String word){
    return (double)spam.getOrDefault(preprocessWord(word), alpha) / nSpam;
  }

  // Private Methods
  /**
   * Reade a mail and adds all words to `words`
   * @param mail A BufferedReader for the mail content
   * @param spam True if the mail contains spam
   */
  private void readMail(BufferedReader mail, boolean spam) {
    try {
      while (mail.ready()){
        for (String word : mail.readLine().split(" ")) {  // Split the line into words, separated by a whitespace
          if (!word.isBlank())
            add(word, spam);
        }
      }
    } catch (IOException e) {
      // An error occurred on the reading process
      e.printStackTrace();
    }
  }

  /**
   * Reade a `.zip`-file and adds calls the callback
   *
   * @param path The path from the content root to the `.zip`-file
   * @param spam True if the `.zip`-file contains spam mails
   * @param processor The callback what with the mail should happen
   */
  private void readZip(String path, boolean spam, MailProcessor processor){
    try {
      ZipFile zipFile = new ZipFile(path);
      zipFile.stream().forEach(mail -> {  // Read each mail in the `.zip`-file
        try {
          BufferedReader content = new BufferedReader(new InputStreamReader(zipFile.getInputStream(mail)));
          processor.process(content, spam);
          content.close();
        } catch (IOException e) {
          // Couldn't get the InputStream for the ZipEntity
          e.printStackTrace();
        }
      });
      zipFile.close();
    } catch (IOException e) {
      // Couldn't find the `.zip`-file
      e.printStackTrace();
    }
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
}
