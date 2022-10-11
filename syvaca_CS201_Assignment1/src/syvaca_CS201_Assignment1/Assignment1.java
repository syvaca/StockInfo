package syvaca_CS201_Assignment1;

import com.google.gson.*;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.InputMismatchException;
import java.util.Scanner;

import javax.swing.event.RowSorterListener;
import org.apache.commons.text.WordUtils;

import java.util.List;
import Helper.Stock;
import Helper.Companies;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class Assignment1 {
	private static ArrayList<Stock> allStocks;
	
	public static boolean isValidDate(String dateStr) {
		//valid format
		try {
			String year = dateStr.substring(0,4);
			int i = Integer.parseInt(year);
			if(i>9999 || i < 1000) {
				return false;
			}
		} catch(Exception e) {
			return false;
		}
		
		try {
			String month = dateStr.substring(5, 7);
			int i = Integer.parseInt(month);
			if(i>12 || i<1) {
				return false;
			}
		} catch(Exception e) {
			return false;
		}
		
		try {
			String day = dateStr.substring(8);
			int i = Integer.parseInt(day);
			if(i>31 || i<1) {
				return false;
			}
		} catch(Exception e) {
			return false;
		}
		
		try {
			String ap1 = dateStr.substring(4,5);
			String ap2 = dateStr.substring(7,8);
			if(!ap1.equals("-") || !ap2.equals("-")) {
				return false;
			}
		} catch(Exception e) {
			return false;
		}
		
		
		SimpleDateFormat sdfrmt = new SimpleDateFormat("YYYY-mm-dd");
	    sdfrmt.setLenient(false);
        try {
        	sdfrmt.parse(dateStr);
        } catch (ParseException e) {
            return false;
        }
        return true;
	
    }
	
	public static void sortList() {
		Collections.sort(allStocks, new Comparator<Stock>(){

	        @Override
	        public int compare(Stock arg0, Stock arg1) {
	            if(arg0.getName().equals(arg1.getName())){
	                return 0;
	            }
	            return arg0.getName().toLowerCase().compareTo(arg1.getName().toLowerCase());  
	        }
	    });
	}
	
	public static void main(String []args) {
		boolean validJason = false;
		String info = "";
		String company_file = "";
		Scanner in = new Scanner(System.in);
		Gson gson = null;
		Companies company = null;
		while(!validJason) {
			System.out.print("What is the name of the company file? ");
			company_file = in.nextLine();
			File file = new File(company_file);
			Scanner scan;
			try {
				info = "";
				scan = new Scanner(file);
				while(scan.hasNext()) {
					info += scan.nextLine();
					validJason = true;
				}
			} catch(FileNotFoundException e) {
				System.out.println("\nThe file " + company_file + " could not be found\n");
				validJason = false;
				continue;
			} 
		
			try {
				gson = new Gson();
			}catch (Exception e) {
				System.out.println("Exception error: " + e);
			}
			
			try {
				company = gson.fromJson(info, Companies.class);
			} catch(JsonSyntaxException e) {
				System.out.println("\nThe file " + company_file + " is not formatted properly (Json Syntax Error)\n");
				validJason = false;
				continue;
			}	
			
			allStocks = company.getData();
			
			// JSON IS NOT FORMATTED PROPERLY
			if(!(allStocks == null)) {
				for(Stock tempStock : allStocks) {
					if(tempStock.getName() == null || tempStock.getName().equals("")) { // if empty name
						validJason = false;
						System.out.println("\nThe file " + company_file + " is missing parameters (name)\n");
						break;
					}
					if(tempStock.getTicker() == null || tempStock.getTicker().equals("")) { // if empty name
						validJason = false;
						System.out.println("\nThe file " + company_file + " is missing parameters (ticker)\n");
						break;
					}
					if(tempStock.getStartDate() == null || !isValidDate(tempStock.getStartDate())) { // if any dates are not valid
						validJason = false;
						System.out.println("\nThe file " + company_file + " is missing parameters (invalid/missing date)\n");
						break;
					}
					if(tempStock.getExchangeCode() == null || !(tempStock.getExchangeCode().equals("NYSE") || tempStock.getExchangeCode().equals("NASDAQ"))) {
						validJason = false;
						System.out.println("\nThe file " + company_file + " is missing parameters (invalid exchange)\n");
						break;
					}
				}
			}
			else {
				validJason = false;
				System.out.println("\nThe file " + company_file + " is not formatted properly");
			}
		}
		
		System.out.println("\n" + "The file has been properly read.\n");
		
		boolean seven = false;
		int choice = 0;
		while(choice != 7) {
			System.out.println("            1) Display all public companies");
			System.out.println("            2) Search for a stock (by ticker)");
			System.out.println("            3) Search for all stocks on an exchange");
			System.out.println("            4) Add a new company/stocks");
			System.out.println("            5) Remove a company");
			System.out.println("            6) Sort companies");
			System.out.println("            7) Exit");
			
	
			while(choice < 1 || choice > 7) {
				System.out.print("What would you like to do? ");
				Scanner scan1 = new Scanner(System.in);
				if(scan1.hasNextInt()) {
					choice = scan1.nextInt();
					if(choice < 1 || choice > 7) {
						System.out.println("\nThat is not a valid option.\n");
					}
				}
				else {
					choice = 0;
					System.out.println("\n" + "That is not a valid option.\n");
				}
			}
			System.out.println("");
			
			// display all public companies
			if(choice == 1) {
				for(Stock currStock : allStocks) {
					System.out.print(currStock.getName());
					System.out.print(", symbol " + currStock.getTicker());
					System.out.print(", started on " + currStock.getStartDate());
					System.out.println(", listed on " + currStock.getExchangeCode() + ",");
					System.out.println(WordUtils.wrap(currStock.getDescription() , 85).replaceAll("(?m)^", "\t"));
				}
				System.out.println("");
			}
			// search for a stock
			else if(choice == 2) {
				Stock currStock = null;
				boolean validTicker = false;
				while(!validTicker) {
					Scanner scan2 = new Scanner(System.in);
					System.out.print("What is the ticker of the company you would like to search for? ");
					String ticker = scan2.nextLine();
					
					for(Stock tempStock: allStocks) {
						if(tempStock.getTicker().equalsIgnoreCase(ticker)) {
							currStock = tempStock;
						}
					}
					if (currStock == null) {
						System.out.println("\n" + ticker + " could not be found.\n");
					}
					else {
						validTicker = true;
					}
				}
				System.out.println("\n" + currStock.getName() + ", symbol " + currStock.getTicker() + ", started on " 
				+ currStock.getStartDate() + ", listed on " + currStock.getExchangeCode() + "\n");
			}
			
			// search for all stocks on an exchange
			else if(choice == 3) {
				boolean validExchange = false;
				while(!validExchange) {
					Scanner scan3 = new Scanner(System.in);
					System.out.print("What Stock Exchange would you like to search for? ");
					String exchange = scan3.nextLine();
					ArrayList<String> result = new ArrayList<String>();
					for(Stock tempStock : allStocks) {
						if (tempStock.getExchangeCode().equalsIgnoreCase(exchange)) {
							result.add(tempStock.getTicker());
						}
					}
					if(result.size() == 0) {
						System.out.println("\nNo exchange named " + exchange.toUpperCase() + " found.\n");
					}
					else {
						System.out.println("");
						if(result.size() == 1) {
							System.out.println(result.get(0) + " found on the " +  exchange.toUpperCase() + " exchange.\n");
						}
						else if (result.size() == 2) {
							System.out.println(result.get(0) + " and " + result.get(1) + " found on the " +  exchange.toUpperCase() + " exchange.\n");
						}
						else if (result.size() > 2) {
							for(int i = 0; i < result.size()-1; i++) {
								System.out.print(result.get(i) + ", ");
							}
							System.out.println("and " + result.get(result.size()-1) + " found on the " +  exchange.toUpperCase() + " exchange.\n");
						}
						validExchange = true;
					}
				}
			}
			
			// add a new company
			else if(choice == 4) {
				boolean validStock = false;
				String stock = null;
				String stock_symbol = null;
				while(!validStock) {
					Scanner scan4 = new Scanner(System.in);
					System.out.print("What is the name of the company you would like to add? ");
					stock = scan4.nextLine();
					System.out.print("\nWhat is the stock symbol of " + stock + "? ");
					stock_symbol = scan4.nextLine();
					validStock = true;
					for(int i = 0; i < allStocks.size(); i++) { // check if the ticker exists
						if(allStocks.get(i).getTicker().toLowerCase().equals(stock_symbol.toLowerCase())) {
							System.out.println("\nThere is already an entry for " + stock + ".\n");
							validStock = false;
							break;
						}
					}
				}
				
				// validate date
				boolean validDate = false;
				String start_date = null;
				while(!validDate) {
					System.out.print("\nWhat is the start date of " + stock + "? ");
					start_date = in.nextLine();
					if(isValidDate(start_date)) {
						validDate = true;
					}
					else {
						System.out.println("\n" + start_date + " is not a valid date.");
					}
						
				}
			    
			    // validate exchange
				boolean validateExc = false;
				String stock_exchange = null;
				while(!validateExc) {
					System.out.print("\nWhat is the exchange where " + stock + " is listed? ");
					stock_exchange = in.nextLine();
					if(stock_exchange.toUpperCase().equals("NYSE") || stock_exchange.toUpperCase().equals("NASDAQ")) {
						validateExc = true;
					}
					else {
						System.out.println("\n" + stock_exchange + " is not a valid exchange.");
					}
				}
				
				System.out.print("\nWhat is the description of " + stock + "? ");
				String stock_description = in.nextLine();
				
				Stock addedStock = new Stock(stock, stock_symbol.toUpperCase(), start_date, stock_description, stock_exchange.toUpperCase());
				allStocks.add(addedStock);
				
				Gson updatedGson = new Gson();
				updatedGson.toJson(addedStock);
				
				System.out.println("\n\nThere is now a new entry for:");
				System.out.println(addedStock.getName() + ", symbol " + addedStock.getTicker() + ", started on "
				+ addedStock.getStartDate() + ", listed on " + addedStock.getExchangeCode() + ",");
				System.out.println(WordUtils.wrap(addedStock.getDescription() , 85).replaceAll("(?m)^", "\t"));
				System.out.println("");
			}
			
			// remove a company
			else if(choice == 5) {
				for (int i = 1; i <= allStocks.size(); i++) {
					System.out.println("      " + i + ") " + allStocks.get(i-1).getName() + " - " +  allStocks.get(i-1).getTicker());
				}
				boolean validNumber = false;
				int remove = 0;
				while(!validNumber) {
					Scanner scan5 = new Scanner(System.in);
					System.out.print("Which company would you like to remove? ");
					remove = scan5.nextInt();
					if (remove < 1 || remove > allStocks.size()) {
						System.out.println("\nNot a valid option.");
					}
					else {
						validNumber = true;
					}
				}
				Stock removedStock = allStocks.get(remove-1);
				allStocks.remove(remove-1);
				System.out.println("\n" + removedStock.getName() + " is now removed.");
			}
			
			// sort companies
			else if(choice == 6) {
				Scanner scan6 = new Scanner(System.in);
				System.out.println("      1) A to Z");
				System.out.println("      2) Z to A");
				System.out.print("How would you like to sort by? ");
				int sort = scan6.nextInt();
				
				System.out.print("\nYour companies are now sorted from in alphabetical order ");
				
				if(sort == 1) {  
					sortList();
					System.out.println("(A-Z).\n");
				}
				else if(sort == 2) {
					sortList();
					Collections.reverse(allStocks);
					System.out.println("(Z-A).\n");
				}
			}
			
			// exit
			else if(choice == 7) {
				seven = true;
				Scanner scan7 = new Scanner(System.in);
				System.out.println("      1) Yes");
				System.out.println("      2) No");
				System.out.print("Would you like to save your edits? ");
				int save = scan7.nextInt();
				
				if(save == 1) {
					gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
					String newJson = gson.toJson(allStocks);
					System.out.println("\nYour edits have been saved to " + company_file);
					
					FileWriter fw;
					try {
						fw = new FileWriter(company_file);
						fw.write("{\"data\":");
						fw.write(newJson);
						fw.write("}");
						fw.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				if(save == 2) {
					// nothing needs to be done
				}
				System.out.println("Thank you for using my program!");
			}
			
			else {
				System.out.println("This is not a valid option.");
			}
			choice = 0;
			if(seven) {
				choice = 7;
			}
		}
		in.close();
	}
}