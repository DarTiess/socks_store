package org.example.socks_store.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SockDto {
    private Long id;
    private String color;
    private int cottonPercentage;
    private int quantity;
}
