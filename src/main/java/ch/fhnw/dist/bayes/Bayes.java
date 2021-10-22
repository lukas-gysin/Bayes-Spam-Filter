package ch.fhnw.dist.bayes;

import java.io.*;
import java.util.zip.ZipFile;

/**
 * The entry point of the application
 *
 * @author Lukas Gysin
 */
public class Bayes {

  private static final Filter bayes = new Filter(1);
  private static int correctMails = 0;
  private static int allMails = 0;

  public static void main(String[] args) {
    bayes.train("src/main/resources/ham-anlern.zip", "src/main/resources/spam-anlern.zip");

    categoriseZip("src/main/resources/ham-test.zip", false);
    categoriseZip("src/main/resources/spam-test.zip", true);

    System.out.println("=== Bayes Spam Filter von Lukas Gysin ===");
    System.out.println("Schwellwert:\t" + bayes.getThreshold());
    System.out.println("Alpha:\t\t\t" + bayes.getAlpha());
    System.out.println("Erkennungsrate:\t" + (double)correctMails/allMails);

    correctMails = 0;
    allMails = 0;

    bayes.calibrate("src/main/resources/ham-kallibrierung.zip", "src/main/resources/spam-kallibrierung.zip");

    categoriseZip("src/main/resources/ham-test.zip", false);
    categoriseZip("src/main/resources/spam-test.zip", true);

    System.out.println("------ Werte nach der Kalibrierung ------");
    System.out.println("Schwellwert:\t" + bayes.getThreshold());
    System.out.println("Alpha:\t\t\t" + bayes.getAlpha());
    System.out.println("Erkennungsrate:\t" + ((double)correctMails/allMails));
  }

  public static void categoriseZip(String path, boolean spam){
    try {
      ZipFile zipFile = new ZipFile(path);
      zipFile.stream().forEach(mail -> {  // Read each mail in the `.zip`-file
        try {
          BufferedReader content = new BufferedReader(new InputStreamReader(zipFile.getInputStream(mail)));
          // When the mail is categorized correct, the `correctMail`-counter should be increase by 1
          correctMails += (bayes.categorize(content) == spam ? 1 : 0);
          allMails++;
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
