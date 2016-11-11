package client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;
import java.util.Scanner;

import master.MasterClientAPI;
import util.DfsAPI;

public class ClientController implements DfsAPI {

	private DfsAPI slave;
	private MasterClientAPI master;

	public ClientController(String masterIP) throws RemoteException,
			NotBoundException {
		String nameMaster = "ClientDFS";
		int portMaster = 2011;

		System.setProperty("java.security.policy", "java.policy");
		// System.setSecurityManager(new RMISecurityManager());

		Registry r = LocateRegistry.getRegistry(masterIP, portMaster);
		master = (MasterClientAPI) r.lookup(nameMaster);
	}

	public void execute(String[] param) {
		String command = param[0];

		if (command.compareTo("cp") == 0) {

			if (param.length < 3) {
				System.out.println("Argumentos invalidos.");
				return;
			}

			try {
				copy(param[1], param[2]);
			} catch (RemoteException e) {
				e.printStackTrace();
			}

		} else if (command.compareTo("mv") == 0) {

			if (param.length < 3) {
				System.out.println("Argumentos invalidos.");
				return;
			}

			try {
				mov(param[1], param[2]);
			} catch (RemoteException e) {
				e.printStackTrace();
			}

		} else if (command.compareTo("rm") == 0) {

			if (param.length < 2) {
				System.out.println("Argumentos invalidos.");
				return;
			}

			try {
				remove(param[1]);
			} catch (RemoteException e) {
				e.printStackTrace();
			}

		} else if (command.compareTo("ls") == 0) {

			if (param.length < 2) {
				System.out.println("Argumentos invalidos.");
				return;
			}

			List<String> l;
			try {
				l = ls(param[1]);
				for (int i = 0; i < l.size(); i++) {
					System.out.println(l.get(i));
				}
				System.out.println();
			} catch (RemoteException e) {
				e.printStackTrace();
			}

		} else if (command.compareTo("mk") == 0) {

			if (param.length < 2) {
				System.out.println("Argumentos invalidos.");
				return;
			}

			try {
				create(param[1]);
			} catch (RemoteException e) {
				e.printStackTrace();
			}

		} else if (command.compareTo("mkdir") == 0) {

			if (param.length < 2) {
				System.out.println("Argumentos invalidos.");
				return;
			}

			try {
				createDir(param[1]);
			} catch (RemoteException e) {
				e.printStackTrace();
			}

		} else if (command.compareTo("help") == 0) {

			// copy, remove, create, createDir, ls, mov
			System.out.println("cp <path + file> <dir>");
			System.out.println("mk <path + file>");
			System.out.println("mkdir <path + dir>");
			System.out.println("ls <path>");
			System.out.println("mv <path + file> <dir>");
			System.out.println("rm <path>");

		} else if (command.compareTo("exit") == 0) {
			System.exit(0);
		} else {
			System.out.println("Comando nao reconhecido.");
		}

	}

	public String[] parseArgs(String cmd) {
		return cmd.split(" ");
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length < 1) {
			System.out.println("Insira o endereco IP do mestre.");
			System.exit(1);
		}

		try {
			ClientController cc = new ClientController(args[0]);
			Scanner reader = new Scanner(System.in);
			System.out.println("Bem vindo ao Java Distributed File System");
			System.out
					.println("digite \"help\" para visualizar os comandos disponiveis");
			System.out.println("-------------------------------------------");
			while (true) {
				System.out.print("> ");
				String cmd = reader.nextLine();
				cc.execute(cc.parseArgs(cmd));
			}
		} catch (RemoteException e) {
			System.err.println(e.getMessage());
			System.err.println("\nNao pode se conectar ao servidor principal.");
			System.err.println("Verifique se o cabo de rede esta conectado!");
			// e.printStackTrace();
			System.exit(1);
		} catch (NotBoundException e) {
			System.err.println(e.getMessage());
			System.err.println("\nNao pode obter uma referencia remota.");
			System.err.println("Tente novamente mais tarde!");
			// e.printStackTrace();
			System.exit(1);
		}
	}

	private void setSlaveRemoteObject(String ip) {
		try {

			String nameSlave = "ClientDFS";
			int portSlave = 2012;

			System.setProperty("java.security.policy", "java.policy");
			// System.setSecurityManager(new RMISecurityManager());

			Registry r = LocateRegistry.getRegistry(ip, portSlave);
			slave = (DfsAPI) r.lookup(nameSlave);

		} catch (RemoteException e) {
			System.err.println(e.getMessage());
			System.err
					.println("\nNao pode se conectar ao servidor de arquivos.");
			System.err.println("Verifique se o cabo de rede esta conectado!");
			// e.printStackTrace();
		} catch (NotBoundException e) {
			System.err.println(e.getMessage());
			System.err
					.println("\nNao pode obter uma referencia remota do servidor de arquivos.");
			System.err.println("Tente novamente mais tarde!");
			// e.printStackTrace();
		}
	}

	@Override
	public void create(String fileID) throws RemoteException {
		String slaveIP = master.create(fileID);
		setSlaveRemoteObject(slaveIP);
		slave.create(fileID);
	}

	@Override
	public void createDir(String fileID) throws RemoteException {
		if (!fileID.endsWith("/")) {
			System.out.println("Os nomes de diretorio devem terminar com '/'");
			return;
		}
		List<String> slaves = master.createDir(fileID);
		for (int i = 0; i < slaves.size(); i++) {
			setSlaveRemoteObject(slaves.get(i));
			slave.createDir(fileID);
		}
	}

	@Override
	public List<String> ls(String fileID) throws RemoteException {
		if (fileID.endsWith("/")) {
			return master.lsDir(fileID);
		} else {
			String slaveIP = master.ls(fileID);
			setSlaveRemoteObject(slaveIP);
			return slave.ls(fileID);
		}
	}

	@Override
	public void remove(String fileID) throws RemoteException {
		String slaveIP = master.remove(fileID);
		setSlaveRemoteObject(slaveIP);
		slave.remove(fileID);
	}

	public void copy(String source, String dest) throws RemoteException {
		// puts a '/' character at end of destination if needed
		if (!dest.endsWith("/")) {
			dest += "/" + getName(source);
		} else {
			dest += getName(source);
		}

		// remote to remote
		if (source.startsWith("sd:") && dest.startsWith("sd:")) {

			try {

				// warn the master about the copy
				master.copy(source, dest);

				long ini = System.currentTimeMillis();
				long fim = 0;

				// first read from the slave with the source file
				String srcSlaveIP = master.getLocation(source);
				setSlaveRemoteObject(srcSlaveIP);
				int fileSz = slave.getLength(source);
				byte[] buffer = new byte[fileSz];
				if (slave.read(source, buffer, 0, fileSz) != 0) {

					fim = System.currentTimeMillis();
					System.out.println("Tempo de leitura remota: "
							+ (fim - ini) + " ms");

					ini = System.currentTimeMillis();

					// then write at the slave that hold the destination
					// directory
					String dstSlaveIP = master.getLocation(dest);
					setSlaveRemoteObject(dstSlaveIP);
					if (slave.write(dest, buffer, 0, fileSz) == 0) {
						System.out.println("Nao pode escrever no destino.");
					}
					fim = System.currentTimeMillis();
					System.out.println("Tempo de escrita remota: "
							+ (fim - ini) + " ms");
				} else {
					System.out.println("Nao pode ler o arquivo fonte.");
				}

			} catch (RemoteException e) {
				System.err.println(e.getMessage());
				System.err
						.println("\nNao pode se conectar ao servidor de arquivos.");
				System.err
						.println("Verifique se o cabo de rede esta conectado!");
				// e.printStackTrace();
			}

			// remote to local
		} else if (source.startsWith("sd:") && !dest.startsWith("sd:")) {

			try {

				long ini = System.currentTimeMillis();
				long fim;

				// first read from the slave with the source file
				String srcSlaveIP = master.getLocation(source);
				setSlaveRemoteObject(srcSlaveIP);
				int fileSz = slave.getLength(source);
				byte[] buffer = new byte[fileSz];

				if (slave.read(source, buffer, 0, fileSz) != 0) {

					fim = System.currentTimeMillis();
					System.out.println("Tempo de leitura remota: "
							+ (fim - ini) + " ms");

					ini = System.currentTimeMillis();

					if (write(dest + "/" + getName(source), buffer, 0, fileSz) == 0) {
						System.out.println("Nao pode escrever no destino.");
					}
					System.out.println("Tempo de escrita local: " + (fim - ini)
							+ " ms");
				} else {
					System.out.println("Nao pode ler o arquivo fonte.");
				}

			} catch (RemoteException e) {
				System.err.println(e.getMessage());
				System.err
						.println("\nNao pode se conectar ao servidor de arquivos.");
				System.err
						.println("Verifique se o cabo de rede esta conectado!");
				// e.printStackTrace();
			}

			// local to remote
		} else if (!source.startsWith("sd:") && dest.startsWith("sd:")) {

			try {

				// warn the master about the copy
				master.copy(source, dest);

				int fileSz = getLength(source);
				byte[] buffer = new byte[fileSz];

				long ini = System.currentTimeMillis();
				long fim = 0;

				if (read(source, buffer, 0, fileSz) != 0) {

					fim = System.currentTimeMillis();
					System.out.println("Tempo de leitura local: " + (fim - ini)
							+ " ms");

					ini = System.currentTimeMillis();

					// then write at the slave that hold the destination
					// directory
					String dstSlaveIP = master.getLocation(dest);
					setSlaveRemoteObject(dstSlaveIP);
					if (slave.write(dest, buffer, 0, fileSz) == 0) {
						System.out.println("Nao pode escrever no destino.");
					}
					fim = System.currentTimeMillis();
					System.out.println("Tempo de escrita remota: "
							+ (fim - ini) + " ms");

				} else {
					System.out.println("Nao pode ler o arquivo fonte.");
				}

			} catch (RemoteException e) {
				System.err.println(e.getMessage());
				System.err
						.println("\nNao pode se conectar ao servidor de arquivos.");
				System.err
						.println("Verifique se o cabo de rede esta conectado!");
				// e.printStackTrace();
			}

			// local to local
		} else if (!source.startsWith("sd:") && !dest.startsWith("sd:")) {

			int fileSz = getLength(source);
			byte[] buffer = new byte[fileSz];

			if (read(source, buffer, 0, fileSz) != 0) {
				if (write(dest + "/" + getName(source), buffer, 0, fileSz) == 0) {
					System.out.println("Nao pode escrever no destino.");
				}
			} else {
				System.out.println("Nao pode ler o arquivo fonte.");
			}

		}
	}

	@Override
	public void mov(String source, String dest) throws RemoteException {
		// puts a '/' character at end of destination if needed
		if (!dest.endsWith("/")) {
			dest += "/" + getName(source);
		} else {
			dest += getName(source);
		}

		// remote to remote
		if (source.startsWith("sd:") && dest.startsWith("sd:")) {

			try {

				// warn the master about the mov
				master.mov(source, dest);

				// first read from the slave with the source file
				String srcSlaveIP = master.getLocation(source);
				setSlaveRemoteObject(srcSlaveIP);
				int fileSz = slave.getLength(source);
				byte[] buffer = new byte[fileSz];

				if (slave.read(source, buffer, 0, fileSz) != 0) {
					// then write at the slave that hold the destination
					// directory
					String dstSlaveIP = master.getLocation(dest);
					setSlaveRemoteObject(dstSlaveIP);
					if (slave.write(dest, buffer, 0, fileSz) == 0) {
						System.out.println("Nao pode escrever no destino.");
					} else {
						// now excludes the file at the source
						setSlaveRemoteObject(srcSlaveIP);
						slave.remove(source);
					}

				} else {
					System.out.println("Nao pode ler o arquivo fonte.");
				}

			} catch (RemoteException e) {
				System.err.println(e.getMessage());
				System.err
						.println("\nNao pode se conectar ao servidor de arquivos.");
				System.err
						.println("Verifique se o cabo de rede esta conectado!");
				// e.printStackTrace();
			}

			// remote to local
		} else if (source.startsWith("sd:") && !dest.startsWith("sd:")) {

			try {

				// first read from the slave with the source file
				String srcSlaveIP = master.getLocation(source);
				setSlaveRemoteObject(srcSlaveIP);
				int fileSz = slave.getLength(source);
				byte[] buffer = new byte[fileSz];

				if (slave.read(source, buffer, 0, fileSz) != 0) {
					if (write(dest + "/" + getName(source), buffer, 0, fileSz) == 0) {
						System.out.println("Nao pode escrever no destino.");
					} else {
						// now excludes the file at the source
						setSlaveRemoteObject(srcSlaveIP);
						slave.remove(source);
					}
				} else {
					System.out.println("Nao pode ler o arquivo fonte.");
				}

			} catch (RemoteException e) {
				System.err.println(e.getMessage());
				System.err
						.println("\nNao pode se conectar ao servidor de arquivos.");
				System.err
						.println("Verifique se o cabo de rede esta conectado!");
				// e.printStackTrace();
			}

			// local to remote
		} else if (!source.startsWith("sd:") && dest.startsWith("sd:")) {

			try {

				// warn the master about the mov
				master.mov(source, dest);

				int fileSz = getLength(source);
				byte[] buffer = new byte[fileSz];

				if (read(source, buffer, 0, fileSz) != 0) {
					// then write at the slave that hold the destination
					// directory
					String dstSlaveIP = master.getLocation(dest);
					setSlaveRemoteObject(dstSlaveIP);
					if (slave.write(dest, buffer, 0, fileSz) == 0) {
						System.out.println("Nao pode escrever no destino.");
					} else {
						// now excludes the file at the source
						remove(source);
					}
				} else {
					System.out.println("Nao pode ler o arquivo fonte.");
				}

			} catch (RemoteException e) {
				System.err.println(e.getMessage());
				System.err
						.println("\nNao pode se conectar ao servidor de arquivos.");
				System.err
						.println("Verifique se o cabo de rede esta conectado!");
				// e.printStackTrace();
			}

			// local to local
		} else if (!source.startsWith("sd:") && !dest.startsWith("sd:")) {

			int fileSz = getLength(source);
			byte[] buffer = new byte[fileSz];

			if (read(source, buffer, 0, fileSz) != 0) {
				if (write(dest + "/" + getName(source), buffer, 0, fileSz) == 0) {
					System.out.println("Nao pode escrever no destino.");
				} else {
					// now excludes the file at the source
					remove(source);
				}

			} else {
				System.out.println("Nao pode ler o arquivo fonte.");
			}
		}
	}

	@Override
	public int write(String fileID, byte[] buffer, int offset, int length)
			throws RemoteException {
		try {

			File file = new File(fileID);
			if (!file.exists()) {
				if (file.createNewFile()) {

				}
			}

			OutputStream out = new FileOutputStream(fileID);
			out.write(buffer, offset, length);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return 0;
		} catch (IOException e) {
			e.printStackTrace();
			return 0;
		}

		return length - offset;
	}

	@Override
	public int read(String fileID, byte[] buffer, int offset, int length)
			throws RemoteException {

		try {

			InputStream in = new FileInputStream(fileID);
			in.read(buffer, offset, length);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return length - offset;
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
