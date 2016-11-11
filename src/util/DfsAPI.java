package util;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface DfsAPI extends Remote {
	
	/**
	 * Creates an empty file.
	 * 
	 * @param fileID
	 *            path + file_name
	 */
	public void create(String fileID) throws RemoteException;

	/**
	 * Creates a directory.
	 * 
	 * @param fileID
	 *            path + directory_name
	 */
	public void createDir(String fileID) throws RemoteException;

	/**
	 * List the content of a directory or show the attributes of a file.
	 * 
	 * @param fileID
	 *            path + name
	 * @return
	 */
	public List<String> ls(String fileID) throws RemoteException;

	/**
	 * Erase a file or directory.
	 * 
	 * @param fileID
	 *            path + name
	 */
	public void remove(String fileID) throws RemoteException;

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

	/**
	 * Write data to a file and return the number of bytes written.
	 * 
	 * @param fileID
	 *            path + file_name
	 * @param buffer
	 *            content to be written
	 * @param offset
	 *            offset from the begin of the file
	 * @param length
	 *            number of bytes to be written
	 * @return
	 */
	public int write(String fileID, byte[] buffer, int offset, int length)
			throws RemoteException;

	/**
	 * Reads data to a local buffer and returns the number of bytes read.
	 * 
	 * @param fileID
	 *            path + file_name
	 * @param buffer
	 *            local buffer to store the data
	 * @param offset
	 *            offset from the begin of the file
	 * @param length
	 *            number of bytes to be read
	 * @return
	 */
	public int read(String fileID, byte[] buffer, int offset, int length)
			throws RemoteException;

	/**
	 * Gets the length of a file.
	 * 
	 * @param fileID
	 *            path + file_name
	 * @return length of the file in bytes
	 */
	public int getLength(String fileID) throws RemoteException;

	/**
	 * Get the name of a file from his source path.
	 * 
	 * @param fileID
	 *            path + file_name
	 * @return
	 */
	public String getName(String fileID) throws RemoteException;

	/**
	 * Get the extension of a file.
	 * 
	 * @param fileID
	 *            path + file_name
	 * @return
	 */
	public String getExtension(String fileID) throws RemoteException;

}
