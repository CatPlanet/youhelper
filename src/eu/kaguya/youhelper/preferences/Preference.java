package eu.kaguya.youhelper.preferences;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Data;
import lombok.NonNull;

@Data
@JsonInclude(Include.NON_NULL)
@JsonTypeInfo(use=Id.CLASS)
public class Preference {
	String uuid;
	String name;
	List<PreferenceOption> options;
	
	Preference(){
		options = new ArrayList<>();
	}
	
	Preference(@NonNull String name){
		this();
		this.name = name;
	}
	
	Preference(@NonNull String uuid, @NonNull String name){
		this(name);
		this.uuid = uuid;
	}
	
	public static Preference create(){
		Preference p = new Preference();
		p.setUuid(UUID.randomUUID().toString());
		return p;
	}
	
	public static void main(String args[]) throws JsonProcessingException{
		Preference defaults = Preference.create();
		defaults.setName("default");
		
		defaults.getOptions().add(PreferenceOptionGroup.createAloneCheckboxOption(false, "playlist", "is playlist"));
		PreferenceOptionGroup rc = PreferenceOptionGroup.createEmptyCheckboxGroup("randomCheckboxes");
		rc.getOptions().add(new PreferenceChoiceOption.Builder().byDefault(true).describedBy("kupa :D").with("1").build());
		rc.getOptions().add(new PreferenceChoiceOption.Builder().byDefault(true).describedBy("fupa :D").with("2").build());
		rc.getOptions().add(new PreferenceChoiceOption.Builder().byDefault(false).describedBy("dupa :(").with("3").build());
		defaults.getOptions().add(rc);
		rc = PreferenceOptionGroup.createEmptyRadioGroup("radios");
		rc.getOptions().add(new PreferenceChoiceOption.Builder().byDefault(true).describedBy("kupa :D").with("4").build());
		rc.getOptions().add(new PreferenceChoiceOption.Builder().byDefault(true).describedBy("fupa :D").with("5").build());
		rc.getOptions().add(new PreferenceChoiceOption.Builder().byDefault(false).describedBy("dupa :(").with("6").build());
		defaults.getOptions().add(rc);
		
		ObjectMapper m = new ObjectMapper().setVisibility(PropertyAccessor.FIELD, Visibility.NON_PRIVATE);
		System.out.println(m.writerFor(Preference.class).withDefaultPrettyPrinter().writeValueAsString(defaults));
	}
}
