package cn.addenda.bc.gateway.exception;

import cn.addenda.bc.bc.sc.result.Status;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;

/**
 * @author addenda
 * @since 2023/8/17 20:07
 */
public class ControllerResultErrorAttributes implements ErrorAttributes {

    private static final String ERROR_INTERNAL_ATTRIBUTE = ControllerResultErrorAttributes.class.getName() + ".ERROR";

    @Override
    public Map<String, Object> getErrorAttributes(ServerRequest request, ErrorAttributeOptions options) {
        Map<String, Object> errorAttributes = getErrorAttributes(request, options.isIncluded(ErrorAttributeOptions.Include.STACK_TRACE));

        StringBuilder reqAttachment = new StringBuilder();

        Map<String, Object> controllerResultAttributes = new HashMap<>();
        controllerResultAttributes.put("ts", ((Date) errorAttributes.get("timestamp")).getTime());
        // todo reqId应该从链路追踪里面来
        controllerResultAttributes.put("reqId", errorAttributes.get("requestId"));
        controllerResultAttributes.put("version", null);
        controllerResultAttributes.put("result", null);
        controllerResultAttributes.put("reqStatus", Status.SYSTEM_EXCEPTION);
        controllerResultAttributes.put("reqFailedCode", -999999);

        reqAttachment.append(errorAttributes.get("path")).append(".");
        reqAttachment.append(" Error:[").append(errorAttributes.get("error")).append("].");
        if (options.isIncluded(ErrorAttributeOptions.Include.EXCEPTION)) {
            Object exception = errorAttributes.remove("exception");
            if (exception != null &&
                (exception instanceof CharSequence && StringUtils.hasLength((CharSequence) exception))) {
                reqAttachment.append(" Exception:[").append(exception).append("].");
            }
        }
        if (options.isIncluded(ErrorAttributeOptions.Include.MESSAGE)) {
            Object message = errorAttributes.remove("message");
            if (message != null &&
                (message instanceof CharSequence && StringUtils.hasLength((CharSequence) message))) {
                reqAttachment.append(" Message:[").append(message).append("].");
            }
        }
        if (options.isIncluded(ErrorAttributeOptions.Include.STACK_TRACE)) {
            Object trace = errorAttributes.remove("trace");
            if (trace != null &&
                (trace instanceof CharSequence && StringUtils.hasLength((CharSequence) trace))) {
                reqAttachment.append(" Trace:[").append(trace).append("].");
            }
        }
        if (options.isIncluded(ErrorAttributeOptions.Include.BINDING_ERRORS)) {
            Object errors = errorAttributes.remove("errors");
            if (errors != null &&
                (errors instanceof CharSequence && StringUtils.hasLength((CharSequence) errors))) {
                reqAttachment.append(" Errors:[").append(errors).append("].");
            }
        }

        controllerResultAttributes.put("reqAttachment", reqAttachment.toString());
        return controllerResultAttributes;
    }

    private Map<String, Object> getErrorAttributes(ServerRequest request, boolean includeStackTrace) {
        Map<String, Object> errorAttributes = new LinkedHashMap<>();
        errorAttributes.put("timestamp", new Date());
        errorAttributes.put("path", request.path());
        Throwable error = getError(request);
        MergedAnnotation<ResponseStatus> responseStatusAnnotation = MergedAnnotations
            .from(error.getClass(), MergedAnnotations.SearchStrategy.TYPE_HIERARCHY).get(ResponseStatus.class);
        HttpStatus errorStatus = determineHttpStatus(error, responseStatusAnnotation);
        errorAttributes.put("status", errorStatus.value());
        errorAttributes.put("error", errorStatus.getReasonPhrase());
        errorAttributes.put("message", determineMessage(error, responseStatusAnnotation));
        errorAttributes.put("requestId", request.exchange().getRequest().getId());
        handleException(errorAttributes, determineException(error), includeStackTrace);
        return errorAttributes;
    }

    private HttpStatus determineHttpStatus(Throwable error, MergedAnnotation<ResponseStatus> responseStatusAnnotation) {
        if (error instanceof ResponseStatusException) {
            return ((ResponseStatusException) error).getStatus();
        }
        return responseStatusAnnotation.getValue("code", HttpStatus.class).orElse(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private String determineMessage(Throwable error, MergedAnnotation<ResponseStatus> responseStatusAnnotation) {
        if (error instanceof BindingResult) {
            return error.getMessage();
        }
        if (error instanceof ResponseStatusException) {
            return ((ResponseStatusException) error).getReason();
        }
        String reason = responseStatusAnnotation.getValue("reason", String.class).orElse("");
        if (StringUtils.hasText(reason)) {
            return reason;
        }
        return (error.getMessage() != null) ? error.getMessage() : "";
    }

    private Throwable determineException(Throwable error) {
        if (error instanceof ResponseStatusException) {
            return (error.getCause() != null) ? error.getCause() : error;
        }
        return error;
    }

    private void addStackTrace(Map<String, Object> errorAttributes, Throwable error) {
        StringWriter stackTrace = new StringWriter();
        error.printStackTrace(new PrintWriter(stackTrace));
        stackTrace.flush();
        errorAttributes.put("trace", stackTrace.toString());
    }

    private void handleException(Map<String, Object> errorAttributes, Throwable error, boolean includeStackTrace) {
        errorAttributes.put("exception", error.getClass().getName());
        if (includeStackTrace) {
            addStackTrace(errorAttributes, error);
        }
        if (error instanceof BindingResult) {
            BindingResult result = (BindingResult) error;
            if (result.hasErrors()) {
                errorAttributes.put("errors", result.getAllErrors());
            }
        }
    }

    @Override
    public Throwable getError(ServerRequest request) {
        Optional<Object> error = request.attribute(ERROR_INTERNAL_ATTRIBUTE);
        error.ifPresent((value) -> request.attributes().putIfAbsent(ErrorAttributes.ERROR_ATTRIBUTE, value));
        return (Throwable) error
            .orElseThrow(() -> new IllegalStateException("Missing exception attribute in ServerWebExchange"));
    }

    @Override
    public void storeErrorInformation(Throwable error, ServerWebExchange exchange) {
        exchange.getAttributes().putIfAbsent(ERROR_INTERNAL_ATTRIBUTE, error);
    }

}
