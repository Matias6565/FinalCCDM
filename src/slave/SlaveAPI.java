package slave;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface SlaveAPI extends Remote {
	
	public List<String> getFiles() throws RemoteException;
	
	public long getNumFiles() throws RemoteException;
	
	public long getUsedSpace() throws RemoteException;
	
}
