package connection;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

import android.util.Log;

public class ConnectorImpl implements Connector {

	public static final int SERVER_PORT = 1024;
	
	ConnectorImpl(){};
	
	private boolean connected = false;

	private String address;
	private ServerSocket serverSocket;
	private Socket clientSocket;

	@Override
	public Packet listen(PacketType expected) throws IOException {
		init();

		Socket client = serverSocket.accept();
		BufferedReader in = new BufferedReader(new InputStreamReader(
				client.getInputStream()));
		String line = in.readLine();
		String[] split = line.split(":");
		if(PacketType.getType(split[0]) != expected)
			throw new UnexpectedResponseException(split[0]);
		return new Packet(split[0], split[1]);
	}
	
	@Override
	public boolean awaitConnection() throws IOException{
		init();
		
		if(connected){
			return false;
		}
		
		String ip = listen(PacketType.CONNECT).getMessage();
		connect(ip);
		return true;
	}

	@Override
	public boolean connect(String ip) throws IOException {
		init();

		if (connected)
			return false;

		InetAddress serverAddr = InetAddress.getByName(ip);
		clientSocket = new Socket(serverAddr, SERVER_PORT);
		connected = true;
		return send(new Packet(PacketType.CONNECT,address));
	}
	
	@Override
	public void disconnect() throws IOException{
		if(!connected)
			return;
		
		send(new Packet(PacketType.STOP, "Disconnect"));
		clientSocket.close();
		clientSocket = null;
		serverSocket.close();
		serverSocket = null;
		address = null;
		connected = false;
	}

	@Override
	public boolean send(Packet packet) throws IOException {
		if(!connected)
			return false;
		PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(clientSocket
                .getOutputStream())), true);
		out.println(packet.toString());
		return true;
	}
	
	@Override
	public String getAddress() throws IOException {
		init();
		return this.address;
	}

	private void init() throws IOException {
		{
			if (address != null && serverSocket != null)
				return;
			serverSocket = new ServerSocket(SERVER_PORT);
			try {
				for (Enumeration<NetworkInterface> en = NetworkInterface
						.getNetworkInterfaces(); en.hasMoreElements();) {
					NetworkInterface intf = en.nextElement();
					for (Enumeration<InetAddress> enumIpAddr = intf
							.getInetAddresses(); enumIpAddr.hasMoreElements();) {
						InetAddress inetAddress = enumIpAddr.nextElement();
						if (!inetAddress.isLoopbackAddress()) {
							this.address = inetAddress.getHostAddress()
									.toString();
						}
					}
				}
			} catch (SocketException ex) {
				Log.e("ServerActivity", ex.toString());
			}
			throw new SocketException();
		}
	}

}
