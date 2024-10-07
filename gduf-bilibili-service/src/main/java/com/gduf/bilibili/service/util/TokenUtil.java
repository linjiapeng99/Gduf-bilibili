package com.gduf.bilibili.service.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.gduf.bilibili.exception.ConditionException;

import java.util.Calendar;
import java.util.Date;

public class TokenUtil {
    private static final String ISSURE="gduf";
    public static String generateToken(Long userId) throws Exception {
        Algorithm algorithm=Algorithm.RSA256(RSAUtil.getPublicKey(),RSAUtil.getPrivateKey());
        Calendar calendar=Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_MONTH,1);
        return JWT.create().withKeyId(String.valueOf(userId))
                .withIssuer(ISSURE)
                .withExpiresAt(calendar.getTime())
                .sign(algorithm);
    }
    public static Long verifyToken(String token){
        try{
            Algorithm algorithm=Algorithm.RSA256(RSAUtil.getPublicKey(),RSAUtil.getPrivateKey());
            JWTVerifier verifier=JWT.require(algorithm).build();
            DecodedJWT jwt = verifier.verify(token);
            return Long.valueOf(jwt.getKeyId());
        }catch (TokenExpiredException e){
            throw new ConditionException("555","token过期！");
        }catch (Exception e){
            throw new ConditionException("非法用户token");
        }


    }

    public static String generateRefreshToken(Long userId) throws Exception {
        Algorithm algorithm=Algorithm.RSA256(RSAUtil.getPublicKey(),RSAUtil.getPrivateKey());
        Calendar calendar=Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_MONTH,7);
        return JWT.create().withKeyId(String.valueOf(userId))
                .withIssuer(ISSURE)
                .withExpiresAt(calendar.getTime())
                .sign(algorithm);
    }
}
