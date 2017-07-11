package eu.kaguya.youhelper.config;

import org.aeonbits.owner.Config;
import org.aeonbits.owner.Config.LoadPolicy;
import org.aeonbits.owner.Config.Sources;
import org.aeonbits.owner.Config.LoadType;

@LoadPolicy(LoadType.FIRST)
@Sources({"file:${user.home}/.youhelper/configuration.properties"})
public interface YouHelperConfiguration extends Config {
	@Key("client.youtubedl.directory")
	String directory();
	@Key("client.youtubedl.executable")
	@DefaultValue("youtube-dl.exe")
	String executable();
}
