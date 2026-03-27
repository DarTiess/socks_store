package org.example.socks_store.exeptions;

public class NotEnoughSocksException extends IllegalArgumentException {
    public NotEnoughSocksException() {
        super("Not enough socks");
    }
}
