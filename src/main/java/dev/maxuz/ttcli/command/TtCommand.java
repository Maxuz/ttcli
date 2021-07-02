package dev.maxuz.ttcli.command;

import picocli.CommandLine;
import picocli.CommandLine.Command;

@Command(subcommands = {CommandLine.HelpCommand.class},
    mixinStandardHelpOptions = true)
public class TtCommand {

}