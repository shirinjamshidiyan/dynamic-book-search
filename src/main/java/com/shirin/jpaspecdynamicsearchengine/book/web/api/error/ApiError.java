package com.shirin.jpaspecdynamicsearchengine.book.web.api.error;

import java.time.LocalDateTime;
import java.util.List;

public record ApiError(
    LocalDateTime timestamp,
    int status,
    String error,
    String message,
    String path,
    List<FieldErrorDTO> fieldErrors) {}
