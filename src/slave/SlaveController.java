package slave;

import java.io.File;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import master.MasterSlaveAPI;

public class SlaveController {

	private static List<String> file;

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

	private static void loadFiles(File dir) {
		String[] files = dir.list();
		for (int i = 0; i < files.length; i++) {
			File f = new File(dir.getAbsolutePath() + "/" + files[i]);
			if (f.isDirectory()) {
				loadFiles(f);
				int index = dir.getAbsolutePath().indexOf("sd:");
				String rootPath = dir.getAbsolutePath().substring(index);
				file.add(rootPath + "/" + files[i] + "/");
				System.out.println(rootPath + "/" + files[i] + "/");
			} else {
				int index = dir.getAbsolutePath().indexOf("sd:");
				String rootPath = dir.getAbsolutePath().substring(index);
				file.add(rootPath + "/" + files[i]);
				System.out.println(rootPath + "/" + files[i]);
			}
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		if (args.length < 1) {
			System.out.println("Must enter the master IP");
			System.exit(1);
		}

		// create, if needed, the root directory of the file system
		File rootDir = new File("sd:/");
		if (!rootDir.exists()) {
			rootDir.mkdir();
		}

		file = new ArrayList<String>();
		loadFiles(rootDir);
		file.add("sd:/");

		String hostname = getHostAddress();

		try {

			String nameSlave = "SlaveDFS";
			int portSlave = 2010;

			System.setProperty("java.security.policy", "java.policy");
			// System.setSecurityManager(new RMISecurityManager());

			Registry r = LocateRegistry.getRegistry(args[0], portSlave);
			MasterSlaveAPI master = (MasterSlaveAPI) r.lookup(nameSlave);

			master.sendLocation(hostname, file);

			int portClient = 2012;
			String nameClient = "ClientDFS";

			System.setProperty("java.rmi.server.hostname", hostname);

			// System.setSecurityManager(new RMISecurityManager());

			Registry rClient = LocateRegistry.createRegistry(portClient);
			rClient.bind(nameClient, new SlaveDFS());

			System.out.println("Slave is running...");

		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (NotBoundException e) {
			e.printStackTrace();
		} catch (AlreadyBoundException e) {
			e.printStackTrace();
		}
	}

	/*
	 * @Override public List<String> getFiles() throws RemoteException { return
	 * file; }
	 * 
	 * @Override public long getNumFiles() throws RemoteException { return
	 * file.size(); }
	 * 
	 * @Override public long getUsedSpace() throws RemoteException { long
	 * usedSpace = 0; for (int i = 0; i < file.size(); i++) { usedSpace +=
	 * getLength(file.get(i)); } return usedSpace; }
	 */
}
