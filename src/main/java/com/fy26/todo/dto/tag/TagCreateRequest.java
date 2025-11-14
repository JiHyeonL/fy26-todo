package com.fy26.todo.dto.tag;

import java.util.List;

public record TagCreateRequest(List<String> tagNames) {
}
