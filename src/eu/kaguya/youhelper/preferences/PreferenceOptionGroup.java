package eu.kaguya.youhelper.preferences;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

import lombok.Getter;
import lombok.NonNull;

@Getter
@JsonInclude(Include.NON_NULL)
@JsonTypeInfo(use=Id.CLASS)
public class PreferenceOptionGroup extends PreferenceOption {
	//enum
	public enum Type {
		ONLY_ONE_CHOICE, ZERO_OR_MORE_CHOICES
	}
	
	//builder
	public static class Builder {
		//fields
		String id;
		PreferenceOptionGroup.Type type;
		List<PreferenceChoiceOption> options;
		Boolean favorite;
		
		//const
		public Builder(){
			options = new ArrayList<>();
		}
		
		//setters
		public Builder with(@NonNull String id){
			this.id = id;
			return this;
		}
		
		public Builder type(@NonNull PreferenceOptionGroup.Type type){
			this.type = type;
			return this;
		}
		
		public Builder using(@NonNull List<PreferenceChoiceOption> options){
			this.options = options;
			return this;
		}
		
		public Builder favoritable(boolean defaultFavoriteStatus){
			this.favorite = defaultFavoriteStatus;
			return this;
		}
		
		public Builder unfavoritable(){
			this.favorite = null;
			return this;
		}
		
		//build
		public PreferenceOptionGroup build(){
			if(type == null) throw new IllegalArgumentException("type cannot be null");
			if(options == null) throw new IllegalArgumentException("options cannot be null");
			PreferenceOptionGroup c = new PreferenceOptionGroup();
			c.id = id;
			c.type = type;
			c.favorite = favorite;
			c.options = options;
			
			return c;
		}
	}
	
	//fields
	protected Type type;
	protected List<PreferenceChoiceOption> options;
	
	//const
	protected PreferenceOptionGroup(){
		super();
	}
	
	//convenient api
	public void add(PreferenceChoiceOption c) {
		options.add(c);
	}
	
	// helper static methods
	public static PreferenceOptionGroup createAloneCheckboxOption(boolean checked, @NonNull String id, @NonNull String checkboxLabel){
		PreferenceOptionGroup g = new PreferenceOptionGroup.Builder()
				.type(PreferenceOptionGroup.Type.ZERO_OR_MORE_CHOICES)
				.with(id)
				.using(Arrays.asList(new PreferenceChoiceOption.Builder().byDefault(checked).describedBy(checkboxLabel).build()))
				.build();
		
		return g;
	}
	
	public static PreferenceOptionGroup createEmptyRadioGroup(@NonNull String id){
		PreferenceOptionGroup g = new PreferenceOptionGroup.Builder()
				.type(PreferenceOptionGroup.Type.ONLY_ONE_CHOICE)
				.with(id)
				.build();
		
		return g;
	}
	
	public static PreferenceOptionGroup createEmptyCheckboxGroup(@NonNull String id){
		PreferenceOptionGroup g = new PreferenceOptionGroup.Builder()
				.type(PreferenceOptionGroup.Type.ZERO_OR_MORE_CHOICES)
				.with(id)
				.build();
		
		return g;
	}
}
