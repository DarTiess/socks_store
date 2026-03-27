package org.example.socks_store.controller;

import jakarta.annotation.Resource;
import org.example.socks_store.dto.SockDto;
import org.example.socks_store.mapper.SockMapperImpl;
import org.example.socks_store.model.Sock;
import org.example.socks_store.repository.SocksRepository;
import org.example.socks_store.service.SocksServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import tools.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = SocksController.class)
@AutoConfigureMockMvc
@Import(value = {
        SocksServiceImpl.class,
        SockMapperImpl.class
})
class SocksControllerTest {

    @Resource
    private MockMvc mockMvc;

    @Resource
    private ObjectMapper objectMapper;

    @MockitoBean
    private SocksRepository socksRepository;
    private Sock sockEntity;
    private SockDto sockDto;

    @BeforeEach
    public void setUp() {
        sockEntity = Sock.builder()
                .id(1L)
                .color("black")
                .cottonPercentage(30)
                .quantity(4)
                .build();

        sockDto = SockDto.builder()
                .id(1L)
                .color("black")
                .cottonPercentage(30)
                .quantity(4)
                .build();
    }

    @ParameterizedTest
    @CsvSource({
            "true",
            "false"
    })
    void incomeSocksTest(boolean isSockPresent) throws Exception {

        String url = "/api/socks/income";

        if (isSockPresent) {
            when(socksRepository.findByColorAndCottonPercentage(sockDto.getColor(), sockDto.getCottonPercentage()))
                    .thenReturn(Optional.of(sockEntity));

            MvcResult mvcResult = mockMvc.perform(post(url)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(sockDto))
                            .characterEncoding("UTF-8")
                    )
                    .andReturn();

            Assertions.assertEquals(mvcResult.getResponse().getStatus(), 200);
            Assertions.assertEquals(mvcResult.getResponse().getContentAsString(), "[]Socks of color=black was add. Total quantity = 8");

        } else {
            MvcResult mvcResult = mockMvc.perform(post(url)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(sockDto))
                            .characterEncoding("UTF-8")
                    )
                    .andReturn();

            Assertions.assertEquals(mvcResult.getResponse().getStatus(), 200);
            Assertions.assertEquals(mvcResult.getResponse().getContentAsString(), "[]Socks of color=black was created. Total quantity = 4");
        }
    }

    @Test
    void outcomeSocksTest() throws Exception {
        String url = "/api/socks/outcome";

        when(socksRepository.findByColorAndCottonPercentage(sockDto.getColor(), sockDto.getCottonPercentage()))
                .thenReturn(Optional.of(sockEntity));

        MvcResult mvcResult = mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sockDto))
                        .characterEncoding("UTF-8")
                )
                .andReturn();

        Assertions.assertEquals(mvcResult.getResponse().getStatus(), 200);
        Assertions.assertEquals(mvcResult.getResponse().getContentAsString(), "[]Socks of color=black was outcoming. Rest quantity = 0");


    }

    @Test
    void updateSockTest() throws Exception {
        String url = "/api/socks/%d";

        when(socksRepository.findById(anyLong()))
                .thenReturn(Optional.of(sockEntity));

        mockMvc.perform(put(url.formatted(1))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sockDto))
                        .characterEncoding("UTF-8")
                )
                .andExpect(status().is(200))
                .andExpect(content()
                        .bytes("[]Socks of color=black was updating. Quantity = 4".getBytes()));
    }

    @Test
    void batchSocksTest() throws Exception {
        String url = "/api/socks/batch";

        ClassPathResource classPathResource = new ClassPathResource("test.csv");
        InputStream inputStream = classPathResource.getInputStream();
        MockMultipartFile multipartFile = new MockMultipartFile("file",
                classPathResource.getFilename(),
                MediaType.TEXT_PLAIN_VALUE,
                inputStream);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.multipart(url)
                        .file(multipartFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(status().is(200))
                .andReturn();

        Assertions.assertEquals(mvcResult.getResponse().getStatus(), 200);
        Assertions.assertEquals(mvcResult.getResponse().getContentAsString(), "[]File was parsing and add to database successfully. Quantity = 3");

    }

    @ParameterizedTest
    @CsvSource({
            "black, null, 30, null, null, null",
            "white, 40, 20, lessThan, 20, 80",
            "black, 30, 30, equal, 67",
    })
    void searchSocksTest(String color, int cottonPercentage, int count, String operators, int cottonPercentageMin, int cottonPercentageMax) throws Exception {
        String url = "/api/socks?color=%s&cottonPercentage=%d&operators=%s"
                .formatted(color, cottonPercentage, operators);


                when(socksRepository.sumQuantityByFilter(
                        color, cottonPercentage,
                        operators, cottonPercentageMin, cottonPercentageMax
                )).thenReturn(Optional.of(Optional.ofNullable(Long.valueOf(count)).orElse(0L)));



        mockMvc.perform(get(url))
                .andExpect(status().is(200))
                .andExpect(content().string(String.valueOf(count)));

    }

}