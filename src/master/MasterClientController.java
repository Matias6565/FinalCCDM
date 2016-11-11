package master;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

public class MasterClientController extends UnicastRemoteObject implements
		MasterClientAPI {

	private static final long serialVersionUID = 1L;

	public MasterClientController() throws RemoteException {
		super();
	}

	@Override
	public String getLocation(String path) throws RemoteException {
		return MasterController.index.getLocation(path);
	}

	@Override
	public String getReplicaLocation(String path) throws RemoteException {
		// this method will be implemented at v2 for fault tolerancy purpose
		return null;
	}

	@Override
	public String create(String fileID) throws RemoteException {
		String ip = MasterController.balance.lessUsed();
		MasterController.index.addSlaveEntry(fileID, ip);
		System.out.println("Added new file '" + fileID + "' to slave '" + ip
				+ "'");
		return ip;
	}

	@Override
	public List<String> createDir(String fileID) throws RemoteException {
		String ip = MasterController.balance.lessUsed();
		MasterController.index.addSlaveEntry(fileID, ip);
		System.out.println("Added new directory '" + fileID + "' to slave '"
				+ ip + "'");
		return MasterController.index.getSlaves();
	}

	@Override
	public List<String> lsDir(String fileID) throws RemoteException {
		return MasterController.index.list(fileID);
	}

	@Override
	public String remove(String fileID) throws RemoteException {
		MasterController.index.removeSlaveEntry(fileID);
		return MasterController.index.getLocation(fileID);
	}

	@Override
	public void copy(String source, String dest) throws RemoteException {
		MasterController.index.updateDirTree(dest);
	}

	@Override
	public void mov(String source, String dest) throws RemoteException {
		MasterController.index.updateDirTree(dest);
		MasterController.index.removeSlaveEntry(source);
	}

	@Override
	public String ls(String fileID) throws RemoteException {
		return MasterController.index.getLocation(fileID);
	}

	@Override
	public List<String> getSlaves() throws RemoteException {
		return MasterController.index.getSlaves();
	}
}
