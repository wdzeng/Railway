package utility;

/**
 * Html contents.
 */
public class HtmlContent {

	private String content;

	/**
	 * Constructs html contents.
	 * @param html html contents
	 */
	public HtmlContent(String html) {
		this.content = html;
	}

	/**
	 * Gets a string which spaces and line-wrapping character are removed.
	 */
	public String collapse() {
		return content.replaceAll("[\\n\\r]", "").replaceAll(">\\s+<", "><").replaceAll("\\s+", " ");
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof HtmlContent) {
			return content.equals(((HtmlContent) o).content);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return content.hashCode();
	}

	@Override
	public String toString() {
		return content;
	}
}
