package lu.r3flexi0n.bungeeonlinetime;

import java.text.ParseException;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.UUID;
import lu.r3flexi0n.bungeeonlinetime.utils.Language;
import lu.r3flexi0n.bungeeonlinetime.utils.Utils;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class OnlineTimeCommand extends Command {

    public OnlineTimeCommand(String command, String permission, String... aliases) {
        super(command, permission, aliases);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage(Language.onlyPlayer);
            return;
        }
        ProxiedPlayer player = (ProxiedPlayer) sender;

        if (args.length == 0) {

            if (!player.hasPermission("onlinetime.own")) {
                player.sendMessage(Language.noPermission);
                return;
            }

            ProxyServer.getInstance().getScheduler().runAsync(BungeeOnlineTime.instance, () -> {
                try {

                    long seconds = BungeeOnlineTime.sql.getOnlineTime(player.getUniqueId(), 0);
                    int hours = (int) (seconds / 3600);
                    int minutes = (int) ((seconds % 3600) / 60);

                    player.sendMessage(Language.onlineTime
                            .replace("%PLAYER%", player.getName())
                            .replace("%HOURS%", String.valueOf(hours))
                            .replace("%MINUTES%", String.valueOf(minutes)));

                } catch (Exception ex) {
                    player.sendMessage(Language.error);
                    ex.printStackTrace();
                }
            });

        } else if (args.length == 2 && args[0].equals("get")) {

            if (!player.hasPermission("onlinetime.others")) {
                player.sendMessage(Language.noPermission);
                return;
            }

            ProxyServer.getInstance().getScheduler().runAsync(BungeeOnlineTime.instance, () -> {
                try {

                    UUID uuid = Utils.getUUID(args[1]);
                    if (uuid == null) {
                        player.sendMessage(Language.playerNotFound
                                .replace("%PLAYER%", args[1]));
                        return;
                    }

                    long seconds = BungeeOnlineTime.sql.getOnlineTime(uuid, 0);
                    int hours = (int) (seconds / 3600);
                    int minutes = (int) ((seconds % 3600) / 60);

                    player.sendMessage(Language.onlineTime
                            .replace("%PLAYER%", args[1])
                            .replace("%HOURS%", String.valueOf(hours))
                            .replace("%MINUTES%", String.valueOf(minutes)));

                } catch (Exception ex) {
                    player.sendMessage(Language.error);
                    ex.printStackTrace();
                }
            });

        } else if (args.length == 3 && args[0].equals("get")) {

            if (!player.hasPermission("onlinetime.others.since")) {
                player.sendMessage(Language.noPermission);
                return;
            }

            Date date;
            try {
                date = Utils.getDate(args[2]);
            } catch (ParseException ex) {
                player.sendMessage(Language.wrongFormat
                        .replace("%FORMAT%", BungeeOnlineTime.dateFormat));
                return;
            }

            ProxyServer.getInstance().getScheduler().runAsync(BungeeOnlineTime.instance, () -> {
                try {

                    UUID uuid = Utils.getUUID(args[1]);
                    if (uuid == null) {
                        player.sendMessage(Language.playerNotFound
                                .replace("%PLAYER%", args[1]));
                        return;
                    }

                    long seconds = BungeeOnlineTime.sql.getOnlineTime(uuid, date.getTime());
                    int hours = (int) (seconds / 3600);
                    int minutes = (int) ((seconds % 3600) / 60);

                    player.sendMessage(Language.onlineTimeSince
                            .replace("%PLAYER%", args[1])
                            .replace("%DATE%", args[2])
                            .replace("%HOURS%", String.valueOf(hours))
                            .replace("%MINUTES%", String.valueOf(minutes)));

                } catch (Exception ex) {
                    player.sendMessage(Language.error);
                    ex.printStackTrace();
                }
            });

        } else if (args.length == 1 && args[0].equalsIgnoreCase("top")) {

            if (!player.hasPermission("onlinetime.top")) {
                player.sendMessage(Language.noPermission);
                return;
            }

            player.sendMessage(Language.topTimeLoading);

            ProxyServer.getInstance().getScheduler().runAsync(BungeeOnlineTime.instance, () -> {
                try {

                    LinkedHashMap<UUID, Long> top = BungeeOnlineTime.sql.getTopOnlineTimes(10, 0);

                    StringBuilder builder = new StringBuilder();
                    builder.append(Language.topTimeAbove);
                    builder.append("\n");
                    for (Entry<UUID, Long> entries : top.entrySet()) {

                        String name = Utils.getName(entries.getKey());
                        if (name == null) {
                            name = "?";
                        }

                        long seconds = entries.getValue();
                        int hours = (int) (seconds / 3600);
                        int minutes = (int) ((seconds % 3600) / 60);

                        builder.append(Language.topTime
                                .replace("%PLAYER%", name)
                                .replace("%HOURS%", String.valueOf(hours))
                                .replace("%MINUTES%", String.valueOf(minutes)));
                        builder.append("\n");
                    }
                    builder.append(Language.topTimeBelow);

                    player.sendMessage(builder.toString());

                } catch (Exception ex) {
                    player.sendMessage(Language.error);
                    ex.printStackTrace();
                }
            });

        } else if (args.length == 2 && args[0].equalsIgnoreCase("top")) {

            if (!player.hasPermission("onlinetime.top.since")) {
                player.sendMessage(Language.noPermission);
                return;
            }

            Date date;
            try {
                date = Utils.getDate(args[1]);
            } catch (ParseException ex) {
                player.sendMessage(Language.wrongFormat
                        .replace("%FORMAT%", BungeeOnlineTime.dateFormat));
                return;
            }

            player.sendMessage(Language.topTimeLoading);

            ProxyServer.getInstance().getScheduler().runAsync(BungeeOnlineTime.instance, () -> {
                try {

                    LinkedHashMap<UUID, Long> top = BungeeOnlineTime.sql.getTopOnlineTimes(10, date.getTime());

                    StringBuilder builder = new StringBuilder();
                    builder.append(Language.topTimeSinceAbove.replace("%DATE%", args[1]));
                    builder.append("\n");
                    for (Entry<UUID, Long> entries : top.entrySet()) {

                        String name = Utils.getName(entries.getKey());
                        if (name == null) {
                            name = "?";
                        }

                        long seconds = entries.getValue();
                        int hours = (int) (seconds / 3600);
                        int minutes = (int) ((seconds % 3600) / 60);

                        builder.append(Language.topTimeSince
                                .replace("%PLAYER%", name)
                                .replace("%HOURS%", String.valueOf(hours))
                                .replace("%MINUTES%", String.valueOf(minutes)));
                        builder.append("\n");
                    }
                    builder.append(Language.topTimeSinceBelow.replace("%DATE%", args[1]));

                    player.sendMessage(builder.toString());

                } catch (Exception ex) {
                    player.sendMessage(Language.error);
                    ex.printStackTrace();
                }
            });

        } else if (args.length == 1 && args[0].equalsIgnoreCase("resetall")) {

            if (!player.hasPermission("onlinetime.resetall")) {
                player.sendMessage(Language.noPermission);
                return;
            }

            ProxyServer.getInstance().getScheduler().runAsync(BungeeOnlineTime.instance, () -> {
                try {

                    BungeeOnlineTime.sql.resetAll(System.currentTimeMillis());
                    player.sendMessage(Language.resetAll);

                } catch (Exception ex) {
                    player.sendMessage(Language.error);
                    ex.printStackTrace();
                }
            });

        } else if (args.length == 2 && args[0].equalsIgnoreCase("resetall")) {

            if (!player.hasPermission("onlinetime.resetall.before")) {
                player.sendMessage(Language.noPermission);
                return;
            }

            Date date;
            try {
                date = Utils.getDate(args[1]);
            } catch (ParseException ex) {
                player.sendMessage(Language.wrongFormat
                        .replace("%FORMAT%", BungeeOnlineTime.dateFormat));
                return;
            }

            ProxyServer.getInstance().getScheduler().runAsync(BungeeOnlineTime.instance, () -> {
                try {

                    BungeeOnlineTime.sql.resetAll(date.getTime());
                    player.sendMessage(Language.resetAllBefore
                            .replace("%DATE%", args[1]));

                } catch (Exception ex) {
                    player.sendMessage(Language.error);
                    ex.printStackTrace();
                }
            });

        } else if (args.length == 2 && args[0].equalsIgnoreCase("reset")) {

            if (!player.hasPermission("onlinetime.reset")) {
                player.sendMessage(Language.noPermission);
                return;
            }

            ProxyServer.getInstance().getScheduler().runAsync(BungeeOnlineTime.instance, () -> {
                try {

                    UUID uuid = Utils.getUUID(args[1]);
                    if (uuid == null) {
                        player.sendMessage(Language.playerNotFound
                                .replace("%PLAYER%", args[1]));
                        return;
                    }

                    BungeeOnlineTime.sql.reset(uuid, System.currentTimeMillis());
                    player.sendMessage(Language.resetPlayer
                            .replace("%PLAYER%", args[1]));

                } catch (Exception ex) {
                    player.sendMessage(Language.error);
                    ex.printStackTrace();
                }
            });

        } else if (args.length == 3 && args[0].equalsIgnoreCase("reset")) {

            if (!player.hasPermission("onlinetime.reset.before")) {
                player.sendMessage(Language.noPermission);
                return;
            }

            Date date;
            try {
                date = Utils.getDate(args[2]);
            } catch (ParseException ex) {
                player.sendMessage(Language.wrongFormat
                        .replace("%FORMAT%", BungeeOnlineTime.dateFormat));
                return;
            }

            ProxyServer.getInstance().getScheduler().runAsync(BungeeOnlineTime.instance, () -> {
                try {

                    UUID uuid = Utils.getUUID(args[1]);
                    if (uuid == null) {
                        player.sendMessage(Language.playerNotFound
                                .replace("%PLAYER%", args[1]));
                        return;
                    }

                    BungeeOnlineTime.sql.reset(uuid, date.getTime());
                    player.sendMessage(Language.resetPlayerBefore
                            .replace("%PLAYER%", args[1])
                            .replace("%DATE%", args[2]));

                } catch (Exception ex) {
                    player.sendMessage(Language.error);
                    ex.printStackTrace();
                }
            });

        } else {
            player.sendMessage("§7Usage:");
            player.sendMessage("§7/onlinetime");
            player.sendMessage("§7/onlinetime get <player> [since]");
            player.sendMessage("§7/onlinetime top [since]");
            player.sendMessage("§7/onlinetime reset <player> [before]");
            player.sendMessage("§7/onlinetime resetall [before]");
        }
    }
}
