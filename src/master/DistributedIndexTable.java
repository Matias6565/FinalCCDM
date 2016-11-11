package master;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class DistributedIndexTable {

	// File -> IP
	private HashMap<String, String> table;
	
	// Dir -> files at dir
	private HashMap<String, Set<String>> dirTree;
	
	// list of slaves address
	private List<String> slaves;

	public DistributedIndexTable() {
		this.table = new HashMap<String, String>();
		this.dirTree = new HashMap<String, Set<String>>();
		this.slaves = new LinkedList<String>();
	}
	
	public void updateDirTree(String path) {
		if (path.compareTo("sd:/") == 0) {
			Set<String> v = dirTree.get(path);
			if (v == null) {
				v = new TreeSet<String>();
				dirTree.put(path, v);
			}
		} else if (path.endsWith("/")) {
			path = path.substring(0, path.length() - 1);
			String name = path.substring(path.lastIndexOf("/") + 1);
			String dir = path.substring(0, path.indexOf(name));

			Set<String> v = dirTree.get(dir);
			if (v == null) {
				v = new TreeSet<String>();
				v.add(name + "/");
				dirTree.put(dir, v);
			} else {
				v.add(name + "/");
			}

			Set<String> w = new TreeSet<String>();
			dirTree.put(path + "/", w);

		} else {
			String name = path.substring(path.lastIndexOf("/") + 1);
			String dir = path.substring(0, path.indexOf(name));

			Set<String> v = dirTree.get(dir);
			if (v == null) {
				v = new TreeSet<String>();
				v.add(name);
				dirTree.put(dir, v);
			} else {
				v.add(name);
			}
		}
	}

	public void addSlaveEntry(String path, String slaveIP) {
		table.put(path, slaveIP);
		slaves.add(slaveIP);
		updateDirTree(path);
	}

	public void removeSlaveEntry(String path) {
		if (path.endsWith("/")) {
			Set<String> s = dirTree.get(path);
			if (s.isEmpty()) {
				dirTree.remove(path);
				table.remove(path);
			}
		} else {
			String name = path.substring(path.lastIndexOf("/") + 1);
			String dir = path.substring(0, path.indexOf(name));

			Set<String> s = dirTree.get(dir);

			if (s != null) {
				s.remove(name);
				table.remove(path);
			}
		}
	}

	public String getLocation(String path) {
		return table.get(path);
	}

	public List<String> list(String dir) {
		Set<String> files = dirTree.get(dir);
		List<String> res = new LinkedList<String>();
		Iterator<String> i = files.iterator();
		while (i.hasNext()) {
			res.add(i.next());
		}
		return res;
	}

	public List<String> getSlaves() {
		return slaves;
	}

}
