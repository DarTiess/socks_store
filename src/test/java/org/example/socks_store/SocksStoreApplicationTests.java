package org.example.socks_store;

import jakarta.annotation.Resource;
import org.example.socks_store.dto.SockDto;
import org.example.socks_store.mapper.SockMapper;
import org.example.socks_store.model.Sock;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.hibernate.autoconfigure.HibernateJpaAutoConfiguration;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@EnableAutoConfiguration(
        exclude = {
                DataSourceAutoConfiguration.class,
                HibernateJpaAutoConfiguration.class
        }
)
class SocksStoreApplicationTests {

    @Resource
    private SockMapper sockMapper;

    @Test
    void contextLoads() {
    }

    @Test
    void testMapping(){
        Sock entity = Sock.builder()
                .id(1L)
                .color("black")
                .cottonPercentage(20)
                .quantity(3)
                .build();

        SockDto dto = SockDto.builder().id(1L)
                .color("black")
                .cottonPercentage(20)
                .quantity(3)
                .build();

        Sock entity1 = sockMapper.dtoToEntity(dto);
        SockDto dto1 = sockMapper.entityToDto(entity);

        Assertions.assertEquals(entity,entity1);
        Assertions.assertEquals(dto,dto1);


    }

}
