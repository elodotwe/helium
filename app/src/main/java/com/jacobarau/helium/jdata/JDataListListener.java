package com.jacobarau.helium.jdata;

import java.util.List;

public interface JDataListListener<Element> extends JDataListener<List<Element>> {
    void onAdd(Element newElement, int index);
    void onDelete(Element deletedElement, int index);
}
