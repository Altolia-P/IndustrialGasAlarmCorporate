package com.niit.industrialgasalarmcorporate.domain.product;

import java.util.Objects;

public class ProductImage {

    private final String url;
    private final String altText;
    private final int sortOrder;

    public ProductImage(String url, String altText, int sortOrder) {
        this.url = url;
        this.altText = altText;
        this.sortOrder = sortOrder;
    }

    public String getUrl() {
        return url;
    }

    public String getAltText() {
        return altText;
    }

    public int getSortOrder() {
        return sortOrder;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductImage that)) return false;
        return sortOrder == that.sortOrder && Objects.equals(url, that.url) && Objects.equals(altText, that.altText);
    }

    @Override
    public int hashCode() {
        return Objects.hash(url, altText, sortOrder);
    }
}
