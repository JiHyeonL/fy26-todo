package com.fy26.todo.service;

import com.fy26.todo.domain.Member;
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
    public List<Tag> createAndBindNewTags(final Todo todo, final List<String> tagNames) {
        if (tagNames.isEmpty()) {
            return List.of();
        }
        final Member member = todo.getMember();
        final List<Tag> tags = tagNames.stream()
                .map(tag -> new Tag(tag, member))
                .toList();
        tagRepository.saveAll(tags);
        final List<TodoTag> todoTags = tags.stream()
                .map(tag -> new TodoTag(todo, tag))
                .toList();
        todoTagRepository.saveAll(todoTags);
        return tags;
    }
    
    @Transactional
    public List<TodoTag> bindExistingTags(final Todo todo, final List<Tag> tags) {
        final List<TodoTag> todoTags = tags.stream()
                .map(tag -> new TodoTag(todo, tag))
                .toList();
        return todoTagRepository.saveAll(todoTags);
    }

    public List<Tag> getTagsForTodo(final Long todoId) {
        final List<TodoTag> todoTags = todoTagRepository.findAllByTodoId(todoId);
        return todoTags.stream()
                .map(TodoTag::getTag)
                .toList();
    }

    public List<Tag> getExistingTags(final List<String> tagNames, final Member member) {
        return tagRepository.findAllByMemberAndNameIn(member, tagNames);
    }
}
