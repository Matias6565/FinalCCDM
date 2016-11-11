package master;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface MasterClientAPI extends Remote {

	public String getLocation(String path) throws RemoteException;

	public String getReplicaLocation(String path) throws RemoteException;

	public List<String> getSlaves() throws RemoteException;
	
	/**
	 * Creates an empty file.
	 * 
	 * @param fileID
	 *            path + file_name
	 */
	public String create(String fileID) throws RemoteException;

	/**
	 * Creates a directory.
	 * 
	 * @param fileID
	 *            path + directory_name
	 */
	public List<String> createDir(String fileID) throws RemoteException;

	/**
	 * List the content of a directory or show the attributes of a file.
	 * 
	 * @param fileID
	 *            path + name
	 * @return
	 */
	public List<String> lsDir(String fileID) throws RemoteException;
	
	public String ls(String fileID) throws RemoteException;

	/**
	 * Erase a file or directory.
	 * 
	 * @param fileID
	 *            path + name
	 */
	public String remove(String fileID) throws RemoteException;

	/**
	 * Copy a file to a directory. Both file or directory can be either local or
	 * remote. If the file doesn't exists in the destination, a new file is
	 * created, however if the file does exists it will be overwritten.
	 * 
	 * @param source
	 *            path + file_name
	 * @param dest
	 *            path
	 */
	public void copy(String source, String dest) throws RemoteException;

	/**
	 * Move a file to a directory.
	 * 
	 * @param source
	 *            path + file_name
	 * @param dest
	 *            path
	 */
	public void mov(String source, String dest) throws RemoteException;

}
