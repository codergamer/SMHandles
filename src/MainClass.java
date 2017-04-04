import java.util.List;

import org.jsoup.nodes.Document;

public class MainClass {

	public static void main(String[] args) throws Exception {

	//	Utils.populateData();
		new Thread(new MakeRequests()).start();
		//Utils.writeToExcel();

		// check with one key for debugging
		// checkForDebug();

	}

	public static void checkForDebug() {
		String key = "Sunny & Honey";
		Document doc = Utils.getDocumentOfUrl(key, true);
		Brand brand = new Brand();
		Utils.populateBrand(brand, doc);
		String foundUrl = ValidateBrandUrl.validateURL(key, brand);
		System.out.println(foundUrl);
		for (URL url : brand.getUrls())
			System.out.println("url: " + url.getUrl() + "   cite: " + url.getCite());
	}
}

class MakeRequests implements Runnable {
	@Override
	public void run() {
		int alreadyAvailable = 0;
		int currentCount = 0;
		LoadHtmlContent.initDriver();
		List<Brand> brands = Utils.getBrands();
		System.out.println(brands.size());
		for (Brand brand : brands) {
		//if (brand.getBrandName().equalsIgnoreCase("anker"))
			System.out.println("Current brand count: " + ++currentCount);
				try {
					boolean foundResult = false;
					// check with brand key first
					String key = brand.getBrandName().toLowerCase();
					// Document doc = Utils.getDocumentOfUrl(key, true);
					// populate brands
					// Utils.populateBrand(brand, doc);
					if (!Utils.checkIfKeyExistInSql(key)) {
						// validate key
						System.out.println("Starting for brand: " + brand.getBrandName());
						Utils.populateBrandWebDriver(brand, key);
						String foundUrl = ValidateBrandUrl.validateURL(key, brand);
						if (!foundUrl.isEmpty()) {
							foundResult = true;
							Utils.pushToSQLSearchResults(key, brand.getResult(), brand.getBrandName(),
									brand.getMerchantName(), brand.getReportingCountry(), brand.getFBHandle(), foundUrl,
									brand.getOldUrl(), brand.getUSPTO());
							continue;
						}
					} else{
						foundResult = true;
						System.out.println("Already available " + brand.getBrandName());
						alreadyAvailable++;
						Utils.updateResult(brand.getBrandName(), brand.getOldUrl());
					}

					// check with seller

					if (!foundResult) {
						key = brand.getMerchantName() + " " + brand.getBrandName();
						if (!Utils.checkIfKeyExistInSql(key)) {
							// doc = Utils.getDocumentOfUrl(key, true);
							// populate brands
							Utils.populateBrandWebDriver(brand, key);
							// validate key
							String finalUrl = "";
							for (URL url : brand.getUrls()) {
								boolean tempURL = ValidateBrandUrl.isLinkValid(url.getUrl());
								if (tempURL) {
									finalUrl = url.getUrl();
									break;
								}
							}
							if (!finalUrl.isEmpty()) {
								foundResult = true;
								System.out.println("Found url for merchant name + brand name : " + brand.getBrandName()
										+ " , url: " + finalUrl);
								Utils.pushToSQLSearchResults(key, brand.getResult(), brand.getBrandName(),
										brand.getMerchantName(), brand.getReportingCountry(), brand.getFBHandle(),
										finalUrl, brand.getOldUrl(), brand.getUSPTO());
								continue;
							}
						} else{
							foundResult = true;
							System.out.println("Already available " + brand.getBrandName());
							alreadyAvailable++;
							Utils.updateResult(brand.getBrandName(), brand.getOldUrl());
						}
					}

					if (!foundResult) {
						key = brand.getMerchantName() + " " + brand.getBrandName() + " brand";
						if (!Utils.checkIfKeyExistInSql(key)) {
							Utils.populateBrandWebDriver(brand, key);
							// validate key
							String finalUrl = "";
							for (URL url : brand.getUrls()) {
								boolean tempURL = ValidateBrandUrl.isLinkValid(url.getUrl());
								if (tempURL)
									finalUrl = url.getUrl();
							}
							if (!finalUrl.isEmpty()) {
								foundResult = true;
								System.out.println("Found url for merchant name + brand name + brand key: "
										+ brand.getBrandName() + " , url: " + brand.getUrls().get(0).getUrl());
								Utils.pushToSQLSearchResults(key, brand.getResult(), brand.getBrandName(),
										brand.getMerchantName(), brand.getReportingCountry(), brand.getFBHandle(),
										finalUrl, brand.getOldUrl(), brand.getUSPTO());
								continue;
							}
						}else{
							foundResult = true;
							System.out.println("Already available " + brand.getBrandName());
							alreadyAvailable++;
							Utils.updateResult(brand.getBrandName(), brand.getOldUrl());
						}
					}

					if (!foundResult) {
						key = brand.getMerchantName() + " brand";
						if (!Utils.checkIfKeyExistInSql(key)) {
							// doc = Utils.getDocumentOfUrl(key, true);
							// populate brands
							// Utils.populateBrand(brand, doc);
							Utils.populateBrandWebDriver(brand, key);
							// validate key
							String finalUrl = "";
							for (URL url : brand.getUrls()) {
								boolean tempURL = ValidateBrandUrl.isLinkValid(url.getUrl());
								if (tempURL)
									finalUrl = url.getUrl();
							}
							if (!finalUrl.isEmpty()) {
								foundResult = true;
								System.out.println("Found url for merchant name + brand key: " + brand.getBrandName()
										+ " , url: " + brand.getUrls().get(0).getUrl());
								Utils.pushToSQLSearchResults(key, brand.getResult(), brand.getBrandName(),
										brand.getMerchantName(), brand.getReportingCountry(), brand.getFBHandle(),
										finalUrl, brand.getOldUrl(), brand.getUSPTO());
								continue;
							}
						} else{
							foundResult = true;
							System.out.println("Already available " + brand.getBrandName());
							alreadyAvailable++;
							Utils.updateResult(brand.getBrandName(), brand.getOldUrl());
						}
					}

					if (!foundResult)
						System.out.println("Found none for brand: " + brand.getBrandName());

				} catch (Exception e) {
					System.out.println("Exception occurred in MakeRequests.run() for key: " + brand.getBrandName() + e);
				}
				System.out.println("Already available were: " + alreadyAvailable);
		}

		LoadHtmlContent.closeDriver();
	}
}
