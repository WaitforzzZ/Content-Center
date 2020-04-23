package com.waitfor.contentcenter.auth;

import com.waitfor.contentcenter.util.JwtOperator;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Aspect
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CheckLoginAspect {

    private final JwtOperator jwtOperator;

    @Around("@annotation(com.waitfor.contentcenter.auth.CheckLogin)")
    public Object checkLogin(ProceedingJoinPoint point){
        try {
            // 1. 从header里面获取token
            HttpServletRequest request = getHttpServletRequest();
            String token = request.getHeader("X-Token");
            // 2. 校验token是否合法&是否过期；如果不合法或已过期直接抛异常；如果合法放行
            Boolean isValid = jwtOperator.validateToken(token);
            if(!isValid){
                throw new SecurityException("Token不合法！");
            }
            // 3. 如果校验成功，那么就将用户的信息设置到request的attribute里面
            Claims claims = jwtOperator.getClaimsFromToken(token);
            request.setAttribute("id", claims.get("id"));
            request.setAttribute("wxNickname", claims.get("wxNickname"));
            request.setAttribute("role", claims.get("role"));
            return point.proceed();
        } catch (Throwable throwable) {
            throw new SecurityException("Token不合法！");
        }
    }

    private HttpServletRequest getHttpServletRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes attributes = (ServletRequestAttributes) requestAttributes;
        return attributes.getRequest();
    }
}
