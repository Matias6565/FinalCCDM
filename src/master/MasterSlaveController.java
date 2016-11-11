package master;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

public class MasterSlaveController extends UnicastRemoteObject implements
		MasterSlaveAPI {

	private static final long serialVersionUID = 1L;

	public MasterSlaveController() throws RemoteException {
		super();
	}

	@Override
	public void sendLocation(String ip, List<String> files)
			throws RemoteException {
		System.out.println("Got location of a new slave: " + ip);
		MasterController.balance.addToRank(ip);

		// maps every file in that slave to his location
		for (int i = 0; i < files.size(); i++) {
			MasterController.index.addSlaveEntry(files.get(i), ip);
			System.out.println("Found: " + files.get(i));
		}
		System.out.println("His files were succesful indexed");
	}
}
