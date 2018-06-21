package cn.lemonit.lemage.observer;

/**
 * 被观察者
 */
public interface Observable {
    /**
     * 添加观察者
     * @param observer
     */
    void addObserver(Observer observer);

    /**
     * 删除观察者
     * @param observer
     */
    void removeObserver(Observer observer);

    /**
     * 刷新数据
     * @param object
     */
    void notifyObservers(Object object);

}
