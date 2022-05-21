package lite.system.vo;

/**
 * 轉換成 MenuDto 前的基礎資料型態
 * 
 * @author DerrekTseng
 *
 */
public class MenuVo {

	/** rowid */
	Integer id;

	/** 名稱 */
	String name;

	/** 網址 */
	String url;

	/** 圖示 */
	String icon;

	/** 父目錄 */
	Integer parent;

	/** 排序 */
	Integer seq;

	/** 類型 0=群組 1=按鈕 2=連結 */
	Integer type;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public Integer getParent() {
		return parent;
	}

	public void setParent(Integer parent) {
		this.parent = parent;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}
}
