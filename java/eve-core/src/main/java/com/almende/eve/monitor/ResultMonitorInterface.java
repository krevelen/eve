package com.almende.eve.monitor;

import java.io.IOException;
import java.net.ProtocolException;
import java.net.URI;
import java.util.List;

import com.almende.eve.rpc.annotation.Access;
import com.almende.eve.rpc.annotation.AccessType;
import com.almende.eve.rpc.annotation.Name;
import com.almende.eve.rpc.annotation.Required;
import com.almende.eve.rpc.annotation.Sender;
import com.almende.eve.rpc.jsonrpc.JSONRPCException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.node.ObjectNode;

// Necessary for describe();
@Access(AccessType.PUBLIC)
public interface ResultMonitorInterface {
	/**
	 * Callback method for monitoring framework, doing the work for
	 * requester-side polling
	 * part of a connection.
	 * 
	 * @param monitorId
	 * @throws JSONRPCException
	 * @throws IOException 
	 * @throws Exception
	 */
	void doPoll(@Name("monitorId") String monitorId) throws JSONRPCException, IOException;
	
	/**
	 * Callback method for monitoring framework, doing the work for pushing data
	 * back to the requester.
	 * 
	 * @param pushParams
	 * @throws JSONRPCException
	 * @throws ProtocolException
	 * @throws Exception
	 */
	void doPush(@Name("pushParams") ObjectNode pushParams,
			@Required(false) @Name("triggerParams") ObjectNode triggerParams)
			throws ProtocolException, JSONRPCException;
	
	/**
	 * Callback method for the monitoring framework, doing the work for
	 * receiving pushed data in the requester.
	 * 
	 * @param result
	 * @param monitorId
	 */
	void callbackPush(@Name("result") Object result,
			@Name("monitorId") String monitorId,
			@Name("callbackParams") ObjectNode callbackParams);
	
	/**
	 * Register a Push request as part of the monitoring framework. The sender
	 * in this case is the requesting agent, the receiver has the requested RPC
	 * method.
	 * 
	 * @param pushParams
	 * @param senderUrl
	 * @return
	 */
	List<String> registerPush(@Name("params") ObjectNode pushParams,
			@Sender String senderUrl);
	
	/**
	 * Unregister a Push request, part of the monitoring framework.
	 * 
	 * @param id
	 */
	void unregisterPush(@Name("pushId") String id);
	
	/**
	 * Sets up a monitored RPC call subscription. Conveniency method, which can
	 * also be expressed as:
	 * new ResultMonitor(getId(), url,method,params).add(ResultMonitorConfigType
	 * config).add(ResultMonitorConfigType config).store();
	 * 
	 * @param url
	 * @param method
	 * @param params
	 * @param callbackMethod
	 * @param confs
	 * @return
	 */
	@Access(AccessType.UNAVAILABLE)
	String create(URI url, String method, ObjectNode params,
			String callbackMethod, ResultMonitorConfigType... confs);
	
	/**
	 * Cancels a running monitor subscription.
	 * 
	 * @param monitorId
	 */
	@Access(AccessType.UNAVAILABLE)
	void cancel(String monitorId);
	
	/**
	 * Gets an actual return value of this monitor subscription. If a cache is
	 * available,
	 * this will return the cached value if the maxAge filter allows this.
	 * Otherwise it will run the actual RPC call (similar to "send");
	 * 
	 * @param monitorId
	 * @param filterParms
	 * @param returnType
	 * @return
	 * @throws JSONRPCException
	 * @throws IOException
	 * @throws Exception
	 */
	@Access(AccessType.UNAVAILABLE)
	<T> T getResult(String monitorId, ObjectNode filterParms,
			JavaType returnType) throws IOException, JSONRPCException;
	
	/**
	 * Gets an actual return value of this monitor subscription. If a cache is
	 * available,
	 * this will return the cached value if the maxAge filter allows this.
	 * Otherwise it will run the actual RPC call (similar to "send");
	 * 
	 * @param monitorId
	 * @param filterParms
	 * @param returnType
	 * @return
	 * @throws JSONRPCException
	 * @throws IOException
	 * @throws Exception
	 */
	@Access(AccessType.UNAVAILABLE)
	<T> T getResult(String monitorId, ObjectNode filterParms,
			Class<T> returnType) throws IOException, JSONRPCException;
}
