package dev.espi.ProtectionStones.commands;

import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

public class ArgSetparent implements PSCommandArg {
    @Override
    public List<String> getNames() {
        return Collections.singletonList("setparent");
    }

    @Override
    public boolean allowNonPlayersToExecute() {
        return false;
    }

    @Override
    public boolean executeArgument(CommandSender s, String[] args) {
        return false;
    }
}