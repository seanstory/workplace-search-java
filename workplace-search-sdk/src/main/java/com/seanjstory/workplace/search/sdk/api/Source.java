package com.seanjstory.workplace.search.sdk.api;

import java.util.Iterator;

public interface Source<T extends DocumentBase> {

    public Iterator<T> getDocuments();

}
