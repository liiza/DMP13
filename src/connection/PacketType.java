package connection;

public enum PacketType {
	
	CONNECT("CON"),
	ACCEPT("ACC"),
	REJECT("REJ"),
	START("STA"),
	RESTART("RES"),
	STOP("STO"),
	HINT("HIN"),
	GUESS("GUE"),
	CORRECT("COR"),
	INCORRECT("INC");
	
	private String marker;

	private PacketType(String marker){
		this.marker = marker;
	}
	
	public String getMarker(){
		return marker;
	}
	
	public static PacketType getType(String s){
		for (PacketType type : PacketType.values()) {
			if(s.equals(type.getMarker())){
				return type;
			}
		}
		throw new InvalidPacketException("No packet type defined for marker byte: " + s);
	}
}
