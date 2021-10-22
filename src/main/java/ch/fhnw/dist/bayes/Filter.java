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

  public static final double DEFAULT_ALPHA = 1;
  public static final double DEFAULT_THRESHOLD = 0.5;

  private double alpha;
  /**
   * The filter has to be sure for this amount or higher, that something is spam, before he categorizes it as spam
   */
  private final double threshold;
  private int nHam = 0;
  private int nSpam = 0;
  private final HashMap<String, Integer> ham = new HashMap<>();
  private final HashMap<String, Integer> spam = new HashMap<>();

  // Constructors
  public Filter(){
    this(DEFAULT_ALPHA, DEFAULT_THRESHOLD);
  }

  public Filter(double alpha){
    this(alpha, DEFAULT_THRESHOLD);
  }

  public Filter(double alpha, double threshold){
    // Alpha shouldn't be less than 0
    if (alpha <= 0){
      this.alpha = Double.MIN_VALUE;
    } else {
      this.alpha = alpha;
    }

    // Threshold has to be between 1 and 0, since it represents %
    if (threshold > 1){
      this.threshold = 1;
    } else if (threshold < 0) {
      this.threshold = 0;
    } else {
      this.threshold = threshold;
    }
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
    if (!tmpWord.isBlank()) {
      ham.put(tmpWord, (ham.containsKey(tmpWord) ? ham.get(tmpWord) + 1 : 1));
      nHam++;
    }
  }

  /**
   * Adds a word found in a spam-mail
   *
   * @param word The word found in the spam-mail
   */
  public void addSpam(String word){
    String tmpWord = preprocessWord(word); // Preprocessing of the word
    if (!tmpWord.isBlank()) {
      spam.put(tmpWord, (spam.containsKey(tmpWord) ? spam.get(tmpWord) + 1 : 1));
      nSpam++;
    }
  }

  /**
   * Calibrates the filter by adjusting alpha to a good level
   * @param ham The path to a `.zip`-file with ham-mails for calibration in it.
   * @param spam The path to a `.zip`-file with spam-mails for calibration in it.
   */
  public void calibrate(String ham, String spam){
    // Hier würde eine Ableitung der categorize()-Methode hinkommen in welcher nur alpha variabel wäre.
    // So könnte man das lokale Minumum, bzw. Maximum herausfinden.
    // Ich kann das einfach gerade nicht berechnen / in Java niederschreiben.
    // Durch ausprobieren habe ich herausgefunden, dass ein alpha von 0.01 eine 96.3% Erkennungsrate ergibt.
    alpha = 0.01;
  }

  /**
   * Categorizes if a mail is spam or not
   * @param mail The content of the mail
   * @return True, if the mail is spam
   * @throws IOException Throws a IOException, if an error occurs while reading the mail
   */
  public boolean categorize(BufferedReader mail) throws IOException {
    double numerator = 1;
    double denominator = 1;
    while (mail.ready()){
      for (String word : mail.readLine().split(" ")) {  // Split the line into words, separated by a whitespace
        if (!preprocessWord(word).isBlank()){ // Skip blank words
          numerator *= spamProbability(word);
          denominator *= hamProbability(word);
        }
      }
    }
    denominator += numerator;
    return (numerator / denominator) >= threshold; // The spam probability
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

  /**
   * Trains the filter
   * @param ham The path to a `.zip`-file with ham-mails for training in it.
   * @param spam The path to a `.zip`-file with spam-mails for training in it.
   */
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
    if (!ham.containsKey(preprocessWord(word))){
      return alpha / nHam;
    } else {
      return (double)ham.get(preprocessWord(word)) / nHam;
    }
  }

  /**
   * Calculates the frequency on witch rate the word appears in spam mails
   *
   * @param word The word
   * @return The frequency the word appears in spam mails
   */
  double spamFrequency(String word){
    if (!spam.containsKey(preprocessWord(word))){
      return alpha / nSpam;
    } else {
      return (double)spam.get(preprocessWord(word)) / nSpam;
    }
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

  // Getter and Setter
  public double getAlpha() {
    return alpha;
  }

  public double getThreshold() {
    return threshold;
  }
}
