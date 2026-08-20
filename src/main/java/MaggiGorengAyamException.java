/**
 * Signals an error caused by invalid user input that Maggi Goreng Ayam
 * (the chatbot) can recognize and explain, as opposed to an unexpected
 * programming error.
 */
public class MaggiGorengAyamException extends Exception {
    public MaggiGorengAyamException(String message) {
        super(message);
    }
}
