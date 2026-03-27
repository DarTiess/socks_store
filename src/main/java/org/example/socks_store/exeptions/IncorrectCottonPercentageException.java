package org.example.socks_store.exeptions;

public class IncorrectCottonPercentageException extends IllegalArgumentException {
    public IncorrectCottonPercentageException() {
        super("Incorrect Cotton Percentage");
    }
}
