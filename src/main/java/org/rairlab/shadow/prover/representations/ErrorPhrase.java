package org.rairlab.shadow.prover.representations;

/** REWRITE needs better explanation.
 * A Class representing an error when processing with Phrases
 * Created by naveensundarg on 8/28/17.
 */
public class ErrorPhrase extends Phrase {

    private final String message;

    /**
     * @param message the cause of the error
     */
    public ErrorPhrase(String message) {
        this.message = message;
    }

    /**
     * @return a string containing the cause of the error
     */
    @Override
    public String toString() {
        return "(error \"" + message  + "\")";
    }
}
