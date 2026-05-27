package com.niit.industrialgasalarmcorporate.domain.product;

import java.util.Objects;

public class ProductAttribute {

    private final String attrKey;
    private final String attrVal;

    public ProductAttribute(String attrKey, String attrVal) {
        this.attrKey = attrKey;
        this.attrVal = attrVal;
    }

    public String getAttrKey() {
        return attrKey;
    }

    public String getAttrVal() {
        return attrVal;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductAttribute that)) return false;
        return Objects.equals(attrKey, that.attrKey) && Objects.equals(attrVal, that.attrVal);
    }

    @Override
    public int hashCode() {
        return Objects.hash(attrKey, attrVal);
    }
}
