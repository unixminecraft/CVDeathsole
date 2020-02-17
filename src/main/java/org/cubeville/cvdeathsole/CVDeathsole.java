package org.cubeville.cvdeathsole;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class CVDeathsole extends JavaPlugin implements Listener
{
    HashMap<UUID,Long> lastDeathMessage = new HashMap<>();
    HashMap<String,List<String>> deathMessages = new HashMap<>();
    
    Random random = new Random();
    
    public void onEnable() {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(this, this);

        Configuration config = getConfig();
        Set<String> keys = config.getKeys(false);
        for(String key: keys)
            deathMessages.put(key, config.getStringList(key));
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {

        Player player = event.getEntity();
        UUID playerId = player.getUniqueId();

        if(lastDeathMessage.containsKey(playerId)) {
            if(System.currentTimeMillis() - lastDeathMessage.get(playerId) < 45000) return;
        }
        lastDeathMessage.put(playerId, System.currentTimeMillis());
        
        String cause;
        
        EntityDamageEvent damageEvent = player.getLastDamageCause();
        if(damageEvent instanceof EntityDamageByEntityEvent) {
        	String damagerName = ((EntityDamageByEntityEvent) damageEvent).getDamager().getClass().getSimpleName().toLowerCase();
        	if(deathMessages.containsKey(damagerName)) {
        		cause = damagerName;
        	}
        	else {
        		cause = damageEvent.getCause().toString().toLowerCase();
        	}
        }
        else {
        	cause = damageEvent.getCause().toString().toLowerCase();
        }
        
        List<String> clist = deathMessages.get(cause);
        if(clist == null) {
            clist = deathMessages.get("unknown");
            if(clist == null) {
            	clist = new ArrayList<String>();
            	clist.add("&f<&5Deathsole&f> &5%player%'s &cdeath was so weird, even I couldn't figure it out.");
            }
        }
        String message = clist.get(random.nextInt(clist.size())).replace('&', '§').replace("%player%", player.getName());

        for(Player p: Bukkit.getOnlinePlayers()) {
            p.sendMessage(message);
        }
        // TODO: When done testing, replace this with pcmd tr, or make configurable?
    }
        
}
