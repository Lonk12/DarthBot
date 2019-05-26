package me.darth.darthbot.commands;

import java.awt.Color;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Category;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class ServerSetup extends ListenerAdapter {

	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
		
		String[] args = e.getMessage().getContentRaw().split(" ");
		if (args[0].equalsIgnoreCase("!setup")) {
			try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/DarthBot", "root", "a8fc6c25d5c155c39f26f61def5376b0")) {
				EmbedBuilder eb = new EmbedBuilder().setAuthor("Server Management", null, e.getGuild().getIconUrl()).setColor(Color.blue);
				eb.setFooter("Use | !setup <Feature> 0 | to disable a feature!", null);
				if (args.length < 3) {
					eb.addField("Server Setup", "`!setup JoinLog <Channel>` Setup the channel for welcome/leave messages to be logged\n"
							+ "`!setup ActionLog <Channel>` Setup the action log channel to log all notable events on the server\n"
							+ "`!setup StaffRole <Role>` Setup the role that can use Punishment Commands (Ban, Mute, Kick etc)\n"
							+ "`!setup PublicRooms <Category>` Setup the Category that will have automatic public rooms", true);
					e.getChannel().sendMessage(eb.build()).queue();
					return;
				}
				if (args[1].equalsIgnoreCase("JoinLog")) {
					TextChannel channel = null;
					if (!args[2].equals("0")) {
						if (!e.getMessage().getMentionedChannels().isEmpty()) {
							channel = e.getMessage().getMentionedChannels().get(0);
						} else if (!e.getGuild().getTextChannelsByName(e.getMessage().getContentRaw().replace(args[0]+" "+args[1]+" ", ""), true).isEmpty()) {
							channel = e.getGuild().getTextChannelsByName(e.getMessage().getContentRaw().replace(args[0]+" "+args[1]+" ", ""), true).get(0);
						} else {
							try {
								channel = e.getGuild().getTextChannelById(e.getMessage().getContentRaw().replace(args[0]+" "+args[1]+" ", ""));
							} catch (NumberFormatException e1) {channel=null;}
						}
						if (channel == null) {
							e.getChannel().sendMessage(":no_entry: Couldn't find channel!").queue();
							return;
						}
						con.prepareStatement("UPDATE GuildInfo SET WelcomeChannel = "+channel.getIdLong()+" WHERE GuildID = "+e.getGuild().getIdLong()).execute();
						eb.setDescription(":white_check_mark: "+channel.getAsMention()+" has been set as the Join/Leave Message Log Channel!");
						e.getChannel().sendMessage(eb.build()).queue();
						return;
					}
					con.prepareStatement("UPDATE GuildInfo SET WelcomeChannel = 0 WHERE GuildID = "+e.getGuild().getIdLong()).execute();
					eb.setDescription("Disabled Welcome/Leave messages!");
					e.getChannel().sendMessage(eb.build()).queue();
				}
				if (args[1].equalsIgnoreCase("ActionLog")) {
					TextChannel channel = null;
					if (!args[2].equals("0")) {
						if (!e.getMessage().getMentionedChannels().isEmpty()) {
							channel = e.getMessage().getMentionedChannels().get(0);
						} else if (!e.getGuild().getTextChannelsByName(e.getMessage().getContentRaw().replace(args[0]+" "+args[1]+" ", ""), true).isEmpty()) {
							channel = e.getGuild().getTextChannelsByName(e.getMessage().getContentRaw().replace(args[0]+" "+args[1]+" ", ""), true).get(0);
						} else {
							try {
								channel = e.getGuild().getTextChannelById(e.getMessage().getContentRaw().replace(args[0]+" "+args[1]+" ", ""));
							} catch (NumberFormatException e1) {channel=null;}
						}
						if (channel == null) {
							e.getChannel().sendMessage(":no_entry: Couldn't find channel!").queue();
							return;
						}
						con.prepareStatement("UPDATE GuildInfo SET LogChannel = "+channel.getIdLong()+" WHERE GuildID = "+e.getGuild().getIdLong()).execute();
						eb.setDescription(":white_check_mark: "+channel.getAsMention()+" has been set as the Action Log Channel!");
						e.getChannel().sendMessage(eb.build()).queue();
						return;
					}
					con.prepareStatement("UPDATE GuildInfo SET LogChannel = 0 WHERE GuildID = "+e.getGuild().getIdLong()).execute();
					eb.setDescription("Disabled Action Log!");
					e.getChannel().sendMessage(eb.build()).queue();
				}
				if (args[1].equalsIgnoreCase("StaffRole")) {
					Role role = null;
					if (!args[2].equals("0")) {
						if (!e.getMessage().getMentionedRoles().isEmpty()) {
							role = e.getMessage().getMentionedRoles().get(0);
						} else if (!e.getGuild().getRolesByName(e.getMessage().getContentRaw().replace(args[0]+" "+args[1]+" ", ""), true).isEmpty()) {
							role = e.getGuild().getRolesByName(e.getMessage().getContentRaw().replace(args[0]+" "+args[1]+" ", ""), true).get(0);
						} else {
							try {
								role = e.getGuild().getRoleById(e.getMessage().getContentRaw().replace(args[0]+" "+args[1]+" ", ""));
							} catch (NumberFormatException e1) {role=null;}
						}
						if (role == null) {
							e.getChannel().sendMessage(":no_entry: Couldn't find role!").queue();
							return;
						}
						con.prepareStatement("UPDATE GuildInfo SET Moderator = "+role.getIdLong()+" WHERE GuildID = "+e.getGuild().getIdLong()).execute();
						eb.setDescription(":white_check_mark: "+role.getAsMention()+" has been set as the Staff Role, and enabled Moderation!");
						e.getChannel().sendMessage(eb.build()).queue();
						return;
					}
					con.prepareStatement("UPDATE GuildInfo SET Moderator = 0 WHERE GuildID = "+e.getGuild().getIdLong()).execute();
					eb.setDescription("Disabled the Moderation System!");
					e.getChannel().sendMessage(eb.build()).queue();
				}
				if (args[1].equalsIgnoreCase("PublicRooms")) {
					Category cat = null;
					if (!args[2].equals("0")) {
						if (!e.getGuild().getCategoriesByName(e.getMessage().getContentRaw().replace(args[0]+" "+args[1]+" ", ""), true).isEmpty()) {
							cat = e.getGuild().getCategoriesByName(e.getMessage().getContentRaw().replace(args[0]+" "+args[1]+" ", ""), true).get(0);
						} else {
							try {
								cat = e.getGuild().getCategoryById(e.getMessage().getContentRaw().replace(args[0]+" "+args[1]+" ", ""));
							} catch (NumberFormatException e1) {cat=null;}
						}
						if (cat == null) {
							e.getChannel().sendMessage(":no_entry: Couldn't find category!").queue();
							return;
						}
						cat.createVoiceChannel("Voice #1").queue();
						con.prepareStatement("UPDATE GuildInfo SET PublicCategory = "+cat.getIdLong()+" WHERE GuildID = "+e.getGuild().getIdLong()).execute();
						eb.setDescription(":white_check_mark: "+cat.getName()+" has been set as the Public Room Category!");
						e.getChannel().sendMessage(eb.build()).queue();
					}
					con.prepareStatement("UPDATE GuildInfo SET PublicCategory = 0 WHERE GuildID = "+e.getGuild().getIdLong()).execute();
					eb.setDescription("Disabled Public Rooms!");
					e.getChannel().sendMessage(eb.build()).queue();
				}
			} catch (SQLException e1) {
				
			}
		}
		
	}

}
