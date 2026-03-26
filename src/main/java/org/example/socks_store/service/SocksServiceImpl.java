package org.example.socks_store.service;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.example.socks_store.dto.SockDto;
import org.example.socks_store.mapper.SockMapper;
import org.example.socks_store.model.Sock;
import org.example.socks_store.repository.SocksRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class SocksServiceImpl implements SocksService {

    private final SocksRepository socksRepository;
    private final SockMapper sockMapper;

    public SocksServiceImpl(SocksRepository socksRepository, SockMapper sockMapper) {
        this.socksRepository = socksRepository;
        this.sockMapper = sockMapper;
    }

    @Override
    public String incomingSocks(SockDto sockDto) {
        String responseMessage = "";

        Optional<Sock> sock = socksRepository.findByColorAndCottonPercentage(sockDto.getColor(), sockDto.getCottonPercentage());

        if (sock.isPresent()) {
            int count = sock.get().getQuantity() + sockDto.getQuantity();
            sock.get().setQuantity(count);
            socksRepository.save(sock.get());
            responseMessage = "[]Socks of color=%s was add. Total quantity = %d".formatted(sockDto.getColor(), count);
        } else {
            socksRepository.save(sockMapper.dtoToEntity(sockDto));
            responseMessage = "[]Socks of color=%s was created. Total quantity = %d".formatted(sockDto.getColor(), sockDto.getQuantity());
        }

        return responseMessage;
    }

    @Override
    public String outcomingSocks(SockDto sockDto) {

        Optional<Sock> sock = socksRepository.findByColorAndCottonPercentage(sockDto.getColor(), sockDto.getCottonPercentage());

        if (sock.isEmpty()) {
            throw new IllegalArgumentException();
        }

        if (sock.get().getQuantity() < sockDto.getQuantity()) {
            throw new IllegalArgumentException();
        }

        int quantity = sock.get().getQuantity() - sockDto.getQuantity();
        sock.get().setQuantity(quantity);
        socksRepository.save(sock.get());

        return "[]Socks of color=%s was outcoming. Rest quantity = %d".formatted(sockDto.getColor(), quantity);
    }

    @Override
    public String updateSock(Long id, SockDto sockDto) {
        Optional<Sock> findedSock = socksRepository.findById(id);

        if (findedSock.isEmpty()) {
            throw new IllegalArgumentException();
        }

        findedSock.get().setColor(sockDto.getColor());
        findedSock.get().setCottonPercentage(sockDto.getCottonPercentage());
        findedSock.get().setQuantity(sockDto.getQuantity());

        socksRepository.save(findedSock.get());

        return "[]Socks of color=%s was updating. Quantity = %d"
                .formatted(sockDto.getColor(), sockDto.getQuantity());

    }

    @Override
    public String parseAndSaveSocks(MultipartFile multipartFile) {
        if (!isCSVFile(multipartFile)) {
            throw new IllegalArgumentException();
        }

        List<SockDto> sockDtoList = new ArrayList<>();

        try (InputStreamReader reader = new InputStreamReader(multipartFile.getInputStream())){
            CSVParser csvParser =  new CSVParser(
                    reader,
                    CSVFormat.DEFAULT.builder()
                            .setHeader("id", "color", "cottonPercentage", "quantity")
                            .setSkipHeaderRecord(true)
                            .setIgnoreHeaderCase(true)
                            .setTrim(true)
                            .build()
            );

            for (CSVRecord csvRecord:csvParser){
                long id =Long.parseLong(csvRecord.get("id"));
                String color =csvRecord.get("color");
                int cottonPercentage = Integer.parseInt(csvRecord.get("cottonPercentage"));
                int quantity = Integer.parseInt(csvRecord.get("quantity"));

                SockDto sockDto = SockDto.builder()
                        .id(id)
                        .color(color)
                        .cottonPercentage(cottonPercentage)
                        .quantity(quantity)
                        .build();

                sockDtoList.add(sockDto);
            }
        }catch (IOException e){
            throw new IllegalArgumentException();
        }

        socksRepository.saveAll(sockMapper.dtoListToEntitiesList(sockDtoList));

        return "[]File was parsing and add to database successfully. Quantity = %d"
                .formatted(sockDtoList.size());
    }

    private boolean isCSVFile(MultipartFile file){
        String fileName = file.getOriginalFilename();
        return fileName != null && fileName.endsWith(".csv");
    }
}
