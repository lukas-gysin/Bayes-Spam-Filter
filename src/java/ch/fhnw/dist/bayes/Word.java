package ch.fhnw.dist.bayes;

/**
 * A word from the bayes spam filter
 *
 * @author Lukas Gysin
 */
class Word {
  private final String word;
  private int ham = 0;
  private int spam = 0;

  // Constructors
  Word(String word){
    this.word = word.toLowerCase();
  }

  // Public Classes
  @Override
  public String toString() {
    return word.toUpperCase() + ":\t"
        + "ham=" + ham + "\t"
        + "spam=" + spam;
  }

  // Package-private Classes

  /**
   * Increase the word counter
   * @param spam True if the counter for spam-mails should be increased
   */
  void count(boolean spam){
    if (spam){
      countSpam();
    } else {
      countHam();
    }
  }

  /**
   * Increase the word count for ham-mails.
   */
  void countHam(){
    ham++;
  }

  /**
   * Increase the word count for spam-mails.
   */
  void countSpam(){
    spam++;
  }

  // Getter & Setter
  int getHam() {
    return ham;
  }

  int getSpam() {
    return spam;
  }

  String getWord() {
    return word;
  }
}
