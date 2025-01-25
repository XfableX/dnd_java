package au.com.lachlanmaxwell.ttrpgbattlemanagerbackend.session;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
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
public class SessionController {

    @Autowired
    AuthenticationController authenticationController;

    @Autowired
    UserRepository userRepository;

    public TtrpgUser getUserFromJWT(String token) {
        try{
            var jwt = Jwts.parser().verifyWith(authenticationController.key).build().parseSignedClaims(token);


            return userRepository.findByUsername(jwt.getPayload().getSubject());
        }
        catch(JwtException e){
            throw new RuntimeException(e);
        }
    }

          //  new GameSession("Session", new ArrayList<CharacterEntity>());

    private SimpMessagingTemplate template;

    @Autowired
    public SessionController(SimpMessagingTemplate template) {
        this.template = template;
    }

    @Autowired
    public CharacterRepository characterRepo;


    @Autowired
    public SessionRepository sessionRepo;




    @PostMapping("nextTurn")
    public void nextTurn(@RequestHeader String sessionId, @RequestHeader String token){
        GameSession session = sessionRepo.findGameSessionBy_sessionId(sessionId);
        TtrpgUser user = getUserFromJWT(token);
        if(session.getOwner().equals(user)){
            session.nextTurn();
            sessionRepo.save(session);
            update(sessionId);
        }

    }

    @PostMapping("/updateCharacter")
    public void updateCharacter(@RequestBody String profileString, @RequestHeader String sessionId, @RequestHeader String token){
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
        String Owner = "";
        if(profile.has("Owner")) {
            Owner = profile.getString("Owner");
        }
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
        TtrpgUser user = getUserFromJWT(token);
        CharacterEntity newChara = session.CharacterEntities.stream().filter(character -> uuid.equals(character.get_uuid())).findFirst().orElse(null);
        if(session.getOwner().equals(user) || newChara.get_owner().equals(user.getUsername())){
            System.out.println("You are the owner");

            newChara.set_spellSlots(SpellSlots);
            newChara.setStatus(status);
            newChara.set_owner(Owner);
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
            if(userRepository.findByUsername(Owner) != null){
                System.out.println("User is not null");
                TtrpgUser newJoiner = userRepository.findByUsername(Owner);
                if(!newJoiner.JoinedSessions.contains(session)){
                    session.addJoinedTtrpgUser(newJoiner);
                    newJoiner.JoinedSessions.add(session);
                }

            }
            else{
                System.out.println("User is null");
            }

            characterRepo.save(newChara);
            sessionRepo.save(session);

        }
        update(sessionId);
    }

    @PostMapping("/addCharacter")
    public void recieveCharacter(@RequestBody String profileString, @RequestHeader String sessionId, @RequestHeader String token){
        GameSession session = sessionRepo.findGameSessionBy_sessionId(sessionId);
        TtrpgUser user = getUserFromJWT(token);
        if(session.getOwner().equals(user)) {
            JSONObject profile = new JSONObject(profileString);
            CharacterEntity newCharacter = new CharacterEntity(profile.getString("Name"), profile.getInt("Initiative"), profile.getInt("ArmorClass"), profile.getInt("MaxHealth"));
            session.CharacterEntities.add(newCharacter);
            characterRepo.save(newCharacter);
            List<CharacterEntity> charactersDB = characterRepo.findAll();
            for (CharacterEntity characterEntity : charactersDB) {
                System.out.println(characterEntity.get_characterName());
            }
            sessionRepo.save(session);
            update(sessionId);
        }
    }


    @PostMapping("/addEmptyCharacter")
    public void addEmptyChar(@RequestHeader String sessionId, @RequestHeader String token){
        GameSession session = sessionRepo.findGameSessionBy_sessionId(sessionId);
        TtrpgUser user = getUserFromJWT(token);
        if(session.getOwner().equals(user)) {
            CharacterEntity newCharacter = new CharacterEntity("Untitled Character", 0, 0, 0);
            session.CharacterEntities.add(newCharacter);
            //characterRepo.save(newCharacter);
            sessionRepo.save(session);
            update(sessionId);
        }
    }

    @GetMapping("/getAll")
    public String getString(@RequestHeader String sessionId, @RequestHeader String token){
        GameSession session = sessionRepo.findGameSessionBy_sessionId(sessionId);
        TtrpgUser user = getUserFromJWT(token);
        if(session.getOwner().equals(user)) {
            System.out.println(session.toJson().toString());
            return session.toJson();
        }
        else if(session.getJoinedUsers().contains(user)) {
            return session.toJsonJoinedUser(user.getUsername());
        }
        return  null;
    }

    @GetMapping("/newSession")
    public String newSession(@RequestHeader String token){
        TtrpgUser user = getUserFromJWT(token);
        GameSession session = new GameSession(getSaltString(), new ArrayList<CharacterEntity>());
        System.out.println(session.toJson().toString());
        session.setOwner(user);
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
