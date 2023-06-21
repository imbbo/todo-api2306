package com.example.todo.todoapi.api;

import com.example.todo.todoapi.dto.request.TodoCreateRequestDTO;
import com.example.todo.todoapi.dto.request.TodoModifyRequestDTO;
import com.example.todo.todoapi.dto.response.TodoDetailResponseDTO;
import com.example.todo.todoapi.dto.response.TodoLIstResponseDTO;
import com.example.todo.todoapi.service.TodoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/todos")
@CrossOrigin(origins = "http://localhost:3000")
public class TodoController {

    private final TodoService todoService;

    // 할 일 등록 요청
    @PostMapping
    public ResponseEntity<?> createTodo(@Validated @RequestBody TodoCreateRequestDTO requestDTO, BindingResult result) {

//        if (dto == null) {
//            return ResponseEntity
//                    .badRequest()
//                    .body("등록 게시물 정보 없음");
//        }
//        ResponseEntity<List<FieldError>> fieldError = getValidatedResult(result);
//        if(fieldError != null) return fieldError;
//
//
//        try {
//           TodoDetailResponseDTO responseDTO = todoService.insert(dto);
//            return ResponseEntity
//                    .ok()
//                    .body(responseDTO);
//        } catch (Exception e) {
//            return ResponseEntity
//                    .internalServerError()
//                    .body("안됨");
//        }   내가 쓴거
        if(result.hasErrors()) {
            log.warn("DTO 검증 에러 발생: {}", result.getFieldError());
            return ResponseEntity
                    .badRequest()
                    .body(result.getFieldError());
        }
        try {
            TodoLIstResponseDTO responseDTO = todoService.create(requestDTO);
            return ResponseEntity
                    .ok()
                    .body(responseDTO);
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity
                    .internalServerError()
                    .body(TodoLIstResponseDTO.builder().error(e.getMessage()));
        }
    }

//    private ResponseEntity<List<FieldError>> getValidatedResult(BindingResult result) {
//        if(result.hasErrors()) {
//        List<FieldError> fieldErrors = result.getFieldErrors();
//        fieldErrors.forEach(err -> {
//            log.warn("invalid client data - {}", err.toString());
//        });
//            return ResponseEntity
//                    .badRequest()
//                    .body(fieldErrors);
//        }
//        return null;
//    } 내가 쓴거

    // 할 일 삭제 요청
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTodo(@PathVariable("id") String todoId) {

        log.info("/api/todos/{} DELETE request!", todoId);

        if(todoId == null || todoId.trim().equals("")) {
            return ResponseEntity
                    .badRequest()
                    .body(TodoLIstResponseDTO.builder().error("ID를 전달해 주세요."));
        }

        try {
            TodoLIstResponseDTO responseDTO = todoService.delete(todoId);
            return ResponseEntity.ok().body(responseDTO);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(TodoLIstResponseDTO.builder().error(e.getMessage()));
        }

    }

    // 할 일 목록 요청
    @GetMapping
    public ResponseEntity<?> retrieveTodoList() {
    log.info("/api/todos GET request");
    TodoLIstResponseDTO responseDTO = todoService.retrieve();

    return ResponseEntity.ok().body(responseDTO);

    }

    // 할 일 수정 요청
    @RequestMapping(method = {RequestMethod.PUT, RequestMethod.PATCH})
    public ResponseEntity<?> updateTodo(
            @Validated @RequestBody TodoModifyRequestDTO requestDTO,
            BindingResult result,
            HttpServletRequest request
    ) {

        if(result.hasErrors()) {
            return ResponseEntity.badRequest()
                    .body(result.getFieldError());
        }
        log.info("/api/todos {} request!", request.getMethod());
        log.info("modifying dto: {}", requestDTO);

        try {
            TodoLIstResponseDTO responseDTO = todoService.update(requestDTO);
            return ResponseEntity.ok().body(responseDTO);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(TodoLIstResponseDTO.builder().error(e.getMessage()));
        }

    }

}
