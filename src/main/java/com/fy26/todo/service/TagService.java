package com.fy26.todo.service;

import com.fy26.todo.domain.Tag;
import com.fy26.todo.domain.Todo;
import com.fy26.todo.domain.TodoTag;
import com.fy26.todo.exception.TagErrorCode;
import com.fy26.todo.exception.TagException;
import com.fy26.todo.repository.TagRepository;
import com.fy26.todo.repository.TodoRepository;
import com.fy26.todo.repository.TodoTagRepository;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class TagService {

    private final TodoRepository todoRepository;
    private final TagRepository tagRepository;
    private final TodoTagRepository todoTagRepository;

    @Transactional
    public List<Tag> createTagsForTodo(final Todo todo, final List<String> tagNames) {
        final List<Tag> tags = tagNames.stream()
                .map(Tag::new)
                .toList();
        tagRepository.saveAll(tags);
        final List<TodoTag> todoTags = tags.stream()
                .map(tag -> new TodoTag(todo, tag))
                .toList();
        todoTagRepository.saveAll(todoTags);
        return tags;
    }

    public List<Tag> getTagsForTodo(final Long todoId) {
        final List<TodoTag> todoTags = todoTagRepository.findAllByTodoId(todoId);
        final List<Tag> tags = new ArrayList<>();
        for (final TodoTag todoTag : todoTags) {
            Tag tag = tagRepository.findById(todoTag.getTag().getId())
                    .orElseThrow(() -> new TagException(TagErrorCode.TAG_NOT_FOUND, Map.of("tagId", todoTag.getTag())));
            tags.add(tag);
        }
        return tags;
    }
}
