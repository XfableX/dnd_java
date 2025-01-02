package au.com.lachlanmaxwell.ttrpgbattlemanagerbackend.session;

import java.util.*;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.json.JSONObject;
import org.springframework.data.repository.Repository;

@Entity
public class CharacterEntity
{
    public  CharacterEntity(){
        super();
    }
    public CharacterEntity(String _uuid, String _characterName, int _initiative, int _armorClass, int _maxHealth, int _currentHealth, boolean _concentrating, boolean _reactionUsed, Condition condition, List<Status> status, int _savingThrowPos, int _savingThrowNeg, Map<String, Integer> _spellSlots, Map<String, Integer> _usedSpellSlots) {
        this._uuid = _uuid;
        this._characterName = _characterName;
        this._initiative = _initiative;
        this._armorClass = _armorClass;
        this._maxHealth = _maxHealth;
        this._currentHealth = _currentHealth;
        this._concentrating = _concentrating;
        this._reactionUsed = _reactionUsed;
        this.condition = condition;
        this.status = status;
        this._savingThrowPos = _savingThrowPos;
        this._savingThrowNeg = _savingThrowNeg;
        this._spellSlots = _spellSlots;
        this._usedSpellSlots = _usedSpellSlots;
    }

    public JSONObject getCharacterEntityJson(){
        JSONObject character = new JSONObject();
        character.put("UUID", _uuid);
        character.put("Name",_characterName);
        character.put("Inititative",_initiative);
        character.put("ArmorClass",_armorClass);
        character.put("MaxHealth", _maxHealth);
        character.put("CurrentHealth",_currentHealth);
        character.put("Concentrating",_concentrating);
        character.put("ReactionUsed",_reactionUsed);
        character.put("Condition",condition);
        character.put("Status", status);
        character.put("SpellSlots", _spellSlots);
        character.put("UsedSlots", _usedSpellSlots);
        character.put("PosSavingThrow", _savingThrowPos);
        character.put("NegSavingThrow", _savingThrowNeg);

        return character;
    }
    public CharacterEntity(String characterName, int initiative, int armorClass, int maxHealth, String uuid)
    {
        _uuid = uuid;
      _characterName = characterName;
      _initiative = initiative;
      _armorClass = armorClass;
      _maxHealth = maxHealth;
      _currentHealth = maxHealth;
    }

 public CharacterEntity(String characterName, int initiative, int armorClass, int maxHealth, Map<String, Integer> spellSlots)
 {
     this(characterName, initiative, armorClass, maxHealth);
     _spellSlots = spellSlots;
 }

    public CharacterEntity(String characterName, int initiative, int armorClass, int maxHealth)
    {
        this(characterName, initiative, armorClass, maxHealth, UUID.randomUUID().toString());
        this._spellSlots.put("1",0);
        this._spellSlots.put("2",0);
        this._spellSlots.put("3",0);
        this._spellSlots.put("4",0);
        this._spellSlots.put("5",0);
        this._spellSlots.put("6",0);
        this._spellSlots.put("7",0);
        this._spellSlots.put("8",0);
        this._spellSlots.put("9",0);
        this._usedSpellSlots.put("1",0);
        this._usedSpellSlots.put("2",0);
        this._usedSpellSlots.put("3",0);
        this._usedSpellSlots.put("4",0);
        this._usedSpellSlots.put("5",0);
        this._usedSpellSlots.put("6",0);
        this._usedSpellSlots.put("7",0);
        this._usedSpellSlots.put("8",0);
        this._usedSpellSlots.put("9",0);
    }
    @Id
    private String _uuid;
   // @ManyToOne
    //public GameSession _sessionId;
    private String _characterName;
    private int _initiative;
    private int _armorClass;
    private int _maxHealth;
    private int _currentHealth;
    private boolean _concentrating = false;
    private boolean _reactionUsed = false;
    private Condition condition = Condition.healthy;
    private List<Status> status = new ArrayList<Status>();
    private int _savingThrowPos = 0;
    private int _savingThrowNeg = 0;

    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Integer> _spellSlots = new HashMap<String,Integer>();

    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Integer> _usedSpellSlots =  new HashMap<String,Integer>();

    @Transient
    public String get_uuid() {
        return _uuid;
    }
    @Transient
    public void set_uuid(String _uuid) {
        this._uuid = _uuid;
    }
    @Transient
    public String get_characterName() {
        return _characterName;
    }
    @Transient
    public void set_characterName(String _characterName) {
        this._characterName = _characterName;
    }
    @Transient
    public int get_initiative() {
        return _initiative;
    }
    @Transient
    public void set_initiative(int _initiative) {
        this._initiative = _initiative;
    }
    @Transient
    public int get_armorClass() {
        return _armorClass;
    }
    @Transient
    public void set_armorClass(int _armorClass) {
        this._armorClass = _armorClass;
    }
    @Transient
    public int get_maxHealth() {
        return _maxHealth;
    }
    @Transient
    public void set_maxHealth(int _maxHealth) {
        this._maxHealth = _maxHealth;
    }
    @Transient
    public int get_currentHealth() {
        return _currentHealth;
    }
    @Transient
    public void set_currentHealth(int _currentHealth) {
        this._currentHealth = _currentHealth;
    }
    @Transient
    public boolean is_concentrating() {
        return _concentrating;
    }
    @Transient
    public void set_concentrating(boolean _concentrating) {
        this._concentrating = _concentrating;
    }
    @Transient
    public boolean is_reactionUsed() {
        return _reactionUsed;
    }
    @Transient
    public void set_reactionUsed(boolean _reactionUsed) {
        this._reactionUsed = _reactionUsed;
    }
    @Transient
    public Condition getCondition() {
        return condition;
    }
    @Transient
    public void setCondition(Condition condition) {
        this.condition = condition;
    }
    @Transient
    public List<Status> getStatus() {
        return status;
    }
    @Transient
    public void setStatus(List<Status> status) {
        this.status = status;
    }
    @Transient
    public int get_savingThrowPos() {
        return _savingThrowPos;
    }
    @Transient
    public void set_savingThrowPos(int _savingThrowPos) {
        this._savingThrowPos = _savingThrowPos;
    }
    @Transient
    public int get_savingThrowNeg() {
        return _savingThrowNeg;
    }
    @Transient
    public void set_savingThrowNeg(int _savingThrowNeg) {
        this._savingThrowNeg = _savingThrowNeg;
    }
    @Transient
    public Map<String, Integer> get_spellSlots() {
        return _spellSlots;
    }
    @Transient
    public void set_spellSlots(Map<String, Integer> _spellSlots) {
        this._spellSlots = _spellSlots;
    }
    @Transient
    public Map<String, Integer> get_usedSpellSlots() {
        return _usedSpellSlots;
    }
    @Transient
    public void set_usedSpellSlots(Map<String, Integer> _usedSpellSlots) {
        this._usedSpellSlots = _usedSpellSlots;
    }
}

@org.springframework.stereotype.Repository
interface CharacterRepository extends Repository<CharacterEntity,String> {
    CharacterEntity save(CharacterEntity character);
    List<CharacterEntity> findAll();
    CharacterEntity findBy_uuid(String _uuid);


}