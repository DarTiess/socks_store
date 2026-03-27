package org.example.socks_store.exeptions;

public class NotFoundSocksException extends IllegalArgumentException{
    public NotFoundSocksException() {
        super("Socks not found");
    }
}
