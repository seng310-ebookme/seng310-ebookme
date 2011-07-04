package cz.cacek.ebook.packager;


/**
 * Simple JavaBean used for storing project info.
 * @author Josef Cacek
 */
public class ProjectInfo {
	private int bookCount;
	private String splashPath;
	private String jadPath;

	public String getSplashPath() {
		return splashPath;
	}

	public void setSplashPath(String splashPath) {
		this.splashPath = splashPath;
	}

	public String getJadPath() {
		return jadPath;
	}

	public void setJadPath(String jadPath) {
		this.jadPath = jadPath;
	}

	public int getBookCount() {
		return bookCount;
	}

	public void setBookCount(int bookCount) {
		this.bookCount = bookCount;
	}
}
