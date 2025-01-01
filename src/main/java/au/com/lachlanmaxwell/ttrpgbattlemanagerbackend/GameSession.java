package au.com.lachlanmaxwell.ttrpgbattlemanagerbackend;

import jakarta.persistence.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.data.repository.Repository;

import java.util.List;

@Entity
public class GameSession {
    public GameSession(){}
    public GameSession(String SessionId, List<CharacterEntity> Characters){
        CharacterEntities = Characters;
        _sessionId = SessionId;
    }

    @Transient
    public String toJson(){
        JSONObject response = new JSONObject();
        response.put("Turn",currentTurn);
        response.put("Round",round);
        JSONArray Characters = new JSONArray();
        for(CharacterEntity i : CharacterEntities){
            Characters.put(i.getCharacterEntityJson());
        }
        response.put("Characters",Characters);
        return  response.toString();
    }
    @Id
    String _sessionId;
    int currentTurn = 0;
    int round = 1;

    @OneToMany(cascade=CascadeType.ALL)
    @JoinColumn(name = "_sessionId")
    public List<CharacterEntity> CharacterEntities;

    public void nextTurn() {
        if (currentTurn < CharacterEntities.size() - 1) {
            currentTurn++;
        } else {
            currentTurn = 0;
            round++;
        }
        if(currentTurn==0){
            for(CharacterEntity character: CharacterEntities){
                character.set_reactionUsed(false);
            }
        }
    }
}

@org.springframework.stereotype.Repository
interface SessionRepository extends Repository<GameSession,String> {
    GameSession save(GameSession character);
    List<GameSession> findAll();
    GameSession findGameSessionBy_sessionId(String _uuid);


}
