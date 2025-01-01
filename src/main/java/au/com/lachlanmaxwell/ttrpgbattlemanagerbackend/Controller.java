package au.com.lachlanmaxwell.ttrpgbattlemanagerbackend;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.web.bind.annotation.*;
import org.json.*;

import java.io.IOException;
import java.util.*;

@CrossOrigin
@RestController
public class Controller{



          //  new GameSession("Session", new ArrayList<CharacterEntity>());

    private SimpMessagingTemplate template;

    @Autowired
    public Controller(SimpMessagingTemplate template) {
        this.template = template;
    }

    @Autowired
    public CharacterRepository characterRepo;


    @Autowired
    public SessionRepository sessionRepo;




    @PostMapping("nextTurn")
    public void nextTurn(@RequestHeader String sessionId){
        GameSession session = sessionRepo.findGameSessionBy_sessionId(sessionId);
        session.nextTurn();
        sessionRepo.save(session);
        update(sessionId);
    }
    @PostMapping("/syncSession")
    public void syncSession(@RequestBody String sessionReq, @RequestHeader String sessionId){
        GameSession session = sessionRepo.findGameSessionBy_sessionId(sessionId);
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
            newChara.set_usedSpellSlots(UsedSlots);
            newChara.set_currentHealth(CurHealth);
            newChara.set_savingThrowNeg(NegSavingThrow);
            newChara.set_savingThrowPos(PosSavingThrow);
            newChara.set_reactionUsed(reactionUsed);
            newChara.set_concentrating(concentrating);
            session.CharacterEntities.add(newChara);
            System.out.println(sessionReq);
        }
    }

    @PostMapping("/updateCharacter")
    public void updateCharacter(@RequestBody String profileString, @RequestHeader String sessionId){
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
        GameSession session = sessionRepo.findGameSessionBy_sessionId(sessionId);
        CharacterEntity newChara = session.CharacterEntities.stream().filter(character -> uuid.equals(character.get_uuid())).findFirst().orElse(null);
        newChara.set_spellSlots(SpellSlots);
        newChara.setStatus(status);
        newChara.set_usedSpellSlots(UsedSlots);
        newChara.set_currentHealth(CurHealth);
        newChara.set_savingThrowNeg(NegSavingThrow);
        newChara.set_savingThrowPos(PosSavingThrow);
        newChara.set_reactionUsed(reactionUsed);
        newChara.set_concentrating(concentrating);
        newChara.setCondition(condition);
        newChara.set_characterName(name);
        newChara.set_armorClass(armorClass);
        newChara.set_initiative(initiative);
        newChara.set_maxHealth(MaxHealth);
        characterRepo.save(newChara);
        sessionRepo.save(session);
        update(sessionId);
    }

    @PostMapping("/addCharacter")
    public void recieveCharacter(@RequestBody String profileString, @RequestHeader String sessionId){
        GameSession session = sessionRepo.findGameSessionBy_sessionId(sessionId);
        JSONObject profile = new JSONObject(profileString);
        CharacterEntity newCharacter = new CharacterEntity(profile.getString("Name"),profile.getInt("Initiative"), profile.getInt("ArmorClass"), profile.getInt("MaxHealth"));
        session.CharacterEntities.add(newCharacter);
        characterRepo.save(newCharacter);
        List<CharacterEntity> charactersDB = characterRepo.findAll();
        for (CharacterEntity characterEntity : charactersDB) {
            System.out.println(characterEntity.get_characterName());
        }
        sessionRepo.save(session);
        update(sessionId);
    }


    @PostMapping("/addEmptyCharacter")
    public void addEmptyChar(@RequestHeader String sessionId){
        GameSession session = sessionRepo.findGameSessionBy_sessionId(sessionId);
        CharacterEntity newCharacter = new CharacterEntity("Untitled Character",0, 0, 0);
        session.CharacterEntities.add(newCharacter);
        //characterRepo.save(newCharacter);
        sessionRepo.save(session);
        update(sessionId);
    }

    @GetMapping("/getAll")
    public String getString(@RequestHeader String sessionId){
        GameSession session = sessionRepo.findGameSessionBy_sessionId(sessionId);
        System.out.println(session.toJson().toString());
        return  session.toJson();
    }

    @GetMapping("/newSession")
    public String newSession(){
        GameSession session = new GameSession(getSaltString(), new ArrayList<CharacterEntity>());
        System.out.println(session.toJson().toString());
        sessionRepo.save(session);
        return session._sessionId;
    }

    protected String getSaltString() {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 6) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();
        return saltStr;

    }
    @MessageMapping("/app")
    @SendTo("/socket/update")
    public String update(String sessionId)  {
        System.out.println("update sent");
        template.convertAndSend("/socket/update/" + sessionId,"update");
        return "update";
    }

    @SubscribeMapping("/socket/update")
    public String initialReply() throws Exception {
        System.out.println("update sent");

        return "Welcome to the chat room.";
    }

}
