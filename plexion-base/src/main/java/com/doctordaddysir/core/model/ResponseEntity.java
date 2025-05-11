package com.doctordaddysir.core.model;

import com.doctordaddysir.model.User;
import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ResponseEntity<T> {
    private T data;
    private HttpStatusCode status;

    public ResponseEntity(T data) {
        this.data = data;
    }

    public static <T> ResponseEntity<T> ok(T data) {
        return new ResponseEntity<T>(data, HttpStatusCode.OK);
    }
    public static <T> ResponseEntity<T> badRequest(T data) {
        return new ResponseEntity<T>(data, HttpStatusCode.BAD_REQUEST);
    }
    public static <T> ResponseEntity<T> unauthorized(T data) {
        return new ResponseEntity<T>(data, HttpStatusCode.UNAUTHORIZED);
    }
    public static <T> ResponseEntity<T> forbidden(T data) {
        return new ResponseEntity<T>(data, HttpStatusCode.FORBIDDEN);
    }
    public static <T> ResponseEntity<T> notFound(T data) {
        return new ResponseEntity<T>(data, HttpStatusCode.NOT_FOUND);
    }
    public static <T> ResponseEntity<T> internalServerError(T data) {
        return new ResponseEntity<T>(data, HttpStatusCode.INTERNAL_SERVER_ERROR);
    }
    public static <T> ResponseEntity<T> created(T data) {
        return new ResponseEntity<T>(data, HttpStatusCode.CREATED);
    }
    public static <T> ResponseEntity<T> noContent(T data) {
        return new ResponseEntity<T>(data, HttpStatusCode.NO_CONTENT);
    }


    public enum HttpStatusCode{
        OK(200),
        CREATED(201),
        NO_CONTENT(204),
        BAD_REQUEST(400),
        UNAUTHORIZED(401),
        FORBIDDEN(403),
        NOT_FOUND(404),
        INTERNAL_SERVER_ERROR(500);

        HttpStatusCode(int code) {
        }
    }

}
