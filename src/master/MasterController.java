package master;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Enumeration;

public class MasterController {

	public static DistributedIndexTable index;
	public static LoadBalancer balance;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		index = new DistributedIndexTable();
		balance = new LoadBalancer();

		try {
			String serverIP = getHostAddress();
			int portSlave = 2010;
			int portClient = 2011;
			String nameSlave = "SlaveDFS";
			String nameClient = "ClientDFS";
			
			System.setProperty("java.rmi.server.hostname", serverIP);
			System.setProperty("java.security.policy", "java.policy");

			// System.setSecurityManager(new RMISecurityManager());

			Registry rSlave = LocateRegistry.createRegistry(portSlave);
			rSlave.bind(nameSlave, new MasterSlaveController());
			
			Registry rClient = LocateRegistry.createRegistry(portClient);
			rClient.bind(nameClient, new MasterClientController());

			System.out.println("Master is running...");

		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (AlreadyBoundException e) {
			e.printStackTrace();
		}

	}

	private static String getHostAddress() {
		String hostname = null;
		try {
			NetworkInterface ni = NetworkInterface.getByName("eth0");
			for (int cnt = 1; ni == null && cnt <= 5; cnt++) {
				ni = NetworkInterface.getByName("eth" + String.valueOf(cnt));
			}

			if (ni == null) {
				System.err.println("Computador não conectado na rede!");
				System.exit(1);
			}

			Enumeration<InetAddress> inetAddresses = ni.getInetAddresses();

			while (inetAddresses.hasMoreElements()) {
				InetAddress ia = inetAddresses.nextElement();
				if (!ia.isLinkLocalAddress()) {
					hostname = ia.getHostAddress();
					break;
				}
			}
		} catch (SocketException e) {
			e.printStackTrace();
		}

		return hostname;
	}

	/**
	 * Force a load balance at the whole file system.
	 */
	public static void makeBalance() {

	}

}
