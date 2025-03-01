package com.tw.api.unit.test.controller;

import com.tw.api.unit.test.domain.todo.Todo;
import com.tw.api.unit.test.domain.todo.TodoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
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
        Todo todo = new Todo("title", false);

        //when
        ResultActions result = mockMvc.perform(post("/todos")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(todo.toString()));

        //then
        result.andExpect(status().isCreated());
    }

    @Test
    public void deleteOneTodoNotFound() throws Exception{
        //given
        Todo todo = null;
        Optional<Todo> todoOptional = Optional.ofNullable(todo);
        when(mockTodoRepository.findById(1)).thenReturn(todoOptional);

        //when
        ResultActions result = mockMvc.perform(delete("/todos/1"));
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
        ResultActions result = mockMvc.perform(delete("/todos/1"));
        //then
        verify(mockTodoRepository, times(1)).delete(todo);
        result.andExpect(status().isOk());
    }


    @Test
    public void updateTodo() throws Exception{
        //given
        Todo todo = new Todo(1, "title", false, 1);
        Optional<Todo> todoOptional = Optional.of(todo);
        when(mockTodoRepository.findById(1)).thenReturn(todoOptional);

        //when
        ResultActions result = mockMvc.perform(patch("/todos/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(todo.toString()));

        //then
        result.andExpect(status().isOk());
        verify(mockTodoRepository, times(1)).delete(todo);
        verify(mockTodoRepository, times(1)).add(todo);
    }

    @Test
    public void updateTodoByNull() throws Exception{
        //given
        Todo todo = new Todo(1, "title", false, 1);
        Optional<Todo> todoOptional = Optional.ofNullable(null);
        when(mockTodoRepository.findById(1)).thenReturn(todoOptional);


        //when
        ResultActions result = mockMvc.perform(patch("/todos/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(todo.toString()));

        //then
        result.andExpect(status().isNotFound());
    }

}

//这种测试和之前写的controller 测试的区别，
//import 不同的依赖会直接影响测试能不能过
// 415 或者不支持contentType
// 400 参数错误
// 405 方法不支持，