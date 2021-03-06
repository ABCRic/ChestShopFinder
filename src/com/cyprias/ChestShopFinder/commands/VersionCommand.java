package com.cyprias.ChestShopFinder.commands;

import java.io.IOException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.bukkit.command.CommandSender;
import com.cyprias.ChestShopFinder.Logger;
import com.cyprias.ChestShopFinder.Perm;
import com.cyprias.ChestShopFinder.Plugin;
import com.cyprias.ChestShopFinder.VersionChecker;
import com.cyprias.ChestShopFinder.VersionChecker.versionInfo;
import com.cyprias.ChestShopFinder.command.Command;
import com.cyprias.ChestShopFinder.command.CommandAccess;
import com.cyprias.ChestShopFinder.configuration.Config;
import com.cyprias.ChestShopFinder.utils.ChatUtils;

public class VersionCommand implements Command {
	public void listCommands(CommandSender sender, List<String> list) {
		if (Plugin.hasPermission(sender, Perm.VERSION))
			list.add("/%s version - Get the plugin version.");
	}

	public boolean execute(final CommandSender sender, org.bukkit.command.Command cmd, String[] args) {
		if (!Plugin.checkPermission(sender, Perm.VERSION))
			return false;
		
		/*
		 * if (args.length == 0){ getCommands(sender, cmd); return true; }
		 */

		final Plugin instance = Plugin.getInstance();

		if (Config.getBoolean("properties.check-new-version")) {

			instance.getServer().getScheduler().runTaskAsynchronously(instance, new Runnable() {
				public void run() {
					try {
						VersionChecker version = new VersionChecker("http://dev.bukkit.org/server-mods/chestshopfinder/files.rss");
						versionInfo info = (version.versions.size() > 0) ? version.versions.get(0) : null;
						String curVersion = instance.getDescription().getVersion();
						if (info != null) {
							Logger.info("compare " + VersionChecker.compareVersions(curVersion, info.getTitle()));
							if (VersionChecker.compareVersions(curVersion, info.getTitle()) < 0) {
								ChatUtils.send(sender, "�7We're running v�f" + curVersion + "�7, v�f" + info.getTitle() + " �7is available");
							} else if (VersionChecker.compareVersions(curVersion, info.getTitle()) == 1) {
								ChatUtils.send(sender, "�7We're running the latest version v�f" + curVersion);
							}
						} else {
							ChatUtils.send(sender, "�7We're running version v�f" + curVersion);
						}
					} catch (IOException e) {
						e.printStackTrace();
					} catch (ParserConfigurationException e) {
						e.printStackTrace();
					}

				}
			});
		} else {
			ChatUtils.send(sender, "�7We're running version v�f" + instance.getDescription().getVersion());
		}

		return true;
	}

	public CommandAccess getAccess() {
		return CommandAccess.BOTH;
	}

	public void getCommands(CommandSender sender, org.bukkit.command.Command cmd) {
		ChatUtils.sendCommandHelp(sender, Perm.VERSION, "/%s version - Get the plugin version.", cmd);
	}

	public boolean hasValues() {
		return false;
	}
}
