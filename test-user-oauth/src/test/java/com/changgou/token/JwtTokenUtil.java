package com.changgou.token;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.util.PublicKeyFactory;
import org.bouncycastle.openssl.PEMParser;
import org.springframework.core.io.ClassPathResource;





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

    
    public static void main(String[] args) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
    	Map<String,Object> cliam = new HashMap<>();
    	cliam.put("1", 1);
    	String token = generateToken("test","brook",1000000,cliam);

    	
    	
    	publicKey = getPubKeyFromFile("huawei.pub");

    	
    	
    	//String key =new  Base64Enc
    	Claims claim = parseToken(token,publicKey);
    	System.out.println("claim:"+claim);
    	
    }
    
    

    public static Claims parseToken(String token, PublicKey publicKey) {
    	Claims claims = null;
        try {
           claims = getTokenBody(token,publicKey);
        } catch (Exception e) {
        	System.out.println(e.getMessage());
        }
        return claims;
    }

  

   
    private static Claims getTokenBody(String token,PublicKey publicKey){

        return Jwts.parser()
        		//.setSigningKey(base64EncodedKeyBytes)
                .setSigningKey(publicKey)
                .parseClaimsJws(token)
                .getBody();
    }
    
    public static PublicKey getPubKeyFromFile(String fileName)  {
       	PublicKey pubKey = null ;
    	 ClassPathResource resource = new ClassPathResource(fileName);
    	 InputStreamReader reader=null;
 
        try  {
         	 reader =  new InputStreamReader(resource.getInputStream());
        	@SuppressWarnings("resource")
			PEMParser pp = new PEMParser(reader);
    	    SubjectPublicKeyInfo subjPubKeyInfo = (SubjectPublicKeyInfo) pp.readObject();
    	    RSAKeyParameters rsa = (RSAKeyParameters) PublicKeyFactory.createKey(subjPubKeyInfo);
    	 
    	    RSAPublicKeySpec rsaSpec = new RSAPublicKeySpec(rsa.getModulus(), rsa.getExponent());
    	    KeyFactory kf = KeyFactory.getInstance("RSA");
    	    pubKey = kf.generatePublic(rsaSpec);
    	    System.out.println(pubKey);
        } catch(Exception e) {
        	System.out.println(e.getMessage());
        } finally {
        	try {
				reader.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
		return pubKey;
    }
    
}

