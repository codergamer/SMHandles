import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.google.common.collect.HashMultimap;
import com.mysql.jdbc.StringUtils;

public class Utils {

	private static HashMultimap<String, String> websiteMapping = HashMultimap.create();
	private static HashMultimap<String, String> facebookMapping = HashMultimap.create();
	private static List<Brand> brands = new LinkedList<Brand>();
	public static String fileStorePath = "/Users/badhwarv/SBC/SearchResults/";
	private static String[] citeCharactersToRemove = { "<b>", "</b>", "-", "|", "</em>", "<em>" };

	public static HashMultimap<String, String> getWebsiteMapping() {
		return websiteMapping;
	}

	public static HashMultimap<String, String> getFacebookMapping() {
		return facebookMapping;
	}

	public static List<Brand> getBrands() {
		return brands;
	}

	public static void populateData() {
		try {
			String excelFilePath = "USPTO_BRANDS.xls";
			FileInputStream inputStream = new FileInputStream(new File(excelFilePath));
			// Workbook workbook = new XSSFWorkbook(inputStream);
			HSSFWorkbook workbook = new HSSFWorkbook(inputStream);

			HSSFSheet firstSheet = workbook.getSheetAt(0);
			for (int i = 1; i <= 1470; i++) {
				Row nextRow = firstSheet.getRow(i);
				websiteMapping.put(String.valueOf(nextRow.getCell(0)), String.valueOf(nextRow.getCell(2)));
				facebookMapping.put(String.valueOf(nextRow.getCell(1)), String.valueOf(nextRow.getCell(3)));
				Brand brand = new Brand(String.valueOf(nextRow.getCell(15)), String.valueOf(nextRow.getCell(17)),
						String.valueOf(nextRow.getCell(1)), String.valueOf(nextRow.getCell(2)),
						String.valueOf(nextRow.getCell(0)), String.valueOf(nextRow.getCell(11)), 1);
				brands.add(brand);

			}
		} catch (Exception e) {
			System.out.println("Exception in Utils.populateData()" + e + "" + e.getMessage());
		}
	}

	public static float getCoveragePercentage(List<String> input) {
		int totalCount = input.size();
		int validValues = 0;
		for (String value : input) {
			if (isValid(value))
				validValues++;
		}
		System.out.println("Total values: " + totalCount + ", Valid values: " + validValues);
		return validValues * 100 / totalCount;
	}

	public static void populateBrandWebDriver(Brand brand, String key) {
		try {
			WebDriver driver = LoadHtmlContent.getWebDriver(key);
			List<URL> listOfUrls = new ArrayList<URL>();
			brand.setUrls(listOfUrls);
			List<WebElement> listOfElements = driver.findElements(By.className("g"));

			StringBuilder sb = new StringBuilder();
			for (WebElement element : listOfElements) {
				String url = "", desc = "", title = "";
				try {
					WebElement urlElement = element.findElement(By.tagName("cite"));
					url = urlElement.getAttribute("innerHTML");
					WebElement descElement = element.findElement(By.className("st"));
					desc = descElement.getAttribute("innerHTML");
					WebElement titleElement = element.findElement(By.tagName("a"));
					title = titleElement.getAttribute("innerHTML");
					// url = element.select("cite").size() > 0 ?
					// element.select("cite").get(0).html() : "";
					// desc = element.select("span.st").size() > 0 ?
					// element.select("span.st").get(0).html() : "";
					// title = element.select("a[href]").size() > 0 ?
					// element.select("a[href]").get(0).html() : "";
					url = url.split(" ›")[0];
					url = url.trim();
					if (Utils.checkIfUrlIsNotOwnerWebsite(url) && ValidateBrandUrl.isLinkValid(url)) {
						sb.append(url + " , ");
						for (String temp : citeCharactersToRemove) {
							title = title.replaceAll(temp, "");
							desc = desc.replaceAll(temp, "");
						}
						title = title.replaceAll("[^\\x00-\\x7F]", "");
						desc = desc.replaceAll("[^\\x00-\\x7F]", "");
						desc = desc.replaceAll("[^\\x00-\\x7F]", "");
						brand.getUrls().add(new URL(url, title, desc));
					}
				} catch (Exception e) {
					System.out.println(
							"Issue while extracting url value for brand" + title + " " + url + " " + desc + " ");
				}
			}
			brand.setResult(sb.toString());
		} catch (Exception e) {
			System.out.println("Utils.populateBrandWebDriver() " + e);
		}

	}

	public static String getContentOfUrl(String input, boolean cache) {
		return getDocumentOfUrl(input, cache).html();
	}

	public static Document getDocumentOfUrl(String input, boolean useCache) {
		if (useCache) {
			try {
				File f = new File(fileStorePath + input);
				if (f.exists() && !f.isDirectory()) {
					byte[] encoded = Files.readAllBytes(Paths.get(fileStorePath + input));
					// System.out.println("Cache exist for " + input);
					return Jsoup.parse(new String(encoded, "UTF-8"));
				}
			} catch (Exception e) {
				System.out.println("Exception occurred in Utils.getDocumentOfUrl() " + e);
			}
		}

		String key = input;
		if (input.contains("&")) {
			key = key.replaceAll("&", "\\%26");
		}
		key = key.toLowerCase();
		// Document document = null;
		String result = "";
		try {
			result = LoadHtmlContent.getHtmlContent(key);
		} catch (Exception e) {
			System.err.println("Error occurred while doing google search of :" + key + " " + e.getMessage());
		}
		System.out.println(result);
		storeLocalDisk(fileStorePath + key, result);
		return Jsoup.parse(result);
	}

	public static void pushToSQLSearchResults(String key, String result, String brandName, String sellerName,
			String country, String fbHandle, String newUrl, String oldUrl, int uspto) {
		Connection connect = null;
		if (checkIfKeyExistInSql(key)) {
			System.out.println("key exist in sqs: " + key);
			return;
		}

		newUrl = newUrl.replaceAll("[^\\x00-\\x7F]", "");
		newUrl = newUrl.replaceAll("http://", "");
		newUrl = newUrl.replaceAll("https://", "");
		newUrl = newUrl.replaceAll("www.", "");
		newUrl = newUrl.split("/")[0];

		try {
			Class.forName("com.mysql.jdbc.Driver");
			String query = " insert into SBC.SearchResults" + " values (?, ?, ?, ?, ?, ?, ?, ?, ?)";
			connect = DriverManager
					.getConnection("jdbc:mysql://localhost/SBC?" + "user=root&password=password10&useSSL=true");
			PreparedStatement preparedStmt = connect.prepareStatement(query);
			preparedStmt.setString(1, key.toLowerCase());
			preparedStmt.setString(2, result);
			preparedStmt.setString(3, brandName);
			preparedStmt.setString(4, sellerName);
			preparedStmt.setString(5, country);
			preparedStmt.setString(6, fbHandle);
			preparedStmt.setString(7, newUrl);
			preparedStmt.setString(8, oldUrl);
			preparedStmt.setInt(9, uspto);
			preparedStmt.execute();
			System.out.println("Successfully added in sql " + brandName);
		} catch (Exception e) {
			System.out.println("Exception occurred in Utils.pushToSearchResults() " + e);
		} finally {
			try {
				connect.close();
			} catch (Exception e_) {
				System.out.println("Exception in Utils.pushToSQLSearchResults() " + e_);
			}
		}
	}

	public static boolean checkIfKeyExistInSql(String key_) {
		Connection connect = null;
		String key = key_.toLowerCase();
		try {
			Class.forName("com.mysql.jdbc.Driver");
			String query = "select * from SBC.SearchResults where SearchKey = \"" + key + "\"";
			connect = DriverManager
					.getConnection("jdbc:mysql://localhost/SBC?" + "user=root&password=password10&useSSL=true");
			Statement Stmt = connect.createStatement();
			ResultSet rs = Stmt.executeQuery(query);
			if (rs.first())
				return true;
		} catch (Exception e) {
			System.out.println("Exception occurred in Utils.pushToSearchResults() " + e);
		} finally {
			try {
				connect.close();
			} catch (Exception e_) {
				System.out.println("Exception in Utils.checkIfKeyExistInSql() " + e_);
			}
		}
		return false;
	}

	public static boolean updateResult(String key_, String oldUrl) {
		Connection connect = null;
		try {

			Class.forName("com.mysql.jdbc.Driver");
			String query = "update SBC.SearchResults set USPTO = 1, ExistingUrl = \"" + oldUrl + "\""
					+ "  where BrandName = \"" + key_ + "\"";
			connect = DriverManager
					.getConnection("jdbc:mysql://localhost/SBC?" + "user=root&password=password10&useSSL=true");
			PreparedStatement preparedStmt = connect.prepareStatement(query);

			preparedStmt.execute();

			System.out.println("Successfully updated uspto in sql " + key_);
		} catch (Exception e) {
			System.out.println("Exception occurred in Utils.pushToSearchResults() " + e);
		} finally {
			try {
				connect.close();
			} catch (Exception e_) {
				System.out.println("Exception in Utils.pushToSQLSearchResults() " + e_);
			}
		}
		return false;
	}

	public static boolean isValid(String value) {
		if (!(value == null || value.equalsIgnoreCase("null") || value.isEmpty()))
			return true;

		return false;
	}

	public static String getBrandUrl(String link) {
		Pattern p = Pattern.compile(".*q=(.*)");
		Matcher m = p.matcher(link);
		if (m.matches())
			return m.group(1).split("&")[0];
		return "";
	}

	public static void storeLocalDisk(String fileName, String html) {
		try {
			PrintWriter writer = new PrintWriter(fileName, "UTF-8");
			writer.println(html);
			writer.close();
		} catch (IOException e) {
			System.out.println("Exception in Utils.storeLocalDisk() " + e);
		}
	}

	public static boolean checkIfUrlIsNotOwnerWebsite(String result) {
		if ((!(result == null || result.isEmpty() || result.contains("amazon") || result.contains("wikipedia")
				|| result.contains("ebay") || result.contains("walmart") || result.contains("urbandictionary")
				|| result.contains("dictionary.com") || result.contains("linkedin.com")
				|| result.contains("oxforddictionaries") || result.contains("thefreedictionary.com")
				|| result.contains("github.com") || result.contains("youtube.com") || result.contains("wikia.com")
				|| result.contains("merriam-webster.com") || result.contains(" tripadvisor.com")
				|| result.contains("bloomberg.com") || result.contains("independent.co")
				|| result.contains("telegraph.co") || result.contains("sherlockology.com") || result.contains(".gov")
				|| result.contains("dictionary.") || result.contains(".dictionary") || result.contains("facebook")
				|| result.contains(".edu") || result.contains("w3schools") || result.contains("webopedia")
				|| result.contains("imdb.com") || result.contains("dailymail.com") || result.contains("snapdeal.com")
				|| result.contains("washingtonpost") || result.contains("theguardian.") || result.contains(".org")
				|| result.contains("shopping.rediff.") || result.contains("fakespot.com")
				|| result.contains("pinterest.com") || result.contains("twitter.com")
				|| result.contains("wiki.southpark.cc.com") || result.contains("aliexpress.com")
				|| result.contains("tesco.com") || result.contains("newegg.com") || result.contains("flipkart.com")
				|| result.contains("alibaba.com") || result.contains("sears.com") || result.contains("target.com")
				|| result.contains("yelp.com") || result.contains("finance.yahoo.com") || result.contains("rakuten.com")
				|| result.contains("shopyourway.com") || result.contains("instagram.com")
				|| result.contains("independent.ie") || result.contains("knoji.com")
				|| result.contains("sites.google.com")) && ValidateBrandUrl.isLinkValid(result))) {
			return true;
		}
		return false;
	}

	public static void populateBrand(Brand brand, Document doc) {

		if (StringUtils.isNullOrEmpty(brand.getBrandName())) {
			System.out.println("Brand has null name " + brand.getMerchantName() + ", " + brand.getBrandName());
		}
		// get brand
		try {
			List<URL> listOfUrls = new ArrayList<URL>();
			brand.setUrls(listOfUrls);
			StringBuilder sb = new StringBuilder();
			Elements elements = doc.getElementsByClass("g");
			String url = "", desc = "", title = "";
			for (Element element : elements) {
				try {
					url = element.select("cite").size() > 0 ? element.select("cite").get(0).html() : "";
					desc = element.select("span.st").size() > 0 ? element.select("span.st").get(0).html() : "";
					title = element.select("a[href]").size() > 0 ? element.select("a[href]").get(0).html() : "";

					if (Utils.checkIfUrlIsNotOwnerWebsite(url) && ValidateBrandUrl.isLinkValid(url)) {
						sb.append(url + " , ");
						for (String temp : citeCharactersToRemove) {
							title = title.replaceAll(temp, "");
							desc = desc.replaceAll(temp, "");
						}
						title = title.replaceAll("[^\\x00-\\x7F]", "");
						desc = desc.replaceAll("[^\\x00-\\x7F]", "");
						brand.getUrls().add(new URL(url, title, desc));

					}
				} catch (Exception e) {
					System.out.println("Issue while extracting url value for brand" + brand.getBrandName());
				}
			}
			brand.setResult(sb.toString());
		} catch (Exception e_) {
			System.out.println("Utils.populateBrand()");
		}
	}

	public static void writeToExcel() throws Exception {
		Connection connect = null;
		File file = new File("/Users/badhwarv/programming/Brands/USPTO_BRANDS_Result.xls");
		if (!file.exists())
			file.createNewFile();
		FileOutputStream fileOut = new FileOutputStream(file);
		try {
			Map<String, String> brandList = new HashMap<>();
			Class.forName("com.mysql.jdbc.Driver");
			String query = "select * from SBC.SearchResults where USPTO = 1";
			connect = DriverManager
					.getConnection("jdbc:mysql://localhost/SBC?" + "user=root&password=password10&useSSL=true");
			Statement Stmt = connect.createStatement();
			ResultSet rs = Stmt.executeQuery(query);
			while (rs.next()) {
				String brandName = rs.getString(3).trim();
				String url = rs.getString(7);
				if (brandList.containsKey(brandName)) {
					brandList.put(brandName, brandList.get(brandName) + " --- " + url);
				} else {
					brandList.put(brandName, url);
				}
			}

			FileInputStream inputStream = new FileInputStream(new File("USPTO_BRANDS.xls"));
			HSSFWorkbook workbook = new HSSFWorkbook(inputStream);
			HSSFSheet firstSheet = workbook.getSheetAt(0);

			HSSFWorkbook writeWorkBook = new HSSFWorkbook();
			HSSFSheet writeSheet = writeWorkBook.createSheet("USPTO_BRANDS");

			for (int i = 0; i <= 1469; i++) {
				Row row = firstSheet.getRow(i);
				Row writeRow = writeSheet.createRow(i);
				for (int j = 0; j < 18; j++) {
					Cell cell = writeRow.createCell(j);
					if (isValid(String.valueOf(row.getCell(j))))
						cell.setCellValue(String.valueOf(row.getCell(j)));
				}
				String brandName = String.valueOf(row.getCell(15));
				Cell cell = writeRow.createCell(18);
				String newURL = brandList.get(brandName.trim());
				if (Utils.isValid(newURL))
					cell.setCellValue(newURL);
			}

			writeWorkBook.write(fileOut);
			fileOut.flush();
			fileOut.close();
		} catch (Exception e) {
			System.out.println("Exception occurred in Utils.pushToSearchResults() " + e.getMessage());
		} finally {
			try {
				connect.close();
			} catch (Exception e_) {
				System.out.println("Exception in Utils.checkIfKeyExistInSql() " + e_);
			}
		}
	}
}
