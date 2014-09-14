package net.clashwars.cwchat.wrappers;

public class ChatPrefix extends Object {
	private String	prefixName;
	private String	chatPrefix;
	private String	chatSuffix;
	private String	permissionNode;
	private boolean	owner;

	public ChatPrefix(String prefixName) {
		this.prefixName = prefixName;
	}

	public ChatPrefix(String prefixName, String chatPrefix, String chatSuffix, String permissionNode, boolean owner) {
		this.prefixName = prefixName;
		this.chatPrefix = chatPrefix;
		this.chatSuffix = chatSuffix;
		this.permissionNode = permissionNode;
		this.owner = owner;
	}

	public String getPrefixName() {
		return prefixName;
	}

	public String getPermissionNode() {
		return permissionNode;
	}

	public void setPermissionNode(String permissionNode) {
		this.permissionNode = permissionNode;
	}

	public String getChatPrefix() {
		return chatPrefix;
	}

	public void setChatPrefix(String chatPrefix) {
		this.chatPrefix = chatPrefix;
	}

	public String getChatSuffix() {
		return chatSuffix;
	}

	public void setChatSuffix(String chatSuffix) {
		this.chatSuffix = chatSuffix;
	}

	public boolean isOwner() {
		return owner;
	}

	public void setOwner(boolean owner) {
		this.owner = owner;
	}
}
