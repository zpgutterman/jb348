package com.redhat.training.hasingleton;

import java.text.SimpleDateFormat;
import java.util.Date;
import javax.ejb.Schedule;
import javax.ejb.Singleton;


@Singleton
public class Timer {

	// Log the current date on this server to console every 5 seconds. Note that persistent flag must be false - the HA Singleton Activation service triggers the deployment.
    @Schedule(second = "*/5", minute = "*", hour = "*", persistent = false)
    public void logDate() {
        Date currentTime = new Date();
        String serverName = System.getProperty("jboss.server.name");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd '--' HH:mm:ss z");
        System.out.println("Current date on the server " + serverName + " is " + simpleDateFormat.format(currentTime));
    }

}
