package com.almende.eve.transport.http;

import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletRegistration;

import com.almende.eve.agent.AgentHost;
import com.almende.eve.config.Config;

public class AgentListener implements ServletContextListener {
	private static final Logger LOG = Logger.getLogger(AgentListener.class
			.getSimpleName());
	private static ServletContext c;
	
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		c = sce.getServletContext();
		init(c);
	}

	protected static String getParam(String param){
		return getParam(param,null);
	}
	protected static String getParam(String param, String defaultVal){
		String result = c.getInitParameter(param);
		if (result == null) {
			for (Entry<String, ? extends ServletRegistration> ent : c.getServletRegistrations().entrySet()){
				result = ent.getValue().getInitParameter(param);
				if (result != null){
					LOG.warning("Init param '"+param+"' should be migrated to <context-param>'");
					break;
				}
			}
		}
		if (result == null && defaultVal != null) {
			
			result = defaultVal;
			LOG.warning("Init parameter '"+param+"' missing in servlet configuration web.xml. "
					+ "Trying default value '" + defaultVal + "'.");
		}
		return result;
	}
	
	public static void init(ServletContext ctx) {
		
		if (ctx != null) {
			c=ctx;
			
			String filename = getParam("config","eve.yaml");
			
			String fullname = "/WEB-INF/" + filename;

			LOG.info("loading configuration file '"
					+ c.getRealPath(fullname) + "'...");

			Config config = new Config(c.getResourceAsStream(fullname));
			try {
				AgentHost.getInstance().loadConfig(config);
			} catch (Exception e) {
				LOG.log(Level.WARNING,"",e);
			}
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
	}

}
