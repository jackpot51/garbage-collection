package me.Jackpot.NoteMod;

public class Note {
	String message = "";
	public Note(String[] messages){
		for(int i = 1; i < messages.length; i++){
			message += messages[i] + " ";
		}
		message = message.trim().replace("\\c", "§").replace("\\n", "\n");
	}
	public Note(String message){
		this.message = message;
	}
}
