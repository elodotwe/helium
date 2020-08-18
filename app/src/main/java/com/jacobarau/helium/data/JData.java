package com.jacobarau.helium.data;

import android.os.Handler;

import java.util.ArrayList;
import java.util.List;

public class JData<Value, ListenerType extends JDataListener<Value>> {
    protected List<ListenerType> listeners = new ArrayList<>();
    protected final Object listenersLock = new Object();
    protected Value value;
    protected Handler handler = new Handler();

    public JData(Value initialValue) {
        value = initialValue;
    }

    public JData() {
        this(null);
    }

    public void subscribe(final ListenerType listener) {
        synchronized (listenersLock) {
            listeners.add(listener);
            final Value toSend = value;
            handler.post(new Runnable() {
                @Override
                public void run() {
                    listener.onDataUpdated(toSend);
                }
            });
        }
    }

    public void unsubscribe(ListenerType listener) {
        synchronized (listenersLock) {
            listeners.remove(listener);
        }
    }

    public void setValue(final Value newValue) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                synchronized (listenersLock) {
                    value = newValue;
                    for (JDataListener<Value> listener: listeners) {
                        listener.onDataUpdated(value);
                    }
                }
            }
        });
    }
}
