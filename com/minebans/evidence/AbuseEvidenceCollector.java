package com.minebans.evidence;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;

import uk.co.jacekk.bukkit.baseplugin.util.ListUtils;

import com.minebans.MineBans;
import com.minebans.events.PlayerBanEvent;

public class AbuseEvidenceCollector extends EvidenceCollector implements Listener {
	
	private HashMap<String, HashMap<String, Integer>> wordListLog;
	
	private List<String> wordList;
	
	public AbuseEvidenceCollector(MineBans plugin){
		this.wordListLog = new HashMap<String, HashMap<String, Integer>>();
		
		// from http://www.noswearing.com/
		this.wordList = Arrays.asList("anus", "arse", "arsehole", "ass", "ass-hat", "ass-jabber", "ass-pirate", "assbag", "assbandit", "assbanger", "assbite", "assclown", "asscock", "asscracker", "asses", "assface", "assfuck", "assfucker", "assgoblin", "asshat", "asshead", "asshole", "asshopper", "assjacker", "asslick", "asslicker", "assmonkey", "assmunch", "assmuncher", "assnigger", "asspirate", "assshit", "assshole", "asssucker", "asswad", "asswipe", "axwound", "bampot", "bastard", "beaner", "bitch", "bitchass", "bitches", "bitchtits", "bitchy", "blowjob", "bollocks", "bollox", "boner", "brotherfucker", "bullshit", "bumblefuck", "butt-pirate", "buttfucka", "buttfucker", "carpetmuncher", "chesticle", "chinc", "chink", "choad", "chode", "clit", "clitface", "clitfuck", "clusterfuck", "cock", "cockass", "cockbite", "cockburger", "cockface", "cockfucker", "cockhead", "cockjockey", "cockknoker", "cockmaster", "cockmongler", "cockmongruel", "cockmonkey", "cockmuncher", "cocknose", "cocknugget", "cockshit", "cocksmith", "cocksmoke", "cocksmoker", "cocksniffer", "cocksucker", "cockwaffle", "coochie", "coochy", "coon", "cooter", "cracker", "cum", "cumbubble", "cumdumpster", "cumguzzler", "cumjockey", "cumslut", "cumtart", "cunnie", "cunnilingus", "cunt", "cuntass", "cuntface", "cunthole", "cuntlicker", "cuntrag", "cuntslut", "dago", "damn", "deggo", "dick", "dick-sneeze", "dickbag", "dickbeaters", "dickface", "dickfuck", "dickfucker", "dickhead", "dickhole", "dickjuice", "dickmilk", "dickmonger", "dicks", "dickslap", "dicksucker", "dicksucking", "dicktickler", "dickwad", "dickweasel", "dickweed", "dickwod", "dike", "dildo", "dipshit", "doochbag", "dookie", "douche", "douche-fag", "douchebag", "douchewaffle", "dumass", "dumbass", "dumbfuck", "dumbshit", "dumshit", "dyke", "fag", "fagbag", "fagfucker", "faggit", "faggot", "faggotcock", "fagtard", "fatass", "fellatio", "feltch", "flamer", "fuck", "fuckass", "fuckbag", "fuckboy", "fuckbrain", "fuckbutt", "fuckbutter", "fucked", "fucker", "fuckersucker", "fuckface", "fuckhead", "fuckhole", "fuckin", "fucking", "fucknut", "fucknutt", "fuckoff", "fucks", "fuckstick", "fucktard", "fucktart", "fuckup", "fuckwad", "fuckwit", "fuckwitt", "fudgepacker", "gay", "gayass", "gaybob", "gaydo", "gayfuck", "gayfuckist", "gaylord", "gaytard", "gaywad", "goddamn", "goddamnit", "gooch", "gook", "gringo", "guido", "handjob", "heeb", "hell", "ho", "hoe", "homo", "homodumbshit", "honkey", "humping", "jackass", "jagoff", "jap", "jerkass", "jigaboo", "jizz", "junglebunny", "kike", "kooch", "kootch", "kraut", "kunt", "kyke", "lameass", "lardass", "lesbian", "lesbo", "lezzie", "mcfagget", "mick", "minge", "mothafucka", "motherfucker", "motherfucking", "muff", "muffdiver", "munging", "negro", "nigaboo", "nigga", "nigger", "niggers", "niglet", "nutsack", "paki", "panooch", "pecker", "peckerhead", "penis", "penisbanger", "penisfucker", "penispuffer", "piss", "pissed", "pissflaps", "polesmoker", "pollock", "poon", "poonani", "poonany", "poontang", "porchmonkey", "prick", "punanny", "punta", "pussies", "pussy", "pussylicking", "puto", "queef", "queer", "queerbait", "queerhole", "renob", "rimjob", "ruski", "sandnigger", "schlong", "scrote", "shit", "shitass", "shitbag", "shitbagger", "shitbrains", "shitbreath", "shitcanned", "shitcunt", "shitdick", "shitface", "shitfaced", "shithead", "shithole", "shithouse", "shitspitter", "shitstain", "shitter", "shittiest", "shitting", "shitty", "shiz", "shiznit", "skank", "skeet", "skullfuck", "slut", "slutbag", "smeg", "snatch", "spic", "spick", "splooge", "spook", "suckass", "tard", "testicle", "thundercunt", "tit", "titfuck", "tits", "tittyfuck", "twat", "twatlips", "twats", "twatwaffle", "unclefucker", "va-j-j", "vag", "vagina", "vajayjay", "vjayjay", "wank", "wankjob", "wetback", "whore", "whorebag", "whoreface", "wop");
		
		plugin.pluginManager.registerEvents(this, plugin);
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerChat(PlayerChatEvent event){
		String playerName = event.getPlayer().getName();
		HashMap<String, Integer> playerData;
		
		if (this.wordListLog.containsKey(playerName) == false){
			playerData = new HashMap<String, Integer>();
		}else{
			playerData = this.wordListLog.get(playerName);
		}
		
		for (String word : ChatColor.stripColor(event.getMessage().toLowerCase()).split(" ")){
			if (this.wordList.contains(word)){
				playerData.put(word, (playerData.containsKey(word)) ? playerData.get(word) + 1 : 1);
			}
		}
		
		if (playerData.size() > 0){
			this.wordListLog.put(playerName, playerData);
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerBan(PlayerBanEvent event){
		String playerName = event.getPlayerName();
		
		if (this.wordListLog.containsKey(playerName)){
			this.wordListLog.remove(playerName);
		}
	}
	
	public Integer collect(String playerName){
		if (this.wordListLog.containsKey(playerName) == false){
			return 0;
		}
		
		return ListUtils.sumIntegers(this.wordListLog.get(playerName).values());
	}
	
}
