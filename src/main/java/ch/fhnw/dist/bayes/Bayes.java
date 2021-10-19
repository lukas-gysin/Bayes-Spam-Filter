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
    readZip("src/main/resources/ham-anlern.zip", false, Bayes::readMail);
    readZip("src/main/resources/spam-anlern.zip", true, Bayes::readMail);
    readZip("src/main/resources/ham-kallibrierung.zip", false, Bayes::categorizeMail);
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

  /**
   * Reade a mail and adds all words to `words`
   * @param mail A BufferedReader for the mail content
   * @param spam True if the mail contains spam
   */
  private static void readMail(BufferedReader mail, boolean spam) {
    try {
      while (mail.ready()){
        for (String word : mail.readLine().split(" ")) {  // Split the line into words, separated by a whitespace
          if (!word.isBlank())
            bayes.add(word, spam);
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
  private static void readZip(String path, boolean spam, MailProcessor processor){
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

}
