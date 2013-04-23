package com.game.alias;

import java.io.IOException;
import java.net.SocketException;

import connection.Connector;
import connection.Packet;
import connection.PacketType;

public class Game {
	
	private String word;
	private boolean guessed = false;
	
	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public boolean guess(String guess) {
		boolean b = guess.equals(word);
		this.guessed = b;
		return b;
	}

	public boolean isGuessed() {
		return guessed;
	}
	
	public String receiveHint() {
		try {
			return Connector.INSTANCE.listen(PacketType.HINT).getMessage();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}

	public void sendGuess(String guess) {
		try {
			Connector.INSTANCE.send(new Packet(PacketType.GUESS, guess));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean sendStart(String ip){
		
		try {
			Connector.INSTANCE.connect(ip);
			String connectedIp = Connector.INSTANCE.listen(PacketType.CONNECT).getMessage();
			if(!ip.equals(connectedIp)){
				//display error message, something went wrong
				throw new SocketException();
			}
			Connector.INSTANCE.send(new Packet(PacketType.START, word));
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public boolean receiveStart(){
		try{
			Connector.INSTANCE.awaitConnection();
			this.word = Connector.INSTANCE.listen(PacketType.START).getMessage();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public boolean sendRestart(){
		try {
			Connector.INSTANCE.send(new Packet(PacketType.RESTART, word));
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	
	public boolean receiveRestart(){
		try {
			this.word = Connector.INSTANCE.listen(PacketType.RESTART).getMessage();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	public String receiveGuess() {
		try {
			return Connector.INSTANCE.listen(PacketType.GUESS).getMessage();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}

	public void sendHint(String hint) {
		try {
			Connector.INSTANCE.send(new Packet(PacketType.HINT, hint));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
