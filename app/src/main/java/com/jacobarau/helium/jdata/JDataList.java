package com.jacobarau.helium.jdata;

import java.util.ArrayList;
import java.util.List;

public class JDataList<Element extends Copyable<Element>> extends JData<List<Element>, JDataListListener<Element>> {
    public JDataList() {
        super(new ArrayList<Element>());
    }

    public void add(final int index, final Element newElement) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                synchronized (valueAndListenersLock) {
                    value.add(index, newElement);
                    // Trigger the generic broadcast.
                    setValue(value);
                    for (JDataListListener<Element> listener: listeners) {
                        listener.onAdd(newElement, index);
                    }
                }
            }
        });
    }

    public void add(Element newElement) {
        add(value.size(), newElement);
    }

    public void remove(final int index) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                synchronized (valueAndListenersLock) {
                    Element removed = value.remove(index);
                    // Trigger the generic broadcast.
                    setValue(value);
                    for (JDataListListener<Element> listener : listeners) {
                        listener.onDelete(removed, index);
                    }
                }
            }
        });
    }

    public void remove(Element toDelete) {
        synchronized (valueAndListenersLock) {
            int index = value.indexOf(toDelete);
            if (index < 0) return;
            remove(index);
        }
    }

    public List<Element> getClonedList() {
        synchronized (valueAndListenersLock) {
            List<Element> copy = new ArrayList<>();
            for (Element e : value) {
                copy.add(e.copy());
            }
            return copy;
        }
    }
}
