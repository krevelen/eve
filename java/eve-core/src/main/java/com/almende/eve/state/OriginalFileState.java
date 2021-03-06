package com.almende.eve.state;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.channels.FileLock;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @class FileState
 * 
 *        A state for an Eve Agent, which stores the data on disk.
 *        Data is stored in the path provided by the configuration file.
 * 
 *        The state provides general information for the agent (about itself,
 *        the environment, and the system configuration), and the agent can
 *        store its
 *        state in the state.
 *        The state extends a standard Java Map.
 * 
 *        Usage:<br>
 *        AgentHost factory = AgentHost.getInstance(config);<br>
 *        State state = new State("agentId");<br>
 *        state.put("key", "value");<br>
 *        System.out.println(state.get("key")); // "value"<br>
 * 
 * @author jos
 */
// TODO: create an in memory cache and reduce the number of reads/writes
public class OriginalFileState extends FileState {
	private static final Logger			LOG			= Logger.getLogger(OriginalFileState.class
															.getCanonicalName());
	private String						filename	= null;
	private Map<String, Serializable>	properties	= Collections
															.synchronizedMap(new HashMap<String, Serializable>());
	
	protected OriginalFileState() {
	}
	
	public OriginalFileState(String agentId, String filename) {
		super(agentId);
		this.filename = filename;
	}
	
	/**
	 * write properties to disk
	 * 
	 * @return success True if successfully written
	 * @throws IOException
	 */
	private boolean write() {
		try {
			FileOutputStream fos = new FileOutputStream(filename);
			// block until lock is acquired.
			FileLock fl = fos.getChannel().lock();
			if (fl != null) {
				ObjectOutput out = new ObjectOutputStream(fos);
				out.writeObject(properties);
				fl.release();
				out.close();
			} else {
				LOG.log(Level.WARNING,"Warning, couldn't get file lock for writing!");
			}
			fos.close();
			return (fl != null);
			
		} catch (IOException e) {
			LOG.log(Level.WARNING, "", e);
		}
		return false;
	}
	
	/**
	 * read properties from disk
	 * 
	 * @return success True if successfully read
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	private boolean read() {
		try {
			File file = new File(filename);
			if (file.length() > 0) {
				FileInputStream fis = new FileInputStream(filename);
				ObjectInput in = new ObjectInputStream(fis);
				properties.clear();
				properties.putAll((Map<String, Serializable>) in.readObject());
				in.close();
				fis.close();
				return true;
			}
		} catch (FileNotFoundException e) {
			// FIXME! Comment can't be right! no need to give an error, we
			// suppose this is a new agent
			LOG.log(Level.WARNING, "", e);
		} catch (IOException e) {
			LOG.log(Level.WARNING, "", e);
		} catch (ClassNotFoundException e) {
			LOG.log(Level.WARNING, "", e);
		}
		return false;
	}
	
	/**
	 * init is executed once before the agent method is invoked
	 */
	@Override
	public void init() {
	}
	
	/**
	 * destroy is executed once after the agent method is invoked
	 * if the properties are changed, they will be saved
	 */
	@Override
	public void destroy() {
	}
	
	@Override
	public void clear() {
		synchronized (properties) {
			read();
			properties.clear();
			write();
		}
	}
	
	@Override
	public Set<String> keySet() {
		synchronized (properties) {
			read();
			return properties.keySet();
		}
	}
	
	@Override
	public boolean containsKey(Object key) {
		synchronized (properties) {
			read();
			return properties.containsKey(key);
		}
	}
	
	@Override
	public boolean containsValue(Object value) {
		synchronized (properties) {
			read();
			return properties.containsValue(value);
		}
	}
	
	@Override
	public Set<java.util.Map.Entry<String, Serializable>> entrySet() {
		synchronized (properties) {
			read();
			return properties.entrySet();
		}
	}
	
	@Override
	public Serializable get(Object key) {
		synchronized (properties) {
			read();
			return properties.get(key);
		}
	}
	
	@Override
	public boolean isEmpty() {
		synchronized (properties) {
			read();
			return properties.isEmpty();
		}
	}
	
	@Override
	public Serializable put(String key, Serializable value) {
		synchronized (properties) {
			read();
			Serializable ret = properties.put(key, value);
			write();
			return ret;
		}
	}
	
	@Override
	public void putAll(Map<? extends String, ? extends Serializable> map) {
		synchronized (properties) {
			read();
			properties.putAll(map);
			write();
		}
	}
	
	@Override
	public boolean putIfUnchanged(String key, Serializable newVal,
			Serializable oldVal) {
		synchronized (properties) {
			boolean result = false;
			read();
			if ((oldVal == null && properties.containsKey(key))
					|| properties.get(key).equals(oldVal)) {
				properties.put(key, newVal);
				write();
				result = true;
			}
			return result;
		}
	}
	
	@Override
	public Serializable remove(Object key) {
		synchronized (properties) {
			read();
			Serializable value = properties.remove(key);
			write();
			return value;
		}
	}
	
	@Override
	public int size() {
		synchronized (properties) {
			read();
			return properties.size();
		}
	}
	
	@Override
	public Collection<Serializable> values() {
		synchronized (properties) {
			read();
			return properties.values();
		}
	}
	
}