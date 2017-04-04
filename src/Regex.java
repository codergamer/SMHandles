import java.io.IOException;

import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class Regex {

	public static void main(String[] args) {

		String url = "http://www.bestduffelbag.net/bluboon-travel-duffel-bag-canvas-leather-overnight-bag/";
		url = url.replaceAll("[^\\x00-\\x7F]", "");
		Document document = null;
		try {
			document = Jsoup.connect("http://www.google.com/search?q=" + "aesthetica")
					.userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:5.0) Gecko/20100101 Firefox/5.0")
					.followRedirects(false).get();
			
			Response res = Jsoup.connect("http://www.google.com/search?q=" + "aesthetica")
					.userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:5.0) Gecko/20100101 Firefox/5.0")
					.followRedirects(false).execute();
			
			System.out.println(res.url());
			
			res = Jsoup.connect(res.header("location"))
					.userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:5.0) Gecko/20100101 Firefox/5.0")
					.followRedirects(false).execute();
			
			System.out.println(res.parse().toString());

			Utils.storeLocalDisk("/Users/badhwarv/SBC/SearchResults/" + "aesthetica", res.parse().toString());

		} catch (IOException e) {
			// System.err.println("Error occurred while doing google search of
			// :" + file + " " + e.getMessage());
		}

		// url = url.replaceAll("http://", "");
		// url = url.replaceAll("www.", "");
		// url = url.split("/")[0];
		System.out.println(url);
		// String input[] =
		// {"http://www.google.co.in/url?q=http://www.ritzcamera.com/&sa=U&ved=0ahUKEwjp5Pbu8KLSAhVFMI8KHaTsBjwQFggUMAA&usg=AFQjCNGaSTpi05l-a4Dpk4crheLNe5CT3Q","http://www.google.co.in/url?q=https://en.wikipedia.org/wiki/Ritz_Camera_Centers&sa=U&ved=0ahUKEwjp5Pbu8KLSAhVFMI8KHaTsBjwQFggoMAc&usg=AFQjCNHnFEeGozrz9Wr-i-yHRk28Tg9gog","http://www.google.co.in/url?q=https://www.amazon.com/Ritz-Camera/pages/7296466011&sa=U&ved=0ahUKEwjp5Pbu8KLSAhVFMI8KHaTsBjwQFgguMAg&usg=AFQjCNGq0Pc3afdmjW2UHaPFBMQ2TXxpFA","http://www.google.co.in/url?q=https://www.groupon.com/coupons/stores/ritzcamera.com&sa=U&ved=0ahUKEwjp5Pbu8KLSAhVFMI8KHaTsBjwQFgg0MAk&usg=AFQjCNHEUy4gYe4yQU9UCPo9DALqmk_k2Q","http://www.google.co.in/url?q=http://photosecrets.com/the-rise-and-fall-of-ritz-camera&sa=U&ved=0ahUKEwjp5Pbu8KLSAhVFMI8KHaTsBjwQFgg6MAo&usg=AFQjCNGc82Mj8qiOQzt2m2gqUIbUlt43wA","http://www.google.co.in/url?q=https://www.ritzpix.com/about&sa=U&ved=0ahUKEwjp5Pbu8KLSAhVFMI8KHaTsBjwQFghAMAs&usg=AFQjCNEu0Cti9PP1An8Ibykjq6rQSWxYDA","http://www.google.co.in/url?q=http://www.resellerratings.com/store/Ritz_Camera_7&sa=U&ved=0ahUKEwjp5Pbu8KLSAhVFMI8KHaTsBjwQFghGMAw&usg=AFQjCNHR082BfWtP5Duy-Umt_tDNgO0v7Q","http://www.google.co.in/url?q=http://t0.gstatic.com/images%3Fq%3Dtbn:ANd9GcQBP7sB96SofE_5LodsZcA2avnqBRFOMtXAuSYoGTdMIsXcqaoA&sa=U&ved=0ahUKEwjp5Pbu8KLSAhVFMI8KHaTsBjwQndQBCF8wDQ&usg=AFQjCNGuhCx-cuLxsj3q5hZSyKssFbT3Ww"};
		// Pattern p = Pattern.compile(".*q=(.*)");
		// for(int i = 0; i < input.length; i++){
		// Matcher m = p.matcher(input[i]);
		// if(m.matches())
		// System.out.println(m.group(1).split("&")[0]);
		// }

		// String csvFile = "result.csv";
		// String line = "";
		// int count = 0;
		// try (BufferedReader br = new BufferedReader(new FileReader(csvFile)))
		// {
		// while ((line = br.readLine()) != null) {
		// String[] result = line.split(";");
		// if (result.length > 7 && result[6] != null &&
		// !result[6].equalsIgnoreCase("null")) {
		// result[6] = result[6].replaceAll("http://", "");
		// result[6] = result[6].replaceAll("https://", "");
		// result[6] = result[6].replaceAll("www.", "");
		// if (result[6].endsWith("/")) {
		// result[6] = result[6].substring(0, result[6].length() - 1);
		// }
		// if (!result[6].trim().equalsIgnoreCase(result[7].trim())){
		// System.out.println(result[6] + " " + result[7]);
		// count++;
		// }
		// } else
		// System.out.println("--------------Issue with line: " +
		// result.toString());
		//
		// }
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
	}

}
