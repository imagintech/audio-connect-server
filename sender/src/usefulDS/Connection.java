package usefulDS;

import java.net.*;
import java.io.*; 

/* Class to contain all connection related details */
public class Connection {
	private String  server_ip    = "server ip";
	private Integer server_port  = 1234;
	private InetAddress  client_ip    = null;
	private Integer client_port  = 4321;
	private String  current_status = "connected";
	
	private DataInputStream dis = null;
	private DataOutputStream dos = null;
	private Socket s = null;
	
	public Connection(InetAddress client_ip, Integer client_port, DataInputStream dis, DataOutputStream dos, Socket s)
	{
		this.client_ip = client_ip;
		this.client_port = client_port;
		this.dis = dis;
		this.dos = dos;
		this.s = s;
	}
	
	public InetAddress get_client_ip() {return this.client_ip;}
	public Integer get_client_port() {return this.client_port;}
	public DataInputStream get_dis() {return this.dis;}
	public DataOutputStream get_dos() {return this.dos;}
	public Socket get_socket() {return this.s;}

	@Override
	public String toString()
	{
		String conn="";
		conn = conn+client_ip+" "+client_port+" "+current_status+" "+s;
		return conn;
	}
}
