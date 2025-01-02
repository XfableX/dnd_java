package au.com.lachlanmaxwell.ttrpgbattlemanagerbackend.session;


import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Date;
import java.util.List;

@RestController
@CrossOrigin
public class AuthenticationController {

    //Move to config
    public SecretKey key = Jwts.SIG.HS256.key().build();

    @Autowired
    UserRepository userRepository;

    @GetMapping("/createUser")
    public String createUser(@RequestParam String username, @RequestParam String password) {
        TtrpgUser ttrpgUser = new TtrpgUser();
        ttrpgUser.setUsername(username);
        ttrpgUser.setPassword(hashPassword(password));
        userRepository.save(ttrpgUser);

        return Jwts.builder().subject(username).issuedAt(Date.from(Instant.now())).signWith(key).compact();
    }

    @GetMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password) {
        TtrpgUser ttrpgUser = userRepository.findByUsername(username);
        if(ttrpgUser == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid username");
        }
        if(!bytesToHex(ttrpgUser.getPassword()).equals(bytesToHex(hashPassword(password)))) {
            System.out.println();
            System.out.println(bytesToHex(hashPassword(password)));
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid password");
        }

        if(bytesToHex(ttrpgUser.getPassword()).equals(bytesToHex(hashPassword(password)))) {
            return Jwts.builder().subject(username).issuedAt(Date.from(Instant.now())).signWith(key).compact();
        }

        throw new ResponseStatusException(
                HttpStatus.FORBIDDEN, "entity not found"
        );
    }

    @GetMapping("/getUserSessions")
    public String getUserSessions(@RequestHeader String token) {
        System.out.println("Getting sesh");
        try{
            var jwt = Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
            List<GameSession> sessionList = userRepository.findByUsername(jwt.getPayload().getSubject()).OwnedSessions;
            String sessionListStr = "";
            for(GameSession session : sessionList) {
                sessionListStr += session._sessionId + ",";
            }
            System.out.println("Sessions: " + sessionListStr);
            return sessionListStr;
        }
        catch(JwtException e){
            return "err";
        }
    }

    public byte[] hashPassword(String password) {
        byte[] passwordBytes = password.getBytes(StandardCharsets.UTF_8);
        byte[] passwordHash;
        try {
            passwordHash = MessageDigest.getInstance("SHA-256").digest(passwordBytes);
        } catch (NoSuchAlgorithmException e) {
            // It should be impossible to get here, since SHA-256 is
            // a standard algorithm supported by all Java runtimes.
            throw new RuntimeException(e);
        }
        return passwordHash;
    }
    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }
}
