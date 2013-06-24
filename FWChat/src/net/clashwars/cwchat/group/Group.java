package net.clashwars.cwchat.group;

import java.util.ArrayList;

import org.bukkit.entity.Player;

public class Group {
	private final ArrayList<Player>	members	= new ArrayList<Player>();
	private final ArrayList<Player>	admins	= new ArrayList<Player>();
	private Player					owner;
	private String					name;

	public Group(String name, Player owner) {
		this.name = name;
		this.owner = owner;
	}

	public String getName() {
		return name;
	}

	public Player getOwner() {
		return owner;
	}

	public void setOwner(Player owner) {
		this.owner = owner;
	}

	public ArrayList<Player> getMembers() {
		return members;
	}

	public ArrayList<Player> getAdmins() {
		return admins;
	}

	public void promote(Player player) {
		if (!members.contains(player))
			return;

		members.remove(player);
		admins.add(player);
	}

	public void demote(Player player) {
		if (!admins.contains(player))
			return;

		admins.remove(player);
		members.add(player);
	}

	public boolean containsMember(Player member) {
		return owner.equals(member) || members.contains(member) || admins.contains(member);
	}

	public boolean isAdministrative(Player member) {
		return owner.equals(member) || admins.contains(member);
	}

	public void broadcast(String msg) {
		ArrayList<Player> ppl = new ArrayList<Player>();
		ppl.addAll(members);
		ppl.addAll(admins);
		ppl.add(owner);

		for (Player p : ppl) {
			p.sendMessage(msg);
		}
	}

	public void kick(Player player) {
		members.remove(player);
		admins.remove(player);
	}
}
