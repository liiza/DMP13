package connection;

public class Packet {
	
	private PacketType type;
	private String message;
	
	public Packet(PacketType type, String message) {
		this.type = type;
		this.message = message;
	}

	public Packet(String prefix, String message) {
		this.type = PacketType.getType(prefix);
		this.message = message;
	}

	public PacketType getType() {
		return type;
	}

	public void setType(PacketType type) {
		this.type = type;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	@Override
	public String toString(){
		return this.type.getMarker() + ":" + this.getMessage();
	}
	
}
