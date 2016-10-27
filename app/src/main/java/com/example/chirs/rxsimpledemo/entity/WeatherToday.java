package com.example.chirs.rxsimpledemo.entity;

public class WeatherToday {
	private String date;
	private String highTemp;
	private String lowTemp;
	private String wind;    
	private String humidity;
	private boolean isFind;//是否是晴天， 简单点， 不是晴天就是雨天
	private int a;
	private int b;
	
	public WeatherToday() {}
	
	public WeatherToday(String date, String highTemp, String lowTemp, String wind, String humidity, boolean isFind, int a) {
		this.date = date;
		this.highTemp = highTemp;
		this.lowTemp = lowTemp;
		this.wind = wind;
		this.humidity = humidity;
		this.isFind = isFind;
		this.a = a;
	}
	
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getHighTemp() {
		return highTemp;
	}
	public void setHighTemp(String highTemp) {
		this.highTemp = highTemp;
	}
	public String getLowTemp() {
		return lowTemp;
	}
	public void setLowTemp(String lowTemp) {
		this.lowTemp = lowTemp;
	}
	public String getWind() {
		return wind;
	}
	public void setWind(String wind) {
		this.wind = wind;
	}
	public String getHumidity() {
		return humidity;
	}
	public void setHumidity(String humidity) {
		this.humidity = humidity;
	}
	public boolean isFind() {
		return isFind;
	}
	public void setFind(boolean isFind) {
		this.isFind = isFind;
	}

	public int getA() {
		return a;
	}

	public void setA(int a) {
		this.a = a;
	}

	public int getB() {
		return b;
	}

	public void setB(int b) {
		this.b = b;
	}

	@Override
	public String toString() {
		return "WeatherToday{" +
				"date='" + date + '\'' +
				", highTemp='" + highTemp + '\'' +
				", lowTemp='" + lowTemp + '\'' +
				", wind='" + wind + '\'' +
				", humidity='" + humidity + '\'' +
				", isFind=" + isFind +
				", a=" + a +
				", b=" + b +
				'}';
	}
}
