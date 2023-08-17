package com.igd.bc.model;

import lombok.Data;
import lombok.ToString;
import java.util.List;

@Data
@ToString
public class APIGQLResponse{
    private List<Transfer> data;
}
