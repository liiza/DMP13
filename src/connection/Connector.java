package connection;

import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;


public interface Connector {
	
	public static final Connector INSTANCE = new ConnectorImpl();
	
	public Packet listen(PacketType expected) throws IOException;
	
	boolean awaitConnection() throws IOException;
	
	public boolean connect(String ip) throws IOException;
	
	public boolean send(Packet packet) throws IOException;

	public void disconnect() throws IOException;

	public String getAddress() throws IOException;
	
}
