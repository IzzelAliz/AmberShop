package io.izzel.ambershop.cmd;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.izzel.ambershop.conf.AmberLocale;
import io.izzel.ambershop.data.ShopDataSource;
import io.izzel.ambershop.util.AmberTasks;
import io.izzel.ambershop.util.Blocks;
import lombok.val;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.annotation.NonnullByDefault;

@Singleton
public class SetUnlimitedExecutor implements CommandExecutor {

    @Inject private AmberLocale locale;
    @Inject private AmberTasks tasks;
    @Inject private ShopDataSource ds;

    @NonnullByDefault
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) {
        val ul = args.<Boolean>getOne(Text.of("unlimited")).get();
        if (src instanceof Player) {
            val player = ((Player) src);
            val opt = Blocks.playerOnCursor(player).flatMap(ds::getByLocation);
            if (opt.isPresent()) {
                val rec = opt.get();
                rec.setUnlimited(ul);
                tasks.async().submit(() -> {
                    val result = ds.updateRecord(rec).get();
                    player.sendMessage(result.reason());
                    return null;
                });
            } else player.sendMessage(locale.getText("commands.unlimited.fail.no-shop"));
        } else src.sendMessage(locale.getText("commands.unlimited.fail.player-only"));
        return CommandResult.success();
    }
}
