import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidateBrandUrl {

	private static String[] specialCharacters = { "'", "-", "&", "\\+", "!"};
	private static Map<String, List<String>> similarWords = new HashMap<String, List<String>>();

	public static void main(String[] args) {

		similarWords.put("jewelry", new ArrayList<String>());
		similarWords.get("jewelry").add("jewelers");
		similarWords.put("jewelers", new ArrayList<String>());
		similarWords.get("jewelers").add("jewelry");
		similarWords.get("+").add("plus");
		// calculatePercentageCorrectness();
	}

	// public static void calculatePercentageCorrectness() {
	//
	// Connection connect = null;
	// float total = 0;
	// float correct = 0;
	// try {
	// Class.forName("com.mysql.jdbc.Driver");
	// String query = "select * from SBC.SearchResults";
	// connect = DriverManager
	// .getConnection("jdbc:mysql://localhost/SBC?" +
	// "user=root&password=password10&useSSL=true");
	// Statement Stmt = connect.createStatement();
	// ResultSet rs = Stmt.executeQuery(query);
	// while (rs.next()) {
	// String key = rs.getString(1);
	// String urls = rs.getString(2);
	// String foundUrl = validateURL(key, urls);
	// if (!foundUrl.isEmpty()) {
	// correct++;
	// } else
	// System.out.println(key + " " + urls);
	// total++;
	// }
	// } catch (Exception e) {
	// System.out.println("Exception occurred in Utils.pushToSearchResults() " +
	// e);
	// } finally {
	// try {
	// connect.close();
	// } catch (Exception e_) {
	// System.out.println("Exception in Utils.checkIfKeyExistInSql() " + e_);
	// }
	// }
	// System.out.println(correct + " " + total);
	// System.out.println((correct / total) * 100);
	// }

	public static String validateURL(String key, Brand brand) {
		key = key.toLowerCase();
		String foundUrl = "";
		// String foundUrl = validateURLUtil(key, brand);
		// if (!foundUrl.isEmpty())
		// return foundUrl;

		// removing special characters from string and perform search again
		for (String temp : specialCharacters) {
			key = key.replaceAll(temp, "");
		}
		foundUrl = validateURLUtil(key, brand);
		if (!foundUrl.isEmpty())
			return foundUrl;

		// check with cite
		for (URL url : brand.getUrls()) {
			if (url.getCite() != null && !url.getCite().equalsIgnoreCase("null") && !url.getCite().isEmpty()) {
				String cite = url.getCite().toLowerCase();
				String lowerCaseKey = key.toLowerCase();
				if (cite.contains(lowerCaseKey)) {
					return url.getUrl();
				}
			}
		}
		String[] listOfWords = key.split(" ");
		for (int i = 0; i < listOfWords.length; i++) {
			if (similarWords.containsKey(listOfWords[i])) {
				List<String> matchingWords = similarWords.get(listOfWords[i]);
				for (String word : matchingWords) {
					String[] updatedList = listOfWords.clone();
					updatedList[i] = word;

					// removing special characters from string and perform
					// search again
					StringBuilder builder = new StringBuilder();
					for (String s : updatedList) {
						builder.append(s);
					}
					String newKey = builder.toString();

					for (String temp : specialCharacters) {
						newKey = newKey.replaceAll(temp, "");
					}
					foundUrl = validateURLUtil(newKey, brand);
					if (!foundUrl.isEmpty())
						return foundUrl;

				}
			}
		}
		return "";

	}

	public static String validateURLUtil(String key, Brand brand) {
		List<String> listOfUrls = new ArrayList<String>();

		// file the urls, remove amazon.com, wikipedia.com
//		for (URL url : brand.getUrls()) {
//			if (Utils.checkIfUrlIsNotOwnerWebsite(url.getUrl())) {
//				String tempURL = url.getUrl();
//				// removing special characters from string and perform search
//				// again
//				for (String temp : specialCharacters) {
//					tempURL = tempURL.replaceAll(temp, "");
//				}
//				listOfUrls.add(tempURL);
//			}
//		}
		key = key.trim();
		boolean found = false;
		String foundURL = "";

		foundURL = exactUrlMatch(key, brand);
		if (!foundURL.isEmpty()) {
			return foundURL;
		}

		// remove space and check.
		String tempKey = key;
		tempKey = tempKey.replaceAll(" ", "");

		foundURL = exactUrlMatch(tempKey, brand);
		if (!foundURL.isEmpty()) {
			return foundURL;
		}

		// check if key is prefix exist in a url

		for (URL urlKey : brand.getUrls()) {
			// check if exact key matches
			String url = urlKey.getUrl().trim();
			Pattern p = Pattern.compile("^(http|https)://(\\w.*)");
			Matcher m = p.matcher(url);
			if (m.matches()) {
				String temp = m.group(2).split("/")[0];
				if (temp.startsWith("www")) {
					temp = temp.split("\\.")[1];
				}
				if (temp.startsWith(key)) {
					foundURL = url;
					return foundURL;
				}
			}
		}

		if (!foundURL.isEmpty())
			return foundURL;

		// check any 2, check with one also, but remove common keywords
		if (key.contains(" ")) {
			for (URL urlKey : brand.getUrls()) {
				// check if exact key matches
				String url = urlKey.getUrl().trim();
				Pattern p = Pattern.compile("^(http|https)://(\\w.*)");
				Matcher m = p.matcher(url);
				if (m.matches()) {
					String newURL = m.group(2).split("/")[0];
					String[] temp1 = key.split(" ");
					String[] temp2 = key.split(" ");
					for (int k = 0; k < temp1.length; k++) {
						for (int j = 0; j < temp2.length; j++) {
							if (j == k)
								continue;
							if (newURL.contains(temp1[k] + temp2[j]))
								return url;
						}
					}

				}
			}
		}
		// check if any string matches, can check to omit common keywords
		if (key.contains(" ")) {
			for (String temp : key.split(" ")) {
				String result = exactUrlMatch(temp, brand);
				if (!result.isEmpty()) {
					foundURL = result;
					return result;
				}
			}
		}

		// if string is of many words, check if their first alphabets matches
		if (key.contains(" ")) {
			String newKey = "";
			for (String temp : key.split(" ")) {
				if (!temp.isEmpty())
					newKey = newKey + temp.charAt(0);
			}
			for (int i = 0; i < listOfUrls.size(); i++) {
				// check if exact key matches
				String url = listOfUrls.get(i).trim();
				if (url.contains(newKey)) {
					return url;
				}
			}
		}

		return "";
	}

	public static String exactUrlMatch(String key, Brand brand) {

		String foundURL = "";

		for (URL url : brand.getUrls()) {
			try {
				// check if exact key matches
				String newUrl = url.getUrl();

				newUrl = newUrl.replaceAll("[^\\x00-\\x7F]", "");
				newUrl = newUrl.replaceAll("http://", "");
				newUrl = newUrl.replaceAll("https://", "");
				newUrl = newUrl.replaceAll("www.", "");
				newUrl = newUrl.split("\\.")[0];
				if (key.equalsIgnoreCase(newUrl))
					return url.getUrl();
			} catch (Exception e_) {
				System.out.println("Exception occurred in exactUrlMatch for url: " + key);
			}
		}
		return foundURL;
	}

	public static boolean isLinkValid(String link) {

		if (link == null || link.isEmpty())
			return false;

//		if (link.startsWith("http") || link.startsWith("www") || link.startsWith("https"))
//			return true;

		return true;
	}

}
