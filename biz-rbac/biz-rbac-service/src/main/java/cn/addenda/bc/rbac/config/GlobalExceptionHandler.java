package cn.addenda.bc.rbac.config;

import cn.addenda.bc.bc.ServiceException;
import cn.addenda.bc.bc.SystemException;
import cn.addenda.bc.bc.sc.result.ControllerResult;
import cn.addenda.bc.bc.sc.result.Status;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private static final String MSG = "url：[{}]，异常信息：[{}]。";

    @ExceptionHandler(SystemException.class)
    public Object handleException(SystemException ex, HttpServletRequest request, HttpServletResponse response) {
        log.error(MSG, request.getRequestURI(), ex.getMessage(), ex);

        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

        ControllerResult<String> objectControllerResult = new ControllerResult<>();
        objectControllerResult.setReqStatus(Status.SYSTEM_EXCEPTION);
        objectControllerResult.setReqFailedCode(ex.getErrorCode());
        objectControllerResult.setReqAttachment("系统异常，请联系IT处理！");
        objectControllerResult.setResult(null);
        return objectControllerResult;
    }

    @ExceptionHandler({ServiceException.class, IllegalArgumentException.class})
    public Object handleException(ServiceException ex, HttpServletRequest request) {
        ControllerResult<String> objectControllerResult = new ControllerResult<>();
        objectControllerResult.setReqStatus(Status.SERVICE_EXCEPTION);
        objectControllerResult.setReqFailedCode(ex.getErrorCode());
        objectControllerResult.setReqAttachment(ex.getMessage());
        objectControllerResult.setResult(null);
        return objectControllerResult;
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public Object handleException(NoHandlerFoundException ex, HttpServletRequest request, HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_NOT_FOUND);

        ControllerResult<String> objectControllerResult = new ControllerResult<>();
        objectControllerResult.setReqStatus(Status.SYSTEM_EXCEPTION);
        objectControllerResult.setReqFailedCode(-2);
        objectControllerResult.setReqAttachment(request.getRequestURI());
        objectControllerResult.setResult(null);
        return objectControllerResult;
    }

    @ExceptionHandler(Exception.class)
    public Object handleException(Exception ex, HttpServletRequest request, HttpServletResponse response) {
        log.error(MSG, request.getRequestURI(), ex.getMessage(), ex);

        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

        ControllerResult<String> objectControllerResult = new ControllerResult<>();
        objectControllerResult.setReqStatus(Status.SYSTEM_EXCEPTION);
        objectControllerResult.setReqFailedCode(-1);
        objectControllerResult.setReqAttachment("系统异常，请联系IT处理！");
        objectControllerResult.setResult(null);
        return objectControllerResult;
    }

}

