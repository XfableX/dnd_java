package au.com.lachlanmaxwell.ttrpgbattlemanagerbackend.session;

import jakarta.persistence.*;
import org.springframework.data.repository.Repository;

import java.util.List;

@Entity
public class TtrpgUser {
    public TtrpgUser() {
    }

    public byte[] getPassword() {
        return password;
    }

    public void setPassword(byte[] password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Id
    private String username;
    private byte[] password;

    @ManyToMany()
    public List<GameSession> JoinedSessions;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "Owner")
    public List<GameSession> OwnedSessions;
}

@org.springframework.stereotype.Repository
interface UserRepository extends Repository<TtrpgUser,String> {
    TtrpgUser save(TtrpgUser ttrpgUser);

    List<TtrpgUser> findAll();

    TtrpgUser findByUsername(String username);


}