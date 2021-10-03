package sample.device;

import java.time.LocalDate;
import java.time.LocalTime;

public class EcoDeviceUtil {

	public String getTime(){
		StringBuilder buffer = new StringBuilder();
		LocalDate date = LocalDate.now();
		LocalTime time = LocalTime.now();
		
		buffer.append(date.getYear()).append("-");
		buffer.append(date.getMonthValue()).append("-");
		buffer.append(date.getDayOfMonth()).append(" ");
		
		buffer.append(time.getHour()).append(":");
		buffer.append(time.getMinute()).append(":");
		buffer.append(time.getSecond());
		
		return buffer.toString();
	}
}
