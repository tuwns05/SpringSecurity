package com.demo.socialmedia.util;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.expression.ParseException;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;


public class JwtUtil {

    private static final String SECRET = "mySuperSecretKeyThatIsAtLeast32CharactersLong!";
    private static final Long EXPIRATION = 86400000L;

    // Tạo signing key từ secret
    private byte[] getSigningKey() {
        // Đảm bảo secret đủ dài (HS256 yêu cầu 256 bits = 32 bytes)
        return SECRET.getBytes();
    }

    // Tạo JWT token
    public String createToken( String username, String role,Integer userId) {
        try {
            // Tạo claims
            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                    .subject(username)
                    .claim("userId", userId)
                    .claim("role", role)
                    .claim("username", username)
                    .issueTime(new Date())
                    .expirationTime(new Date(System.currentTimeMillis() + EXPIRATION))
                    .build();

            // Tạo JWS object với HMAC SHA-256
            JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.HS256)
                    .contentType("JWT")
                    .build();

            SignedJWT signedJWT = new SignedJWT(header, claimsSet);

            // Ký token
            JWSSigner signer = new MACSigner(getSigningKey());
            signedJWT.sign(signer);

            // Serialize thành chuỗi
            return signedJWT.serialize();

        } catch (JOSEException e) {
            throw new RuntimeException("Error creating JWT token", e);
        }
    }

    // Kiểm tra token có hợp lệ không
    public boolean isValid(String token) {
        if (token == null || token.trim().isEmpty()) {
            return false;
        }

        try {
            SignedJWT signedJWT = SignedJWT.parse(token);

            // Kiểm tra chữ ký
            JWSVerifier verifier = new MACVerifier(getSigningKey());
            if (!signedJWT.verify(verifier)) {
                return false; // Chữ ký không hợp lệ
            }

            // Kiểm tra hết hạn
            JWTClaimsSet claims = signedJWT.getJWTClaimsSet();
            Date expirationTime = claims.getExpirationTime();
            Date now = new Date();

            return expirationTime != null && expirationTime.after(now);

        } catch (ParseException | JOSEException e) {
            return false;
        } catch (java.text.ParseException e) {
            throw new RuntimeException(e);
        }
    }

    // Lấy username từ token
    public String getUsername(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            JWTClaimsSet claims = signedJWT.getJWTClaimsSet();
            return claims.getSubject();
        } catch (ParseException | java.text.ParseException e) {
            throw new RuntimeException("Error parsing token", e);
        }
    }

    // Lấy userId từ token
    public Integer getUserId(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            JWTClaimsSet claims = signedJWT.getJWTClaimsSet();
            return claims.getIntegerClaim("userId");
        } catch (ParseException | java.text.ParseException e) {
            throw new RuntimeException("Error parsing token", e);
        }
    }

    // Lấy role từ token
    public String getRole(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            JWTClaimsSet claims = signedJWT.getJWTClaimsSet();
            return claims.getStringClaim("role");
        } catch (ParseException | java.text.ParseException e) {
            throw new RuntimeException("Error parsing token", e);
        }
    }

    // Lấy expiration date
    public Date getExpirationDate(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            JWTClaimsSet claims = signedJWT.getJWTClaimsSet();
            return claims.getExpirationTime();
        } catch (ParseException | java.text.ParseException e) {
            throw new RuntimeException("Error parsing token", e);
        }
    }
}
