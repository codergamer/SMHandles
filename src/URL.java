
public class URL {
	private String mUrl;
	private String mCite;
	private String mDesc;

	public URL(String url, String cite, String desc) {
		mUrl = url;
		mCite = cite;
		mDesc = desc;
	}

	public String getUrl() {
		return mUrl;
	}

	public String getCite() {
		return mCite;
	}

	public String getDesc() {
		return mDesc;
	}
}
