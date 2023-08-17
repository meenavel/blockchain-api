package com.igd.bc.model;

import lombok.*;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Transfer {
    private String id;
    private String from;
    private String to;
    private String value;
    private String blockTimestamp;
}
