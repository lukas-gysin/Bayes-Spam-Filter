package ch.fhnw.dist.bayes;

import java.io.*;
import java.util.zip.ZipFile;

/**
 * The entry point of the application
 *
 * @author Lukas Gysin
 */
public class Bayes {

  public static final Filter bayes = new Filter(1);

  public static void main(String[] args) {
    bayes.train("src/main/resources/ham-anlern.zip", "src/main/resources/spam-anlern.zip");
//    readZip("src/main/resources/ham-kallibrierung.zip", false, Bayes::categorizeMail);
  }

  private static void categorizeMail(BufferedReader mail, boolean spam) {
    double spamProbability;
    double numerator = 1;
    double denominator = 1;
    try {
      while (mail.ready()){
        for (String word : mail.readLine().split(" ")) {  // Split the line into words, separated by a whitespace
          if (!word.isBlank()){
            numerator *= bayes.spamProbability(word);
            denominator *= bayes.hamProbability(word);
          }
        }
      }
      denominator += numerator;
      spamProbability = numerator / denominator;
      System.out.println("Spam probability: " + spamProbability);
      System.in.read();
    } catch (IOException e) {
      // An error occurred on the reading process
      e.printStackTrace();
    }
  }

}
