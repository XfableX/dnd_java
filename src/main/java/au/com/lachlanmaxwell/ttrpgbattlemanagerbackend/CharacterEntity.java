package au.com.lachlanmaxwell.ttrpgbattlemanagerbackend;

import com.fasterxml.jackson.databind.util.JSONPObject;

import java.util.*;

import org.json.JSONObject;
public class CharacterEntity
{

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
        character.put("PosSavingThrow", savingThrowPos);
        character.put("NegSavingThrow",savingThrowNeg);

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
    String _uuid;
    String _characterName;
    int _initiative;
    int _armorClass;
    int _maxHealth;
    int _currentHealth;
    boolean _concentrating = false;
    boolean _reactionUsed = false;
    Condition condition = Condition.healthy;
    List<Status> status = new ArrayList<Status>();
    int savingThrowPos = 0;
    int savingThrowNeg = 0;

    Map<String, Integer> _spellSlots = new HashMap<String,Integer>();
  Map<String, Integer> _usedSpellSlots =  new HashMap<String,Integer>();

}

