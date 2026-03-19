package org.example.socks_store.mapper;

import org.example.socks_store.dto.SockDto;
import org.example.socks_store.model.Sock;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SockMapper {
    SockDto entityToDto(Sock sock);
    Sock dtoToEntity(SockDto sockDto);
}
