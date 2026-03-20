package org.example.socks_store.service;

import org.example.socks_store.dto.SockDto;

public interface SocksService {

    String incomingSocks(SockDto sockDto);

    String outcomingSocks(SockDto sockDto);

    String updateSock(Long id, SockDto sockDto);
}
