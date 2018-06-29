package cn.lemonit.lemage.bean;

/**
 * Lemage全局使用的文本信息对象
 *
 * @author LemonITCN - liuri
 */
public class LemageUsageText {

    private String complete;
    private String cancel;
    private String back;
    private String preview;
    private String originalImage;
    private String allImages;

    private static LemageUsageText CN_TEXT;
    private static LemageUsageText EN_TEXT;

    public static LemageUsageText cnText() {
        if (CN_TEXT == null) {
            CN_TEXT = new LemageUsageText();
            CN_TEXT.setComplete("完成");
            CN_TEXT.setCancel("取消");
            CN_TEXT.setBack("返回");
            CN_TEXT.setPreview("预览");
            CN_TEXT.setOriginalImage("原图");
            CN_TEXT.setAllImages("全部照片");
        }
        return CN_TEXT;
    }

    public static LemageUsageText enText() {
        if (EN_TEXT == null) {
            EN_TEXT = new LemageUsageText();
            EN_TEXT.setComplete("Complete");
            EN_TEXT.setCancel("Cancel");
            EN_TEXT.setBack("Back");
            EN_TEXT.setPreview("Preview");
            EN_TEXT.setOriginalImage("Original");
            EN_TEXT.setAllImages("All images");
        }
        return EN_TEXT;
    }

    public String getComplete() {
        return complete;
    }

    public void setComplete(String complete) {
        this.complete = complete;
    }

    public String getCancel() {
        return cancel;
    }

    public void setCancel(String cancel) {
        this.cancel = cancel;
    }

    public String getBack() {
        return back;
    }

    public void setBack(String back) {
        this.back = back;
    }

    public String getPreview() {
        return preview;
    }

    public void setPreview(String preview) {
        this.preview = preview;
    }

    public String getOriginalImage() {
        return originalImage;
    }

    public void setOriginalImage(String originalImage) {
        this.originalImage = originalImage;
    }

    public String getAllImages() {
        return allImages;
    }

    public void setAllImages(String allImages) {
        this.allImages = allImages;
    }
}
