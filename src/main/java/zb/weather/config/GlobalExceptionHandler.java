package zb.weather.config;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import zb.weather.error.InvalidDate;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public Exception handleAllException() {
        System.out.println("error from GlobalExceptionHandler");
        return new Exception();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST) //
    @ExceptionHandler(InvalidDate.class)
    public String handleInvalidDate(InvalidDate e) {
        System.out.println("Invalid date exception: " + e.getMessage());
        return e.getMessage();
    }
}
