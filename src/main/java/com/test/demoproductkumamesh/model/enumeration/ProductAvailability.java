package com.test.demoproductkumamesh.model.enumeration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProductAvailability {
    IN_STOCK("in_stock"), OUT_OF_STOCK("out_of_stock"), UNAVAILABLE("unavailable");
    private final String value;
}
