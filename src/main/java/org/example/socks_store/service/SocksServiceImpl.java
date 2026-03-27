package org.example.socks_store.service;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.example.socks_store.dto.SockDto;
import org.example.socks_store.exeptions.*;
import org.example.socks_store.mapper.SockMapper;
import org.example.socks_store.model.Sock;
import org.example.socks_store.repository.SocksRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

@Service
@Transactional
@Slf4j
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
        log.info(responseMessage);
        return responseMessage;
    }

    @Override
    public String outcomingSocks(SockDto sockDto) {

        Optional<Sock> sock = socksRepository.findByColorAndCottonPercentage(sockDto.getColor(), sockDto.getCottonPercentage());

        if (sock.isEmpty()) {
            throw new NotFoundSocksException();
        }

        if (sock.get().getQuantity() < sockDto.getQuantity()) {
            throw new NotEnoughSocksException();
        }

        int quantity = sock.get().getQuantity() - sockDto.getQuantity();
        sock.get().setQuantity(quantity);
        socksRepository.save(sock.get());
        String responseMessage = "[]Socks of color=%s was outcoming. Rest quantity = %d".formatted(sockDto.getColor(), quantity);
        log.info(responseMessage);
        return responseMessage;
    }

    @Override
    public String updateSock(Long id, SockDto sockDto) {
        Optional<Sock> findedSock = socksRepository.findById(id);

        if (findedSock.isEmpty()) {
            throw new NotFoundSocksException();
        }

        findedSock.get().setColor(sockDto.getColor());
        findedSock.get().setCottonPercentage(sockDto.getCottonPercentage());
        findedSock.get().setQuantity(sockDto.getQuantity());

        socksRepository.save(findedSock.get());

        String responseMessage = "[]Socks of color=%s was updating. Quantity = %d"
                .formatted(sockDto.getColor(), sockDto.getQuantity());
        log.info(responseMessage);

        return responseMessage;
    }

    @Override
    public String parseAndSaveSocks(MultipartFile multipartFile) {

        if (!isCSVFile(multipartFile)) {
            throw new IncorrectFormatFileException();
        }

        Map<String, SockDto> sockDtoMap = new HashMap<>();
        int errorsCount = 0;

        try (InputStreamReader reader = new InputStreamReader(multipartFile.getInputStream())) {
            CSVParser csvParser = new CSVParser(
                    reader,
                    CSVFormat.DEFAULT.builder()
                            .setHeader("color", "cottonPercentage", "quantity")
                            .setSkipHeaderRecord(true)
                            .setIgnoreHeaderCase(true)
                            .setTrim(true)
                            .build()
            );

            for (CSVRecord csvRecord : csvParser) {
                String color = csvRecord.get("color");
                String cottonPercentageStr = csvRecord.get("cottonPercentage");
                String quantityStr = csvRecord.get("quantity");

                if (color == null
                        || cottonPercentageStr == null
                        || quantityStr == null
                        || color.isEmpty()
                        || cottonPercentageStr.isEmpty()
                        || quantityStr.isEmpty()) {

                    errorsCount++;
                    continue;
                }

                int cottonPercentage = Integer.parseInt(csvRecord.get("cottonPercentage"));
                int quantity = Integer.parseInt(csvRecord.get("quantity"));

                String key = color.toLowerCase() + "-" + cottonPercentage;

                SockDto sockDtoExisted = sockDtoMap.get(key);

                if (sockDtoExisted == null) {
                    sockDtoMap.put(key, SockDto.builder()
                            .color(color)
                            .cottonPercentage(cottonPercentage)
                            .quantity(quantity)
                            .build());
                } else {
                    sockDtoExisted.setQuantity(sockDtoExisted.getQuantity() + quantity);
                }
            }
        } catch (IOException e) {
            throw new FileProcessingException();
        }

        List<SockDto> sockDtoList = createDtoList(sockDtoMap);
        socksRepository.saveAll(sockMapper.dtoListToEntitiesList(sockDtoList));

        String responseMessage = "[]File was parsing and add to database successfully. Quantity = %d. Was find %d errors"
                .formatted(sockDtoList.size(), errorsCount);
        log.info(responseMessage);
        return responseMessage;
    }

    @Override
    public long searchSocks(
            String color,
            Integer cottonPercentage,
            String sortOperator,
            Integer cottonPercentageMin,
            Integer cottonPercentageMax) {

        if (sortOperator != null && hasNoOperator(sortOperator)) {
            throw new IncorrectOperatorException();
        }

        if ((cottonPercentageMin != null
                || cottonPercentageMax != null)
                && cottonPercentage != null) {
            throw new IncorrectCottonPercentageException();
        }

        Long result = socksRepository.sumQuantityByFilter(color,
                        cottonPercentage, sortOperator,
                        cottonPercentageMin, cottonPercentageMax)
                .orElse(0L);
        log.info("Result: {}", result);
        return result;
    }

    private boolean hasNoOperator(String sortOperator) {
        return switch (sortOperator) {
            case "moreThan", "lessThan", "equal" -> false;
            default -> true;
        };
    }

    private List<SockDto> createDtoList(Map<String, SockDto> sockDtoMap) {
        List<SockDto> sockDtoList = new ArrayList<>();

        for (SockDto sockDto : sockDtoMap.values()) {
            Optional<Sock> sockOptional = socksRepository
                    .findByColorAndCottonPercentage(
                            sockDto.getColor(), sockDto.getCottonPercentage());
            if (sockOptional.isPresent()) {
                Sock sock = sockOptional.get();

                sock.setQuantity(sock.getQuantity() + sockDto.getQuantity());
                sockDtoList.add(sockMapper.entityToDto(sock));
            } else {
                sockDtoList.add(SockDto.builder()
                        .color(sockDto.getColor())
                        .cottonPercentage(sockDto.getCottonPercentage())
                        .quantity(sockDto.getQuantity()).build());
            }
        }
        log.info(sockDtoList.size() + " socks were found.");
        return sockDtoList;
    }

    private boolean isCSVFile(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        return fileName != null && fileName.endsWith(".csv");
    }
}
