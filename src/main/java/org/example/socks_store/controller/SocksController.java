package org.example.socks_store.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.example.socks_store.dto.SockDto;
import org.example.socks_store.service.SocksService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Socks Store API", description = "socks store")
@RestController
@RequestMapping("/api/socks")
@Validated
public class SocksController {

    private final SocksService socksService;

    public SocksController(SocksService socksService) {
        this.socksService = socksService;
    }

    @Operation(
            summary = "Add new socks to store",
            description = "Income socks to database, return message",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "socks data",
                    content = @Content(
                            schema = @Schema(implementation = SockDto.class),
                            examples = {
                                    @ExampleObject("""
                                            {
                                                "color": "black",
                                                "cottonPercentage":67,
                                                "quantity": 70
                                            }
                                            """)
                            }
                    )
            ),
            responses = {@io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", description = "Ok"
            ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "400", description = "Validation failed", content =@Content()
                    )
            }
    )
    @PostMapping(value = "/income", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> incomeSocks(@RequestBody @NotNull SockDto sockDto) {
        String response = socksService.incomingSocks(sockDto);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Get socks from store",
            description = "Outcome socks from database, return message",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "socks data",
                    content = @Content(
                            schema = @Schema(implementation = SockDto.class),
                            examples = {
                                    @ExampleObject("""
                                            {
                                                "color": "black",
                                                "cottonPercentage":67,
                                                "quantity": 10
                                            }
                                            """)
                            }
                    )
            ),
            responses = {@io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", description = "Ok"
            ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "400", description = "Validation failed", content =@Content()
                    )
            }
    )
    @PostMapping(value = "/outcome")
    public ResponseEntity<String> outcomeSocks(@RequestBody @NotNull SockDto sockDto) {
        String response = socksService.outcomingSocks(sockDto);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Search socks in store",
            description = "Find socks from database by filter and get, return total count"
    )
    @GetMapping("")
    public ResponseEntity<Long> searchSocks(
            @RequestParam(name = "color", required = false) String color,
            @RequestParam(name = "cottonPercentage", required = false) @Min(0) @Max(100) Integer cottonPercentage,
            @RequestParam(name = "operators", required = false) String operators,
            @RequestParam(name = "cottonPercentageMin", required = false) @Min(0) @Max(100) Integer cottonPercentageMin,
            @RequestParam(name = "cottonPercentageMax", required = false) @Min(0) @Max(100) Integer cottonPercentageMax) {

        long count = socksService.searchSocks(color, cottonPercentage, operators, cottonPercentageMin, cottonPercentageMax);
        return ResponseEntity.ok(count);
    }

    @Operation(
            summary = "Update data of sock in store",
            description = "Find socks from database by  id and update info, return message"
    )
    @PutMapping("/{id}")
    public ResponseEntity<String> updateSock(@NotNull @PathVariable("id") Long id,
                                             @NotNull @RequestBody SockDto sockDto) {
        String message = socksService.updateSock(id, sockDto);
        return ResponseEntity.ok(message);
    }

    @Operation(
            summary = "Loading file of socks in store",
            description = "Load file of socks data to database, return message"
    )
    @PostMapping("/batch")
    public ResponseEntity<String> batchSocks(@NotNull @RequestParam("file") MultipartFile file) {
        String parsed = socksService.parseAndSaveSocks(file);
        return ResponseEntity.ok(parsed);
    }

}
