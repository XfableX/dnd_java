package au.com.lachlanmaxwell.ttrpgbattlemanagerbackend;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

public class GameSession {

    public GameSession(String SessionId, List<CharacterEntity> Characters){
        CharacterEntities = Characters;
        _sessionId = SessionId;
    }

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
    String _sessionId;
    int currentTurn = 0;
    int round = 1;
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
                character._reactionUsed = false;
            }
        }
    }
}
