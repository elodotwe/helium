package com.jacobarau.helium.jdata;

import android.os.Handler;

import java.util.ArrayList;
import java.util.List;

public class JData<Value, ListenerType extends JDataListener<Value>> {
    protected List<ListenerType> listeners = new ArrayList<>();
    protected final Object valueAndListenersLock = new Object();
    protected Value value;
    protected Handler handler = new Handler();

    public JData(Value initialValue) {
        value = initialValue;
    }

    public JData() {
        this(null);
    }

    public void subscribe(final ListenerType listener) {
        synchronized (valueAndListenersLock) {
            listeners.add(listener);
        }
        handler.post(new Runnable() {
            @Override
            public void run() {
                synchronized (valueAndListenersLock) {
                    listener.onDataUpdated(value);
                }
            }
        });
    }

    public void unsubscribe(ListenerType listener) {
        synchronized (valueAndListenersLock) {
            listeners.remove(listener);
        }
    }

    public void setValue(final Value newValue) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                synchronized (valueAndListenersLock) {
                    value = newValue;
                    for (JDataListener<Value> listener: listeners) {
                        listener.onDataUpdated(value);
                    }
                }
            }
        });
    }
}
