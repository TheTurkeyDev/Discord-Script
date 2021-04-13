package dev.theturkey.discordscript;

import dev.theturkey.discordscript.program.DefaultScope;
import dev.theturkey.discordscript.program.OutputWrapper;
import dev.theturkey.discordscript.program.Program;
import dev.theturkey.discordscript.tokenizer.Tokenizer;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import reactor.core.publisher.Mono;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.OutputStream;

public class DiscordScriptCode
{
	public static void main(String[] args)
	{
		Config.load();

		GatewayDiscordClient client = DiscordClientBuilder.create(Config.discordBotToken)
				.build()
				.login()
				.block();

		client.getEventDispatcher().on(ReadyEvent.class).subscribe(event ->
		{
			User self = event.getSelf();
			System.out.println(String.format("Logged in as %s#%s", self.getUsername(), self.getDiscriminator()));
		});

		client.getEventDispatcher().on(MessageCreateEvent.class)
				.map(MessageCreateEvent::getMessage)
				.filter(message -> message.getAuthor().map(user -> !user.isBot()).orElse(false))
				.flatMap(DiscordScriptCode::runProgram)
				.subscribe();
		client.onDisconnect().block();
	}

	private static final StringBuilder returnStr = new StringBuilder();

	public static Mono<Message> runProgram(Message message)
	{
		if(!message.getAuthor().isPresent())
			return message.getChannel().flatMap(channel -> channel.createMessage("Error, missing author???"));


		long start = System.currentTimeMillis();
		returnStr.setLength(0);

		File f = new File("./res/temp/" + message.getAuthor().get().getUsername().hashCode() + ".txt");
		try
		{
			BufferedWriter writer = new BufferedWriter(new FileWriter(f));
			writer.write(message.getContent());

			writer.close();
			OutputWrapper wrapper = new OutputWrapper(new OutputStream()
			{
				@Override
				public void write(int b)
				{
					returnStr.append((char) b);
				}
			});
			Tokenizer tokenizer = new Tokenizer(f);
			TokenStream stream = tokenizer.genTokenStream(wrapper);

			Program program = new Program(stream);
			if(!stream.hasErrored())
			{
				program.execute(new DefaultScope(wrapper));
				long runTime = System.currentTimeMillis() - start;
				returnStr.append("Program Complete! Ran in ").append(runTime).append("ms");
			}
		} catch(Exception e)
		{
			e.printStackTrace();
			returnStr.append("**Failed to run the program! Something dun messed up... ").append(e.getMessage()).append("**");
		}
		return message.getChannel().flatMap(channel -> channel.createMessage(returnStr.toString()));
	}
}
