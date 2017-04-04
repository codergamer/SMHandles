import java.util.List;

public class Brand {
	private String mTitle;
	private List<URL> mUrls;
	private String mDesc;
	private String mReportingCountry;
	private String mCustomerId;
	private String mMerchantName;
	private String mSalesChannel;
	private String mLunchYear;
	private String mBrandName;
	private String mCategory;
	private String mFacebookHandle;
	private String mFBLikes;
	private String mResult;
	private String mOldUrl;
	private String mUrl;
	private String mCite;
	private int mUSPTO;

	public Brand() {

	}

	public Brand(String brandName, String merchantName, String url, String fbHandle, String reportingCountry,
			String category, int uspto) {
		mBrandName = brandName;
		mMerchantName = merchantName;
		mFacebookHandle = fbHandle;
		mOldUrl = url;
		mReportingCountry = reportingCountry;
		mCategory = category;
		mUSPTO = uspto;
	}

	public String getUrl() {
		return mUrl;
	}

	public void setUrl(String url) {
		mUrl = url;
	}
	

	public int getUSPTO() {
		return mUSPTO;
	}

	public void setUSPTO(int uspto) {
		mUSPTO = uspto;
	}

	public String getCategory() {
		return mCategory;
	}

	public void setCategory(String category) {
		mCategory = category;
	}

	public String getCite() {
		return mCite;
	}

	public void setCite(String cite) {
		mCite = cite;
	}

	public void setResult(String result) {
		mResult = result;
	}

	public void setUrls(List<URL> urls) {
		mUrls = urls;
	}

	public List<URL> getUrls() {
		return mUrls;
	}

	public String getResult() {
		return mResult;
	}

	public String getMerchantName() {
		return mMerchantName;
	}

	public String getOldUrl() {
		return mOldUrl;
	}

	public String getBrandName() {
		return mBrandName;
	}

	public void setBrandName(String name) {
		mBrandName = name;
	}

	public String getFBHandle() {
		return mFacebookHandle;
	}

	public String getReportingCountry() {
		return mReportingCountry;
	}

	public String getTitle() {
		return mTitle;
	}

	public String getDesc() {
		return mDesc;
	}
}
