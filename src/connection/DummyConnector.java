package connection;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class DummyConnector implements Connector {
	
	private String connectedIP;
	private static final String address = "127.0.0.1";
	
	private static final List<String> GUESSES = new ArrayList<String>();
	
	private static final List<String> HINTS = new ArrayList<String>();
	private Random generator = new Random(System.currentTimeMillis());
	
	private boolean connected = false;
	
	static{
		GUESSES.add("KALA");
		GUESSES.add("SiIkA");
		GUESSES.add("helikopteri");
		GUESSES.add("helikopter");
		GUESSES.add("wHATeVER");
		
		HINTS.add("Vihje");
		HINTS.add("EtpäsArvaakkaan");
		HINTS.add("LOLOLOL");
	}
	
	DummyConnector(){}

	@Override
	public Packet listen(PacketType expected) {
		long time = System.currentTimeMillis();
		while(System.currentTimeMillis() - time  < 1000){
			// WAITING
		}
		switch(expected){
		case CONNECT:
			connected = true;
			return new Packet(PacketType.CONNECT,connectedIP);
		case GUESS:
			return new Packet(PacketType.GUESS, GUESSES.get(generator.nextInt(GUESSES.size()-1)));
		case HINT:
			return new Packet(PacketType.HINT, HINTS.get(generator.nextInt(HINTS.size()-1)));
		case CORRECT:
			return new Packet(PacketType.CORRECT,"");
		case RESTART:
			return new Packet(PacketType.RESTART,"");
		case START:
			return new Packet(PacketType.START,"derp");
		case INCORRECT:
			return new Packet(PacketType.INCORRECT,"");
		case STOP:
			connected = false;
			return new Packet(PacketType.STOP,"");
		}
		
		return null;
	}

	@Override
	public boolean connect(String ip) {
		if(connected)
			return false;
		long time = System.currentTimeMillis();
		while(System.currentTimeMillis() - time  < 1000){
			// WAITING
		}
		this.connectedIP = ip;
		connected = true;
		return true;
	}

	@Override
	public boolean send(Packet packet) {
		if(!connected)
			return false;
		long time = System.currentTimeMillis();
		while(System.currentTimeMillis() - time  < 1000){
			// WAITING
		}
		
		return true;
	}

	@Override
	public void disconnect() {
		if(!connected)
			return;
		connected = false;
		
	}

	@Override
	public boolean awaitConnection() throws IOException {
		if(connected == true){
			return false;
		}
		
		listen(PacketType.CONNECT).getMessage();
		return true;
	}

	@Override
	public String getAddress() throws IOException {
		return address;
	}

	
}
