package cn.lemonit.lemage.observer;

import java.util.ArrayList;

/**
 * 被观察者实现类
 */
public class ObservableAchieve implements Observable {

    private ArrayList<Observer> observers;

    @Override
    public void addObserver(Observer observer) {
        if (observers == null) {
            observers = new ArrayList<>();
        }
        observers.add(observer);
    }

    @Override
    public void removeObserver(Observer observer) {
        if (observers == null || observers.size() <= 0) {
            return;
        }
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(Object object) {
        if (observers == null || observers.size() <= 0) {
            return;
        }
        for (Observer observer : observers) {
            observer.updata(object);
        }
    }
}
