package com.jacobarau.helium.jdata;

public interface JDataListener<DataType> {
    /**
     * Called on main thread on first subscription and subsequent updates.
     * @param value New data value
     */
    void onDataUpdated(DataType value);
}
