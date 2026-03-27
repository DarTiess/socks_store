package org.example.socks_store.exeptions;

public class FileProcessingException extends RuntimeException {
    public FileProcessingException() {
        super("File processing failed");
    }
}
