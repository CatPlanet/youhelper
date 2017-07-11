package eu.kaguya.youhelper.config;

import org.aeonbits.owner.Accessible;
import org.aeonbits.owner.Config.LoadPolicy;
import org.aeonbits.owner.Config.LoadType;
import org.aeonbits.owner.Config.Sources;
import org.aeonbits.owner.Mutable;
import org.aeonbits.owner.Reloadable;

@LoadPolicy(LoadType.FIRST)
@Sources({"file:${user.home}/.youhelper/configuration.properties"})
public interface YouHelperConfiguration extends Mutable, Accessible, Reloadable {
	@Key("client.youtubedl.directory")
	String directory();
	@Key("client.youtubedl.executable")
	@DefaultValue("youtube-dl.exe")
	String executable();
}
