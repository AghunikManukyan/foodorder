package com.example.api.rest;

import com.example.foodorder.common.model.ToDo;
import io.swagger.annotations.ApiOperation;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RestController
public class ToDoEndpoint {

  @GetMapping("/todos")
  @ApiOperation(value = "get All users", response = ToDo.class, responseContainer = "list")
  public ResponseEntity getAll() {
    String url = "https://jsonplaceholder.typicode.com/todos";
    RestTemplate restTemplate = new RestTemplate();

    ResponseEntity<List<ToDo>> response = restTemplate.exchange(
      url,
      HttpMethod.GET,
      null,
      new ParameterizedTypeReference<List<ToDo>>() {
      });

    List<ToDo> toDos = response.getBody();
    return ResponseEntity.ok(toDos);
  }


}
