package me.darth.darthbot.main;

import java.awt.Color;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.Instant;
import java.time.ZonedDateTime;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;

public class Leaderboards {

	public static void GlobalLeaderboard() {
		Guild g = me.darth.darthbot.main.Main.jda.getGuildById("568849490425937940");
		EmbedBuilder eb = new EmbedBuilder().setAuthor("Global Leaderboard", null, g.getIconUrl()).setColor(Color.blue).setThumbnail(g.getIconUrl())
				.setTimestamp(Instant.from(ZonedDateTime.now())).setFooter("Last Updated", null);
		try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/DarthBot", "root", "a8fc6c25d5c155c39f26f61def5376b0")) {
		      ResultSet rs = con.createStatement().executeQuery("SELECT * FROM profiles ORDER BY `DBux` DESC LIMIT 15");
		      int counter = 1;
		      while (counter <= 15 && rs.next()) {
		    	  String dbux = new DecimalFormat("#,###").format(rs.getLong("DBux"));
		    	  String m = null;
		    	  if (g.getMemberById(rs.getLong("UserID")) == null) {
		    		  m=rs.getString("Name");
		    	  } else {
		    		  m=g.getMemberById(rs.getLong("UserID")).getAsMention();
		    	  }
		    	  if (counter == 1) {
		    		  eb.addField("🥇 1st Place 🥇", "👑**"+m+"** with `$"+dbux+"`", false);
		    	  } else if (counter == 2) {
		    		  eb.addField("🥈 2nd Place 🥈", "**"+m+"** with `$"+dbux+"`", false);
		    	  } else if (counter == 3) {
		    		  eb.addField("🥉 3rd Place 🥉", "**"+m+"** with `$"+dbux+"`", false);
		    	  } else {
		    		  eb.addField(counter+"th Place", "**"+m+"** with `$"+dbux+"`", false);
		    	  }
		    	  counter++;
		      }
		      rs.close();
		      con.close();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		me.darth.darthbot.main.Main.jda.getGuildById("568849490425937940").getTextChannelById("584150264509104139")
			.getMessageById("584150350899052558").complete().editMessage(eb.build()).queue();
		
	}
	
}
