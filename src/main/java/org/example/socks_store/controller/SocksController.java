package org.example.socks_store.controller;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.example.socks_store.dto.SockDto;
import org.example.socks_store.service.SocksService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/socks")
public class SocksController {

    private final SocksService socksService;

    public SocksController(SocksService socksService) {
        this.socksService = socksService;
    }

    @PostMapping(value = "/income", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> incomeSocks(@RequestBody @NotNull SockDto sockDto) {
        String response = socksService.incomingSocks(sockDto);
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/outcome")
    public ResponseEntity<String> outcomeSocks(@RequestBody @NotNull SockDto sockDto) {
        String response = socksService.outcomingSocks(sockDto);
        return ResponseEntity.ok(response);
    }

    @GetMapping("")
    public ResponseEntity<Long> searchSocks(
            @RequestParam(name = "color", required = false) String color,
            @RequestParam(name = "cottonPercentage", required = false) @Min(0) @Max(100) Integer cottonPercentage,
            @RequestParam(name = "operators", required = false) String operators,
            @RequestParam(name = "cottonPercentageMin", required = false) Integer cottonPercentageMin,
            @RequestParam(name = "cottonPercentageMax", required = false) Integer cottonPercentageMax) {

        long count = socksService.searchSocks(color, cottonPercentage, operators, cottonPercentageMin, cottonPercentageMax);
        return ResponseEntity.ok(count);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateSock(@NotNull @PathVariable("id") Long id,
                                             @NotNull @RequestBody SockDto sockDto) {
        String message = socksService.updateSock(id, sockDto);
        return ResponseEntity.ok(message);
    }

    @PostMapping("/batch")
    public ResponseEntity<String> batchSocks(@NotNull @RequestParam("file") MultipartFile file) {
        String parsed = socksService.parseAndSaveSocks(file);
        return ResponseEntity.ok(parsed);
    }

}
