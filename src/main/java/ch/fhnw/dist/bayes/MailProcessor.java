package ch.fhnw.dist.bayes;

import java.io.BufferedReader;

public interface MailProcessor {

  /**
   * Method witch processes a mail
   *
   * @param mail A BufferedReader with the content of the mail
   * @param spam True if the mail contains spam
   */
  void process(BufferedReader mail, boolean spam);
}
