package master;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface MasterSlaveAPI extends Remote {

	/**
	 * Used by slaves to send his location to the master when it first join the
	 * file system.
	 * 
	 * @param ip
	 *            address of the slave.
	 * @throws RemoteException
	 */
	public void sendLocation(String ip, List<String> files)
			throws RemoteException;

}
