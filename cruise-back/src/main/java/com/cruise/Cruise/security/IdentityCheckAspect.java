package com.cruise.Cruise.security;

import com.cruise.Cruise.security.jwt.JwtTokenUtil;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

// Prerequisites:
@Aspect
@Component
public class IdentityCheckAspect {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Before("@annotation(IdentityCheck)")
    public void checkUserIdentity(JoinPoint joinPoint) {
        int indexOfId = findIndexOfIdParameter(joinPoint);
        if (indexOfId == -1) {
            return;
        }
        Long idFromRequest = (Long) joinPoint.getArgs()[indexOfId];

        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        String token = request.getHeader("Authorization").split(" ")[1];
        String role = jwtTokenUtil.getRoleFromToken(token);
        Long id = jwtTokenUtil.getUserIdFromToken(token);

        if (role.equals("ROLE_ADMIN")) {
            return;
        }

        if (!Objects.equals(id, idFromRequest)) {
            System.err.println("Identity not valid! " + joinPoint.getSignature().toShortString());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Requested endpoint does not exist");
        }
    }

    private int findIndexOfIdParameter(JoinPoint joinPoint) {
        String[] tokens = joinPoint.getSignature().toString().replace(")", "").split("\\(")[1].split(",");
        for (int i = 0; i < tokens.length; i++) {
            if (tokens[i].equals("Long")) {
                return i;
            }
        }
        return -1;
    }
}
