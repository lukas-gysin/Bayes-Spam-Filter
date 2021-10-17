package ch.fhnw.dist.bayes;

import java.io.*;
import java.util.zip.ZipFile;

/**
 * The entry point of the application
 *
 * @author Lukas Gysin
 */
public class Bayes {

  public static final Filter bayes = new Filter();

  public static void main(String[] args) {
    readZip("src/ressources/ham-anlern.zip", false);
    readZip("src/ressources/spam-anlern.zip", true);
  }

  /**
   * Reade a mail and adds all words to `words`
   * @param mail A BufferedReader for the mail content
   * @param spam True if the mail contains spam
   */
  private static void readMail(BufferedReader mail, boolean spam) throws IOException {
      while (mail.ready()){
        for (String word : mail.readLine().split(" ")) {                // Split the line into words, separated by a whitespace
          bayes.add(word, spam);
        }
      }
  }

  /**
   * Reade a `.zip`-file and adds all words to `words`
   *
   * @param path The path from the content root to the `.zip`-file
   * @param spam True if the `.zip`-file contains spam mails
   */
  private static void readZip(String path, boolean spam){
    try {
      ZipFile zipFile = new ZipFile(path);
      zipFile.stream().forEach(mail -> {  // Read each mail in the `.zip`-file
        try {
          BufferedReader content = new BufferedReader(new InputStreamReader(zipFile.getInputStream(mail)));
          readMail(content, spam);
          content.close();
        } catch (IOException e) {
          // Couldn't get the InputStream for the ZipEntity
          // or an error occurred on the reading process
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
