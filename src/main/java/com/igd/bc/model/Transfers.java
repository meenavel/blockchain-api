package com.igd.bc.model;

import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class Transfers {
    private List<Transfer> transfers;
}
