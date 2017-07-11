package eu.kaguya.youhelper.preferences;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

import lombok.Getter;
import lombok.NonNull;

@Getter
@JsonInclude(Include.NON_NULL)
@JsonTypeInfo(use=Id.CLASS)
public class PreferenceChoiceOption extends PreferenceOption {
	//builder
	public static class Builder {
		//fields
		String id;
		String label;
		boolean checked;
		PreferenceOption extendable;
		
		//const
		public Builder(){}
		
		//setters
		public Builder byDefault(boolean checked){
			this.checked = checked;
			return this;
		}
		
		public Builder with(@NonNull String id){
			this.id = id;
			return this;
		}
		
		public Builder describedBy(@NonNull String label){
			this.label = label;
			return this;
		}
		
		public Builder extendsWith(@NonNull PreferenceOption extendable){
			this.extendable = extendable;
			return this;
		}
		
		//builder
		public PreferenceChoiceOption build(){
			PreferenceChoiceOption c = new PreferenceChoiceOption();
			c.id = id;
			c.label = label;
			c.checked = checked;
			c.extendable = extendable;
			
			return c;
		}
	}
	
	//fields
	protected boolean checked;
	protected PreferenceOption extendable;
	
	//const
	protected PreferenceChoiceOption(){
		super();
	}
}
