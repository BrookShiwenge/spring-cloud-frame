package com.test.entity;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;



import java.io.InputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class JwtTokenUtil {

    // 寻找证书文件
    private static InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("huawei.jks");
    private static PrivateKey privateKey = null;
    private static PublicKey publicKey = null;

    static { // 将证书文件里边的私钥公钥拿出来
        try {
            KeyStore keyStore = KeyStore.getInstance("JKS"); // java key store 固定常量
            keyStore.load(inputStream, "huawei".toCharArray());
            privateKey = (PrivateKey) keyStore.getKey("huawei", "huawei".toCharArray()); // jwt 为 命令生成整数文件时的别名
            publicKey = keyStore.getCertificate("huawei").getPublicKey();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 生成token
     * @param subject （主体信息）
     * @param expirationSeconds 过期时间（秒）
     * @param claims 自定义身份信息
     * @return
     */
    public static String generateToken(String subject,String issue, int expirationSeconds, Map<String,Object> claims) {
        return Jwts.builder()
                .setClaims(claims)
                .setIssuer(issue)
                .setSubject(subject)
                .setExpiration(new Date(System.currentTimeMillis() + expirationSeconds * 1000))
                .signWith(SignatureAlgorithm.RS512, privateKey)
                .compact();
    }

    
    public static void main(String[] args) {
    	Map<String,Object> cliam = new HashMap<>();
    	cliam.put("1", 1);
    	String token = generateToken("test","brook",1000000,cliam);
    	Claims claim = parseToken(token,"");
    	System.out.println(claim);
    	
    }
    
    

    public static Claims parseToken(String token, String salt) {
    	Claims claims = null;
        try {
           claims = getTokenBody(token);
        } catch (Exception e) {
        	System.out.println(e.getMessage());
        }
        return claims;
    }

  

   
    private static Claims getTokenBody(String token){
        return Jwts.parser()
                .setSigningKey(publicKey)
                .parseClaimsJws(token)
                .getBody();
    }
}

