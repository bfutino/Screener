package com.screener.Screener;

public class Stock {
	String Ticker;
	String Name;
	String Sector;
	String Industry;
	double Price;
	double TargetPrice;
	double PERatio; // 20 < 25 // None 
	double PEGRatio; // < 1 - undervalued //  1 > overvalued
	double PETrailing;
	double PEForward;
	double DividendPerShare;
	double DividendYield;
	double ProfitMargin;
	double High52;
	double Low52;
	double MA50;
	double MA200;
	String Sentiment; // Work on sentiment score
	
	
	public String toString() {
		return "Ticker: " + Ticker + "\n" +
				"Name: " + Name + "\n" +
				"Sector: " + Sector + "\n" +
				"Industry: " + Industry + "\n" +
				"Price: " + Price + "\n" +
				"TargetPrice: " + TargetPrice + "\n" +
				"PERatio: " + PERatio + "\n" +
				"PEGRatio: " + PEGRatio + "\n" +
				"Trailing PE: " + PETrailing + "\n" +
				"Forward PE: " + PEForward + "\n" +
				"DividendPerShare: " + DividendPerShare + "\n" +
				"DividendYield: " + DividendYield + "\n" +
				"ProfitMargin: " + ProfitMargin + "\n" +
				"52weekHigh: " + High52 + "\n" +
				"52weekLow: " + Low52 + "\n" +
				"50dayMovingAverage: " + MA50 + "\n" +
				"200dayMovingAverage: " + MA200 + "\n"
				; 
	}
	
}
