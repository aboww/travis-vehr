/*
 * Copyright (c) 2015 Christian Chevalley
 * This file is part of Project Ethercis
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
This code is a refactoring and adaptation of the original
work provided by the XmlBlaster project (see http://xmlblaster.org)
for more details.
This code is therefore supplied under LGPL 2.1
 */

/**
 * Project: EtherCIS system application
 * 
 * @author <a href="mailto:christian@adoc.co.th">Christian Chevalley</a>
 * @author <a href="mailto:michele@laghi.eu">Michele Laghi</a>
 * @author <a href="mailto:xmlblast@marcelruff.info">Marcel Ruff</a>
 */


package com.ethercis.servicemanager.cluster;

import com.ethercis.servicemanager.common.ReplaceVariable;
import com.ethercis.servicemanager.common.StringPairTokenizer;
import com.ethercis.servicemanager.common.def.Constants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * This represents one node in the administrative hierarchy, and is a linked
 * list to its parent and its childs.
 * 
 */
public final class ContextNode {
	private static Logger log = LogManager.getLogger(ContextNode.class);
	/** Name for logging response */
	public final static String SEP = "/";
	public final static String ROOT_MARKER_TAG = SEP + "ehrserver";
	/** Use to mark a cluster node name */
	public final static String CLUSTER_MARKER_TAG = "node";
	/** Use to mark a client */
	public final static String SUBJECT_MARKER_TAG = "client";
	/** Use to mark a contrib */
	public final static String CONTRIB_MARKER_TAG = "contrib";
	/** Use to mark an admin */
	public final static String ADMIN_MARKER_TAG = "admin";
	/** Use to mark a login session */
	public final static String SESSION_MARKER_TAG = "session";
	/** Use for client side ehrserverAccess */
	public final static String CONNECTION_MARKER_TAG = "connection";
	/** Use to mark a queue */
	public final static String QUEUE_MARKER_TAG = "queue";
	/** Use to mark a service */
	public final static String SERVICE_MARKER_TAG = "service";
	/** Use to mark a plugin inside the RunlevelManager (services.xml) */
	public final static String RUNLEVEL_SERVICE_MARKER_TAG = "service";
	/** Use to mark a protocol plugin like XMLRPC or email */
	public final static String PROTOCOL_MARKER_TAG = "protocol";
	/** Use to mark a message store map */
	public final static String MAP_MARKER_TAG = "map";
	/** Use to mark a topic */
	public final static String TOPIC_MARKER_TAG = "topic";
	/** Use to mark a subscription */
	public final static String SUBSCRIPTION_MARKER_TAG = "subscription";
	/** Use to mark event methods */
	public final static String EVENT_MARKER_TAG = "event";
	/**
	 * Use to mark a system and configuration properties and command line
	 * arguments
	 */
	public final static String SYSPROP_MARKER_TAG = "sysprop";
	/** Use to mark logging settings */
	public final static String LOGGING_MARKER_TAG = "logging";
	/** Use to mark a remote cluster node configuration */
	public final static String CLUSTERCONF_MARKER_TAG = "nodeconf";
	/** Use to mark a remote cluster node configuration */
	public final static String CLUSTERMASTER_MARKER_TAG = "master";

	/** For XPath conforming query */
	public final static String SCHEMA_XPATH = "xpath";
	/** For JMX conforming ObjectName string */
	public final static String SCHEMA_JMX = "jmx";
	/** For JMX conforming ObjectName string, "com.ethercis:" is our JMX domain */
	public final static String SCHEMA_JMX_DOMAIN = "com.ethercis";
	/** For URL conforming string */
	public final static String SCHEMA_URL = "url";

	private String className; // e.g. "node"
	private String instanceName; // e.g. "heron"
	private ContextNode parent;
	private Map childs; // key=absoluteName, value contains 'new
						// WeakReference(ContextNode)'

	public static char QUOTE = '\"';

	// Placeholder for top level node
	public final static ContextNode ROOT_NODE = null; // new ContextNode(null,
														// "/ehrserver", "",
														// (ContextNode)null);

	/**
	 * @deprecated Use constructor without global
	 */
	public ContextNode(RunTimeSingleton glob, String className,
			String instanceName, ContextNode parent) {
		this(className, instanceName, parent);
	}

	/**
	 * @param className
	 *            The tag name like 'node' (ContextNode.CLUSTER_MARKER_TAG) or
	 *            'client' (ContextNode.SUBJECT_MARKER_TAG)
	 * @param instanceName
	 *            The instance like 'heron' or 'joe', can be null
	 * @param parent
	 *            The parent node or null if root ContextNode ('node/heron' etc)
	 */
	public ContextNode(String className, String instanceName, ContextNode parent) {
		if (className == null) {
			throw new IllegalArgumentException(
					"ContextNode: Missing className argument");
		}
		this.className = className;
		setInstanceName(instanceName);
		this.parent = parent;
		if (this.parent != null)
			this.parent.addChild(this);
	}

	/*
	 * public Global getGlobal() { return this.glob; }
	 */

	/**
	 * @return The tag name like 'node' (ContextNode.CLUSTER_MARKER_TAG) or
	 *         'client' (ContextNode.SUBJECT_MARKER_TAG)
	 */
	public String getClassName() {
		return this.className;
	}

	/**
	 * @param className
	 *            The tag name like 'node' (ContextNode.CLUSTER_MARKER_TAG) or
	 *            'client' (ContextNode.SUBJECT_MARKER_TAG)
	 */
	public boolean isOfClass(String className) {
		if (this.className == null)
			return false;
		return this.className.equals(className);
	}

	public void setInstanceName(String instanceName) {
		/**
		 * TODO: Needs some testing //Escape offending '/' in the name with
		 * quotes "joe/the/great" if (instanceName != null &&
		 * instanceName.indexOf("/") != -1 && instanceName.charAt(0) != QUOTE) {
		 * instanceName = QUOTE + instanceName + QUOTE; }
		 * 
		 * //For the time being we suppress '/' in JmxWrapper.validateJmxValue()
		 */
		this.instanceName = instanceName;
	}

	/**
	 * @return Can be null
	 */
	public String getInstanceName() {
		return this.instanceName;
	}

	/**
	 * @return Is never null
	 */
	public String getInstanceNameNotNull() {
		return (this.instanceName == null) ? "" : this.instanceName;
	}

	/**
	 * @return The parent node or null
	 */
	public ContextNode getParent() {
		return this.parent;
	}

	/**
	 * Walk up the hierarchy and return the matching ContextNode.
	 * 
	 * @param className
	 *            The context node to retrieve
	 * @return The parent node or null
	 */
	public ContextNode getParent(String className) {
		if (className == null) {
			return null;
		}
		if (className.equals(this.getClassName())) {
			return this;
		}
		if (this.parent != null) {
			return this.parent.getParent(className);
		}
		return null;
	}

	/**
	 * Walk up the hierarchy until we find the given className and rename the
	 * instance name.
	 * 
	 * For example rename
	 * "com.ethercis:nodeClass=node,node=clientSUB1,clientClass=connection,connection=jack"
	 * to
	 * "com.ethercis:nodeClass=node,node=heron,clientClass=connection,connection=jack"
	 * 
	 * Checks the current node logonservice well.
	 * 
	 * @param className
	 *            The class to change, e.g. "node"
	 * @param instanceName
	 *            The new name to use, e.g. "heron"
	 */
	public void changeParentName(String className, String instanceName) {
		if (className == null) {
			return;
		}
		ContextNode found = getParent(className);
		if (found != null) {
			found.setInstanceName(instanceName);
		}
	}

	/**
	 * Walk up the hierarchy until we find the given className and rename the
	 * instance name.
	 * 
	 * For example rename
	 * "com.ethercis:nodeClass=node,node=clientSUB1,clientClass=connection,connection=jack"
	 * to
	 * "com.ethercis:nodeClass=node,node=heron,clientClass=connection,connection=jack"
	 * 
	 * Checks the current node logonservice well.
	 * 
	 * @param newParentNode
	 *            The new parent name to use, e.g. "heron"
	 */
	public void changeParentName(final ContextNode newParentNode) {
		if (newParentNode == null) {
			return;
		}
		ContextNode found = getParent(newParentNode.getClassName());
		if (found != null) {
			found.setInstanceName(newParentNode.getInstanceName());
		}
	}

	/**
	 * Add the given child, it exists already nothing happens. The child is hold
	 * logonservice a weak reference, so you don't need to cleanup.
	 * 
	 * @param child
	 *            The child to add
	 * @return true if the child was added, the parent of your child is
	 *         modified! false if it existed already or if you given child is
	 *         null
	 */
	public boolean addChild(ContextNode child) {
		return addChild(child, false);
	}

	/**
	 * Add the given child, it exists already nothing happens. The child is hold
	 * logonservice a weak reference, so you don't need to cleanup.
	 * 
	 * @param child
	 *            The child to add
	 * @param doClone
	 *            If true the given child is not modified if false the given
	 *            child is changed to have us logonservice a new parent
	 * @return true if the child was added, false if it existed already or if
	 *         you given child is null
	 */
	public boolean addChild(ContextNode child, boolean doClone) {
		if (child == null)
			return false;
		if (this.childs != null
				&& this.childs.get(child.getAbsoluteName()) != null) {
			return false; // Child is already here
		}
		synchronized (this) {
			if (this.childs == null) {
				this.childs = new HashMap();
			}
			if (doClone) {
				ContextNode clone = child.getClone();
				clone.parent = this;
				this.childs.put(clone.getAbsoluteName(), new WeakReference(
						clone));
			} else {
				if (child.parent != null && this != child.parent) {
					// If child had another parent already remove it
					child.parent.removeChild(child);
				}
				child.parent = this;
				this.childs.put(child.getAbsoluteName(), new WeakReference(
						child));
			}
		}
		return true;
	}

	public ContextNode getClone() {
		return ContextNode.valueOf(getAbsoluteName());
	}

	/**
	 * Merges the given child, it exists already nothing happens.
	 * <p />
	 * 
	 * <pre>
	 * this = "/node/heron/client/joe"
	 * child = "/node/xyz/client/joe/session/1"
	 * results to "/node/heron/client/joe/session/1"
	 * </pre>
	 * 
	 * <pre>
	 * this = "/node/heron/client/joe/session/1"
	 * child = "/node/xyz/service/Pop3Driver"
	 * results to "/ehrserver/node/heron/client/joe/session/1/service/Pop3Driver"
	 * </pre>
	 * 
	 * @param child
	 *            The child to add, it is not touched logonservice we take a clone
	 * @return The new leave or null if failed
	 */
	public ContextNode mergeChildTree(final ContextNode child) {
		if (child == null)
			return null;
		ContextNode childClone = child.getClone();
		ContextNode childParent = childClone;
		while (true) {
			ContextNode thisParent = this.getParent(childParent.getClassName());
			if (thisParent != null) {
				// System.out.println("thisParent=" +
				// thisParent.getAbsoluteName() + " - childParent=" +
				// childParent.getAbsoluteName());
				if (thisParent.getInstanceNameNotNull().equals(
						childParent.getInstanceNameNotNull())) {
					ContextNode childs[] = childParent.getChildren();
					for (int i = 0; i < childs.length; i++) {
						thisParent.addChild(childs[i]); // suppresses duplicates
					}
					return thisParent.getChild(child.getClassName(),
							child.getInstanceName());
				}
				ContextNode pp = thisParent.getParent();
				if (pp != null) {
					// Found a node to merge, attach it here
					pp.addChild(childParent);
					return thisParent.getChild(child.getClassName(),
							child.getInstanceName());
				}
				break; // nothing found to merge with
			}
			childParent = childParent.getParent();
			if (childParent == null)
				break;
		}
		// Append if not merged:
		this.addChild(childClone);
		return getChild(child.getClassName(), child.getInstanceName());
	}

	/**
	 * @return All children, never null (but empty array)
	 */
	public synchronized ContextNode[] getChildren() {
		if (this.childs == null || this.childs.size() == 0) {
			return new ContextNode[0];
		}
		WeakReference[] refs = (WeakReference[]) this.childs.values().toArray(
				new WeakReference[this.childs.size()]);
		ArrayList list = new ArrayList(refs.length);
		for (int i = 0; i < refs.length; i++) {
			Object referent = refs[i].get();
			if (referent != null) {
				list.add(referent);
			} else {
				this.childs.remove(refs[i]); // Cleanup the obsolete
												// WeakReference
				// TODO: ReferenceQueue
			}
		}
		return (ContextNode[]) list.toArray(new ContextNode[list.size()]);
	}

	/**
	 * Remove a child.
	 * 
	 * @param child
	 * @return true if the child was removed
	 */
	public synchronized boolean removeChild(ContextNode child) {
		if (this.childs == null || this.childs.size() == 0 || child == null) {
			return false;
		}
		Object removed = this.childs.remove(child.getAbsoluteName());
		return (removed != null);
	}

	/**
	 * Search down the children tree for the given className and instanceName.
	 * Only the first match is returned
	 * 
	 * @param className
	 *            If null only a given instanceName is searched
	 * @param instanceName
	 *            If null only the given className is searched
	 * @return The child, is null if not found
	 */
	public ContextNode getChild(String className, String instanceName) {
		return getChild(this, className, instanceName);
	}

	/**
	 * Search down the children tree for the given className. Only the first
	 * match is returned
	 * 
	 * @param className
	 *            If null only a given instanceName is searched
	 * @return The child, is null if not found
	 */
	public ContextNode getChild(String className) {
		return getChild(this, className, null);
	}

	/**
	 * Recursive search
	 */
	protected ContextNode getChild(ContextNode node, String className,
			String instanceName) {
		if (className == null && instanceName == null)
			return null;
		if (className == null
				&& node.getInstanceNameNotNull().equals(instanceName))
			return node;
		if (instanceName == null && node.getClassName().equals(className))
			return node;
		if (node.getClassName().equals(className)
				&& node.getInstanceNameNotNull().equals(instanceName))
			return node;
		ContextNode[] childsArr = node.getChildren();
		for (int i = 0; i < childsArr.length; i++) {
			return getChild(childsArr[i], className, instanceName);
		}
		return null;
	}

	/**
	 * Access the absolute name in standard notation
	 * 
	 * @return e.g. "/ehrserver/node/heron/client/joe/session/2", never null
	 */
	public String getAbsoluteName() {
		StringBuffer sb = new StringBuffer(256);
		if (this.parent == ROOT_NODE) {
			sb.append(ROOT_MARKER_TAG).append(SEP).append(this.className);
			if (this.instanceName != null) {
				sb.append(SEP).append(this.instanceName);
			}
			return sb.toString();
		}
		sb.append(this.parent.getAbsoluteName()).append(SEP)
				.append(this.className);
		if (this.instanceName != null) {
			sb.append(SEP).append(this.instanceName);
		}
		return sb.toString();
	}

	/**
	 * Access the absolute name but without root tag /ehrserver. <br>
	 * Can be parsed by SessionName
	 * 
	 * @return e.g. "/node/heron/client/joe/session/2", never null
	 */
	public String getSessionNameCompatible() {
		String abs = getAbsoluteName();
		if (abs == null)
			return "";
		abs = ReplaceVariable.replaceFirst(abs, "/connection/", "/client/");
		int index = abs.indexOf("/ehrserver");
		if (index == 0)
			return abs.substring(11);
		return abs;
	}

	private String quote(String value) {
		if (value == null)
			return value;
		try {
			return javax.management.ObjectName.quote(value);
		} catch (Throwable e) {
			return value;
		}
	}

	/**
	 * Access the absolute name in standard notation
	 * 
	 * @param schema
	 *            Currently only "xpath"
	 * @return e.g.
	 *         "xpath:/ehrserver/node[@id='heron']/client[@id='joe']/session[@id='2']"
	 *         , never null
	 */
	public String getAbsoluteName(String schema) {
		StringBuffer sb = new StringBuffer(256);
		if (SCHEMA_JMX.equalsIgnoreCase(schema)) {
			// "com.ethercis:nodeClass=node,node=heron,clientClass=client,client=joe,queueClass=queue,queue=subject665,entryClass=entry,entry=1002"
			// like this jconsole creates a nice tree (see JmxWrapper.java for a
			// discussion)
			if (this.parent == ROOT_NODE) {
				sb.append(SCHEMA_JMX_DOMAIN).append(":")
						.append("nodeClass=node,node=");
				sb.append((this.instanceName == null) ? "null"
						: quote(this.instanceName));
				return sb.toString();
			}
			sb.append(this.parent.getAbsoluteName(schema));
			sb.append(",").append(this.className).append("Class=")
					.append(this.className);
			// JMX ',' make problems with or without quotes
			// ':' is only OK if quoted
			if (this.instanceName != null) {
				sb.append(",").append(this.className).append("=")
						.append(quote(this.instanceName));
			}
			return sb.toString();
		} else if (SCHEMA_XPATH.equalsIgnoreCase(schema)) {
			if (this.parent == ROOT_NODE) {
				sb.append(schema).append(":").append(ROOT_MARKER_TAG)
						.append(SEP).append(this.className);
				if (this.instanceName != null) {
					sb.append("[@id='").append(this.instanceName).append("']");
				}
				return sb.toString();
			}
			sb.append(this.parent.getAbsoluteName(schema)).append(SEP)
					.append(this.className);
			if (this.instanceName != null) {
				sb.append("[@id='").append(this.instanceName).append("']");
			}
			return sb.toString();
		} else /* if (SCHEMA_URL.equalsIgnoreCase(schema)) */{
			return getAbsoluteName();
		}
	}

	/**
	 * @return #getAbsoluteName()
	 */
	public String toString() {
		return getAbsoluteName();
	}

	/**
	 * @return e.g. "client/joe", never null
	 */
	public String getRelativeName() {
		StringBuffer sb = new StringBuffer(256);
		sb.append(this.className);
		if (this.instanceName != null) {
			sb.append(SEP).append(this.instanceName);
		}
		return sb.toString();
	}

	/**
	 * @param schema
	 *            Currently only "xpath"
	 * @return e.g. "xpath:client[@id='joe']", never null
	 */
	public String getRelativeName(String schema) {
		StringBuffer sb = new StringBuffer(256);
		if (SCHEMA_JMX.equalsIgnoreCase(schema)) {
			// "com.ethercis:clientClass=client,client=joe"
			sb.append(SCHEMA_JMX_DOMAIN).append(":");
			sb.append(this.className).append("Class=").append(this.className);
			if (this.instanceName != null) {
				sb.append(",").append(this.className).append("=")
						.append(quote(this.instanceName));
			}
			return sb.toString();
		} else if (SCHEMA_XPATH.equalsIgnoreCase(schema)) {
			sb.append(schema).append(":").append(this.className);
			if (this.instanceName != null) {
				sb.append("[@id='").append(this.instanceName).append("']");
			}
			return sb.toString();
		} else /* if (SCHEMA_URL.equalsIgnoreCase(schema)) */{
			return getRelativeName();
		}
	}

	/**
	 * @return true if relative name equals
	 */
	public boolean equalsRelative(ContextNode contextNode) {
		return getRelativeName().equals(contextNode.getRelativeName());
	}

	/**
	 * Compare the absolute name.
	 * 
	 * @param contextNode
	 *            Returns false if null
	 * @return
	 */
	public boolean equalsAbsolute(ContextNode contextNode) {
		if (contextNode == null)
			return false;
		return getAbsoluteName().equals(contextNode.getAbsoluteName());
	}

	/**
	 * Parse the given string. NOTE: The returned ContextNode is a new instance
	 * and NOT the instance from inside a given Global
	 * 
	 * @param url
	 *            For example "/ehrserver/node/heron/client/joe/session/2"
	 * @return The lowest ContextNode instance, you can navigate upwards with
	 *         getParent() or ContextNode.ROOT_NODE==null.
	 */
	public static ContextNode valueOf(String url) {
		if (url == null || url.length() == 0)
			return ROOT_NODE;
		String lower = url.toLowerCase();
		if (lower.startsWith("xpath")) {
			throw new IllegalArgumentException(
					"ContextNode.valueOf(): Unkown schema in '" + url + "'");
		}
		if (url.startsWith(SCHEMA_JMX_DOMAIN + ":")) { // SCHEMA_JMX
			// com.ethercis:nodeClass=node,node="heron",clientClass=connection,connection="jack",queueClass=queue,queue="connection-99"
			int index = url.indexOf(":");
			String tmp = url.substring(index + 1);
			String[] toks = StringPairTokenizer
					.parseLine(tmp, ',', QUOTE, true);
			ContextNode node = ROOT_NODE;
			for (int i = 0; i < toks.length; i++) {
				index = toks[i].indexOf("=");
				String className = (index > 0) ? toks[i].substring(index + 1)
						: null;
				if (className.startsWith("\""))
					className = className.substring(1, className.length() - 1);
				String instanceName = null;
				if (i < toks.length - 1) {
					index = toks[i + 1].indexOf("=");
					instanceName = (index > 0) ? toks[i + 1]
							.substring(index + 1) : null;
					if (instanceName.startsWith("\""))
						instanceName = instanceName.substring(1,
								instanceName.length() - 1);
					i++;
				}
				node = new ContextNode(className, instanceName, node);
			}
			return node;
		} else if (url.startsWith("/ehrserver/node/")
				|| url.startsWith("/node/") || !url.startsWith("/")/*
																	 * relative
																	 * name
																	 */) {
			String[] toks = StringPairTokenizer
					.parseLine(url, '/', QUOTE, true);
			ContextNode node = ROOT_NODE;
			for (int i = 0; i < toks.length; i++) {
				String className = toks[i];
				String instanceValue = null;
				if (i == 0 && "ehrserver".equals(className)) {
					node = ROOT_NODE;
					continue;
				}
				if (i == toks.length - 1) {
					if (node != null
							&& ContextNode.SUBJECT_MARKER_TAG.equals(node
									.getClassName())) {
						className = ContextNode.SESSION_MARKER_TAG;
						i--; // Hack: We add "session" for "client/joe/1" ->
								// "client/joe/session/1" for backward
								// compatibility
						instanceValue = toks[i + 1];
					} else {
						// For example "/ehrserver/node/heron/logging"
						instanceValue = null;
						log.debug("Unexpected syntax in '"
								+ url
								+ "', missing value for class, we assume a 'null' value.");
					}
				} else {
					instanceValue = toks[i + 1];
				}
				node = new ContextNode(className, instanceValue, node);
				i++;
			}
			return node;
		}

		throw new IllegalArgumentException(
				"ContextNode.valueOf(): unknown formatting schema is not implemented: '"
						+ url + "'");
	}

	/**
	 * Dump state of this object into XML. <br>
	 * 
	 * @return XML dump of ContextNode
	 */
	public final String toXml() {
		return toXml((String) null);
	}

	/**
	 * Dump state of this object into XML. <br>
	 * 
	 * @param extraOffset
	 *            indenting of tags
	 * @return XML dump of ContextNode
	 */
	public final String toXml(String extraOffset) {
		StringBuffer sb = new StringBuffer(256);
		if (extraOffset == null)
			extraOffset = "";
		String offset = Constants.OFFSET + extraOffset;

		sb.append(offset).append("<ContextNode class='").append(this.className)
				.append("' instance='").append(this.instanceName).append("'/>");
		return sb.toString();
	}

}
