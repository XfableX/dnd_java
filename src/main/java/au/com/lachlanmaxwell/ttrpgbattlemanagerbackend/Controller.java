package au.com.lachlanmaxwell.ttrpgbattlemanagerbackend;

import au.com.lachlanmaxwell.ttrpgbattlemanagerbackend.CharacterEntity;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.jws.soap.SOAPBinding;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.web.bind.annotation.*;
import org.json.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@CrossOrigin
@RestController
public class Controller {
    public GameSession session = new GameSession("Session", new ArrayList<CharacterEntity>());


    private SimpMessagingTemplate template;

    @Autowired
    public Controller(SimpMessagingTemplate template) {
        this.template = template;
    }


    @PostMapping("nextTurn")
    public void nextTurn(){
        session.nextTurn();
        update();
    }
    @PostMapping("/syncSession")
    public void syncSession(@RequestBody String sessionReq){
        session.CharacterEntities = new ArrayList<CharacterEntity>();
        JSONObject sessionJson = new JSONObject(sessionReq);
        session.currentTurn = sessionJson.getJSONObject("TurnController").getInt("currentTurn");
        session.round = sessionJson.getJSONObject("TurnController").getInt("currentRound");
        JSONArray CharacterEntities = sessionJson.getJSONArray("CharacterEntities");
        for(int i = 0; i < CharacterEntities.length(); i ++){
            Map<String, Integer> SpellSlots = new HashMap<String,Integer>();
            Map<String, Integer> UsedSlots = new HashMap<String,Integer>();
            String name = CharacterEntities.getJSONObject(i).getString("Name");
            int CurHealth = CharacterEntities.getJSONObject(i).getInt("CurrentHealth");
            Condition condition = Condition.valueOf(CharacterEntities.getJSONObject(i).getString("Condition"));
            boolean concentrating = CharacterEntities.getJSONObject(i).getBoolean("Concentrating");
            boolean reactionUsed = CharacterEntities.getJSONObject(i).getBoolean("ReactionUsed");
            int initiative = CharacterEntities.getJSONObject(i).getInt("Initiative");
            int armorClass = CharacterEntities.getJSONObject(i).getInt("ArmorClass");
            int MaxHealth = CharacterEntities.getJSONObject(i).getInt("MaxHealth");
            int PosSavingThrow = CharacterEntities.getJSONObject(i).getInt("PosSavingThrow");
            int NegSavingThrow = CharacterEntities.getJSONObject(i).getInt("NegSavingThrow");
            try {
                UsedSlots = new ObjectMapper().readValue(CharacterEntities.getJSONObject(i).get("UsedSlots").toString(), Map.class);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            try {
                SpellSlots = new ObjectMapper().readValue(CharacterEntities.getJSONObject(i).get("SpellSlots").toString(), Map.class);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            CharacterEntity newChara = new CharacterEntity(name,initiative,armorClass,MaxHealth,SpellSlots);
            newChara._usedSpellSlots = UsedSlots;
            newChara._currentHealth = CurHealth;
            newChara.savingThrowNeg = NegSavingThrow;
            newChara.savingThrowPos = PosSavingThrow;
            newChara._reactionUsed = reactionUsed;
            newChara._concentrating = concentrating;
            session.CharacterEntities.add(newChara);
            System.out.println(sessionReq);
        }
    }

    @PostMapping("/updateCharacter")
    public void updateCharacter(@RequestBody String profileString){
        JSONObject profile = new JSONObject(profileString);
        String uuid = profile.getString("UUID");
        Map<String, Integer> SpellSlots = new HashMap<String,Integer>();
        Map<String, Integer> UsedSlots = new HashMap<String,Integer>();
        List<Status> status =  new ArrayList<Status>();
        JSONArray blah = profile.getJSONArray("status");
        System.out.println(blah.length());
        for(var i =0; i < blah.length(); i ++){
            status.add(Status.valueOf(blah.getString(i)));
        }
        String name = profile.getString("Name");
        int CurHealth = profile.getInt("CurrentHealth");
        Condition condition = Condition.valueOf(profile.getString("Condition"));
        boolean concentrating = profile.getBoolean("Concentrating");
        boolean reactionUsed = profile.getBoolean("ReactionUsed");
        int initiative = profile.getInt("Initiative");
        int armorClass = profile.getInt("ArmorClass");
        int MaxHealth = profile.getInt("MaxHealth");
        int PosSavingThrow = profile.getInt("PosSavingThrow");
        int NegSavingThrow = profile.getInt("NegSavingThrow");
        try {
            UsedSlots = new ObjectMapper().readValue(profile.get("UsedSlots").toString(), Map.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            SpellSlots = new ObjectMapper().readValue(profile.get("SpellSlots").toString(), Map.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        CharacterEntity newChara = session.CharacterEntities.stream().filter(character -> uuid.equals(character._uuid)).findFirst().orElse(null);
        newChara._spellSlots = SpellSlots;
        newChara.status = status;
        newChara._usedSpellSlots = UsedSlots;
        newChara._currentHealth = CurHealth;
        newChara.savingThrowNeg = NegSavingThrow;
        newChara.savingThrowPos = PosSavingThrow;
        newChara._reactionUsed = reactionUsed;
        newChara._concentrating = concentrating;
        newChara.condition = condition;
        newChara._characterName = name;
        newChara._armorClass = armorClass;
        newChara._initiative = initiative;
        newChara._maxHealth = MaxHealth;


        update();
    }

    @PostMapping("/addCharacter")
    public void recieveCharacter(@RequestBody String profileString){
        JSONObject profile = new JSONObject(profileString);
        CharacterEntity newCharacter = new CharacterEntity(profile.getString("Name"),profile.getInt("Initiative"), profile.getInt("ArmorClass"), profile.getInt("MaxHealth"));
        session.CharacterEntities.add(newCharacter);

        update();
    }

    @PostMapping("/addEmptyCharacter")
    public void addEmptyChar(){
        CharacterEntity newCharacter = new CharacterEntity("Untitled Character",0, 0, 0);
        session.CharacterEntities.add(newCharacter);

        update();
    }

    @GetMapping("/getAll")
    public String getString(){
        System.out.println(session.toJson().toString());
        return  session.toJson();
    }

    @MessageMapping("/app")
    @SendTo("/socket/update")
    public String update()  {
        System.out.println("update sent");
        template.convertAndSend("/socket/update","update");
        return "update";
    }

    @SubscribeMapping("/socket/update")
    public String initialReply() throws Exception {
        System.out.println("update sent");

        return "Welcome to the chat room.";
    }

}
