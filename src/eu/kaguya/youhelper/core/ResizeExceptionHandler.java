package eu.kaguya.youhelper.core;

import java.lang.Thread.UncaughtExceptionHandler;

public class ResizeExceptionHandler implements UncaughtExceptionHandler {

	private UncaughtExceptionHandler handler;
	
	public ResizeExceptionHandler(UncaughtExceptionHandler handler) {
		this.handler = handler;
	}
	
	@Override
	public void uncaughtException(Thread t, Throwable e) {
		if(!(e instanceof ThreadRemovalException)){
			if(handler != null){
				handler.uncaughtException(t, e);
			}
		}
	}

}
