package cn.gdeiassistant.ExceptionHandler;

import cn.gdeiassistant.Exception.TokenValidException.TokenExpiredException;
import cn.gdeiassistant.Exception.TokenValidException.TokenNotMatchingException;
import cn.gdeiassistant.Exception.TokenValidException.TokenServerException;
import cn.gdeiassistant.Exception.TokenValidException.SuspiciouseRequestException;
import cn.gdeiassistant.Pojo.Result.JsonResult;
import cn.gdeiassistant.Constant.ErrorConstantUtils;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(annotations = {RestController.class, Aspect.class})
@Order(value = 0)
public class TokenExceptionHandler {

    private Logger logger = LoggerFactory.getLogger(TokenExceptionHandler.class);

    /**
     * 处理登录凭证已过期的异常
     *
     * @return
     */
    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity HandleTokenExpiredException(TokenExpiredException e) {
        logger.error("TokenExceptionHandler：", e);
        return ResponseEntity.ok(new JsonResult(ErrorConstantUtils.TOKEN_EXPIRED_EXCEPTION
                , false, "登录凭证已过期，请重新登录"));
    }

    /**
     * 处理可疑访问请求的异常
     *
     * @return
     */
    @ExceptionHandler(SuspiciouseRequestException.class)
    public ResponseEntity HandleUnusualLocationException(SuspiciouseRequestException e) {
        logger.error("TokenExceptionHandler：", e);
        return ResponseEntity.ok(new JsonResult(ErrorConstantUtils.UNUSUAL_LOCATION_EXCEPTION
                , false, "可疑的登录请求，请退出账号并重新登录进行身份验证"));
    }

    /**
     * 处理没有登录凭证记录的异常
     *
     * @return
     */
    @ExceptionHandler(TokenNotMatchingException.class)
    public ResponseEntity HandleTokenNotMatchingException(TokenNotMatchingException e) {
        logger.error("TokenExceptionHandler：", e);
        return ResponseEntity.ok(new JsonResult(ErrorConstantUtils.TOKEN_NOT_MATCHING
                , false, "没有对应的登录凭证记录，请尝试重新登录"));
    }

    /**
     * 处理登录凭证校验服务异常的错误
     *
     * @return
     */
    @ExceptionHandler(TokenServerException.class)
    public ResponseEntity HandleTokenServerException(TokenServerException e) {
        logger.error("TokenExceptionHandler：", e);
        return ResponseEntity.ok(new JsonResult(ErrorConstantUtils.TOKEN_SERVER_ERROR
                , false, "登录凭证校验服务异常，请联系管理员"));
    }
}
