
package jdz.gcBoosters.commands.member;

import java.util.Arrays;
import java.util.List;

import org.bukkit.plugin.java.JavaPlugin;

import jdz.bukkitUtils.commands.CommandExecutor;
import jdz.bukkitUtils.commands.SubCommand;
import jdz.bukkitUtils.commands.annotations.CommandExecutorAlias;
import jdz.bukkitUtils.commands.annotations.CommandExecutorPlayerOnly;

@CommandExecutorAlias("boosters")
@CommandExecutorPlayerOnly
public class BoosterCommandExecutor extends CommandExecutor {

	private final List<SubCommand> subCommands = Arrays.asList(new BoosterStartCommand(), new BoosterStopCommand(),
			new BoosterListCommand(), new BoosterQueueCommand());

	public BoosterCommandExecutor(JavaPlugin plugin) {
		super(plugin, "booster", true);
		setDefaultCommand(new BoosterDefaultCommand());
		getHelpCommand().addExtraCommand(BoosterTipCommand.getInstance());
		getHelpCommand().addExtraCommand(BoosterTipAllCommand.getInstance());
	}

	@Override
	protected List<SubCommand> getSubCommands() {
		return subCommands;
	}

}
