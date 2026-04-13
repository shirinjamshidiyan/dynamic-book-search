package com.shirin.jpaspecdynamicsearchapp.book.application.search;

import java.util.List;

public record PageResponse<T>(
    List<T> content, int page, int size, long totalElements, int totalPages) {}
