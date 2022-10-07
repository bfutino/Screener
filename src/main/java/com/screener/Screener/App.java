package com.screener.Screener;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.io.File;
import java.io.FileWriter;
import java.time.LocalDateTime;    
import java.io.BufferedReader;
import java.io.BufferedInputStream;
import java.io.InputStreamReader;
import java.net.URL;
import org.json.JSONObject;
import java.net.MalformedURLException;
import java.net.HttpURLConnection;

public class App {
    // Optimize: Load from file instead
    static final String sp500[] = {};
    static final String nasdaq[] = {"AMD", "ADBE", "ABNB", "ALGN","AMZN", "AMGN", "AEP", "ADI","ANSS", "AAPL", "AMAT",
            "ASML", "TEAM", "ADSK", "ATVI", "ADP", "AZN", "AVGO", "BIDU","BIIB", "BMRN", "BKNG","CDNS","CHTR","CPRT",
            "CRWD", "CTAS", "CSCO", "CMCSA", "COST", "CSX", "CTSH", "DDOG","DOCU","DXCM", "DLTR", "EA", "EBAY","EXC",
            "FAST","META", "FISV", "FTNT","GILD","GOOGL","HON", "ILMN", "INTC", "INTC", "INTU", "ISRG", "MRVL", "IDXX",
            "JD","KDP","KLAC","KHC","LRCX","LCID","LULU","MELI","MAR"};
    static final String test[] = {"AMD" ,"AAPL", "AMZN", "TSLA", "MSFT","NVDA", "NFLX"};
    
	public static void main(String[] args) {
		List<Stock> list = getIndex(test);
		
		toCSV(list, "Report");
	}
	
	
	// -----------------------------   Filter Methods -----------------------------
	
	// Filters based on the percentage of the current price from the 52 week high
	public static List<Stock> filterFromHigh(List<Stock> stocks, double targetPercentage){
        List<Stock> filteredList = new ArrayList<>();
        
        for(Stock current : stocks) {
            double fromHigh = ((current.High52 - current.Price)/ current.High52) * 100;
            if( fromHigh >= targetPercentage){
                filteredList.add(current);
            }
        }
        
        return filteredList;
    }
	
	// Filters based on the percentage of the current price from the 52 week low
    public static List<Stock> filterFromLow(List<Stock> stocks, double targetPercentage){
        List<Stock> filteredList = new ArrayList<>();
        
        for(Stock current : stocks) {
            double fromLow = ((current.Price - current.Low52)/ current.Low52) * 100;
            if(fromLow <= targetPercentage){
                filteredList.add(current);
            }
        }
        
        return filteredList;
    }
	
	// filters stocks based on percentage below 200 moving average
	public static List<Stock> filterMA(List<Stock> stocks, double target200MA){
		List<Stock> filteredList = new ArrayList<>();
		
		for(Stock current : stocks) {
			// Calculates percentage below moving average
			double percent = ((current.Price - current.MA200)/ current.Price) * 100;
			if( percent < target200MA){
			    filteredList.add(current);
			}
		}
		
		return filteredList;
	}
	
	// filters stocks based on percentage below 200 and 50 day moving average
    public static List<Stock> filterMA(List<Stock> stocks, double target200MA, double target50MA){
        List<Stock> filteredList = new ArrayList<>();
        
        for(Stock current : stocks) {
            double percent200MA = ((current.Price - current.MA200)/ current.Price) * 100;
            double percent50MA = ((current.Price - current.MA50)/ current.Price) * 100;
            if( percent200MA < target200MA && percent50MA < target50MA){
                filteredList.add(current);
            }
        }
        
        return filteredList;
    }
    
	// Filters on PEratio, will return stocks that have PEratio under targetPE
	public static List<Stock> filterPE(List<Stock> stocks, double targetPE){
        List<Stock> filteredList = new ArrayList<>();
        
        for(Stock current : stocks) {
            if( current.PERatio < targetPE){
                filteredList.add(current);
            }
        }
        return filteredList;
    }
	
	// Filters on PEratio, will return stocks that have lower trailing PE than target
	public static List<Stock> filterPETrailing(List<Stock> stocks, double targetTrailingPE){
        List<Stock> filteredList = new ArrayList<>();
        
        for(Stock current : stocks) {
            if( current.PETrailing < targetTrailingPE){
                filteredList.add(current);
            }
        }
        return filteredList;
    }
	
	// Filters based on profit margin, will return stocks with profit margin greater or equal than target
    public static List<Stock> filterProfitMargin(List<Stock> stocks, double target){
        List<Stock> filteredList = new ArrayList<>();
        
        for(Stock current : stocks) {
            if( current.ProfitMargin >= target){
                filteredList.add(current);
            }
        }
        return filteredList;
    }
	
		
	// -----------------------------   Retrieval and helper methods  -----------------------------

	// Prints list to CSV file
	public static void toCSV(List<Stock> list, String filename){
	    try{
	        FileWriter writer = new FileWriter(new File(filename + ".csv"));
	        writer.write("Ticker,Name,Sector,Price,TargetPrice,PERatio,PEGRatio,TrailingPE"
	                + ",ForwardPE,DividendPerShare,DividendYield,ProfitMargin,52weekHigh,52weekLow,50ma,200ma\n");
	        
	        for(Stock stock : list){
	            String line = "";
	            line += stock.Ticker + ",";
	            line += stock.Name + ",";
	            line += stock.Sector + ",";
	            line += stock.Price + ",";
	            line += stock.TargetPrice + ",";
	            line += stock.PERatio + ",";
	            line += stock.PEGRatio + ",";
	            line += stock.PETrailing + ",";
	            line += stock.PEForward + ",";
	            line += stock.DividendPerShare + ",";
	            line += stock.DividendYield + ",";
	            line += stock.ProfitMargin + ",";
	            line += stock.High52 + ",";
	            line += stock.Low52 + ",";
	            line += stock.MA50 + ",";
	            line += stock.MA200 + "\n";
	            writer.write(line);
	        }
	        writer.close();
	    }catch(Exception e){
	        e.printStackTrace();
	    }
	}
	
	// takes in a index and returns a list of stocks
    public static List<Stock> getIndex(String[] stockIndex){
        List<Stock> stocksList = new ArrayList<>();
        System.out.println("Loading...");
        System.out.println("Start Time: " + java.time.LocalTime.now());
        System.out.println("Estimated Loading time: " + (Math.ceil((double) stockIndex.length * 2/5)));
        for(int i = 0; i < stockIndex.length;i++) {
            boolean wait = false;
            if(i > 0 && i % 2 == 0){
                wait = true;
            }
            stocksList.add(getStock(stockIndex[i],wait)); 
        }
        System.out.println("Done at: " + java.time.LocalTime.now());
        return stocksList;
    }
    
	// Improve later to handle different search types
	// TIME_SERIES_DAILY 
	// GLOBAL_QUOTE
	// OVERVIEW
	// EARNINGS
	// NEWS_SENTIMENT
	//INFLATION_EXPECTATION
	// TREASURY_YIELD
	// RETAIL_SALES
	// UNEMPLOYMENT
	public static Stock getStock(String ticker, boolean wait){
		String apiKey = "CPFRPP38JAL92UA4";
        String searchType = "OVERVIEW"; 
		String url = "https://www.alphavantage.co/query?function=" + searchType + "&outputsize=full&symbol=" + ticker + "&apikey=" + apiKey;
		JSONObject json = new JSONObject(getJson(url).toString());

		Stock stock = new Stock();
		stock.Ticker = ticker.toUpperCase();
		stock.Name = json.get("Name").toString();
		stock.Sector = json.get("Sector").toString();
		stock.Industry = json.get("Industry").toString();
		stock.TargetPrice = validate(json.get("AnalystTargetPrice").toString());
		stock.PEGRatio = validate(json.get("PEGRatio").toString());
		stock.PEForward = validate(json.get("ForwardPE").toString());
		stock.DividendPerShare = validate(json.get("DividendPerShare").toString());
		stock.DividendYield = validate(json.get("DividendYield").toString());
		stock.ProfitMargin = validate(json.get("ProfitMargin").toString());
		stock.High52 = validate(json.get("52WeekHigh").toString());
		stock.Low52 = validate(json.get("52WeekLow").toString());
		stock.MA50 = validate(json.get("50DayMovingAverage").toString());
		stock.MA200 = validate(json.get("200DayMovingAverage").toString());
		stock.PERatio = validate(json.get("PERatio").toString());
		stock.PETrailing = validate(json.get("TrailingPE").toString());
		
		// timer for API calls limit to reset
		// API only allows for 5 request per minute
		if(wait) {
            try {
                TimeUnit.SECONDS.sleep(61);
            }catch(Exception e) {
               e.printStackTrace();
            }
        }
		json = new JSONObject(getJson("https://www.alphavantage.co/query?function=GLOBAL_QUOTE&outputsize=full&symbol=" + ticker + "&apikey=" + apiKey).toString());
		JSONObject quote = new JSONObject(json.get("Global Quote").toString());
		stock.Price = Double.parseDouble(quote.get("05. price").toString());
        return stock;
	}

	// retrieves JSON object for the given stock in link
	public static StringBuilder getJson(String link){
		StringBuilder builder = new StringBuilder();
		try{
			URL url = new URL(link);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("accept","application/json");

            if (connection.getResponseCode() == 200){
                String line;
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                while ((line = reader.readLine()) != null){
                    builder.append(line);
                }
                connection.disconnect();
			}
		}catch(Exception e){
			e.printStackTrace();
			System.out.println(link);
		}
		return builder;
	}

	// Validates input that might not be double
	// If input is not a value then assign 0
	// Converts to double if valid
	public static double validate(String value){
	    if(value.equals("None") || value.equals("-")) return 0;
	    return Double.parseDouble(value);
	}
}
