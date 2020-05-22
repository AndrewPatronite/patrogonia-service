package com.patronite.service.configuration;

import com.patronite.service.message.BattleMessenger;
import com.patronite.service.message.PlayerMessenger;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
	private final Environment environment;
	private final PlayerMessenger playerMessenger;
	private final BattleMessenger battleMessenger;

	public WebSocketConfig(Environment environment, PlayerMessenger playerMessenger, BattleMessenger battleMessenger) {
		this.environment = environment;
		this.playerMessenger = playerMessenger;
		this.battleMessenger = battleMessenger;
	}

	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		String[] allowedOrigins = environment.getProperty("patrogonia-service.crossorigin", String[].class);
		registry.addHandler(playerMessenger, "/players")
				.setAllowedOrigins(allowedOrigins);
		registry.addHandler(battleMessenger, "/battles")
				.setAllowedOrigins(allowedOrigins);
	}
}
