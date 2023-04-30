package ru.practicum.shareit.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.Objects;

public final class Pagination extends PageRequest {
    final int from;

    public Pagination(int from, int size, Sort sort) {
        super(from / size, size, sort);
        this.from = from;
    }

    @Override
    public long getOffset() {
        return from;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Pagination that = (Pagination) o;
        return from == that.from;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), from);
    }
}
