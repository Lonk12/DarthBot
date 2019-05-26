package me.darth.darthbot.moderation;

import java.awt.Color;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ExecutionException;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.exceptions.HierarchyException;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class Kick extends ListenerAdapter {

	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
		if (e.getGuild().getId().equals("393499439739961366")) {
			return;
		}
		String[] args = e.getMessage().getContentRaw().split(" ");
		if (args[0].equalsIgnoreCase("!kick")) {
			Member target = null;
			if (!e.getMessage().getMentionedMembers().isEmpty()) {
				target = e.getMessage().getMentionedMembers().get(0);
			} else {
				me.darth.darthbot.main.Main.findUser(args[1]);
			}
			if (target == null) {
				e.getChannel().sendMessage("User not found!").queue();
				return;
			}
			try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/DarthBot", "root", "a8fc6c25d5c155c39f26f61def5376b0")) {
			      ResultSet rs = con.createStatement().executeQuery("SELECT * FROM GuildInfo WHERE GuildID = "+e.getGuild().getIdLong());
			      while (rs.next())
			      {
			        long ModRoleID = rs.getLong("Moderator");
			        if (ModRoleID == 0L) {
			        	e.getChannel().sendMessage("You must setup the staff role before using the moderation system!").queue();
			        	return;
			        }
			        if (!e.getMember().getRoles().contains(e.getGuild().getRoleById(ModRoleID))) {
			        	MessageEmbed eb = new EmbedBuilder().setDescription("⛔ "+e.getMember().getAsMention()+", you must have the "+e.getGuild().getRoleById(ModRoleID).getAsMention()
			        			+ " role to use that command!").setColor(Color.red).build();
			        	e.getChannel().sendMessage(eb).queue();
			        	return;
			        }
					if (target.getRoles().contains(e.getGuild().getRoleById(ModRoleID))) {
						MessageEmbed eb = new EmbedBuilder().setDescription("⛔ "+e.getMember().getAsMention()+", you cannot kick users with the "+e.getGuild().getRoleById(ModRoleID).getAsMention()
			        			+ " role!").setColor(Color.red).build();
			        	e.getChannel().sendMessage(eb).queue();
			        	return;
					}
			      }
			      
			   
			      String reason = e.getMessage().getContentRaw().replace(args[0], "").replace(args[1], "");
			      if (reason.replace(" ", "").isEmpty()) {
			    	  reason = "No Reason Provided";
			      }
			      final String finalreason = reason;
			      
			     target.getUser().openPrivateChannel().queue((channel) ->
			        {
			        	EmbedBuilder eb = new EmbedBuilder().setAuthor("You have been kicked!", null, e.getGuild().getIconUrl()).setDescription("You have been "
			        		+ "kicked from **"+e.getGuild().getName()+"** by **"+e.getMember().getUser().getName()+"#"+e.getMember().getUser().getDiscriminator()
			        		+ "** because `"+finalreason+"`").setThumbnail(e.getGuild().getIconUrl()).setColor(Color.red);
						try {
							channel.sendMessage(eb.build()).embed(eb.build()).submit().get();
						} catch (InterruptedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (ExecutionException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
			
			        });
			      try {
			    	  e.getGuild().getController().kick(target, "[By "+e.getAuthor().getName()+"#"+e.getAuthor().getDiscriminator()+"] "+reason).reason("[By "+e.getAuthor().getName()+"#"+e.getAuthor().getDiscriminator()+"] "+reason).queue();
			        } catch (HierarchyException e1) {
			        	e.getChannel().sendMessage(":no_entry: I can't punish that user!").queue();
			        }
			    	 MessageEmbed eb = new EmbedBuilder().setDescription("⛔ "+e.getMember().getAsMention()+", you have kicked **"+target.getUser().getName()
		  						+"#"+target.getUser().getDiscriminator()+"** due to `"+reason+"`").setColor(Color.green).build();
	  				con.prepareStatement("INSERT INTO ModHistory (Timestamp, GuildID, PunishedID, PunisherID, Type, Reason)  values "
				      		+ "("+System.currentTimeMillis()+", "+e.getGuild().getIdLong()+", "+target.getUser().getIdLong()+", "+e.getAuthor().getIdLong()
				      		+ ", 'KICK', '"+reason+"')").execute();
		
		          	e.getChannel().sendMessage(eb).queue();
		          	
			      rs.close();
			      con.close();
			} catch (SQLException e1) {
			   e1.printStackTrace();
			}
			
		}
		
	}

}
