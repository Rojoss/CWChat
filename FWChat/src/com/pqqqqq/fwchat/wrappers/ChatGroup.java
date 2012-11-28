package com.pqqqqq.fwchat.wrappers;

public class ChatGroup extends Object {
	private String	groupName;
	private String	prefix;
	private String	permissionNode;
	private String	listNode;
	private String	listName;
	private int		priority;
	private int		chatRadius;
	private boolean	displayDefault;
	private boolean	ignoreClick;

	public ChatGroup(String groupName) {
		this.groupName = groupName;
	}

	public ChatGroup(String groupName, String prefix, String permissionNode, String listNode, int priority, int chatRadius, boolean displayDefault,
			boolean ignoreClick, String listName) {
		this.groupName = groupName;
		this.prefix = prefix;
		this.permissionNode = permissionNode;
		this.listNode = listNode;
		this.priority = priority;
		this.chatRadius = chatRadius;
		this.displayDefault = displayDefault;
		this.ignoreClick = ignoreClick;
		this.listName = listName;
	}

	public String getGroupName() {
		return groupName;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getPermissionNode() {
		return permissionNode;
	}

	public void setPermissionNode(String permissionNode) {
		this.permissionNode = permissionNode;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public int getChatRadius() {
		return chatRadius;
	}

	public void setChatRadius(int chatRadius) {
		this.chatRadius = chatRadius;
	}

	public String getListNode() {
		return listNode;
	}

	public void setListNode(String listNode) {
		this.listNode = listNode;
	}

	public boolean isDisplayDefault() {
		return displayDefault;
	}

	public void setDisplayDefault(boolean displayDefault) {
		this.displayDefault = displayDefault;
	}

	public String getListName() {
		return listName;
	}

	public void setListName(String listName) {
		this.listName = listName;
	}

	public boolean isIgnoreClick() {
		return ignoreClick;
	}

	public void setIgnoreClick(boolean ignoreClick) {
		this.ignoreClick = ignoreClick;
	}
}
