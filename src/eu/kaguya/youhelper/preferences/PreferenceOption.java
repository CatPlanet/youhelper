package eu.kaguya.youhelper.preferences;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

import lombok.Getter;
import lombok.NonNull;

@Getter
@JsonInclude(Include.NON_NULL)
@JsonTypeInfo(use=Id.CLASS)
public class PreferenceOption {
	//builder
	public static class Builder {
		//fields
		String id;
		String label;
		Boolean favorite;
		
		//const
		public Builder(){}
		
		//setters
		public Builder with(@NonNull String id){
			this.id = id;
			return this;
		}
		
		public Builder describedBy(@NonNull String label){
			this.label = label;
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
		
		//builder
		public PreferenceOption build(){
			PreferenceOption c = new PreferenceOption();
			c.id = id;
			c.label = label;
			c.favorite = favorite;
			
			return c;
		}
	}
	
	//fields
	protected String id;
	protected String label;
	protected Boolean favorite;
	
	// const
	protected PreferenceOption(){}
}
