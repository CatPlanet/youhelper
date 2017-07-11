package eu.kaguya.youhelper.preferences;

import java.util.ArrayList;
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
public class PreferenceOptionList extends PreferenceOption {
	//builder
	public static class Builder {
		//fields
		String id;
		String label;
		Boolean favorite;
		List<PreferenceOption> options;
		
		//const
		public Builder(){
			options = new ArrayList<>();
		}
		
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
		
		public Builder using(@NonNull List<PreferenceOption> options){
			this.options = options;
			return this;
		}
		
		//builder
		public PreferenceOptionList build(){
			if(options == null) throw new IllegalArgumentException("options cannot be null");
			PreferenceOptionList c = new PreferenceOptionList();
			c.id = id;
			c.label = label;
			c.favorite = favorite;
			c.options = options;
			
			return c;
		}
	}
	
	//fields
	protected List<PreferenceOption> options;
	
	//const
	protected PreferenceOptionList(){
		super();
	}
}
