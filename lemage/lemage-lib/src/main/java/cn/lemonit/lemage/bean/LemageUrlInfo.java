package cn.lemonit.lemage.bean;

import java.util.Map;

/**
 * LemageURL信息对象
 *
 * @author LemonITCN - liuri
 */
public class LemageUrlInfo {

    // URL格式
    // lemage://source/type/tagggggggggg?param1=xxx&param2=xxx
    // URL举例
    // lemage://album/local/SDCARD/DICM/XXXXX/AAA.PNG?width=1024&height=768
    // lemage://sandbox/short/1111-2222-33333333-44444

    /**
     * 图片来源
     */
    private String source;
    /**
     * 图片类型
     */
    private String type;
    /**
     * 图片具体标识
     */
    private String tag;
    /**
     * 图片的参数列表
     */
    private Map<String, String> params;

    public LemageUrlInfo() {
    }

    /**
     * 通过LemageURL初始化对象
     *
     * @param url LemageURL字符串
     */
    public LemageUrlInfo(String url) {

    }

    public LemageUrlInfo(String source, String type, String tag, Map<String, String> params) {
        this.source = source;
        this.type = type;
        this.tag = tag;
        this.params = params;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }
}
