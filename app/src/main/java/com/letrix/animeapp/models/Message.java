package com.letrix.animeapp.models;

import com.google.gson.annotations.SerializedName;

public class Message{

	@SerializedName("stack")
	private String stack;

	@SerializedName("message")
	private String message;

	public void setStack(String stack){
		this.stack = stack;
	}

	public String getStack(){
		return stack;
	}

	public void setMessage(String message){
		this.message = message;
	}

	public String getMessage(){
		return message;
	}
}