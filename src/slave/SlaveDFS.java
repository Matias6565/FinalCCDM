package slave;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import util.DfsAPI;

public class SlaveDFS extends UnicastRemoteObject implements DfsAPI {

	private static final long serialVersionUID = 1L;

	public SlaveDFS() throws RemoteException {
		super();
	}

	@Override
	public void create(String fileID) throws RemoteException {
		File f = new File(fileID);
		try {
			f.createNewFile();
		} catch (IOException e) {
			System.out.println("Could not create the file: " + e.getMessage());
		}
	}

	@Override
	public void createDir(String fileID) throws RemoteException {
		File f = new File(fileID);
		if (!f.mkdir()) {
			System.out.println("Could not create directory");
		}
	}

	@Override
	public List<String> ls(String fileID) throws RemoteException {
		File f = new File(fileID);
		List<String> l = new LinkedList<String>();
		if (f.isDirectory()) {
			String[] s = f.list();
			l.addAll(Arrays.asList(s));
		} else if (f.isFile()) {
			int i = getName(fileID).lastIndexOf(".");
			if (i == -1)
				i = getName(fileID).length();
			l.add("Name: " + getName(fileID).substring(0, i));
			l.add("Extension: " + getExtension(fileID));
			l.add("Size: " + getLength(fileID) + " bytes");
		}
		return l;
	}

	@Override
	public void remove(String fileID) throws RemoteException {
		if (fileID.equals(new String("sd:"))
				|| fileID.equals(new String("sd:/"))) {
			System.out.println("Cannot not delete the root folder");
			return;
		}
		if (!new File(fileID).delete()) {
			System.out.println("Could not delete file");
		}
	}

	/**
	 * The copy command is empty, because the client is responsible for sending
	 * the reads and writes at the source and destination locations.
	 * 
	 * This is used to avoid the slaves going to the master to ask for others
	 * slave's locations. They should work independently.
	 * 
	 * @param source
	 * @param dest
	 * @throws RemoteException
	 */
	@Override
	public void copy(String source, String dest) throws RemoteException {
	}

	/**
	 * The move command is not used by the client for the same reasons that the
	 * copy command, only the client is responsible for the reads and writes.
	 * 
	 * The move command is used by the master for load balance purposes. It will
	 * make connection to others slaves and transfers your files to his disk.
	 * 
	 * Not used at this version because of the round-robin politic implemented
	 * in the load balancer.
	 * 
	 * @param source
	 * @param dest
	 * @throws RemoteException
	 */
	@Override
	public void mov(String source, String dest) throws RemoteException {
	}

	@Override
	public int write(String fileID, byte[] buffer, int offset, int length)
			throws RemoteException {
		try {
			File file = new File(fileID);
			if (!file.exists()) {
				file.createNewFile();
			}
			OutputStream out = new FileOutputStream(fileID);
			out.write(buffer, offset, length);
		} catch (FileNotFoundException e) {
			System.out.println("Could not open the file '" + fileID + "': "
					+ e.getMessage());
			return 0;
		} catch (IOException e) {
			System.out.println("Could not write to the file '" + fileID + "': "
					+ e.getMessage());
			return 0;
		}

		return (length - offset) > 0 ? length - offset : 1;
	}

	@Override
	public int read(String fileID, byte[] buffer, int offset, int length)
			throws RemoteException {
		try {
			InputStream in = new FileInputStream(fileID);
			in.read(buffer, offset, length);
		} catch (FileNotFoundException e) {
			System.out.println("Could not open the file '" + fileID + "': "
					+ e.getMessage());
			return 0;
		} catch (IOException e) {
			System.out.println("Could not write to the file '" + fileID + "': "
					+ e.getMessage());
			return 0;
		}

		return (length - offset) > 0 ? length - offset : 1;
	}

	@Override
	public int getLength(String fileID) throws RemoteException {
		return (int) new File(fileID).length();
	}

	@Override
	public String getName(String fileID) throws RemoteException {
		int i = fileID.lastIndexOf("/");
		return fileID.substring(i + 1);
	}

	@Override
	public String getExtension(String fileID) throws RemoteException {
		int i = fileID.lastIndexOf(".");
		if (i == -1)
			i = fileID.length() - 1;
		return fileID.substring(i + 1);
	}

}
