package es.um.arso.compraventa.rest.exception;

import es.um.arso.compraventa.repositorio.EntidadNoEncontrada;
import es.um.arso.compraventa.servicio.exception.ServicioExternoException;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public RespuestaError handleRuntimeException(RuntimeException e) {
        return new RespuestaError("Internal Server Error", e.getMessage());
    }

    @ExceptionHandler(EntidadNoEncontrada.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public RespuestaError handleEntidadNoEncontrada(EntidadNoEncontrada e) {
        return new RespuestaError("Not Found", e.getMessage());
    }

    @ExceptionHandler(ServicioExternoException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_GATEWAY)
    public RespuestaError handleServicioExternoException(ServicioExternoException e) {
        return new RespuestaError("Bad Gateway", e.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public RespuestaError handleIllegalArgumentException(IllegalArgumentException e) {
        return new RespuestaError("Bad Request", e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return errors;
    }

    public static class RespuestaError {
        private String error;
        private String message;

        public RespuestaError(String error, String message) {
            this.error = error;
            this.message = message;
        }

        public String getError() {
            return error;
        }

        public void setError(String error) {
            this.error = error;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
