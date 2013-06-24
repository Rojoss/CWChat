package net.clashwars.cwchat.group;

import java.util.ArrayList;

import org.bukkit.entity.Player;

public class Groups {
	private final ArrayList<Group>	groups	= new ArrayList<Group>();

	public void addGroup(Group group) {
		groups.add(group);
	}

	public boolean removeGroup(Group group) {
		for (int i = 0; i < groups.size(); i++) {
			Group g = groups.get(i);

			if (g.getName().equals(group.getName())) {
				groups.remove(i);
				return true;
			}
		}
		return false;
	}

	public Group getGroup(String name) {
		for (Group g : groups) {
			if (g.getName().equals(name))
				return g;
		}
		return null;
	}

	public Group getGroup(Player member) {
		for (Group g : groups) {
			if (g.containsMember(member))
				return g;
		}
		return null;
	}
	
	public String getGroupName(Player member) {
		for (Group g : groups) {
			if (g.containsMember(member))
				return g.getName();
		}
		return null;
	}

	public boolean hasGroup(Player member) {
		return getGroup(member) != null;
	}
}
