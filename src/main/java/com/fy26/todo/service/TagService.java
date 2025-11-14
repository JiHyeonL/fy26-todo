package com.fy26.todo.service;

import com.fy26.todo.domain.Tag;
import com.fy26.todo.domain.Todo;
import com.fy26.todo.domain.TodoTag;
import com.fy26.todo.repository.TagRepository;
import com.fy26.todo.repository.TodoRepository;
import com.fy26.todo.repository.TodoTagRepository;
import jakarta.transaction.Transactional;
import java.util.List;
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
        return todoTags.stream()
                .map(TodoTag::getTag)
                .toList();
    }
}
