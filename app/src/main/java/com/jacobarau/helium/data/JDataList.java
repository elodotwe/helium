package com.jacobarau.helium.data;

import java.util.ArrayList;
import java.util.List;

public class JDataList<Element> extends JData<List<Element>, JDataListListener<Element>> {
    public JDataList() {
        super(new ArrayList<Element>());
    }

    public void add(final int index, final Element newElement) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                synchronized (listenersLock) {
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
                Element removed = value.remove(index);
                // Trigger the generic broadcast.
                setValue(value);
                for (JDataListListener<Element> listener: listeners) {
                    listener.onDelete(removed, index);
                }
            }
        });
    }

    public void remove(Element toDelete) {
        int index = value.indexOf(toDelete);
        if (index < 0) return;
        remove(index);
    }
}
