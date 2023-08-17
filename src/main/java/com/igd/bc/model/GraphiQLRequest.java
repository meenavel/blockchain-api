package com.igd.bc.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class GraphiQLRequest {
    private String query;
    private String variables;
    private Extensions extensions;
}
