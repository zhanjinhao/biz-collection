package cn.addenda.bc.bc.jc.cache;

import java.util.LinkedList;

/**
 * @author addenda
 * @since 2023/05/30
 */
public class LinkedListLruDeque<P> implements LruDeque<P> {

    private final LinkedList<P> linkedList = new LinkedList<>();

    @Override
    public P getFirst() {
        return linkedList.getFirst();
    }

    @Override
    public void addLast(P p) {
        linkedList.addLast(p);
    }

    @Override
    public void remove(P p) {
        linkedList.remove(p);
    }

    @Override
    public boolean contains(P p) {
        return linkedList.contains(p);
    }

    @Override
    public long size() {
        return linkedList.size();
    }
}
