package com.santiago.posada.routes;

import com.santiago.posada.repository.ToDoRepository;
import com.santiago.posada.repository.model.ToDo;
import com.santiago.posada.service.ToDoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class TaskRoutes {

    @Autowired
    private ToDoService service;

    @Bean
    public RouterFunction<ServerResponse> getTasks(){
        return route(GET("route/get/all"),
                request -> ServerResponse
                        .ok()
                        .body(BodyInserters.fromPublisher(service.getTasks(), ToDo.class)));
    }

    //Generar un tres router functions
    //Post para guardar una tarea
    @Bean
    public RouterFunction<ServerResponse> saveTasks(){
        return route(POST("/save"),
                request -> request.bodyToMono(ToDo.class)
                        .map(task -> service.addTask(task.getTask()))
                        .flatMap(taskSaved -> ServerResponse.status(HttpStatus.OK).bodyValue(taskSaved)));
    }

    //Put para actualizar
    @Bean
    public RouterFunction<ServerResponse> updateTasks(){
        return route(PUT("/update/{idTask}"),
                request -> request.bodyToMono(ToDo.class)
                        .flatMap(task -> service.updateTask(request.pathVariable("idTask"), task.getTask()))
                        .flatMap(tasksaved -> ServerResponse.ok().bodyValue(tasksaved)));
    }

    //Delete para eliminar una tarea.
    @Bean
    public RouterFunction<ServerResponse> deleteTasks(){
        return route(DELETE("/delete/{idTask}"),
                request -> request.bodyToMono(ToDo.class)
                        .flatMap(task -> service.deleteTask(request.pathVariable("idTask"))
                                .flatMap(taskDelete -> ServerResponse.noContent().build())));
    }

}
