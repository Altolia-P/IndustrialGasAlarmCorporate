package com.niit.industrialgasalarmcorporate.common.base;

import java.util.Collections;
import java.util.List;

public class Page<T> {

    private final List<T> content;
    private final long totalElements;
    private final int totalPages;
    private final int size;
    private final int number;

    public Page(List<T> content, long totalElements, int size, int number) {
        this.content = Collections.unmodifiableList(content);
        this.totalElements = totalElements;
        this.totalPages = size > 0 ? (int) Math.ceil((double) totalElements / size) : 0;
        this.size = size;
        this.number = number;
    }

    public List<T> getContent() {
        return content;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public int getSize() {
        return size;
    }

    public int getNumber() {
        return number;
    }
}
