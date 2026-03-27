package org.example.socks_store.exeptions;

public class IncorrectFormatFileException extends IllegalArgumentException {
    public IncorrectFormatFileException() {
        super("Incorrect format file");
    }
}
