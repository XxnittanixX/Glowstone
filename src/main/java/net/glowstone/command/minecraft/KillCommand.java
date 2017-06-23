package net.glowstone.command.minecraft;

import net.glowstone.command.CommandTarget;
import net.glowstone.command.CommandUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.VanillaCommand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.Collections;
import java.util.List;

public class KillCommand extends VanillaCommand {
    public KillCommand() {
        super("kill", "Destroy entities.", "/kill [target]", Collections.emptyList());
        setPermission("minecraft.command.kill");
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!testPermission(sender)) {
            return false;
        }
        if (args.length == 0) {
            if (sender instanceof Entity) {
                Entity entity = (Entity) sender;
                if (entity.isDead()) {
                    entity.sendMessage("You are already dead");
                } else if (entity instanceof LivingEntity) {
                    LivingEntity living = (LivingEntity) entity;
                    living.damage(Double.MAX_VALUE, EntityDamageEvent.DamageCause.SUICIDE);
                } else {
                    entity.remove();
                }
                return true;
            } else {
                sender.sendMessage(ChatColor.RED + "Only entities can be killed. Use /kill <target> instead.");
                return false;
            }
        }
        if (args.length == 1) {
            String name = args[0];
            if (name.startsWith("@") && name.length() >= 2 && CommandUtils.isPhysical(sender)) {
                Location location = CommandUtils.getLocation(sender);
                CommandTarget target = new CommandTarget(sender, name);
                Entity[] matched = target.getMatched(location);
                for (Entity entity : matched) {
                    if (entity instanceof LivingEntity) {
                        LivingEntity living = (LivingEntity) entity;
                        living.damage(Double.MAX_VALUE);
                    } else {
                        entity.remove();
                    }
                    sender.sendMessage("Killed " + CommandUtils.getName(entity));
                }
                return true;
            } else {
                Player player = Bukkit.getPlayerExact(name);
                if (player == null) {
                    sender.sendMessage(ChatColor.RED + "Player '" + name + "' is not online.");
                    return false;
                } else {
                    player.damage(Double.MAX_VALUE);
                    sender.sendMessage("Killed " + player.getName());
                    return true;
                }
            }
        }
        sender.sendMessage(ChatColor.RED + "Usage: " + usageMessage);
        return false;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        if (args.length > 1) {
            return Collections.emptyList();
        }
        return super.tabComplete(sender, alias, args);
    }
}
