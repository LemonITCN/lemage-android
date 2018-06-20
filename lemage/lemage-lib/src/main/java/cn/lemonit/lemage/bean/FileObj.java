package cn.lemonit.lemage.bean;

/**
 * 文件对象
 * 文件对象的特征是都存在路径属性和名称属性，对比是否为同一个对象的标准是路径是否相同
 *
 * @author liuri
 */
public class FileObj {

    /**
     * 文件对象的名称
     */
    protected String name;
    /**
     * 相册文件夹路径
     */
    protected String path;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        if(path != null) {
            return path.trim();
        }
        return "";
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(getClass().equals(obj.getClass()))) {
            // 不是同一类型的对象实例
            return false;
        } else {
            if (getPath() == null || getPath().equals("")) {
                return false;
            }
            FileObj fileObj = (FileObj) obj;
            return fileObj.getPath().toLowerCase().equals(getPath().toLowerCase());
        }
    }

}
