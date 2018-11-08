package cn.lemonit.lemage.been;

/**
 * 图片尺寸描述类
 *
 * @author LemonITCN - liuri
 */
public class ImageSize {

    private Integer width;
    private Integer height;

    public ImageSize() {
    }

    public ImageSize(Integer width, Integer height) {
        this.width = width;
        this.height = height;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }
}
