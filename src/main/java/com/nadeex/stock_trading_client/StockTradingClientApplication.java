package com.nadeex.stock_trading_client;

import com.nadeex.stock_trading_client.service.StockClientService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class StockTradingClientApplication  implements CommandLineRunner {
	private final StockClientService stockClientService;
	public StockTradingClientApplication(StockClientService stockClientService) {
		this.stockClientService = stockClientService;
	}
	public static void main(String[] args) {
		SpringApplication.run(StockTradingClientApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		/*
		com.nadeex.grpc.StockResponse response = this.stockClientService.getStockPrice("AMZN");
		System.out.println("Stock Price: " + response.getPrice());
		System.out.println("Stock Symbol: " + response.getStockSymbol());
		*/

		// Subscribe to stock price updates
		//this.stockClientService.subscribeStockPrice("AMZN");

		// Place bulk order
		// this.stockClientService.placeBulkOrder();

		// Start the live trading
		this.stockClientService.startLiveTrading();

	}

}
