package org.example.socks_store.service;

import org.example.socks_store.dto.SockDto;
import org.example.socks_store.mapper.SockMapper;
import org.example.socks_store.model.Sock;
import org.example.socks_store.repository.SocksRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SocksService {

    private final SocksRepository socksRepository;
    private final SockMapper sockMapper;

    public SocksService(SocksRepository socksRepository, SockMapper sockMapper) {
        this.socksRepository = socksRepository;
        this.sockMapper = sockMapper;
    }

    public String incomingSocks(SockDto sockDto){
        String responseMessage="";

        Optional<Sock> sock = socksRepository.findByColorAndCottonPercentage(sockDto.getColor(), sockDto.getCottonPercentage());

        if(sock.isPresent()){
            int count = sock.get().getQuantity()+ sockDto.getQuantity();
            sock.get().setQuantity(count);
            socksRepository.save(sock.get());
            responseMessage = "[]Socks of color=%s was add. Total quantity = %d".formatted(sockDto.getColor(), count);
        }else{
            socksRepository.save(sockMapper.dtoToEntity(sockDto));
            responseMessage = "[]Socks of color=%s was created. Total quantity = %d".formatted(sockDto.getColor(), sockDto.getQuantity());
        }

        return responseMessage;
    }

    public String outcomingSocks(SockDto sockDto){

        Optional<Sock> sock = socksRepository.findByColorAndCottonPercentage(sockDto.getColor(), sockDto.getCottonPercentage());

        if(sock.isEmpty()) {
            throw new IllegalArgumentException();
        }

        if(sock.get().getQuantity()<sockDto.getQuantity()){
            throw new IllegalArgumentException();
        }

        int quantity= sock.get().getQuantity() - sockDto.getQuantity();
        sock.get().setQuantity(quantity);
        socksRepository.save(sock.get());

        return "[]Socks of color=%s was outcoming. Rest quantity = %d".formatted(sockDto.getColor(), quantity);
    }
}
