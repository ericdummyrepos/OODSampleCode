class ReadOnlyList<E> extends ArrayList<E> {
    @Override
    public boolean add(E e) {
        throw new UnsupportedOperationException("Cannot modify read-only list");
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException("Cannot modify read-only list");
    }
}

public void addUser(List<String> list) {
    list.add("Alice");
}

addUser(new ReadOnlyList<>());
