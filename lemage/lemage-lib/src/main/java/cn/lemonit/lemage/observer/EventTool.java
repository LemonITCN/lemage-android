package cn.lemonit.lemage.observer;

/**
 * 观察者工具类
 */
public class EventTool {

    private static EventTool instance;

    private ObservableAchieve mObservableAchieve;

    private EventTool() {
        mObservableAchieve = new ObservableAchieve();
    }

    public static EventTool getInstance() {
        if(instance == null) {
            synchronized (EventTool.class) {
                if(instance == null) instance = new EventTool();
            }
        }
        return instance;
    }

    public void register(Observer observer) {
        mObservableAchieve.addObserver(observer);
    }

    public void unRegister(Observer observer) {
        mObservableAchieve.removeObserver(observer);
    }

    public void post(Object object) {
        mObservableAchieve.notifyObservers(object);
    }
}
