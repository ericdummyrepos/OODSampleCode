class ReadOnlyList<E> implements List<E> { // 不用强行绑定所有的行为
    private final List<E> internal;

    public ReadOnlyList(List<E> list) {
        this.internal = Collections.unmodifiableList(list);
    }

    // 可以实现读写分离
    public int size() { return internal.size(); }
    public E get(int index) { return internal.get(index); }
    // ...其他只读方法
}

