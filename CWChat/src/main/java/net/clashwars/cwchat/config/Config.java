package net.clashwars.cwchat.config;

public abstract class Config {

    public abstract void init();

    public abstract void load();

    public void save() {
        return;
    }
}
