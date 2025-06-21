interface Worker {
    void work();
    void eat(); // 不是所有worker都支持
}

class Robot implements Worker {
    public void work() { System.out.println("Robot working"); }

    public void eat() {
        // 很明显需要强行implement，然后抛出异常，同时也违反了里氏替换LSP
        throw new UnsupportedOperationException("Robot doesn't eat");
    }
}
