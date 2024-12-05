package market.api.exceptions;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
	private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);


	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
		Map<String, String> errors = new HashMap<>();

		ex.getBindingResult().getAllErrors().forEach((error) -> {
			String fieldName = ((FieldError) error).getField();
			String errorMessage = error.getDefaultMessage();

			errors.put(fieldName, errorMessage);
		});

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
	}

	@ExceptionHandler({BadCredentialsException.class, ExpiredJwtException.class, SignatureException.class, MalformedJwtException.class})
	public ResponseEntity<Map<String, String>> handleUnauthorizedException(Exception ex) {
		Map<String, String> errorResponse = new HashMap<>();
		errorResponse.put("error", "Unauthorized");
		errorResponse.put("message", ex.getMessage());

		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
	}

	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<Map<String, String>> handleAccessDeniedException(AccessDeniedException ex) {
		Map<String, String> errorResponse = new HashMap<>();
		errorResponse.put("error", "Forbidden");
		errorResponse.put("message", ex.getMessage());
		return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
	}

	@ExceptionHandler(ConflictException.class)
	public ResponseEntity<Map<String, String>> handleConflictException(ConflictException exception) {
		Map<String, String> errorDetail = new HashMap<>();
		errorDetail.put("error", "Conflict");
		errorDetail.put("message", exception.getMessage());

		return ResponseEntity.status(HttpStatus.CONFLICT).body(errorDetail);
	}


	@ExceptionHandler(Exception.class)
	public ResponseEntity<Map<String, String>> handleUnknownTypeException(Exception exception) {
		// TODO: Log the full stack trace to an observability tool

		logger.error("Exception: ", exception);

		Map<String, String> errorDetail = new HashMap<>();
		errorDetail.put("error", "Internal Server Error");
		errorDetail.put("message", "An unexpected error occurred");

		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorDetail);
	}
}
