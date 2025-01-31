
package net.craftstars.general.security;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;

import net.craftstars.general.General;
import net.craftstars.general.security.PermissionsHandler;
import net.craftstars.general.util.LanguageText;

public class PermissionsPermissionsHandler implements PermissionsHandler {
	private PermissionHandler permissions = null;
	private boolean wasLoaded = false;
	private final String version;
	
	public PermissionsPermissionsHandler() {
		Plugin test = General.plugin.getServer().getPluginManager().getPlugin("Permissions");
		
		if(test != null) {
			try {
				General.plugin.getServer().getPluginManager().enablePlugin(test);
				this.permissions = ((Permissions) test).getHandler();
				this.wasLoaded = true;
			} catch(Error e) {
				General.logger.error(LanguageText.LOG_PERMISSIONS_ERROR.value());
				e.printStackTrace();
			}
			this.version = test.getDescription().getVersion();
		} else this.version = "0.0";
	}
	
	@Override
	public boolean hasPermission(Player who, String what) {
		return this.permissions.has(who, what);
	}
	
	@Override
	public boolean wasLoaded() {
		return wasLoaded;
	}
	
	@Override
	public boolean inGroup(Player who, String which) {
		if(which == ".isop") return who.isOp();
		return this.permissions.inGroup(who.getWorld().getName(), who.getName(), which);
	}
	
	@Override
	public String getVersion() {
		return version;
	}
	
	@Override
	public String getName() {
		return "Permissions";
	}
}
