package com.tw.api.unit.test.controller;

import com.tw.api.unit.test.domain.todo.Todo;
import com.tw.api.unit.test.domain.todo.TodoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(TodoController.class)
@ActiveProfiles(profiles = "test")
class TodoControllerTest {

    @Autowired
    private TodoController todoController;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TodoRepository mockTodoRepository;

    @Test
    public void getAll() throws Exception{
        //given
        List<Todo> todoList = new ArrayList<>();
        when(mockTodoRepository.getAll()).thenReturn(todoList);
        //when
        ResultActions result = mockMvc.perform(get("/todos"));
        //then
        result.andExpect(status().isOk());
    }

    @Test
    public void getTodo() throws Exception{
        //given
        Todo todo = new Todo();
        Optional<Todo> todoOptional = Optional.of(todo);
        when(mockTodoRepository.findById(1)).thenReturn(todoOptional);

        //when
        ResultActions result = mockMvc.perform(get("/todos/1"));
        //then
        result.andExpect(status().isOk());
    }

    @Test
    public void getTodoNotFound() throws Exception{
        //given
        Todo todo = null;
        Optional<Todo> todoOptional = Optional.ofNullable(todo);
        when(mockTodoRepository.findById(1)).thenReturn(todoOptional);

        //when
        ResultActions result = mockMvc.perform(get("/todos/1"));
        //then
        result.andExpect(status().isNotFound());
    }

    @Test
    public void saveTodo() throws Exception{
        //given
        List<Todo> todoList = new ArrayList<>();
        when(mockTodoRepository.getAll()).thenReturn(todoList);
        Todo todo = new Todo();

        //when
        ResultActions result = mockMvc.perform(post("/todos/1", todo));
        //then
        verify(mockTodoRepository, times(1)).add(todo);
    }

    @Test
    public void deleteOneTodoNotFound() throws Exception{
        //given
        Todo todo = null;
        Optional<Todo> todoOptional = Optional.ofNullable(todo);
        when(mockTodoRepository.findById(1)).thenReturn(todoOptional);

        //when
        ResultActions result = mockMvc.perform(delete("/todos/1", todo));
        //then
        result.andExpect(status().isNotFound());

    }
    @Test
    public void deleteOneTodo() throws Exception{
        //given
        Todo todo = new Todo();
        Optional<Todo> todoOptional = Optional.of(todo);
        when(mockTodoRepository.findById(1)).thenReturn(todoOptional);

        //when
        ResultActions result = mockMvc.perform(delete("/todos/1", todo));
        //then
        verify(mockTodoRepository, times(1)).delete(todo);
        result.andExpect(status().isOk());
    }
}

//这种测试和之前写的controller 测试的区别，
//import 不同的依赖会直接影响测试能不能过