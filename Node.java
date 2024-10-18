public class Node {
    Tasks data;
    Node prev;
    Node next;

    public Node(Node prev, Tasks data, Node next) {
        this.prev = prev;
        this.data = data;
        this.next = next;
    }
}
