package com.test.demoproductkumamesh.model.enumeration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProductProperty {
    NAME("name"), UNIT_PRICE("unitPrice"), AMOUNT("amount"), CREATED_AT("createdAt"), UPDATED_AT("updatedAt");
    private final String value;
}
