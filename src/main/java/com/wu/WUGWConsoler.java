package com.wu;

import static java.time.temporal.ChronoUnit.SECONDS;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.wu.console.AbstractServiceRequest;
import com.wu.console.GenerateMTCNRequest;
import com.wu.console.PricingTableRequest;
import com.wu.gw.util.UtilFunctions;

@Component
public class WUGWConsoler implements CommandLineRunner {

	private String banner = "";
	private WUGWRuntime wugwRuntime;

	public WUGWConsoler(@Autowired WUGWRuntime wugwRuntime) {
		this.wugwRuntime = wugwRuntime;
	}

	@Override
	public void run(String... args) throws Exception {
		
		/*Thread scheduleRunnerThread1 = new Thread(() -> {
			this.scheduleRunner(13, 30, 0);
		});
		scheduleRunnerThread1.start();
		
		Thread scheduleRunnerThread2 = new Thread(() -> {
			this.scheduleRunner(18, 30, 0);
		});
		scheduleRunnerThread2.start();*/
		
		boolean standby = true;

		do {
			System.out.print(banner);

			String sNum = "";
			//while (UtilFunctions.scan.hasNextLine()) {
				sNum = UtilFunctions.scan.nextLine();
				//break;
			//}
			
			AbstractServiceRequest request = wugwRuntime.getRequestMap().get(sNum);

			if (request == null) {
				UtilFunctions.logger.info("\nService is not available\n");
				continue;
			}

			standby = request.serve(wugwRuntime);

		} while (standby);
	}
	
	@PostConstruct
	public void init() {
		StringBuffer bannerBuffer = new StringBuffer();

		bannerBuffer.append("*****************************************************************************************" + "\n");

		Object[] keyArr = wugwRuntime.getRequestMap().keySet().toArray();
		Arrays.sort(keyArr);
		for (Object key : keyArr) {

			AbstractServiceRequest request = (AbstractServiceRequest) wugwRuntime.getRequestMap().get(key);
			bannerBuffer.append("[" + request.getRequestID() + "] " + request.getDescription() + "\n");
		}

		bannerBuffer.append("*****************************************************************************************" + "\n");
		bannerBuffer.append("\nEnter service number: ");

		banner = bannerBuffer.toString();

		// -----------------------------------------
		GenerateMTCNRequest gMTCNRequest = (GenerateMTCNRequest) wugwRuntime.getRequestMap().get("1");
		Thread runnerThread = new Thread(() -> {
			gMTCNRequest.runServiceStandalone(wugwRuntime);
		});
		runnerThread.start();
	}
	
	private void scheduleRunner(int hr, int min, int sec) {
		ScheduledExecutorService sexec = Executors.newScheduledThreadPool(2);
		
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime scheduledDateTime = LocalDateTime
				.of(now.getYear(),now.getMonth(),now.getDayOfMonth(), hr, min, sec);

		long delay = SECONDS.between(now, scheduledDateTime);
		
		sexec.schedule(() -> {
			PricingTableRequest pricingTableRequest = (PricingTableRequest)wugwRuntime.getRequestMap().get("6");
			pricingTableRequest.runServiceStandalone(wugwRuntime);
		}, delay, TimeUnit.SECONDS);
	}
}
