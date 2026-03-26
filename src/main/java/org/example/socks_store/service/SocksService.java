package org.example.socks_store.service;

import org.example.socks_store.dto.SockDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface SocksService {

    String incomingSocks(SockDto sockDto);

    String outcomingSocks(SockDto sockDto);

    String updateSock(Long id, SockDto sockDto);

    String parseAndSaveSocks(MultipartFile multipartFile);
}
